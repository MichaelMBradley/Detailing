import processing.core.PApplet;
import processing.core.PVector;

import java.awt.geom.Line2D;

import static processing.core.PApplet.*;
import static processing.core.PConstants.HALF_PI;

public class Bezier implements Curve {
	public float[] info;
	
	public Bezier() {
		info = new float[8];
	}
	public Bezier(float p1x, float p1y, float c1x, float c1y, float c2x, float c2y, float p2x, float p2y) {
		info = new float[] {p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y};
	}
	public Bezier(PVector p1, PVector c1, PVector c2, PVector p2) {
		this(p1.x, p1.y, c1.x, c1.y, c2.x, c2.y, p2.x, p2.y);
	}
	public Bezier(Arc from, Arc to) {
		// Bezier connect
		this();
		float angle, m = 2f, r = min(from.r, to.r);
		boolean invalid = true;
		info = new float[8];
		info[0] = from.x + from.r * cos(from.end);
		info[1] = from.y + from.r * sin(from.end);
		info[6] = to.x + to.r * cos(to.start);
		info[7] = to.y + to.r * sin(to.start);
		while(invalid && m > 0.01f) {
			angle = (from.start == from.drawStart) ? HALF_PI : -HALF_PI;
			info[2] = info[0] + m * r * cos(from.end + angle);
			info[3] = info[1] + m * r * sin(from.end + angle);
			info[4] = info[6] + m * r * cos(to.start + angle);
			info[5] = info[7] + m * r * sin(to.start + angle);
			invalid = Line2D.linesIntersect(info[0], info[1], info[2], info[3],
					info[4], info[5], info[6], info[7]);
			m /= 2;
		}
		//println(String.format("From: (x: %.2f, y: %.2f, r: %.2f, s: %.2f, e: %.2f, =: %s)\tTo: (x: %.2f, y: %.2f, r: %.2f, s: %.2f, e: %.2f, =: %s)",
			//from.x, from.y, from.r, from.start, from.end, from.drawStart == from.start, to.x, to.y, to.r, to.start, to.end, to.drawStart == to.start));
	}
	
	public void draw(PApplet sketch) {
		sketch.bezier(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7]);
	}
}
