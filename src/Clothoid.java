import lombok.Getter;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Clothoid implements Curve {
	// Code from Joe (https://math.stackexchange.com/users/221133/joe), Get points in the plane of an Euler spiral given by curvature, URL (version: 2019-04-22): https://math.stackexchange.com/q/3196882
	// This implementation generates a clothoid from angles k1 to k2, and then scales, rotates, and transforms it to fit the given start and endpoints.
	// Unfortuntely, this means that it no longer properly fits the given input angles.
	private final ArrayList<PVector> points;
	@Getter private final float startAngle, endAngle;
	@Getter private final PVector startPVector, endPVector;
	
	public Clothoid() {
		points = new ArrayList<>();
		startAngle = Float.NaN;
		endAngle = Float.NaN;
		startPVector = new PVector();
		endPVector = new PVector();
	}
	public Clothoid(Curve c1, Curve c2) {
		this(c1.getEndAngle(), c2.getStartAngle(), c1.getEndPVector(), c2.getStartPVector());
	}
	public Clothoid(float k1, float k2, PVector p1, PVector p2) {
		points = new ArrayList<>();
		for(float i = 0; i <= 1; i += (1f / (15f * (k2 - k1)))) {
			points.add(fit_clothoid(i, k1, k2, p1, p2));
		}
		startAngle = k1;
		endAngle = k2;
		startPVector = points.get(0);
		endPVector = points.get(points.size() - 1);
	}
	
	public PVector quadrature(float t, float k1, float k2) {
		return quadrature(t, k1, k2, 32);
	}
	public PVector quadrature(float t, float k1, float k2, int steps) {
		float dt = t / steps;
		float t2 = 0, x = 0, y = 0;
		float k, th, dx1, dy1, dx2, dy2;
		for(int i = 0; i < steps; i++) {
			k = k1 * (1 - t2) + k2 * t2;
			th = (-((t2 - 2) * k1 - k2 * t2) * t2) / 2;
			dx1 = cos(th);
			dy1 = sin(th);
			dx2 = sin(th) * k;
			dy2 = -cos(th) * k;
			x += dx1 * dt + dx2 * 0.5 * dt * dt;
			y += dy1 * dt + dy2 * 0.5 * dt * dt;
			t2 += dt;
		}
		return new PVector(x, y);
	}
	public PVector fit_clothoid(float t, float k1, float k2, PVector p1, PVector p2) {
		PVector start = quadrature(0f, k1, k2);
		PVector end = quadrature(1f, k1, k2);
		PVector vec = PVector.sub(end, start);
		float th = atan2(p2.y - p1.y, p2.x - p1.x) - atan2(vec.y, vec.x);
		PVector p = PVector.sub(quadrature(t, k1, k2), start);
		p.mult(PVector.dist(p2, p1) / PVector.dist(end, start));
		p.rotate(th);
		return PVector.add(p, p1);
	}
	
	@Override
	public boolean isEmpty() {
		return startAngle == endAngle || startPVector.equals(endPVector);
	}
	@Override
	public boolean isConnecting() {
		return false;
	}
	@Override
	public float getSize() {
		return startPVector.dist(endPVector);
	}
	@Override
	public float getRange() {
		return endAngle - startAngle;
	}
	
	@Override
	public void draw(PApplet sketch) {
		for(int i = 0; i < points.size() - 1; i++) {
			sketch.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
		}
	}
	public void drawCurve(PApplet sketch) {
		sketch.noFill();
		sketch.beginShape();
		sketch.curveVertex(points.get(0).x, points.get(0).y);
		for(int i = 0; i < points.size() - 1; i += 8) {
			sketch.curveVertex(points.get(i).x, points.get(i).y);
		}
		sketch.curveVertex(points.get(points.size() - 1).x, points.get(points.size() - 1).y);
		sketch.curveVertex(points.get(points.size() - 1).x, points.get(points.size() - 1).y);
		sketch.endShape();
	}
}
