package sak.orbit.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sak.orbit.loader.Datawriter;
import sak.orbit.loader.ResultFrame;

import sak.orbit.instruction.AbstractInstruction;
import sak.orbit.instruction.Instruction;

/**
 * Simple implementation for our vm memory.
 * 
 * @author andreas
 */
public class Memory {

	/**
	 * 14 bit memory pointer.
	 */
	final int MAX_POINTER = 0x4000;

	/**
	 * data memory.
	 */
	final double datas[] = new double[MAX_POINTER];

	/**
	 * instruction memory.
	 */
	final Instruction instructions[] = new Instruction[MAX_POINTER];

	/**
	 * Internal counter for Instructions.
	 */
	int currentNumberOfInstructions = 0;

	/**
	 * Internal counter for Data.
	 * 
	 * @see #addData(int)
	 */
	private int currentNumberOfDatas = 0;

	/**
	 * One bit status register;
	 */
	boolean status;

	/**
	 * 
	 */
	private final double[] outputPort = new double[MAX_POINTER];

	/**
	 * 
	 */
	private final double[] inputPort = new double[MAX_POINTER];

	private Datawriter dw;

	private final Set<Integer> changedPorts = new HashSet<Integer>();

	private boolean iSawAScore = false;

	/**
	 * 
	 */
	public Memory() {
		// nop
	}

	/**
	 * Add one data and increase the internal data-counter. Use for initialize
	 * the data-memory.
	 * 
	 * @param data
	 */
	public void addData(final double data) {
		if (currentNumberOfDatas == MAX_POINTER) {
			throw new IllegalStateException("Max number of data reached!");
		}
		this.datas[currentNumberOfDatas++] = data;
	}

	/**
	 * Add {@link Instruction} and increase the internal instruction-counter.
	 * Use for initialize the instructions-memory.
	 * 
	 * @param instruction
	 */
	public void addInstruction(final Instruction instruction) {
		if (currentNumberOfInstructions == MAX_POINTER) {
			throw new IllegalStateException("Max number of instructions reached!");
		}
		assert (((AbstractInstruction) instruction).instructionPointer == currentNumberOfInstructions);

		this.instructions[currentNumberOfInstructions++] = instruction;
	}

	/**
	 * @param pointer
	 * @return data from position pointer
	 */
	public double getData(final int pointer) {
		return datas[pointer];
	}

	/**
	 * @param dataPointer
	 * @return
	 */
	public double getInputPort(final int dataPointer) {
		return inputPort[dataPointer];
	}

	/**
	 * @param pointer
	 * @return
	 */
	public Instruction getInstruction(final int pointer) {
		return instructions[pointer];
	}

	/**
	 * @return
	 */
	public List<Instruction> getInstructions() {
		final Instruction[] result = new Instruction[currentNumberOfInstructions];
		System.arraycopy(instructions, 0, result, 0, currentNumberOfInstructions);

		return Arrays.asList(result);
	}

	/**
	 * @param dataPointer
	 * @return
	 */
	public double getOutputPort(final int dataPointer) {
		return outputPort[dataPointer];
	}

	/**
	 * @return
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * About to start next simulation step, write log of last one.
	 */
	public void informOfNextRun(final int cycleCounter) {
		if (iSawAScore) {
			return;
		}

		if (!changedPorts.isEmpty()) {
			final List<ResultFrame> results = new ArrayList<ResultFrame>();
			for (final int addr : changedPorts) {
				results.add(new ResultFrame(addr, this.inputPort[addr]));
			}
			dw.addFrame(cycleCounter, results);
			this.changedPorts.clear();
		}
		if (getOutputPort(0) > 0) {
			iSawAScore = true;
			dw.close(cycleCounter);
		}
	}

	private void remember(final int dataPointer, final double data) {
		if (data != inputPort[dataPointer]) {
			this.changedPorts.add(dataPointer);
		}
	}

	/**
	 * @param pointer
	 * @param data
	 *            for position pointer
	 */
	public void setData(final int pointer, final double data) {
		this.datas[pointer] = data;
	}

	/**
	 * @param datawriter
	 */
	public void setDataWriter(final Datawriter datawriter) {
		this.dw = datawriter;
	}

	/**
	 * @param dataPointer
	 * @param data
	 */
	public void setInputPort(final int dataPointer, final double data) {
		remember(dataPointer, data);
		inputPort[dataPointer] = data;
	}

	/**
	 * @param dataPointer
	 * @param data
	 */
	public void setOutputPort(final int dataPointer, final double data) {
		outputPort[dataPointer] = data;
	}

	public void setStatus(final boolean status) {
		this.status = status;
	}
}
