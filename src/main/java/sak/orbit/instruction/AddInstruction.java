package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class AddInstruction extends DoubleInstruction {

	/**
	 * @param instructionPointer
	 * @param dataPointer1
	 * @param dataPointer2
	 */
	public AddInstruction(final int instructionPointer, final int dataPointer1, final int dataPointer2) {
		super(instructionPointer, dataPointer1, dataPointer2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.DoubleInstruction#perform(double, double)
	 */
	public void doPerform(final Memory memory) {
		memory.setData(instructionPointer, memory.getData(dataPointer1) + memory.getData(dataPointer2));
	}
}
