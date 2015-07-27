package sak.orbit.math;

public class Speed {
	public double vx;
	public double vy;

	public Speed(final double vx, final double vy) {
		this.vx = vx;
		this.vy = vy;
	}

	@Override
	public String toString() {
		return "" + vx + "," + vy;
	}
}
