package sak.orbit.loader;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import sak.orbit.Frame;
import sak.orbit.core.Memory;
import sak.orbit.instruction.InstructionFactory;

/**
 * @author andreas
 */
public class MemoryLoader {

	/**
	 * Fill the given {@link Memory} from given {@link FileInputStream}.
	 * 
	 * @param fileInputStream
	 *            never <code>null</code>
	 * @param mem
	 *            never <code>null</code>
	 * @throws IOException
	 */
	public static void load(final FileInputStream fileInputStream, final Memory mem) throws IOException {

		final DataInputStream dis = new DataInputStream(fileInputStream);
		int frameCount = 0;
		while (true) {
			final Frame f = new Frame();
			if (frameCount % 2 == 0) {
				f.data = readDouble(dis);
				f.instr = readInt(dis);
			} else {
				f.instr = readInt(dis);
				f.data = readDouble(dis);
			}

			mem.addData(f.data);
			mem.addInstruction(InstructionFactory.getInstruction(frameCount, f.instr));

			frameCount++;
			if (dis.available() == 0) {
				break;
			}
		}
	}

	private static double readDouble(final DataInputStream dis) throws IOException {
		return Double.longBitsToDouble(readLong(dis));
	}

	private static int readInt(final InputStream in) throws IOException {
		final int ch1 = in.read();
		final int ch2 = in.read();
		final int ch3 = in.read();
		final int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
	}

	private static long readLong(final DataInputStream dis) throws IOException {
		final byte[] work = new byte[8];
		dis.readFully(work, 0, 8);
		return (long) (work[7]) << 56 | (long) (work[6] & 0xff) << 48 | (long) (work[5] & 0xff) << 40 | (long) (work[4] & 0xff) << 32 | (long) (work[3] & 0xff) << 24
				| (long) (work[2] & 0xff) << 16 | (long) (work[1] & 0xff) << 8 | (work[0] & 0xff);
	}
}
