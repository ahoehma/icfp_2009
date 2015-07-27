/**
 *
 */
package sak.orbit.controller;

import java.io.File;

import sak.orbit.core.Memory;
import sak.orbit.math.Point;
import sak.orbit.math.Speed;
import sak.orbit.math.Utils;

/**
 * @author andreas
 */
public class MeetAndGreetController extends AbstractOrbitController {

	protected boolean onMyWayYet;
	protected int targetTime = -1;
	protected double startOrbit;
	protected double sxRelativeToTargetSatellite;
	protected double syRelativeToTargetSatellite;
	protected double prevSxRelativeToTargetSatellite;
	protected double prevSyRelativeToTargetSatellite;
	protected boolean initialized;
	protected Point currPos;
	protected Point otherPos;
	protected Point prevOtherPos;
	protected Speed otherSpeed;
	protected double homanHoldTime;
	protected double targetOrbit;
	protected double segmentAlpha;
	protected double prevSxRelativeToEarth;
	protected double prevSyRelativeToEarth;

	/**
	 * @param file
	 * @param configuration
	 */
	public MeetAndGreetController(final File file, final int configuration) {
		super(file, configuration);
	}

	/**
	 * @param configuration
	 *            supports 2001-2004
	 */
	public MeetAndGreetController(final int configuration) {
		super(new File("src/vms/bin2.obf"), configuration);
	}

	public double getSxRelativeToTargetSatellite() {
		return sxRelativeToTargetSatellite;
	}

	public double getSyRelativeToTargetSatellite() {
		return syRelativeToTargetSatellite;
	}

	@Override
	void readOutput(final Memory memory) {
		prevSxRelativeToTargetSatellite = sxRelativeToTargetSatellite;
		prevSyRelativeToTargetSatellite = syRelativeToTargetSatellite;
		sxRelativeToTargetSatellite = memory.getOutputPort(0x4);
		syRelativeToTargetSatellite = memory.getOutputPort(0x5);
	}

	@Override
	void writeInput(final Memory memory) {
		memory.setInputPort(2, 0);
		memory.setInputPort(3, 0);

		if (cyclesCounter == targetTime) {
			final Speed v2 = Utils.homanSpeed2(currentSpeed, startOrbit,
					targetOrbit);
			memory.setInputPort(2, v2.vx);
			memory.setInputPort(3, v2.vy);
		}

		if (initialized) {
			final double currAngle = Utils.magGetAngleFromPoint(otherPos);
			final double targetAngle = Utils.normalizeAngle(currAngle
					+ segmentAlpha);
			final Point targetPoint = Utils.magGetPointFromAngle(targetOrbit,
					targetAngle);

			final Point homanTargetPoint = Utils.magGetHomanTargetPoint(
					currPos, targetOrbit);

			if (!onMyWayYet
					&& Utils.distance(homanTargetPoint, targetPoint) < 500) {
				// TODO START
				final Speed v1 = Utils.homanSpeed1(currentSpeed, startOrbit,
						targetOrbit);
				targetTime = (int) homanHoldTime + cyclesCounter;
				memory.setInputPort(2, v1.vx);
				memory.setInputPort(3, v1.vy);
				onMyWayYet = true;
			}
		}

		if (prevSxRelativeToEarth != 0 && !initialized) {
			// one time initialization

			startOrbit = (Utils.distance(new Point(sxRelativeToEarth,
					syRelativeToEarth), new Point(0, 0)));
			targetOrbit = Utils.distance(otherPos, new Point(0, 0));
			System.out.println("Current radius: " + startOrbit);
			System.out.println("Target  radius: " + targetOrbit);

			System.out.println("Current v = " + currentSpeed);
			System.out.println("Other v = " + otherSpeed);

			homanHoldTime = Utils.homanHoldTime(startOrbit, targetOrbit);
			System.out.println("Hold time: " + homanHoldTime);

			final double umfang = Utils.magGetUmfang(targetOrbit);
			final double segment = Math.sqrt(otherSpeed.vx * otherSpeed.vx
					+ otherSpeed.vy * otherSpeed.vy)
					* homanHoldTime;
			System.out.println("segment length for other sat: " + segment);

			segmentAlpha = Utils.magGetAngleOfSegmentInUmfang(umfang, segment);
			System.out.println("  this is an angle of: " + segmentAlpha);

			initialized = true;
		}
	}
}
