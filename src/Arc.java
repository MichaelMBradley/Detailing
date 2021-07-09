import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.TWO_PI;

public class Arc extends Circle implements Curve {
	public float start, end, drawStart, drawEnd;
	public boolean connect;
	
	public Arc() {
		super();
		start = 0f;
		start = 0f;
		setDraw(false, false);
		connect = false;
	}
	public Arc(Arc a) {
		super(a.x, a.y, a.r);
		start = a.start;
		end = a.end;
		drawStart = a.drawStart;
		drawEnd = a.drawEnd;
		connect = a.connect;
	}
	public Arc(Circle c, float s, float e) {
		super(c.pv, c.r);
		start = s;
		end = e;
		setDraw(false, false);
		connect = false;
	}
	public Arc(PVector location, float radius, float s, float e) {
		super(location, radius);
		start = s;
		end = e;
		setDraw(false, false);
		connect = false;
	}
	
	public Arc(PVector start, PVector end, PVector through) {
		// Creates an Arc from "start" to "end" intersecting "through"
		this(Geometry.triangleToCircle(start, end, through), new Circle(start), new Circle(end), false);
	}
	public Arc(Circle base, Circle prev, Circle next, boolean clockwise) {
		this(base, prev, next, clockwise, false);
	}
	public Arc(Circle base, Circle prev, Circle next, boolean clockwise, boolean connecting) {
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
		connect = connecting;
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
	public boolean isEmpty() {
		return r == 0f || range() == 0f;
	}
	@Override
	public boolean isConnecting() {
		return connect;
	}
	@Override
	public float getStartAngle() {
		return connect ? start - HALF_PI : start + HALF_PI;
	}
	@Override
	public float getEndAngle() {
		return connect ? end + HALF_PI : end - HALF_PI;
	}
	@Override
	public PVector getStartPVector() {
		return new PVector(x + cos(start) * r, y + sin(start) * r);
	}
	@Override
	public PVector getEndPVector() {
		return new PVector(x + cos(end) * r, y + sin(end) * r);
	}
	@Override
	public float getSize() {
		return r;
	}
	
	@Override
	public void draw(PApplet sketch) {
		sketch.arc(x, y, r * 2, r * 2, drawStart, drawEnd);
		/*int colour = sketch.color(sketch.random(0, 255), sketch.random(0, 255), sketch.random(0, 255));
		sketch.stroke(colour);
		sketch.arc(x, y, r * 2, r * 2, drawStart, drawEnd);
		sketch.textSize(5);
		sketch.fill(colour);
		sketch.text(String.valueOf(connect), x, y);
		sketch.noFill();*/
	}
	public void drawCircle(PApplet sketch) {
		sketch.circle(x, y, r * 2);
	}
	public boolean overlaps(Arc a) {
		Circle[] circles = Touching.getAdjacent(this, a, 0f, true);
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
