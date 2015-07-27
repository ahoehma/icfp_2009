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

public class SpeedTest {
        private static final NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

        public static void main(final String[] args) throws FileNotFoundException, IOException {

                final Memory mem = new Memory();
                MemoryLoader.load(new FileInputStream(args[0]), mem);

                mem.setInputPort(0x3e80, 1001);

                final BufferedWriter bw = new BufferedWriter(new FileWriter("foo.csv"));

                Point pCurr;
                Point pPrev=null;
                for (int i = 0; i < 100000; i++) {
                        runSimulationStep(mem);
                        
                        double px=mem.getOutputPort(2);
                        double py=mem.getOutputPort(3);
                        pCurr=new Point(px,py);
                        
                        if (i % 60 == 0) {
                            if (pPrev!=null) {
                                double r = Utils.distance(pPrev, new Point(0,0));
                                Speed v=Utils.calculateSpeed(pPrev, pCurr, r, new Speed(0,0));
                                bw.write(nf.format(v.vx) + ";" + nf.format(v.vy));
                                bw.write("\n");
                            }
                        }
                        
                        pPrev=pCurr;
                }
                bw.close();
        }

        private static void runSimulationStep(final Memory mem) {
                for (final Instruction inst : mem.getInstructions()) {
                        // System.out.println(inst);
                        inst.perform(mem);
                }
        }

}
