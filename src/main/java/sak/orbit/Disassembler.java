package sak.orbit;

import java.io.FileInputStream;
import java.io.IOException;

import sak.orbit.core.Memory;
import sak.orbit.instruction.Instruction;
import sak.orbit.loader.MemoryLoader;

public class Disassembler {

	public static void main(final String[] args) throws IOException {

		final Memory mem = new Memory();
		MemoryLoader.load(new FileInputStream(args[0]), mem);

		for (final Instruction inst : mem.getInstructions()) {
			System.out.println(inst);
		}
	}

}
