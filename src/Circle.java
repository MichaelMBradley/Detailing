import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.dist;

public class Circle {
    public float x, y, r;
    public PVector pv;

    public Circle(float xpos, float ypos, float rad) {
        x = xpos;
        y = ypos;
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
        x = PVec.x;
        y = PVec.y;
        r = 0;
        pv = PVec;
    }
    public Circle() {
        x = 0;
        y = 0;
        r = 0;
        pv = new PVector(x, y);
    }


    // Distance from a point to the center of the node
    public float distanceToCenter(float xpos, float ypos) {
        return dist(x, y, xpos, ypos);
    }
    public float distanceToCenter(PVector pos) {
        return PVector.dist(pv, pos);
    }
    public float distanceToCenter(Circle c) {
        return distanceToCenter(c.pv);
    }

    // Distance from a point to the perimeter of the node
    public float distanceToRadius(float xpos, float ypos) {
        return distanceToCenter(xpos, ypos) - r;
    }
    public float distanceToRadius(PVector pos) {
        return distanceToCenter(pos) - r;
    }
    public float distanceToRadius(Circle c) {
        return distanceToCenter(c.pv) - r;
    }

    // Distance between the closest points of each node
    public float distanceToCircle(float xpos, float ypos, float r) {
        return distanceToRadius(xpos, ypos) - r;
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
    public void draw(PApplet sketch) {
        sketch.circle(x, y, r * 2);  // p5 accepts diameter, not radius
    }


    public String toString() {
        return String.format("(x: %.2f, y: %.2f, r: %.2f)", x, y, r);
    }
}
