/**
 *
 */
package sak.orbit.controller;

import java.io.File;

import sak.orbit.core.Memory;

/**
 * @author andreas
 */
public class ClearSkiesController extends AbstractOrbitController {

	protected double sxRelativeToFuelingStation;
	protected double syRelativeToFuelingStation;
	protected double sxRelativeToMoon;
	protected double syRelativeToMoon;
	protected double fuelRemainingFuelingStation;
	protected double sxRelativeToTargetSatellite[] = new double[11];
	protected double syRelativeToTargetSatellite[] = new double[11];
	protected double targetSatelliteSuccessfullCollected[] = new double[11];

	public ClearSkiesController(final File file, final int configuration) {
		super(file, configuration);
	}

	/**
	 * @param configuration
	 *            supports 4001-4004
	 */
	public ClearSkiesController(final int configuration) {
		super(new File("src/vms/bin4.obf"), configuration);
	}

	/**
	 * @return the fuelRemainingFuelingStation
	 */
	public double getFuelRemainingFuelingStation() {
		return fuelRemainingFuelingStation;
	}

	/**
	 * @return the sxRelativeToFuelingStation
	 */
	public double getSxRelativeToFuelingStation() {
		return sxRelativeToFuelingStation;
	}

	/**
	 * @return the sxRelativeToMoon
	 */
	public double getSxRelativeToMoon() {
		return sxRelativeToMoon;
	}

	/**
	 * @param theI
	 * @return
	 */
	public double getSxRelativeToTarget(final int i) {
		return sxRelativeToTargetSatellite[i];
	}

	/**
	 * @return the syRelativeToFuelingStation
	 */
	public double getSyRelativeToFuelingStation() {
		return syRelativeToFuelingStation;
	}

	/**
	 * @return the syRelativeToMoon
	 */
	public double getSyRelativeToMoon() {
		return syRelativeToMoon;
	}

	/**
	 * @param i
	 * @return
	 */
	public double getSyRelativeToTarget(final int i) {
		return syRelativeToTargetSatellite[i];
	}

	@Override
	void readOutput(final Memory memory) {
		sxRelativeToFuelingStation = memory.getOutputPort(0x4);
		syRelativeToFuelingStation = memory.getOutputPort(0x5);
		fuelRemainingFuelingStation = memory.getOutputPort(0x6);
		for (int i = 0; i < 11; i++) {
			sxRelativeToTargetSatellite[i] = memory.getOutputPort(3 * i + 0x7);
			syRelativeToTargetSatellite[i] = memory.getOutputPort(3 * i + 0x8);
			targetSatelliteSuccessfullCollected[i] = memory
					.getOutputPort(3 * i + 0x9);
		}
		sxRelativeToMoon = memory.getOutputPort(0x64);
		syRelativeToMoon = memory.getOutputPort(0x65);
	}

	@Override
	void writeInput(final Memory memory) {
		memory.setInputPort(2, 0);
		memory.setInputPort(3, 0);
	}
}
