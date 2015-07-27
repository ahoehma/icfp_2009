/**
 * 
 */
package sak.orbit.instruction;

import sak.orbit.Frame;
import sak.orbit.Frame.InstructionType2;

public enum InstructionType {

	ADD(InstructionType2.DTYPE, 1), SUB(InstructionType2.DTYPE, 2), MULT(InstructionType2.DTYPE, 3), DIV(InstructionType2.DTYPE, 4), OUTPUT(InstructionType2.DTYPE, 5), PHI(
			InstructionType2.DTYPE, 6),

	NOOP(InstructionType2.STYPE, 0), CMPZ(InstructionType2.STYPE, 1), SQRT(InstructionType2.STYPE, 2), COPY(InstructionType2.STYPE, 3), INPUT(InstructionType2.STYPE, 4);

	/**
	 * @param instruction
	 *            32 bit instruction
	 * @return matching operation
	 */
	public static InstructionType findByOpcode(final int instruction) {
		final int opcode = (instruction >>> 28);
		final int subopcode = instruction >>> 24 & 0x0F;

		for (final InstructionType op : InstructionType.values()) {
			if (op.opcode == opcode) {
				if (opcode == 0) {
					if (op.subopcode == subopcode)
						return op;
				} else
					return op;
			}
		}
		throw new RuntimeException("Probably parser error or unknown Opcode!");
	}

	private final InstructionType2 instructionType;

	private int opcode;

	private int subopcode;

	/**
	 * Constructor.
	 * 
	 * @param type
	 * @param opcode
	 */
	InstructionType(final InstructionType2 type, final int opcode) {
		this.instructionType = type;
		this.opcode = opcode;
		if (instructionType == InstructionType2.STYPE) {
			this.opcode = 0;
			this.subopcode = opcode;
		}
	}

	@Override
	public String toString() {
		return this.name();
	}
}