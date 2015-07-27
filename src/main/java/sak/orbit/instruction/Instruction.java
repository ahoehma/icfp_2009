package sak.orbit.instruction;

import sak.orbit.core.Memory;

public interface Instruction {

	public static enum TYPE {
		ADD, SUB, MUL, DIV, OUTPUT, PHI, NOOP, CMPZ, SQRT, COPY, INPUT,
	}

	/**
	 * Execute instruction and return result.
	 */
	void perform(Memory memory);

}
