import java.util.HashSet;

public class Node {
  public HashSet<Node> touching, graph;
  public float x, y, r;
  public PVector pv;
  
  public Node(float xpos, float ypos, float rad) {
    x = xpos;
    y = ypos;
    r = rad;
    pv = new PVector(x, y);
    resetGraph();
  }
  
  public float distanceToCenter(float xpos, float ypos) {
    // Distance from a point to the center of the node
    return dist(x, y, xpos, ypos);
  }
  public float distanceToRadius(float xpos, float ypos) {
    // Distance from a point to the perimeter of the node
    return distanceToCenter(xpos, ypos) - r;
  }
  public float distanceToCircle(Node c) {
    // Distance between the closest points of each node
    return distanceToRadius(c.x, c.y) - c.r;
  }
  public void findTouching(HashSet<Node> nodes) {
    for(Node n : nodes) {
      if(this != n && distanceToCircle(n) <= 1) {  // Allow for rounding errors TODO: See if necessary
        touching.add(n);
      }
    }
  }
  public void graphing(Node n) {
    if(!graph.contains(n)) {
      graph.add(n);
      for(Node t : touching) {
        t.graphing(n);
      }
    }
  }
  public void move(PVector direction) {
    pv.add(direction);
    x += direction.x;
    y += direction.y;
  }
  public void resetGraph() {
    touching = new HashSet<Node>();
    graph = new HashSet<Node>();
    graph.add(this);
  }
  
  public void drw(float xoff, float yoff) {
    circle(x + xoff, y + yoff, r * 2);
  }
  public String toString() {
    return String.format("Node at (%.2f, %.2f) with radius %.2f", x, y, r);
  }
}
