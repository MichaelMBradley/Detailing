import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.dist;

public class Circle {
	public float x, y, r;
	public PVector pv;
	
	public Circle(float xPos, float yPos, float rad) {
		x = xPos;
		y = yPos;
		r = rad;
		pv = new PVector(x, y);
	}
	public Circle(PVector PVec, float rad) {
		x = PVec.x;
		y = PVec.y;
		r = rad;
		pv = PVec;
	}
	public Circle(PVector PVec) {
		this(PVec, 0f);
	}
	public Circle() {
		this(0, 0, 0);
	}
	
	
	// Distance from a point to the center of the node
	public float distanceToCenter(float xPos, float yPos) {
		return dist(x, y, xPos, yPos);
	}
	public float distanceToCenter(PVector pos) {
		return PVector.dist(pv, pos);
	}
	public float distanceToCenter(Circle c) {
		return distanceToCenter(c.pv);
	}
	
	// Distance from a point to the perimeter of the node
	public float distanceToRadius(float xPos, float yPos) {
		return distanceToCenter(xPos, yPos) - r;
	}
	public float distanceToRadius(PVector pos) {
		return distanceToCenter(pos) - r;
	}
	public float distanceToRadius(Circle c) {
		return distanceToCenter(c.pv) - r;
	}
	
	// Distance between the closest points of each node
	public float distanceToCircle(float xPos, float yPos, float r) {
		return distanceToRadius(xPos, yPos) - r;
	}
	public float distanceToCircle(PVector pos, float r) {
		return distanceToRadius(pos) - r;
	}
	public float distanceToCircle(Circle c) {
		return distanceToRadius(c.pv) - c.r;
	}
	
	public boolean overlaps(Circle c) {
		return distanceToCircle(c) < 0;
	}
	
	public void move(PVector direction) {
		pv.add(direction);
		x += direction.x;
		y += direction.y;
	}
	public void setLocation(PVector location) {
		pv = location;
		x = location.x;
		y = location.y;
	}
	public void setLocation(float newX, float newY) {
		x = newX;
		y = newY;
		pv = new PVector(x, y);
	}
	public void draw(PApplet sketch) {
		sketch.circle(x, y, r * 2);  // p5 accepts diameter, not radius
	}
	
	@Override
	public String toString() {
		return String.format("(x: %.2f, y: %.2f, r: %.2f)", x, y, r);
	}
}
