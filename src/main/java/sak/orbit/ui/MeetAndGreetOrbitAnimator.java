package sak.orbit.ui;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import sak.orbit.controller.MeetAndGreetController;
import sak.orbit.math.Point;
import sak.orbit.math.Utils;

public class MeetAndGreetOrbitAnimator extends AbstractOrbitAnimator<MeetAndGreetController> {

  /**
   * The application entry point
   * 
   * @param args
   *          the command line arguments
   */
  public static void main(final String[] args) {
    new MeetAndGreetOrbitAnimator().run();
  }

  double xTarget = 0;
  double yTarget = 0;
  double xOrgTarget = 0;
  double yOrgTarget = 0;

  /**
	 * 
	 */
  public MeetAndGreetOrbitAnimator() {
    super(new MeetAndGreetController(2001), new int[]{2001, 2002, 2003, 2004});
  }

  /**
   * @param theConfigurations
   * @param controller
   */
  public MeetAndGreetOrbitAnimator(final MeetAndGreetController controller, final int[] theConfigurations) {
    super(controller, theConfigurations);
  }

  /*
   * (non-Javadoc)
   * 
   * @see sak.orbit.ui.AbstractOrbitAnimator#drawAdditionalObjects(int, int,
   * org.eclipse.swt.graphics.GC)
   */
  @Override
  protected void drawAdditionalObjects(final int x0, final int y0, final GC gc) {

    final Point p1 = new Point(controller.getSxRelativeToEarth(), controller.getSyRelativeToEarth());
    final Point p2 = new Point(controller.getSxRelativeToTargetSatellite(), controller.getSyRelativeToTargetSatellite());
    final Point other = Utils.magGetOthersPosition(p1, p2);

    // the target satellite
    final int xAbsTarget = x0 + new Double(other.x / getCurrentScale()).intValue();
    final int yAbsTarget = y0 + new Double(other.y / getCurrentScale()).intValue();
    drawImage(gc, IMAGE_NAME_SPUTNIK, xAbsTarget, yAbsTarget, 0.5);

    if (showOrbit) {
      gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
      final double distance = Math.hypot(other.x, other.y);
      final int distRadius = new Double(distance / getCurrentScale()).intValue();
      gc.drawOval(x0 - distRadius, y0 - distRadius, distRadius * 2, distRadius * 2);
    }
  }

  @Override
  protected Map<String, String> getInfoText() {
    final Point p1 = new Point(controller.getSxRelativeToEarth(), controller.getSyRelativeToEarth());
    final Point p2 = new Point(controller.getSxRelativeToTargetSatellite(), controller.getSyRelativeToTargetSatellite());
    final Point other = Utils.magGetOthersPosition(p1, p2);
    final double distance = Math.hypot(other.x, other.y);
    final Map<String, String> result = super.getInfoText();
    result.put("Target x", String.format("%f", other.x));
    result.put("Target y", String.format("%f", other.y));
    result.put("Distance Target to earth", String.format("%s km", nf.format(distance)));
    return result;
  }
}