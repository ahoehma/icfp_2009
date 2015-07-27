package sak.orbit;

import sak.orbit.instruction.InstructionType;

public class Frame {
	public enum InstructionType2 {
		STYPE, DTYPE
	}

	public double data;
	public int instr;

	public InstructionType getInstruction() {
		return InstructionType.findByOpcode(instr);
	}
}
