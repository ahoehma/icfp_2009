package sak.orbit.math;

public class Utils {
	public final static double G = 6.67428e-11;
	public final static double Me = 6.0e24;
	public final static double Re = 6.357e6;

	public final static Speed calculateSpeed(final Point p1, final Point p2, final double r, final Speed deltaV) {
		final double newX = p2.x - p1.x - 1 / 2 * (gt(r) + deltaV.vx);
		final double newY = p2.y - p1.y - 1 / 2 * (gt(r) + deltaV.vy);
		return new Speed(newX, newY);
	}

	public final static double distance(final Point p1, final Point p2) {
		return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
	}

	public static double getSpeedForRadius(final Point pos) {
		return Math.sqrt(G * Me / distance(pos, new Point(0, 0)));
	}

	public final static double gt(final double r) {
		return G * Me / r / r;
	}

	public final static double homanHoldTime(final double r1, final double r2) {
		return Math.PI * Math.sqrt(Math.pow((r1 + r2), 3) / 8.0 / G / Me);
	}

	public final static Speed homanSpeed1(final Speed v, final double r1, final double r2) {
		// notwendige geschwindigkeitsaenderung
		final double dv = Math.sqrt(G * Me / r1) * (Math.sqrt(2 * r2 / (r1 + r2)) - 1);

		// betrag der aktuellen geschwindigkeit
		final double currV = Math.sqrt(v.vx * v.vx + v.vy * v.vy);

		final double factor = dv / currV;
		final double newX = v.vx * factor;
		final double newY = v.vy * factor;

		return new Speed(newX, newY);
	}

	public final static Speed homanSpeed2(final Speed v, final double r1, final double r2) {
		// notwendige geschwindigkeitsaenderung
		final double dv = Math.sqrt(G * Me / r2) * (1 - Math.sqrt(2 * r1 / (r1 + r2)));

		// betrag der aktuellen geschwindigkeit
		final double currV = Math.sqrt(v.vx * v.vx + v.vy * v.vy);

		final double factor = dv / currV;
		final double newX = v.vx * factor;
		final double newY = v.vy * factor;

		return new Speed(newX, newY);
	}

	public final static double magGetAngleFromPoint(final Point p) {
		final double alpha = Math.atan2(p.y, p.x);
		return normalizeAngle(alpha);
	}

	public final static double magGetAngleOfSegmentInUmfang(final double umfang, final double segment) {
		return normalizeAngle(2 * Math.PI * segment / umfang);
	}

	public final static Point magGetHomanTargetPoint(final Point start, final double targetRadius) {
		final double startRadius = Math.sqrt(start.x * start.x + start.y * start.y);
		final double radiusFactor = targetRadius / startRadius;

		return new Point(-start.x * radiusFactor, -start.y * radiusFactor);
	}

	public final static Point magGetOthersPosition(final Point me, final Point myRelPos) {
		return new Point(me.x - myRelPos.x, me.y - myRelPos.y);
	}

	public final static Point magGetPointFromAngle(final double r, final double alpha) {
		return new Point(r * Math.cos(alpha), r * Math.sin(alpha));
	}

	public final static double magGetUmfang(final double r) {
		return 2 * Math.PI * r;
	}

	public final static Speed manualSpeed1(final Speed v, final double dv) {
		// betrag der aktuellen geschwindigkeit
		final double currV = Math.sqrt(v.vx * v.vx + v.vy * v.vy);

		final double factor = dv / currV;
		final double newX = v.vx * factor;
		final double newY = v.vy * factor;

		return new Speed(newX, newY);
	}

	public final static Point nextPoint(final Point p, final Speed v, final double r, final Speed deltaV) {
		final double newX = p.x + v.vx + 1 / 2 * (gt(r) + deltaV.vx);
		final double newY = p.y + v.vy + 1 / 2 * (gt(r) + deltaV.vy);
		return new Point(newX, newY);
	}

	/**
	 * @param v
	 * @param deltaV
	 * @param r1
	 * @param r2
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public final static Speed nextSpeed(final Speed v, final Speed deltaV, final double r1, final double r2) {
		final double newX = v.vx + (deltaV.vx + (gt(r1 + r2)) / 2.0);
		final double newY = v.vy + (deltaV.vy + (gt(r1 + r2)) / 2.0);
		return new Speed(newX, newY);
	}

	/**
	 * 
	 * 
	 * @param v
	 * @param deltaV
	 * @param p1
	 * @param p2
	 * @return
	 */
	public final static Speed nextSpeed2(final Speed v, final Speed deltaV, final Point p1, final Point p2) {
		final double newX = v.vx + (deltaV.vx + (gt(p1.x + p2.x)) / 2.0);
		final double newY = v.vy + (deltaV.vy + (gt(p1.y + p2.y)) / 2.0);
		return new Speed(newX, newY);
	}

	public final static double normalizeAngle(final double alpha) {
		if (alpha > 2 * Math.PI) {
			return alpha - 2 * Math.PI;
		}
		if (alpha < 0) {
			return alpha + 2 * Math.PI;
		}
		return alpha;
	}
}
