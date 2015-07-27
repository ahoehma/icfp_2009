package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class SqrtInstruction extends SingleInstruction implements Instruction {

	SqrtInstruction(final int instructionPointer, final int subOpCode,
			final int dataPointer) {
		super(instructionPointer, subOpCode, dataPointer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.DoubleInstruction#perform(double, double)
	 */
	public void doPerform(final Memory memory) {
		memory.setData(instructionPointer, Math.sqrt(memory
				.getData(dataPointer)));
	}
}
