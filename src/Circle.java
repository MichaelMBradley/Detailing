import lombok.Getter;
import lombok.Setter;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.*;

public class Circle {
	@Getter @Setter private float x, y, r;
	@Getter private PVector PV;
	
	public Circle(float xPos, float yPos, float rad) {
		x = xPos;
		y = yPos;
		r = rad;
		PV = new PVector(x, y);
	}
	public Circle(PVector PVec, float rad) {
		x = PVec.x;
		y = PVec.y;
		r = rad;
		PV = PVec;
	}
	public Circle(PVector PVec) {
		this(PVec, 0f);
	}
	public Circle(Circle c) {
		x = c.getX();
		y = c.getY();
		r = c.getR();
		PV = c.getPV();
	}
	public Circle() {
		this(0, 0, 0);
	}
	
	
	// Distance from a point to the center of the node
	public float distanceToCenter(float xPos, float yPos) {
		return dist(x, y, xPos, yPos);
	}
	public float distanceToCenter(PVector pos) {
		return PVector.dist(PV, pos);
	}
	public float distanceToCenter(Circle c) {
		return distanceToCenter(c.PV);
	}
	
	// Distance from a point to the perimeter of the node
	public float distanceToRadius(float xPos, float yPos) {
		return distanceToCenter(xPos, yPos) - r;
	}
	public float distanceToRadius(PVector pos) {
		return distanceToCenter(pos) - r;
	}
	public float distanceToRadius(Circle c) {
		return distanceToCenter(c.PV) - r;
	}
	
	// Distance between the closest points of each node
	public float distanceToCircle(float xPos, float yPos, float r) {
		return distanceToRadius(xPos, yPos) - r;
	}
	public float distanceToCircle(PVector pos, float r) {
		return distanceToRadius(pos) - r;
	}
	public float distanceToCircle(Circle c) {
		return distanceToRadius(c.PV) - c.r;
	}
	
	public boolean overlaps(Circle c) {
		return distanceToCircle(c) < 0;
	}
	public PVector PVectorOnCircumference(float angle) {
		return new PVector(x + cos(angle) * r, y + sin(angle) * r);
	}
	public Circle adjacent(float angle, float rNew) {
		return new Circle(x + cos(angle) * (r + rNew), y + sin(angle) * (r + rNew), rNew);
	}
	
	public void move(PVector direction) {
		PV.add(direction);
		x += direction.x;
		y += direction.y;
	}
	public void setPV(PVector location) {
		PV.set(location);
		x = location.x;
		y = location.y;
	}
	public void setPV(float newX, float newY) {
		x = newX;
		y = newY;
		PV.set(newX, newY);
	}
	public void draw(PApplet sketch) {
		sketch.circle(x, y, r * 2);  // p5 accepts diameter, not radius
	}
	
	@Override
	public String toString() {
		return String.format("(x: %.2f, y: %.2f, r: %.2f)", x, y, r);
	}
}
