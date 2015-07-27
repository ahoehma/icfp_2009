package sak.orbit.instruction;

import sak.orbit.core.Memory;

public abstract class SingleInstruction extends AbstractInstruction {

	protected final int dataPointer;
	protected final int subOpCode;

	/**
	 * @param instructionPointer
	 * @param subOpCode
	 * @param dataPointer
	 */
	SingleInstruction(final int instructionPointer, final int subOpCode, final int dataPointer) {
		super(instructionPointer);
		this.subOpCode = subOpCode;
		this.dataPointer = dataPointer;
	}

	public void perform(final Memory memory) {
		// System.out.println(getInstructionName() + ": " + instructionPointer +
		// "/" + subOpCode + "/" + dataPointer);
		doPerform(memory);
		// System.out.println("  result: " +
		// memory.getData(instructionPointer));
	}

	@Override
	public String toString() {
		final String clName = getInstructionName();

		return String.format("%s m[%d] to m[%d]", clName, dataPointer, instructionPointer);
	}
}
