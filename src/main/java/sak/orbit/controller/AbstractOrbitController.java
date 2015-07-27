package sak.orbit.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import sak.orbit.core.Memory;
import sak.orbit.instruction.Instruction;
import sak.orbit.loader.Datawriter;
import sak.orbit.loader.MemoryLoader;
import sak.orbit.math.Speed;
import sak.orbit.math.Utils;

/**
 * Controller for a given binary file and a configuration.
 * 
 * @author andreas
 */
public abstract class AbstractOrbitController {

  private static final int CONFIGURATION_INPUT_POINTER = 0x3e80;
  private Memory memory;
  protected int cyclesCounter;
  protected double manualSpeed;
  protected double score;
  protected double fuelRemaining;
  protected double sxRelativeToEarth;
  protected double syRelativeToEarth;
  protected double prevSx;
  protected double prevSy;
  protected Speed currentSpeed;
  private final File aScenarioFile;
  private int configuration;
  private Datawriter datawriter;

  /**
   * @param aScenarioFile
   * @param configuration
   */
  public AbstractOrbitController(final File aScenarioFile, final int configuration) {
    this.aScenarioFile = aScenarioFile;
    this.configuration = configuration;
    initController();
  }

  /**
   * @return current configuration
   */
  public int getConfiguration() {
    return configuration;
  }

  /**
   * @return
   */
  public Speed getCurrentSpeed() {
    return currentSpeed;
  }

  public int getCycleCounter() {
    return cyclesCounter;
  }

  /**
   * @return
   */
  public double getFuelRemaining() {
    return fuelRemaining;
  }

  /**
   * @return
   */
  public String getScenario() {
    return aScenarioFile.getName();
  }

  /**
   * @return
   */
  public double getScore() {
    return score;
  }

  /**
   * @return
   */
  public double getSxRelativeToEarth() {
    return sxRelativeToEarth;
  }

  /**
   * @return
   */
  public double getSyRelativeToEarth() {
    return syRelativeToEarth;
  }

  /**
	 * 
	 */
  public void initController() {
    memory = new Memory();
    try {
      MemoryLoader.load(new FileInputStream(aScenarioFile), memory);
    } catch (final FileNotFoundException e) {
      throw new RuntimeException("Could not load file", e);
    } catch (final IOException e) {
      throw new RuntimeException("Could not load file", e);
    }
    memory.setInputPort(CONFIGURATION_INPUT_POINTER, configuration);
    if (datawriter != null) {
      datawriter.close(0);
    }
    datawriter = new Datawriter(aScenarioFile.getAbsolutePath(), configuration);
    memory.setDataWriter(datawriter);
    cyclesCounter = 0;
  }

  /**
	 * 
	 */
  private void manualSpeed() {
    if (manualSpeed != 0) {
      setInputSpeed(manualSpeed);
      manualSpeed = 0;
    }
  }

  /**
	 * 
	 */
  public void nextRun() {
    memory.informOfNextRun(cyclesCounter);
    writeInput(memory);
    manualSpeed();
    for (final Instruction inst : memory.getInstructions()) {
      inst.perform(memory);
    }
    score = memory.getOutputPort(0x0);
    fuelRemaining = memory.getOutputPort(0x1);
    prevSx = sxRelativeToEarth;
    prevSy = syRelativeToEarth;
    sxRelativeToEarth = memory.getOutputPort(0x2);
    syRelativeToEarth = memory.getOutputPort(0x3);
    currentSpeed = new Speed(prevSx - sxRelativeToEarth, prevSy - syRelativeToEarth);
    readOutput(memory);
    cyclesCounter++;
  }

  /**
   * Read all other values from the output. This base class already read:
   * 
   * <ol>
   * <li>score</li>
   * <li>fuel remaining</li>
   * <li>x pos relative to earth</li>
   * <li>y pos relative to earth</li>
   * </ol>
   * 
   * 
   * @param memory
   */
  abstract void readOutput(Memory memory);

  /**
   * @param newSpeed
   */
  private void setInputSpeed(final double newSpeed) {
    final Speed newVector = Utils.manualSpeed1(currentSpeed, newSpeed);
    memory.setInputPort(2, newVector.vx);
    memory.setInputPort(3, newVector.vy);
  }

  /**
   * @param manualSpeed
   */
  public void setManualSpeed(final double manualSpeed) {
    this.manualSpeed = manualSpeed;
  }

  /**
   * @param aconfiguration
   */
  public void switchConfiguration(final int aconfiguration) {
    configuration = aconfiguration;
    initController();
  }

  /**
   * @param memory
   */
  abstract void writeInput(Memory memory);
}
