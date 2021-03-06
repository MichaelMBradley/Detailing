import lombok.Getter;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedHashMap;

import static processing.core.PApplet.*;

public class Bezier implements Curve {
	private final PVector p1, p2;
	@Getter private final PVector c1, c2;
	
	public Bezier() {
		p1 = new PVector();
		c1 = new PVector();
		c2 = new PVector();
		p2 = new PVector();
	}
	public Bezier(float[] bezierInfo) {
		p1 = new PVector(bezierInfo[0], bezierInfo[1]);
		c1 = new PVector(bezierInfo[2], bezierInfo[3]);
		c2 = new PVector(bezierInfo[4], bezierInfo[5]);
		p2 = new PVector(bezierInfo[6], bezierInfo[7]);
	}
	public Bezier(float p1x, float p1y, float c1x, float c1y, float c2x, float c2y, float p2x, float p2y) {
		p1 = new PVector(p1x, p1y);
		c1 = new PVector(c1x, c1y);
		c2 = new PVector(c2x, c2y);
		p2 = new PVector(p2x, p2y);
	}
	public Bezier(PVector p1in, PVector c1in, PVector c2in, PVector p2in) {
		p1 = p1in;
		c1 = c1in;
		c2 = c2in;
		p2 = p2in;
	}
	public Bezier(Curve from, Curve to) {
		this(from, to, 0.75f);
	}
	public Bezier(Curve from, Curve to, float power) {
		// Bezier connect
		float rF = pow(from.getSize(), power), rT = pow(to.getSize(), power);
		p1 = from.getEndPVector();
		c1 = Traversal.newRelative(p1, rF, from.getEndAngle());
		p2 = to.getStartPVector();
		c2 = Traversal.newRelative(p2, rT, to.getStartAngle());
		// System.out.printf("%s\nFrom: %s\tTo: %s\n", Helpers.floatArrayToString(info), from, to));
	}
	
	@Override public boolean isEmpty() {
		return new PVector().equals(p1) && p1.equals(c1) && c1.equals(c2) && c2.equals(p2);
	}
	@Override public boolean isConnecting() {
		return true;
	}
	@Override public float getStartAngle() {
		return PVector.sub(c1, p1).heading();
	}
	@Override public float getEndAngle() {
		return PVector.sub(p2, c2).heading();
	}
	@Override public PVector getStartPVector() {
		return p1;
	}
	@Override public PVector getEndPVector() {
		return p2;
	}
	@Override public float getSize() {
		return p1.dist(p2);
	}
	@Override public float getRange() {
		return abs(getEndAngle() - getStartAngle());
	}
	
	public Bezier getAdjacent(float offset) {
		PVector n1 = Traversal.newRelative(p1, offset, getStartAngle() + HALF_PI);
		PVector n2 = Traversal.newRelative(c1, offset, getStartAngle() + HALF_PI);
		PVector n3 = Traversal.newRelative(c1, offset, PVector.sub(c2, c1).heading() + HALF_PI);
		PVector n4 = Traversal.newRelative(c2, offset, PVector.sub(c2, c1).heading() + HALF_PI);
		PVector n5 = Traversal.newRelative(c2, offset, getEndAngle() + HALF_PI);
		PVector n6 = Traversal.newRelative(p2, offset, getEndAngle() + HALF_PI);
		return new Bezier(n1, Traversal.crossover(n1, n2, n3, n4), Traversal.crossover(n3, n4, n5, n6), n6);
	}
	public Bezier[] getAdjacent(float offset, int det) {
		if(det < 1) {
			return new Bezier[0];
		}
		Bezier[] bez = new Bezier[det];
		float base = 0f, chng = 1f / det;
		for(int i = 0; i < det; i++) {
			bez[i] = cut(base, base + chng).getAdjacent(offset);
			base += chng;
		}
		return bez;
	}
	public Bezier cut(float min, float max) {
		return cut(max)[0].cut(min / max)[1];
	}
	public Bezier[] cut(float amt) {
		amt = max(0, min(1, amt));
		PVector a1 = PVector.lerp(p1, c1, amt);
		PVector a2 = PVector.lerp(c1, c2, amt);
		PVector a3 = PVector.lerp(c2, p2, amt);
		PVector b1 = PVector.lerp(a1, a2, amt);
		PVector b2 = PVector.lerp(a2, a3, amt);
		PVector c1 = PVector.lerp(b1, b2, amt);
		return new Bezier[] { new Bezier(p1, a1, b1, c1), new Bezier(c1, b2, a3, p2) };
	}
	
	public float distance() {
		return distance(20);
	}
	public float distance(int accuracy) {
		return speed(accuracy).get(1f);
	}
	public LinkedHashMap<Float, Float> speed(int accuracy) {
		LinkedHashMap<Float, Float> lhm = new LinkedHashMap<>();
		PVector prev, next = p1;
		float dist = 0;
		for(int i = 0; i <= accuracy; i++) {
			prev = next;
			next = pointAt((float) i / accuracy);
			dist += prev.dist(next);
			lhm.put((float) i / accuracy, dist);
		}
		return lhm;
	}
	public PVector pointAt(float amt) {
		amt = 1 - amt;
		return PVector.add(PVector.add(PVector.mult(p1, pow(amt, 3)), PVector.mult(c1, 3 * pow(amt, 2) * (1 - amt))),
				PVector.add(PVector.mult(c2, 3 * amt * pow(1 - amt, 2)), PVector.mult(p2, pow((1 - amt), 3))));
	}
	
	@Override public void draw(PApplet sketch) {
		sketch.bezier(p1.x, p1.y, c1.x, c1.y, c2.x, c2.y, p2.x, p2.y);
	}
	public void drawPoints(PApplet sketch) {
		sketch.point(p1.x, p1.y);
		sketch.point(p2.x, p2.y);
		sketch.point(c1.x, c1.y);
		sketch.point(c2.x, c2.y);
	}
	public void drawLines(PApplet sketch) {
		sketch.line(p1.x, p1.y, c1.x, c1.y);
		sketch.line(c1.x, c1.y, c2.x, c2.y);
		sketch.line(c2.x, c2.y, p2.x, p2.y);
	}
	@Override public String toString() {
		return String.format("[%.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f]",
							   p1.x, p1.y, c1.x, c1.y, c2.x, c2.y, p2.x, p2.y);
	}
}
