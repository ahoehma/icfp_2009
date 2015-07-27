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
public class HohmannController extends AbstractOrbitController {

	private double targetOrbit;
	private double orgTargetOrbit;
	private boolean onMyWayYet;
	private int targetTime = -1;
	private double startOrbit;
	private int transferCounter = 0;
	private final int NUM_TRANSFERS = 5;
	private final int STABILIZING_ORBIT_TIME = 100;
	private int currentWaitTime;
	private double originalOrbit;

	/**
	 * @param configuration
	 *            supports 1001-1004
	 */
	public HohmannController(final int configuration) {
		super(new File("src/vms/bin1.obf"), configuration);
	}

	public double getTargetOrbit() {
		return orgTargetOrbit;
	}

	@Override
	public void initController() {
		super.initController();
		onMyWayYet = false;
		targetTime = -1;
		startOrbit = 0;
		originalOrbit = 0;
		transferCounter = 0;
		currentWaitTime = 0;
	}

	@Override
	void readOutput(final Memory memory) {
		if (originalOrbit == 0) {
			originalOrbit = (Utils.distance(new Point(sxRelativeToEarth,
					syRelativeToEarth), new Point(0, 0)));
		}
		orgTargetOrbit = memory.getOutputPort(0x4);
		targetOrbit = (memory.getOutputPort(0x4) - originalOrbit)
				/ NUM_TRANSFERS * (transferCounter + 1) + originalOrbit;
	}

	@Override
	void writeInput(final Memory memory) {
		memory.setInputPort(2, 0);
		memory.setInputPort(3, 0);
		// System.out.println(cyclesCounter);
		if (prevSx != 0 && !onMyWayYet) {
			if (currentWaitTime < STABILIZING_ORBIT_TIME) {
				currentWaitTime++;
				return;
			}
			startOrbit = (Utils.distance(new Point(sxRelativeToEarth,
					syRelativeToEarth), new Point(0, 0)));
			System.out.println("Current radius: " + startOrbit);
			System.out.println("Target  radius: " + targetOrbit);
			System.out.println("Current v = " + currentSpeed);
			final Speed v1 = Utils.homanSpeed1(currentSpeed, startOrbit,
					targetOrbit);
			System.out.println("v1 = " + v1);
			final double holdTime = Utils
					.homanHoldTime(startOrbit, targetOrbit);
			targetTime = (int) holdTime + cyclesCounter;
			System.out.println("idleTime = " + holdTime);
			memory.setInputPort(2, v1.vx);
			memory.setInputPort(3, v1.vy);
			onMyWayYet = true;
		} else if (cyclesCounter == targetTime) {
			final Speed v2 = Utils.homanSpeed2(currentSpeed, startOrbit,
					targetOrbit);
			System.out.println("v2 = " + v2);
			memory.setInputPort(2, v2.vx);
			memory.setInputPort(3, v2.vy);
			// use multiple transfers to use more fuel, bug in binary 1
			if (transferCounter < NUM_TRANSFERS - 1) {
				transferCounter++;
				onMyWayYet = false;
				currentWaitTime = STABILIZING_ORBIT_TIME;
			}
		}
	}
}
