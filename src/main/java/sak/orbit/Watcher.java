package sak.orbit;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import sak.orbit.core.Memory;
import sak.orbit.instruction.Instruction;
import sak.orbit.loader.MemoryLoader;
import sak.orbit.math.Point;
import sak.orbit.math.Speed;
import sak.orbit.math.Utils;

public class Watcher {
	private static final NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

	public static void main(final String[] args) throws FileNotFoundException, IOException {

		final Memory mem = new Memory();
		MemoryLoader.load(new FileInputStream(args[0]), mem);

		mem.setInputPort(0x3e80, 1001);

		final BufferedWriter bw = new BufferedWriter(new FileWriter("foo.csv"));

		double prevX = 0, prevY = 0, r1 = 0, r2 = 0;
		Speed v2 = null;
		int targetTime = 100000000;

		for (int i = 0; i < 50000; i++) {
			runSimulationStep(mem);
			final double x = mem.getOutputPort(2);
			final double y = mem.getOutputPort(3);
			r2 = mem.getOutputPort(4);
			final Speed speed = new Speed(prevX - x, prevY - y);
			switch (i) {
			case 3:
				r1 = (Utils.distance(new Point(x, y), new Point(0, 0)));
				System.out.println("Current radius: " + r1);
				System.out.println("Target  radius: " + r2);
				System.out.println("Current v = " + speed);
				final Speed v1 = Utils.homanSpeed1(speed, r1, r2);
				System.out.println("v1 = " + v1);
				final double holdTime = Utils.homanHoldTime(r1, r2);
				targetTime = (int) holdTime + i;
				System.out.println("idleTime = " + holdTime);
				mem.setInputPort(2, v1.vx);
				mem.setInputPort(3, v1.vy);
				break;
			case 4:
				mem.setInputPort(2, 0);
				mem.setInputPort(3, 0);
				// System.out.println(speed);
				break;
			default:
				prevX = x;
				prevY = y;
				break;
			}
			if (i == targetTime) {
				v2 = Utils.homanSpeed2(speed, r1, r2);
				System.out.println("v2 = " + v2);
				mem.setInputPort(2, v2.vx);
				mem.setInputPort(3, v2.vy);
			} else if (i == targetTime + 1) {
				mem.setInputPort(2, 0);
				mem.setInputPort(3, 0);
			}
			bw.write(nf.format(Utils.distance(new Point(x, y), new Point(0, 0))) + ";" + mem.getOutputPort(0) + "\n");
		}
		// if (i % 60 == 0) {
		// bw.write(nf.format(mem.getOutputPort(4)) + ";" +
		// nf.format(mem.getOutputPort(5)));
		// bw.write("\n");
		// }
		// if (i == 10000) {
		// mem.setInputPort(2, -800);
		// }
		// if (i == 10001) {
		// mem.setInputPort(2, 0);
		// }
		bw.close();
	}

	private static void runSimulationStep(final Memory mem) {
		for (final Instruction inst : mem.getInstructions()) {
			// System.out.println(inst);
			inst.perform(mem);
		}
	}

}
