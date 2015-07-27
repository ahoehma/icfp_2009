package sak.orbit.loader;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Write ICFP 2009 results, all in little endian encoding.
 * 
 * @author Steffen
 * 
 */
public class Datawriter {

  private final DataOutputStream dos;
  private boolean closed;

  /**
   * @param filename
   * @param scenarioId
   */
  public Datawriter(final String filename, final int scenarioId) {
    try {
      dos = new DataOutputStream(new FileOutputStream(filename + "." + scenarioId + ".obf"));
      dos.write(0xBE);
      dos.write(0xBA);
      dos.write(0xFE);
      dos.write(0xCA);
      // teamid
      writeInt(121);
      // scenario
      writeInt(scenarioId);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param cycleCounter
   * @param results
   */
  public void addFrame(final int cycleCounter, final List<ResultFrame> results) {
    if (closed) {
      return;
    }
    try {
      writeInt(cycleCounter);
      writeInt(results.size());
      for (final ResultFrame rf : results) {
        writeInt(rf.addr);
        writeDouble(rf.val);
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void close(final int cycleCounter) {
    if (closed) {
      return;
    }
    try {
      addFrame(cycleCounter + 1, Collections.EMPTY_LIST);
      dos.close();
      closed = true;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeDouble(final double val) throws IOException {
    writeLong(Double.doubleToLongBits(val));
  }

  /**
   * Write binary little endian.
   * 
   * @param i
   * @throws IOException
   */
  private void writeInt(final int i) throws IOException {
    dos.write((byte) i);
    dos.write((byte) (i >>> 8));
    dos.write((byte) (i >>> 16));
    dos.write((byte) (i >>> 24));
  }

  /**
   * @param v
   * @throws IOException
   */
  private void writeLong(final long v) throws IOException {
    dos.write((byte) v);
    dos.write((byte) (v >>> 8));
    dos.write((byte) (v >>> 16));
    dos.write((byte) (v >>> 24));
    dos.write((byte) (v >>> 32));
    dos.write((byte) (v >>> 40));
    dos.write((byte) (v >>> 48));
    dos.write((byte) (v >>> 56));
  }

}
