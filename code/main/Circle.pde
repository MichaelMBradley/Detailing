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
  
  
  public float distanceToCenter(float xpos, float ypos) {
    // Distance from a point to the center of the node
    return dist(x, y, xpos, ypos);
  }
  public float distanceToCenter(PVector pos) {
    return PVector.dist(pv, pos);
  }
  public float distanceToCenter(Circle c) {
    return distanceToCenter(c.pv);
  }
  
  public float distanceToRadius(float xpos, float ypos) {
    // Distance from a point to the perimeter of the node
    return distanceToCenter(xpos, ypos) - r;
  }
  public float distanceToRadius(PVector pos) {
    return distanceToCenter(pos) - r;
  }
  public float distanceToRadius(Circle c) {
    return distanceToCenter(c.pv) - r;
  }
  
  public float distanceToCircle(float xpos, float ypos, float r) {
    return distanceToRadius(xpos, ypos) - r;
  }
  public float distanceToCircle(PVector pos, float r) {
    return distanceToRadius(pos) - r;
  }
  public float distanceToCircle(Circle c) {
    // Distance between the closest points of each node
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
  
  public void draw() {
    circle(x, y, r * 2);
  }
  public void draw(PVector offset) {
    circle(x + offset.x, y + offset.y, r * 2);  // p5 accepts diameter, not radius
  }
  
  public String toString() {
    return String.format("(x: %.2f, y: %.2f, r: %.2f)", x, y, r);
  }
}
