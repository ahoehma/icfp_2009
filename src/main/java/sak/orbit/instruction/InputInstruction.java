package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class InputInstruction extends SingleInstruction implements Instruction {

	/**
	 * @param instructionPointer
	 * @param subOpCode
	 * @param dataPointer
	 */
	public InputInstruction(final int instructionPointer, final int subOpCode,
			final int dataPointer) {
		super(instructionPointer, subOpCode, dataPointer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.Instruction#perform(sak.orbit.core.Memory)
	 */
	public void doPerform(final Memory memory) {
		memory.setData(instructionPointer, memory.getInputPort(dataPointer));
	}
}
