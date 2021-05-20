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
    addKruskal(n, -1);
  }
  
  public void addKruskal(Node n, int restrictSize) {
    /**
    If the spanning tree that this node is a part of doesn't
    contain Node n, combine the trees.
    */
    if(!kruskal.contains(n) && (kruskal.size() < restrictSize || restrictSize == -1)) {
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
  
  ArrayList<Node> kruskalTreeTraverse(Node call, boolean clockwise) {
    /**
    Recursive function that returns a list of all nodes below it
    in it's kruskal-esque tree, in cw or ccw order.
    This method visits every parent node in between leaves.
    */
    ArrayList<Node> below = new ArrayList<Node>();
    ArrayList<Node> orderedChildren = new ArrayList<Node>(kruskalAdjacent);
    if(orderedChildren.contains(call)) {
      orderedChildren.remove(call);
    }
    below.add(this);
    if(kruskalAdjacent.size() == 0) {
      return below;
    } else if(kruskalAdjacent.size() == 1 && kruskalAdjacent.contains(call)) {
      return below;
    }
    orderedChildren = sortRelativeHeadings(orderedChildren, call, clockwise);
    for(Node n : orderedChildren) {
      for(Node k : n.kruskalTreeTraverse(this, clockwise)) {
        below.add(k);
      }
      below.add(this);
    }
    return below;
  }
  
  private ArrayList<Node> sortRelativeHeadings(ArrayList<Node> children, Node call, boolean clockwise) {
    /**
    Returns a mapping of children->heading, offset such that the parent
    is the largest/smallest value (depending in clockwise) so that the
    nodes may be traversed in order.
    */
    HashMap<Float, Node> headings = new HashMap<Float, Node>();
    ArrayList<Float> sortedHeadings = new ArrayList<Float>();
    ArrayList<Node> sortedNodes = new ArrayList<Node>();
    float heading;
    for(Node n : children) {
      heading = PVector.sub(n.pv, this.pv).heading();
      if(heading > PVector.sub(call.pv, this.pv).heading()) {
        if(clockwise) {
          heading -= TWO_PI;
        }
      } else {
        if(!clockwise) {
          heading += TWO_PI;
        }
      }
      headings.put(heading, n);
      sortedHeadings.add(heading);
    }
    Collections.sort(sortedHeadings);
    if(!clockwise) {
      Collections.reverse(sortedHeadings);
    }
    for(Float h : sortedHeadings) {
      sortedNodes.add(headings.get(h));
    }
    return sortedNodes;
  }
  
  
  public void move(PVector direction) {
    pv.add(direction);
    x += direction.x;
    y += direction.y;
  }
  
  public void draw() {
    this.draw(new PVector());
  }
  public void draw(PVector offset) {
    circle(x + offset.x, y + offset.y, r * 2);  // p5 accepts diameter, not radius
  }
  
  public String toString() {
    return String.format("(x: %.2f, y: %.2f r: %.2f)", x, y, r);
  }
}
