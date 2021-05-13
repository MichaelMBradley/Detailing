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
  public Node(PVector PVec) {
    x = PVec.x;
    y = PVec.y;
    r = 0;
    pv = PVec;
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
    /**
    If the spanning tree that this node is a part of doesn't
    contain Node n, combine the trees.
    */
    if(!kruskal.contains(n) && kruskal.size() < 3) {
      ArrayList<Node> og = new ArrayList<Node>(kruskal);
      kruskal.addAll(n.kruskal);
      kruskal.add(n);
      for(Node k : og) {
        k.kruskal.addAll(kruskal);
      }
      kruskalAdjacent.add(n);
      n.makeAddKruskal(this);
    }
  }
  private void makeAddKruskal(Node n) {
    /**
    The maximum size limit of a tree in the above code can cause an issue
    where tree1 adds tree2, but tree2 doesn't add tree1. This gets around that.
    */
    kruskal.addAll(n.kruskal);
    kruskal.add(n);
    ArrayList<Node> og = new ArrayList<Node>(kruskal);
    for(Node k : og) {
      k.kruskal.addAll(kruskal);
    }
    kruskalAdjacent.add(n);
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
  ArrayList<Node> getAllKruskal(boolean clockwise) {
    return getAllKruskal(new Node(), clockwise);
  }
  ArrayList<Node> getAllKruskal(Node call, boolean clockwise) {
    ArrayList<Node> below = new ArrayList<Node>();
    ArrayList<Node> orderedChildren = new ArrayList<Node>(kruskalAdjacent);
    HashMap<Node, Float> headings = new HashMap<Node, Float>();
    float heading;
    if(orderedChildren.contains(call)) {
      orderedChildren.remove(call);
    }
    below.add(this);
    if(kruskalAdjacent.size() == 0) {
      return below;
    } else if(kruskalAdjacent.size() == 1 && kruskalAdjacent.contains(call)) {
      return below;
    }
    for(Node n : orderedChildren) {
      heading = PVector.sub(n.pv, this.pv).heading();
      if(heading > call.pv.heading() == clockwise) {
        if(clockwise) {
          heading -= TWO_PI;
        } else {
          heading += TWO_PI;
        }
      }
      headings.put(n, heading);
    }
    boolean swap = true;
    Node temp;
    while(swap) {
      swap = false;
      for(int i = 0; i < orderedChildren.size() - 1; i++) {
        if(headings.get(orderedChildren.get(i)) > headings.get(orderedChildren.get(i+1)) == clockwise) {
          swap = true;
          temp = orderedChildren.get(i);
          orderedChildren.set(i, orderedChildren.get(i+1));
          orderedChildren.set(i+1, temp);
        }
      }
    }
    for(Node n : orderedChildren) {
      for(Node k : n.getAllKruskal(this, clockwise)) {
        below.add(k);
      }
      below.add(this);
    }
    return below;
  }
  
  public void drw(float xoff, float yoff) {
    circle(x + xoff, y + yoff, r * 2);
  }
  public String toString() {
    return String.format("[(x: %.2f, y: %.2f) r: %.2f]", x, y, r);
  }
}
