package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class DivInstruction extends DoubleInstruction implements Instruction {

	/**
	 * @param instructionPointer
	 * @param dataPointer1
	 * @param dataPointer2
	 */
	public DivInstruction(final int instructionPointer, final int dataPointer1,
			final int dataPointer2) {
		super(instructionPointer, dataPointer1, dataPointer2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.DoubleInstruction#perform(double, double)
	 */
	public void doPerform(final Memory memory) {
		double result = 0.0;
		if (memory.getData(dataPointer2) != 0.0) {
			result = memory.getData(dataPointer1)
					/ memory.getData(dataPointer2);
		}
		memory.setData(instructionPointer, result);
	}
}
