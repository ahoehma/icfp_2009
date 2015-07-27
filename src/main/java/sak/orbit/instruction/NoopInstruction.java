package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class NoopInstruction extends SingleInstruction implements Instruction {

	/**
	 * @param instructionPointer
	 * @param subOpCode
	 * @param dataPointer
	 */
	NoopInstruction(final int instructionPointer, final int subOpCode, final int dataPointer) {
		super(instructionPointer, subOpCode, dataPointer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.DoubleInstruction#perform(double, double)
	 */
	public void doPerform(final Memory memory) {
		// nuescht zu tun!
	}

	@Override
	public String toString() {
		return getInstructionName();
	}
}
