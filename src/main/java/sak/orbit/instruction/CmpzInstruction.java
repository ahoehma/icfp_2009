package sak.orbit.instruction;

import sak.orbit.core.Memory;

public class CmpzInstruction extends SingleInstruction implements Instruction {

	/**
	 * @param instructionPointer
	 * @param subOpCode
	 * @param dataPointer
	 */
	CmpzInstruction(final int instructionPointer, final int subOpCode, final int dataPointer) {
		super(instructionPointer, subOpCode, dataPointer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sak.orbit.core.DoubleInstruction#perform(double, double)
	 */
	@Override
	public void doPerform(final Memory memory) {
		final double EPSILON = 0.0000001;
		boolean newStatus;
		switch (subOpCode) {
		case 0:
			newStatus = memory.getData(dataPointer) < 0;
			break;
		case 1:
			newStatus = memory.getData(dataPointer) <= 0;
			break;
		case 2:
			newStatus = Math.abs(memory.getData(dataPointer)) <= EPSILON;
			break;
		case 3:
			newStatus = memory.getData(dataPointer) >= 0;
			break;
		case 4:
			newStatus = memory.getData(dataPointer) > 0;
			break;

		default:
			throw new RuntimeException("invalid comparison type for CMPZ: " + Integer.toString(subOpCode, 2));
		}
		// System.out.println("  setting status to "+newStatus);
		memory.setStatus(newStatus);
	}

	private String getCmpDirectionString() {
		switch (subOpCode) {
		case 0:
			return "<";
		case 1:
			return "<=";
		case 2:
			return "==";
		case 3:
			return ">=";
		case 4:
			return ">";

		default:
			throw new RuntimeException("invalid cmpz imm: " + subOpCode);
		}
	}

	@Override
	public String toString() {
		final String clName = getInstructionName();
		final String cmp = getCmpDirectionString();

		return String.format("%s m[%d]%s0?", clName, dataPointer, cmp);
	}
}
