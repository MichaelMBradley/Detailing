import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.abs;
import static processing.core.PConstants.TWO_PI;

// I want to see if using a specialised class can help clean up the code
public class Arc extends Circle {
	public float start, end;
	
	public Arc() {
		super();
		start = 0f;
		start = 0f;
	}
	public Arc(Circle c, float s, float e) {
		super(c.pv, c.r);
		start = s;
		end = e;
	}
	public Arc(PVector location, float radius, float s, float e) {
		super(location, radius);
		start = s;
		end = e;
	}
	
	public Arc(PVector start, PVector end, PVector through) {
		// Creates an Arc from "start" to "end" intersecting "through"
		this(ShapeFunctions.triangleToCircle(start, end, through), new Circle(start), new Circle(end), false);
	}
	
	public Arc(Circle base, Circle prev, Circle next, boolean connect) {
		super(base.pv, base.r);
		PVector relPrevPV = PVector.sub(prev.pv, base.pv);
		PVector relNextPV = PVector.sub(next.pv, base.pv);
		start = relPrevPV.heading();
		end = relNextPV.heading();
		// Allow for rounding error
		boolean cross = (abs(abs(end - start) - PVector.angleBetween(relPrevPV, relNextPV)) < 0.01f) != connect;
		// Changing the start and end angles such that
		// * end > start
		// * end - start < 2Pi
		if (start > end) {
			if (cross) {
				e2PI();
				//swapSE();
			} else {
				//e2PI();
				swapSE();
			}
		} else if (cross) {
			swapSE();
			e2PI();
		}
	}
	private void swapSE() {
		float temp;
		temp = start;
		start = end;
		end = temp;
	}
	private void e2PI() {
		end += TWO_PI;
	}
	
	@Override
	public void draw(PApplet sketch) {
		sketch.arc(pv.x, pv.y, r * 2, r * 2, start, end);
		// sketch.circle(x,y,r*2);
	}
	@Override
	public String toString() {
		return String.format("(x: %.2f, y: %.2f, r: %.2f, s: %.2f, e: %.2f)", x, y, r, start, end);
	}
}
