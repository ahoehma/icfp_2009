package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class CopyInstruction extends SingleInstruction implements Instruction {

	/**
	 * @param instructionPointer
	 * @param subOpCode
	 * @param dataPointer
	 */
	CopyInstruction(final int instructionPointer, final int subOpCode,
			final int dataPointer) {
		super(instructionPointer, subOpCode, dataPointer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.DoubleInstruction#perform(double, double)
	 */
	public void doPerform(final Memory memory) {
		memory.setData(instructionPointer, memory.getData(dataPointer));
	}
}
