package sak.orbit.ui;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import sak.orbit.controller.AbstractOrbitController;
import sak.orbit.math.Speed;

/**
 * @author hoehmann
 * @param <CONTROLLER>
 */
public abstract class AbstractOrbitAnimator<CONTROLLER extends AbstractOrbitController> {

	public class DoublePoint {
		double x, y;

		public DoublePoint(final double theX, final double theY) {
			x = theX;
			y = theY;
		}
	}

	static final String IMAGE_NAME_SPUTNIK = "sputnik.jpg";
	static final String IMAGE_NAME_EARTH = "earth.png";
	static final String IMAGE_NAME_SATELLITE = "satellite.jpg";
	private final Map<String, Image> images = new HashMap<String, Image>();

	/**
	 * All real coordinate (meter) must scaled for ui (pixel).
	 * 
	 * 1 m == 100000 pixel
	 */
	protected static final int SCALE = 100000;

	static final NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

	/**
	 * The time interval in milliseconds.
	 */
	static final int TIMER_INTERVAL = 25;

	/**
	 * Max number of stored points in the satellites trajectory.
	 */
	static final int MAX_SCHWEIF = 1500;

	/**
	 * In meter.
	 */
	private static final int EARTH_RADIUS = 6300000;

	/**
	 * SCALE / MAX_SCALE < CURRENT_SCALE < SCALE * MAX_SCALE
	 */
	private static final int MAX_SCALE = 32;
	private double currentScale = SCALE;

	private Canvas canvas;
	private Canvas infoTable;
	protected Display display;
	private ScrolledComposite primaryScrollableComposite;

	/**
	 * Last N points for satellite orbit.
	 */
	private final LinkedList<DoublePoint> points = new LinkedList<DoublePoint>();
	private int skipPoints = 5;

	/**
	 * The x location of the "satellite", read from controller.
	 */
	protected double xOrg = 0;

	/**
	 * The y location of the "satellite", read from controller.
	 */
	protected double yOrg = 0;

	/**
	 * Controller.
	 */
	protected CONTROLLER controller;
	private int[] configurations = new int[] {};

	/**
	 * If <code>true</code> the ui draw a circle for each satellite-orbit.
	 */
	protected boolean showOrbit;

	/**
	 * If <code>true</code> the ui will center the satellite and follow him.
	 */
	protected boolean followSatellite;

	/** 
   * 
   */
	private boolean pauseController;

	/**
	 * @param controller
	 */
	public AbstractOrbitAnimator(final CONTROLLER controller) {
		this.controller = controller;
	}

	/**
	 * @param theClearSkiesController
	 * @param theConfigurations
	 */
	public AbstractOrbitAnimator(final CONTROLLER controller,
			final int[] theConfigurations) {
		this.controller = controller;
		configurations = theConfigurations;
	}

	/**
	 * @param aCanvas
	 * 
	 */
	private void addKeyListener(final Canvas aCanvas) {
		aCanvas.addKeyListener(new KeyListener() {
			public void keyPressed(final KeyEvent arg0) {
				switch (arg0.character) {
				case 's':
					showOrbit = !showOrbit;
					break;
				case 'c':
					followSatellite = false;
					centerEarth();
					break;
				case 'f':
					followSatellite = !followSatellite;
					break;
				case 'p':
					pauseController = !pauseController;
					break;
				case '+':
					controller.setManualSpeed(50);
					break;
				case '-':
					controller.setManualSpeed(-50);
					break;
				case '0':
					scaleTo(SCALE);
					break;
				case '1':
					scaleTo(currentScale / 2);
					break;
				case '2':
					scaleTo(currentScale * 2);
					break;
				}
				if (arg0.keyCode == 27) {
					controller.initController();
					points.clear();
				}
				if (arg0.keyCode == 16777226) {
					controller.switchConfiguration(configurations[0]);
					points.clear();
				}
				if (arg0.keyCode == 16777227) {
					controller.switchConfiguration(configurations[1]);
					points.clear();
				}
				if (arg0.keyCode == 16777228) {
					controller.switchConfiguration(configurations[2]);
					points.clear();
				}
				if (arg0.keyCode == 16777229) {
					controller.switchConfiguration(configurations[3]);
					points.clear();
				}
				handleKeyPress(arg0);
			}

			public void keyReleased(final KeyEvent arg0) {
			}
		});
	}

