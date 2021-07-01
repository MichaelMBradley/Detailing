import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PConstants.TWO_PI;

// I want to see if using a specialised class can help clean up the code
public class Arc extends Circle {
	public float start, end, drawStart, drawEnd;
	boolean circle = false;
	
	public Arc() {
		super();
		start = 0f;
		start = 0f;
		setDraw(false, false);
	}
	public Arc(Circle c, float s, float e) {
		super(c.pv, c.r);
		start = s;
		end = e;
		setDraw(false, false);
	}
	public Arc(PVector location, float radius, float s, float e) {
		super(location, radius);
		start = s;
		end = e;
		setDraw(false, false);
	}
	
	public Arc(PVector start, PVector end, PVector through) {
		// Creates an Arc from "start" to "end" intersecting "through"
		this(ShapeFunctions.triangleToCircle(start, end, through), new Circle(start), new Circle(end), false);
	}
	
	public Arc(Circle base, Circle prev, Circle next, boolean clockwise) {
		super(base.pv, base.r);
		start = PVector.sub(prev.pv, base.pv).heading();
		end = PVector.sub(next.pv, base.pv).heading();
		// Changing the start and end angles such that
		// * end > start
		// * end - start < 2Pi
		if (start > end) {
			if (clockwise) {
				setDraw(false, true);
			} else {
				setDraw(true, false);
			}
		} else {
			if (!clockwise) {
				setDraw(true, true);
			} else {
				setDraw(false, false);
			}
		}
	}
	
	private void setDraw(boolean swap, boolean e2Pi) {
		float mod = e2Pi ? TWO_PI: 0f;
		if(swap) {
			drawStart = end;
			drawEnd = start + mod;
		} else {
			drawStart = start;
			drawEnd = end + mod;
		}
	}
	
	@Override
	public void draw(PApplet sketch) {
		sketch.arc(x, y, r * 2, r * 2, drawStart, drawEnd);
		if(circle) {
			sketch.circle(x, y, r * 2);
		}
	}
	@Override
	public String toString() {
		return String.format("(x: %.2f, y: %.2f, r: %.2f, s: %.2f, e: %.2f)", x, y, r, drawStart, drawEnd);
	}
}
