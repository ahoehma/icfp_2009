package sak.orbit.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

import sak.orbit.controller.ClearSkiesController;
import sak.orbit.math.Point;
import sak.orbit.math.Utils;

/**
 * @author hoehmann
 */
public class ClearSkiesOrbitAnimator extends AbstractOrbitAnimator<ClearSkiesController> {

  static final String IMAGE_NAME_FUELSTATION = "station.png";
  static final String IMAGE_NAME_MOON = "moon.png";

  /**
   * In meter.
   */
  private static final int MOON_RADIUS = 1738000;

  /**
   * The application entry point
   * 
   * @param args
   *          the command line arguments
   */
  public static void main(final String[] args) {
    new ClearSkiesOrbitAnimator().run();
  }

  /**
	 * 
	 */
  public ClearSkiesOrbitAnimator() {
    super(new ClearSkiesController(4001), new int[]{4001, 4002, 4003, 4004});
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void drawAdditionalObjects(final int x0, final int y0, final GC gc) {
    drawMoon(x0, y0, gc);
    drawTargetSatellites(x0, y0, gc);
    drawFuelStation(x0, y0, gc);
  }

  private void drawFuelStation(final int x0, final int y0, final GC gc) {
    final Point p1 = new Point(controller.getSxRelativeToEarth(), controller.getSyRelativeToEarth());
    final Point p2 = new Point(controller.getSxRelativeToFuelingStation(), controller.getSyRelativeToFuelingStation());
    final Point station = Utils.magGetOthersPosition(p1, p2);
    final int x = x0 + new Double(station.x / getCurrentScale()).intValue();
    final int y = y0 + new Double(station.y / getCurrentScale()).intValue();
    drawImage(gc, IMAGE_NAME_FUELSTATION, x, y, 0.2);
  }

  private void drawMoon(final int x0, final int y0, final GC gc) {
    final Point p1 = new Point(controller.getSxRelativeToEarth(), controller.getSyRelativeToEarth());
    final Point p2 = new Point(controller.getSxRelativeToMoon(), controller.getSyRelativeToMoon());
    final Point moon = Utils.magGetOthersPosition(p1, p2);
    final int x = x0 + new Double(moon.x / getCurrentScale()).intValue();
    final int y = y0 + new Double(moon.y / getCurrentScale()).intValue();
    // 400 px == 2 * 1738000 m
    final double radius_in_pixel = MOON_RADIUS * 2 / getCurrentScale();
    final double scale = radius_in_pixel / 400;
    drawImage(gc, IMAGE_NAME_MOON, x, y, scale);
    final double distance = new Double(Math.sqrt(moon.x * moon.x + moon.y * moon.y));
    gc.drawText(String.format("Distance moon earth : %s", nf.format(distance)), 30, 10);
  }

  private void drawTargetSatellites(final int x0, final int y0, final GC gc) {
    final Point p1 = new Point(controller.getSxRelativeToEarth(), controller.getSyRelativeToEarth());
    for (int i = 0; i < 11; i++) {
      final Point p2 = new Point(controller.getSxRelativeToTarget(i), controller.getSyRelativeToTarget(i));
      final Point targetSatellite = Utils.magGetOthersPosition(p1, p2);
      final int x = x0 + new Double(targetSatellite.x / getCurrentScale()).intValue();
      final int y = y0 + new Double(targetSatellite.y / getCurrentScale()).intValue();
      drawImage(gc, IMAGE_NAME_SPUTNIK, x, y, 0.5);
      final double distance = Math.hypot(targetSatellite.x, targetSatellite.y);
      if (showOrbit) {
        gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
        final int distRadius = new Double(distance / getCurrentScale()).intValue();
        gc.drawOval(x0 - distRadius, y0 - distRadius, distRadius * 2, distRadius * 2);
      }
      gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
      gc.drawText(String.format("%s", i + 1), x, y);
    }
  }
  /**
   * {@inheritDoc}
   */
  @Override
  protected void loadImages() {
    super.loadImages();
    try {
      loadImage(IMAGE_NAME_MOON);
      loadImage(IMAGE_NAME_FUELSTATION);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}