	/**
   * 
   */
	void animate() {
		if (!pauseController) {
			// iterationsPerFrame ...
			for (int i = 0; i < 10; i++) {
				controller.nextRun();
			}
			// update ui data
			xOrg = controller.getSxRelativeToEarth();
			yOrg = controller.getSyRelativeToEarth();
			// remember the last N points
			if (skipPoints-- <= 0) {
				points.add(new DoublePoint(xOrg, yOrg));
				if (points.size() > MAX_SCHWEIF) {
					points.removeFirst();
				}
				skipPoints = 5;
			}
		}
		if (followSatellite) {
			centerSatellite();
		}
		canvas.redraw();
		if (infoTable != null) {
			infoTable.redraw();
		}
	}

	protected void centerEarth() {
		final ScrollBar verticalBar = this.primaryScrollableComposite
				.getVerticalBar();
		final ScrollBar horizontalBar = this.primaryScrollableComposite
				.getHorizontalBar();
		final int y = (verticalBar.getMaximum() - verticalBar.getThumb()) / 2;
		final int x = (horizontalBar.getMaximum() - horizontalBar.getThumb()) / 2;
		verticalBar.setSelection(y);
		horizontalBar.setSelection(x);
		primaryScrollableComposite.setOrigin(x, y);
	}

	protected void centerSatellite() {
		final ScrollBar verticalBar = this.primaryScrollableComposite
				.getVerticalBar();
		final ScrollBar horizontalBar = this.primaryScrollableComposite
				.getHorizontalBar();
		final Rectangle bounds = canvas.getBounds();
		final Rectangle clientArea = primaryScrollableComposite.getClientArea();
		final int x0 = bounds.width / 2;
		final int y0 = bounds.height / 2;
		final int xAbs = x0 + new Double(xOrg / getCurrentScale()).intValue()
				- (clientArea.width) / 2;
		final int yAbs = y0 + new Double(yOrg / getCurrentScale()).intValue()
				- (clientArea.height) / 2;
		horizontalBar.setSelection(xAbs);
		verticalBar.setSelection(yAbs);
		primaryScrollableComposite.setOrigin(xAbs, yAbs);
	}

	private void createHelp(final Composite comp) {
		final Composite area = new Composite(comp, SWT.NORMAL);
		area.setLayout(new RowLayout(SWT.VERTICAL));
		new Label(area, SWT.NORMAL).setText("'F1' : scenario configuration 1");
		new Label(area, SWT.NORMAL).setText("'F2' : scenario configuration 2");
		new Label(area, SWT.NORMAL).setText("'F3' : scenario configuration 3");
		new Label(area, SWT.NORMAL).setText("'F4' : scenario configuration 4");
		new Label(area, SWT.NORMAL).setText("'+' : impulse +50");
		new Label(area, SWT.NORMAL).setText("'-' : impulse -50");
		new Label(area, SWT.NORMAL).setText("'1' : zoom in");
		new Label(area, SWT.NORMAL).setText("'2' : zoom out");
		new Label(area, SWT.NORMAL).setText("'s' : show orbit");
		new Label(area, SWT.NORMAL).setText("'c' : center earth");
		new Label(area, SWT.NORMAL).setText("'f' : auto focus the satellite");
		new Label(area, SWT.NORMAL).setText("'p' : pause");
	}

