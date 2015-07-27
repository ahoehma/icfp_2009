package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class PhiInstruction extends DoubleInstruction implements Instruction {

	/**
	 * @param instructionPointer
	 * @param dataPointer1
	 * @param dataPointer2
	 */
	public PhiInstruction(final int instructionPointer, final int dataPointer1,
			final int dataPointer2) {
		super(instructionPointer, dataPointer1, dataPointer2);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.DoubleInstruction#perform(double, double)
	 */
	public void doPerform(final Memory memory) {
		// XXX unverstaendlicher code!
		memory.setData(instructionPointer, memory
				.getData(memory.getStatus() ? dataPointer1 : dataPointer2));
	}
}
