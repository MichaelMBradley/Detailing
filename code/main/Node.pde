import java.util.HashSet;

public class Node {
  public HashSet<Node> delaunay, graph, touching, kruskal, kruskalAdjacent;
  public float x, y, r;
  public PVector pv;
  
  public Node(float xpos, float ypos, float rad) {
    x = xpos;
    y = ypos;
    r = rad;
    pv = new PVector(x, y);
    kruskal = new HashSet<Node>();
    kruskalAdjacent = new HashSet<Node>();
    resetGraph();
  }
  public Node() {
    x = 0;
    y = 0;
    r = 0;
    pv = new PVector(x, y);
    kruskal = new HashSet<Node>();
    kruskalAdjacent = new HashSet<Node>();
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
      if(this != n && distanceToCircle(n) <= 1) {  // Allow for rounding errors
        touching.add(n);
      }
    }
  }
  public void graphing(Node n) {
    /**
    Tells every node it's touching to recursively run this code,
    but only if it does not already have this node in it's graph
    (prevents and infinite recursive loop).
    */
    if(!graph.contains(n)) {
      graph.add(n);
      for(Node t : touching) {
        t.graphing(n);
      }
    }
  }
  public void addKruskal(Node n) {
    if(kruskal.size() < 3 && !kruskal.contains(n)) {
      ArrayList<Node> og = new ArrayList<Node>(kruskal);
      kruskal.addAll(n.kruskal);
      kruskal.add(n);
      for(Node k : og) {
        k.kruskal.addAll(kruskal);
      }
      kruskalAdjacent.add(n);
      n.addKruskal(this);
    }
  }
  public void move(PVector direction) {
    pv.add(direction);
    x += direction.x;
    y += direction.y;
  }
  public void resetGraph() {
    /**
    Attempts to simply update the existing graph fail,
    so when nodes have been moved this should be
    called to help standardize graphs.
    */
    delaunay = new HashSet<Node>();
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
