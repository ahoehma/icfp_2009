/**
 * 
 */
package sak.orbit.instruction;

import sak.orbit.core.Memory;

/**
 * @author andreas
 * 
 */
public abstract class AbstractInstruction implements Instruction {

	public final int instructionPointer;

	public AbstractInstruction(final int instructionPointer) {
		this.instructionPointer = instructionPointer;
	}

	protected String getInstructionName() {
		final String clName = this.getClass().getSimpleName();
		return clName.substring(0, clName.indexOf("Instruction"));
	}
        
        public abstract void doPerform(Memory memory);
        
}
