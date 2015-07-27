package sak.orbit.instruction;


/**
 * @author andreas
 */
public class InstructionFactory {

	private static int getDataPointer1(final int input) {
		return (input >>> 14) & 0x3FFF;
	}

	private static int getDataPointer2(final int input) {
		return input & 0x3FFF;
	}

	private static int getImm(final int input) {
		return (input >>> 21) & 0x7;
	}

	/**
	 * Create {@link Instruction}s for a given input-value.
	 * 
	 * @param instructionPointer
	 * @param input
	 *            a 32bit integer read from binary-stream
	 * @return
	 */
	public static Instruction getInstruction(final int instructionPointer, final int input) {
		final InstructionType opCode = InstructionType.findByOpcode(input);
		final int dataPointer1 = getDataPointer1(input);
		final int dataPointer2 = getDataPointer2(input);
		final int imm = getImm(input);
		switch (opCode) {

		//
		// Double Instructions
		//
		case ADD:
			return new AddInstruction(instructionPointer, dataPointer1, dataPointer2);
		case SUB:
			return new SubInstruction(instructionPointer, dataPointer1, dataPointer2);
		case MULT:
			return new MulInstruction(instructionPointer, dataPointer1, dataPointer2);
		case DIV:
			return new DivInstruction(instructionPointer, dataPointer1, dataPointer2);
		case PHI:
			return new PhiInstruction(instructionPointer, dataPointer1, dataPointer2);
		case OUTPUT:
			return new OutputInstruction(instructionPointer, dataPointer1, dataPointer2);

			//
			// Single Instructions
			//
		case NOOP:
			return new NoopInstruction(instructionPointer, imm, dataPointer2);
		case SQRT:
			return new SqrtInstruction(instructionPointer, imm, dataPointer2);
		case COPY:
			return new CopyInstruction(instructionPointer, imm, dataPointer2);
		case CMPZ:
			return new CmpzInstruction(instructionPointer, imm, dataPointer2);
		case INPUT:
			return new InputInstruction(instructionPointer, imm, dataPointer2);

		default:
			throw new RuntimeException("invalid instruction: " + Integer.toString(input, 2));
		}
	}
}
