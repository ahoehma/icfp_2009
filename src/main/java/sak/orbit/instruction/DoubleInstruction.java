package sak.orbit.instruction;

import sak.orbit.core.Memory;

public abstract class DoubleInstruction extends AbstractInstruction {

	protected final int dataPointer1;
	protected final int dataPointer2;

	/**
	 * @param instructionPointer
	 * @param dataPointer1
	 * @param dataPointer2
	 */
	public DoubleInstruction(final int instructionPointer, final int dataPointer1, final int dataPointer2) {
		super(instructionPointer);
		this.dataPointer1 = dataPointer1;
		this.dataPointer2 = dataPointer2;
	}

	public void perform(final Memory memory) {
		// System.out.println(getInstructionName() + ": " + instructionPointer +
		// "/" + dataPointer1 + "/" + dataPointer2);
		doPerform(memory);
		// System.out.println("  result: " +
		// memory.getData(instructionPointer));
	}

	@Override
	public String toString() {

		return String.format("%s m[%d] m[%d]", getInstructionName(), dataPointer1, dataPointer2);
	}
}
