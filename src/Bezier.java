import processing.core.PApplet;
import processing.core.PVector;

import java.awt.geom.Line2D;
import java.util.Arrays;

import static processing.core.PApplet.*;

public class Bezier implements Curve {
	private float[] info;
	
	public Bezier() {
		info = new float[8];
	}
	public Bezier(float p1x, float p1y, float c1x, float c1y, float c2x, float c2y, float p2x, float p2y) {
		info = new float[] {p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y};
	}
	public Bezier(PVector p1, PVector c1, PVector c2, PVector p2) {
		this(p1.x, p1.y, c1.x, c1.y, c2.x, c2.y, p2.x, p2.y);
	}
	public Bezier(Curve from, Curve to) {
		// Bezier connect
		this();
		float angle, m = 2f, r = min(from.getSize(), to.getSize());
		boolean invalid = true;
		info = new float[8];
		info[0] = from.getEndPVector().x;
		info[1] = from.getEndPVector().y;
		info[6] = to.getStartPVector().x;
		info[7] = to.getStartPVector().y;
		while(invalid && m > 0.01f) {
			info[2] = info[0] + m * r * cos(from.getEndAngle());
			info[3] = info[1] + m * r * sin(from.getEndAngle());
			info[4] = info[6] + m * r * cos(to.getStartAngle());
			info[5] = info[7] + m * r * sin(to.getStartAngle());
			invalid = Line2D.linesIntersect(info[0], info[1], info[2], info[3],
					info[4], info[5], info[6], info[7]);
			m /= 2;
		}
		// println(String.format("%s\nFrom: %s =: %s\tTo: %s, =: %s\n", Helpers.floatArrayToString(info), from, from.isConnecting(), to, to.isConnecting()));
	}
	
	
	@Override
	public boolean isEmpty() {
		return Arrays.equals(info, new float[8]);
	}
	@Override
	public boolean isConnecting() {
		return true;
	}
	@Override
	public float getStartAngle() {
		return new PVector(info[2] - info[0], info[3] - info[1]).heading();
	}
	@Override
	public float getEndAngle() {
		return new PVector(info[4] - info[6], info[5] - info[7]).heading();
	}
	@Override
	public PVector getStartPVector() {
		return new PVector(info[0], info[1]);
	}
	@Override
	public PVector getEndPVector() {
		return new PVector(info[6], info[7]);
	}
	@Override
	public float getSize() {
		return new PVector(info[0], info[1]).dist(new PVector(info[6], info[7]));
	}
	
	public void draw(PApplet sketch) {
		sketch.bezier(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7]);
	}
}
