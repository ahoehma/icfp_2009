package sak.orbit.ui;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import sak.orbit.controller.HohmannController;

public class HohmannOrbitAnimator extends AbstractOrbitAnimator<HohmannController> {

  /**
   * The application entry point
   * 
   * @param args
   *          the command line arguments
   */
  public static void main(final String[] args) {
    new HohmannOrbitAnimator().run();
  }

  public HohmannOrbitAnimator() {
    super(new HohmannController(1002), new int[]{1001, 1002, 1003, 1004});
  }

  @Override
  protected void drawAdditionalObjects(final int x0, final int y0, final GC gc) {
    if (showOrbit) {
      final double distance = controller.getTargetOrbit();
      gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
      final int distRadius = new Double(distance / getCurrentScale()).intValue();
      gc.drawOval(x0 - distRadius, y0 - distRadius, distRadius * 2, distRadius * 2);
    }
  }

  @Override
  protected Map<String, String> getInfoText() {
    final Map<String, String> result = super.getInfoText();
    result.put("Target orbit", nf.format(controller.getTargetOrbit()));
    return result;
  }
}