	/**
	 * Creates the info window's contents. Here we show simulation-informations.
	 * 
	 * @param theShell
	 */
	private void createInfoContents(final Shell theShell) {
		final Composite comp = new Composite(theShell, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING,
				false, true));
		comp.setLayout(new FillLayout(SWT.VERTICAL));
		final ScrolledComposite composite = new ScrolledComposite(comp,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		infoTable = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		infoTable.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent event) {
				final GC gc = event.gc;
				final Rectangle bounds = infoTable.getBounds();
				// clear background
				gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				gc.fillRectangle(0, 0, bounds.width, bounds.height);
				drawMessages(gc);
				drawAdditionalMessages(gc);
			}
		});
		composite.setContent(infoTable);
		composite.setExpandHorizontal(true);
		composite.setExpandVertical(true);
		composite.setMinSize(400, 600);
		createHelp(comp);
	}

	/**
	 * @param composite
	 * @return
	 */
	private Canvas createPrimaryCanvas(final Composite composite) {
		final Canvas result = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		result.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent event) {
				final GC gc = event.gc;
				final Rectangle bounds = result.getBounds();
				final int x0 = bounds.width / 2;
				final int y0 = bounds.height / 2;
				gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				gc.fillRectangle(0, 0, bounds.width, bounds.height);
				drawGrid(gc, bounds, x0, y0);
				drawEarth(gc, x0, y0);
				drawSatellite(gc, x0, y0);
				drawAdditionalObjects(x0, y0, gc);
			}
		});
		addKeyListener(result);
		return result;
	}

	/**
	 * Creates the main window's contents. Here we show the simulation.
	 * 
	 * @param shell
	 *            the main window
	 */
	private void createPrimaryContents(final Shell shell) {
		final Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL,
				GridData.HORIZONTAL_ALIGN_FILL, true, true));
		comp.setLayout(new FillLayout());
		primaryScrollableComposite = new ScrolledComposite(comp, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
		loadImages();
		canvas = createPrimaryCanvas(primaryScrollableComposite);
		// c2.setLayout(new FillLayout());
		primaryScrollableComposite.setContent(canvas);
		primaryScrollableComposite.setExpandHorizontal(true);
		primaryScrollableComposite.setExpandVertical(true);
		primaryScrollableComposite.setMinSize(1500, 1500);
		primaryScrollableComposite.setOrigin(1500, 1500);
	}

	/**
	 * @param gc
	 */
	protected void drawAdditionalMessages(final GC gc) {
	}

	/**
	 * @param y0
	 * @param x0
	 * @param gc
	 */
	abstract void drawAdditionalObjects(final int x0, final int y0, final GC gc);

	/**
	 * @param gc
	 * @param x0
	 * @param y0
	 */
	private void drawEarth(final GC gc, final int x0, final int y0) {
		// 600 px == 2 * 6300000 m
		final double radius_in_pixel = EARTH_RADIUS * 2 / getCurrentScale();
		final double scale = radius_in_pixel / 600;
		drawImage(gc, IMAGE_NAME_EARTH, x0, y0, scale);
	}

	/**
	 * @param gc
	 * @param bounds
	 * @param x0
	 * @param y0
	 */
	private void drawGrid(final GC gc, final Rectangle bounds, final int x0,
			final int y0) {
		gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawLine(0, y0, bounds.width, y0);
		gc.drawLine(x0, 0, x0, bounds.height);
	}

	/**
	 * @param gc
	 * @param name
	 * @param x
	 * @param y
	 * @param scaleFactor
	 */
	protected void drawImage(final GC gc, final String name, final int x,
			final int y, final double scaleFactor) {
		Image image = images.get(name);
		if (image == null) {
			image = loadImage(name);
		}
		if (image == null) {
			throw new RuntimeException(String.format(
					"Could not load image '%s'", name));
		}
		final int width = image.getBounds().width;
		final int height = image.getBounds().height;
		if (scaleFactor == 1) {
			gc.drawImage(image, x - width / 2, y - height / 2);
		} else {
			// scale image
			final int scaledWidth = (int) (width * scaleFactor);
			final int scaledHeight = (int) (height * scaleFactor);
			final Transform t = new Transform(display);
			t.translate(x - scaledWidth / 2, y - scaledHeight / 2);
			t.scale((float) scaleFactor, (float) scaleFactor);
			gc.setTransform(t);
			gc.drawImage(image, 0, 0);
			t.dispose();
			gc.setTransform(null);
		}
	}

	/**
	 * @param gc
	 */
	private void drawMessages(final GC gc) {
		final Map<String, String> infos = getInfoText();
		int i = 10;
		if (pauseController) {
			gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
			gc.drawString("- PAUSE -", 30, i);
			i += 20;
		}
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		for (final Map.Entry<String, String> info : infos.entrySet()) {
			gc.drawString(info.getKey(), 30, i);
			gc.drawString(info.getValue(), 250, i);
			i += 20;
		}
	}

	/**
	 * @param gc
	 * @param x0
	 * @param y0
	 */
	private void drawSatellite(final GC gc, final int x0, final int y0) {
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		final int xAbs = x0 + new Double(xOrg / getCurrentScale()).intValue();
		final int yAbs = y0 + new Double(yOrg / getCurrentScale()).intValue();
		drawImage(gc, IMAGE_NAME_SATELLITE, xAbs, yAbs, 0.3);
		if (followSatellite) {
			gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
			gc.drawText("Follow Mode Activ!", xAbs - 40, yAbs + 10, true);
		}
		gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
		for (final Iterator<DoublePoint> iterator = points.iterator(); iterator
				.hasNext();) {
			final DoublePoint p = iterator.next();
			final int x = x0 + new Double(p.x / getCurrentScale()).intValue();
			final int y = y0 + new Double(p.y / getCurrentScale()).intValue();
			gc.drawPoint(x, y);
		}
		// draw distance circle
		if (showOrbit) {
			final double distance = Math.hypot(xOrg, yOrg);
			gc.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
			final int distRadius = new Double(distance / getCurrentScale())
					.intValue();
			gc.drawOval(x0 - distRadius, y0 - distRadius, distRadius * 2,
					distRadius * 2);
		}
	}

	/**
   * 
   */
	double getCurrentScale() {
		return currentScale;
	}

	/**
	 * @return
	 */
	protected Map<String, String> getInfoText() {
		final Map<String, String> result = new TreeMap<String, String>();
		result.put("Scenario", String.format("%s", controller.getScenario()));
		result.put("Configuration", String.format("%d", controller
				.getConfiguration()));
		result.put("Scale", nf.format(getCurrentScale()));
		result.put("Cycle", nf.format(controller.getCycleCounter()));
		result.put("Score", nf.format(controller.getScore()));
		result.put("Remaining fuel", nf.format(controller.getFuelRemaining()));
		result.put("Satellite x", nf.format(xOrg));
		result.put("Satellite y", nf.format(yOrg));

		final Speed currentSpeed = controller.getCurrentSpeed();
		if (currentSpeed != null) {
			final double speed = Math.sqrt(currentSpeed.vx * currentSpeed.vx
					+ currentSpeed.vy * currentSpeed.vy);
			result.put("Current speed x", String.format("%s km/cycle", nf
					.format(currentSpeed.vx)));
			result.put("Current speed y", String.format("%s km/cycle", nf
					.format(currentSpeed.vy)));
			result.put("Current speed ", String.format("%s km/cycle", nf
					.format(speed)));
		}

		final double distance = Math.hypot(xOrg, yOrg);
		result.put("Distance satellite to earth", String.format("%s km", nf
				.format(distance)));

		return result;
	}

	/**
	 * @param arg0
	 */
	protected void handleKeyPress(final KeyEvent arg0) {
		//
	}

	/**
	 * Cached loading of images. Like a image-registry. All loaded images are
	 * transparent with color white.
	 * 
	 * @param name
	 *            of the image
	 * @return
	 */
	protected Image loadImage(final String name) {
		final InputStream stream = this.getClass().getResourceAsStream(name);
		if (stream == null) {
			throw new IllegalArgumentException(String.format(
					"Could not load image %s", name));
		}
		final ImageData ideaData = new ImageData(stream);
		final int whitePixel = ideaData.palette
				.getPixel(new RGB(255, 255, 255));
		ideaData.transparentPixel = whitePixel;
		final Image image = new Image(display, ideaData);
		images.put(name, image);
		return image;
	}

	/**
	 * Method for initializing images. Use {@link #loadImage(String)} inside.
	 * Overwrite in you class to load more images. Per default the earth
	 * {@link #IMAGE_NAME_EARTH} , a satellite {@link #IMAGE_NAME_SATELLITE} and
	 * a sputnik {@link #IMAGE_NAME_SPUTNIK} image is load.
	 */
	protected void loadImages() {
		try {
			loadImage(IMAGE_NAME_EARTH);
			loadImage(IMAGE_NAME_SATELLITE);
			loadImage(IMAGE_NAME_SPUTNIK);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Runs the application
	 */
	public void run() {
		display = new Display();
		final Shell shell = new Shell(display);
		shell.setText(String
				.format("-#:: ICFP 2009 <> Orbit Simulator by S.A.K. LE ::#-"));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;
		shell.setLayout(gridLayout);
		createInfoContents(shell);
		createPrimaryContents(shell);
		shell.setSize(1024, 786);
		shell.setLocation(30, 30);
		centerEarth();
		shell.open();

		// Set up the timer for the animation
		final Runnable runnable = new Runnable() {
			public void run() {
				if (canvas.isDisposed()) {
					return;
				}
				animate();
				display.timerExec(TIMER_INTERVAL, this);
			}
		};

		// Launch the timer
		display.timerExec(TIMER_INTERVAL, runnable);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// Kill the timer
		display.dispose();
	}

	/**
	 * @param i
	 */
	protected void scaleTo(final double i) {
		if (SCALE / MAX_SCALE < i && i < SCALE * MAX_SCALE) {
			currentScale = i;
			final int scale = (int) (1 / i * 1000 * SCALE);
			primaryScrollableComposite.setMinSize(scale, scale);
			centerEarth();
		}
	}
}
