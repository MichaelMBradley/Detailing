import lombok.Getter;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.TWO_PI;

public class Arc extends Circle implements Curve {
	@Getter private float startAngleBase, endAngleBase, drawStart, drawEnd;
	@Getter private final boolean connecting;
	
	public Arc() {
		super();
		startAngleBase = 0f;
		startAngleBase = 0f;
		setDraw(false, false);
		connecting = false;
	}
	public Arc(Arc a) {
		super(a);
		startAngleBase = a.startAngleBase;
		endAngleBase = a.endAngleBase;
		drawStart = a.drawStart;
		drawEnd = a.drawEnd;
		connecting = a.connecting;
	}
	public Arc(Circle c) {
		this(c, 0, TWO_PI);
	}
	public Arc(Circle c, float s, float e) {
		this(c.getPV(), c.getR(), s, e);
	}
	public Arc(PVector location, float radius, float s, float e) {
		super(location, radius);
		startAngleBase = s;
		endAngleBase = e;
		setDraw(false, false);
		connecting = false;
	}
	
	public Arc(PVector start, PVector end, PVector through) {
		// Creates an Arc from "start" to "end" intersecting "through"
		this(Geometry.triangleToCircle(start, end, through), new Circle(start), new Circle(end), false);
	}
	public Arc(Circle base, Circle prev, Circle next, boolean clockwise) {
		this(base, prev, next, clockwise, false);
	}
	public Arc(Circle base, Circle prev, Circle next, boolean clockwise, boolean connect) {
		super(base);
		startAngleBase = PVector.sub(prev.getPV(), base.getPV()).heading();
		endAngleBase = PVector.sub(next.getPV(), base.getPV()).heading();
		// Changing the start and end angles such that
		// * end > start
		// * end - start < 2Pi
		if (startAngleBase > endAngleBase) {
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
		connecting = connect;
	}
	
	private void setDraw(boolean swap, boolean e2Pi) {
		float mod = e2Pi ? TWO_PI: 0f;
		if(swap) {
			drawStart = endAngleBase;
			drawEnd = startAngleBase + mod;
		} else {
			drawStart = startAngleBase;
			drawEnd = endAngleBase + mod;
		}
	}
	
	@Override public boolean isEmpty() {
		return getR() == 0f || drawEnd - drawStart == 0f;
	}
	@Override public float getStartAngle() {
		return startAngleBase + (connecting ? -HALF_PI :  HALF_PI);
	}
	@Override public float getEndAngle() {
		return endAngleBase + (connecting ? HALF_PI : -HALF_PI);
	}
	@Override public PVector getStartPVector() {
		return PVectorOnCircumference(startAngleBase);
	}
	@Override public PVector getEndPVector() {
		return PVectorOnCircumference(endAngleBase);
	}
	@Override public float getSize() {
		return getR();
	}
	@Override public float getRange() {
		return drawEnd - drawStart;
	}
	
	public boolean overlaps(Arc a) {
		Circle[] circles = Adjacent.getAdjacent(this, a, 0f, true);
		if(Float.isNaN(circles[0].getR())) {
			return false;
		}
		return (inRange(PVector.sub(circles[0].getPV(), getPV()).heading())
				&& a.inRange(PVector.sub(circles[0].getPV(), a.getPV()).heading()))
				|| (inRange(PVector.sub(circles[1].getPV(), getPV()).heading())
				&& a.inRange(PVector.sub(circles[1].getPV(), a.getPV()).heading()));
	}
	public boolean inRange(float chk) {
		while(chk < drawStart) {
			chk += TWO_PI;
		}
		chk %= TWO_PI;
		return drawStart < chk && chk < drawEnd;
	}
	
	@Override public void draw(PApplet sketch) {
		sketch.arc(getX(), getY(), getR() * 2, getR() * 2, drawStart, drawEnd);
		/*int colour = sketch.color(sketch.random(0, 255), sketch.random(0, 255), sketch.random(0, 255));
		sketch.stroke(colour);
		sketch.arc(x, y, r * 2, r * 2, drawStart, drawEnd);
		sketch.textSize(5);
		sketch.fill(colour);
		sketch.text(String.valueOf(connect), x, y);
		sketch.noFill();*/
	}
	public void drawCircle(PApplet sketch) {
		sketch.circle(getX(), getY(), getR() * 2);
	}
	@Override public boolean overlaps(Circle c) {
		return overlaps(new Arc(c, 0, TWO_PI));
	}
	@Override public String toString() {
		return String.format("(x: %.2f, y: %.2f, r: %.2f, s: %.2f, e: %.2f)", getX(), getY(), getR(), drawStart, drawEnd);
	}
}
