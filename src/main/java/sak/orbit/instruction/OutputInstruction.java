package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class OutputInstruction extends DoubleInstruction implements Instruction {

	/**
	 * @param instructionPointer
	 * @param dataPointer1
	 * @param dataPointer2
	 */
	public OutputInstruction(final int instructionPointer,
			final int dataPointer1, final int dataPointer2) {
		super(instructionPointer, dataPointer1, dataPointer2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.Instruction#perform(sak.orbit.core.Memory)
	 */
	public void doPerform(final Memory memory) {
		memory.setOutputPort(dataPointer1, memory.getData(dataPointer2));
	}
}
