import processing.core.PApplet;
import processing.core.PVector;

import java.awt.geom.Line2D;

import static processing.core.PApplet.*;
import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.TWO_PI;

// I want to see if using a specialised class can help clean up the code
public class Arc extends Circle {
	public float start, end, drawStart, drawEnd;
	boolean bezier = false, circle = false;
	float[] bezierInfo;
	
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
	public Arc(Arc from, Arc to) {
		// Bezier connect
		this();
		float angle, m = 2f, r = min(from.r, to.r);
		boolean invalid = true;
		bezierInfo = new float[8];
		bezierInfo[0] = from.x + from.r * cos(from.end);
		bezierInfo[1] = from.y + from.r * sin(from.end);
		bezierInfo[6] = to.x + to.r * cos(to.start);
		bezierInfo[7] = to.y + to.r * sin(to.start);
		while(invalid && m > 0.01f) {
			angle = (from.start == from.drawStart) ? HALF_PI : -HALF_PI;
			bezierInfo[2] = bezierInfo[0] + m * r * cos(from.end + angle);
			bezierInfo[3] = bezierInfo[1] + m * r * sin(from.end + angle);
			angle = (to.end == to.drawEnd) ? -HALF_PI : HALF_PI;
			bezierInfo[4] = bezierInfo[6] + m * r * cos(to.start + angle);
			bezierInfo[5] = bezierInfo[7] + m * r * sin(to.start + angle);
			invalid = Line2D.linesIntersect(bezierInfo[0], bezierInfo[1], bezierInfo[2], bezierInfo[3],
					bezierInfo[4], bezierInfo[5], bezierInfo[6], bezierInfo[7]);
			m /= 2;
		}
		bezier = true;
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
	public float range() {
		return drawEnd - drawStart;
	}
	
	@Override
	public Object clone() {
		Arc arc = new Arc();
		arc.pv = pv;
		arc.x = x;
		arc.y = y;
		arc.r = r;
		arc.start = start;
		arc.end = end;
		arc.drawStart = drawStart;
		arc.drawEnd = drawEnd;
		arc.bezier = bezier;
		arc.circle = circle;
		arc.bezierInfo = bezierInfo.clone();
		return arc;
	}
	@Override
	public void draw(PApplet sketch) {
		if(bezier) {
			sketch.bezier(bezierInfo[0], bezierInfo[1], bezierInfo[2], bezierInfo[3], bezierInfo[4], bezierInfo[5], bezierInfo[6], bezierInfo[7]);
		} else if (r >= 0) {
			sketch.arc(x, y, r * 2, r * 2, drawStart, drawEnd);
			if (circle) {
				sketch.circle(x, y, r * 2);
			}
		}
	}
	public boolean overlaps(Arc a) {
		Circle[] circles = Smoothing.getAdjacent(this, a, 0f, true);
		if(Float.isNaN(circles[0].r)) {
			return false;
		}
		return (within(PVector.sub(circles[0].pv, this.pv).heading(), drawStart, drawEnd)
				&& within(PVector.sub(circles[0].pv, a.pv).heading(), a.drawStart, a.drawEnd))
				|| (within(PVector.sub(circles[1].pv, this.pv).heading(), drawStart, drawEnd)
				&& within(PVector.sub(circles[1].pv, a.pv).heading(), a.drawStart, a.drawEnd));
	}
	@Override
	public boolean overlaps(Circle c) {
		return overlaps(new Arc(c, 0, TWO_PI));
	}
	private boolean within(float check, float min, float max) {
		while(check < min) {
			check += TWO_PI;
		}
		while(check > max) {
			check -= TWO_PI;
		}
		return check > min;
	}
	@Override
	public String toString() {
		return String.format("(x: %.2f, y: %.2f, r: %.2f, s: %.2f, e: %.2f)", x, y, r, drawStart, drawEnd);
	}
}
