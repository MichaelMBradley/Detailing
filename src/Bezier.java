import processing.core.PApplet;
import processing.core.PVector;

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
		float angle, rF = pow(to.getSize(), 0.5f), rT = pow(to.getSize(), 0.5f);
		boolean invalid = true;
		info = new float[8];
		info[0] = from.getEndPVector().x;
		info[1] = from.getEndPVector().y;
		info[6] = to.getStartPVector().x;
		info[7] = to.getStartPVector().y;
		info[2] = info[0] + rF * cos(from.getEndAngle());
		info[3] = info[1] + rF * sin(from.getEndAngle());
		info[4] = info[6] + rT * cos(to.getStartAngle());
		info[5] = info[7] + rT * sin(to.getStartAngle());
		// println(String.format("%s\nFrom: %s\tTo: %s\n", Helpers.floatArrayToString(info), from, to));
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
	@Override
	public float getRange() {
		return abs(getEndAngle() - getStartAngle());
	}
	
	public void draw(PApplet sketch) {
		sketch.bezier(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7]);
	}
	public String toString() {
		return Helpers.floatArrayToString(info);
	}
}
