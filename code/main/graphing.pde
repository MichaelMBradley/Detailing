import java.awt.Polygon;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import org.processing.wiki.triangulate.*;

void condense(HashSet<Node> nodes) {
  /**
  Combine many graphs into one by moving the first graph
  to be touching it's closest neighbour. Keeps doing this
  until there is only one graph left.
  */
  ArrayList<HashSet<Node>> graphs = new ArrayList<HashSet<Node>>(createGraphs(nodes));
  Node closeBase, closeNode;
  PVector moveVector;
  float closeDistance, tempCD;
  while(graphs.size() > 1) {
    closeBase = new Node();
    closeNode = new Node();
    closeDistance = 1e6;  // Arbitrary large number
    // Find closest node oustide the first graph to the first graph
    for(Node n : graphs.get(0)) {  // For every node in the first graph
      for(int i = 1; i < graphs.size(); i++) {
        for(Node o : graphs.get(i)) {  // For every node not in the first graph
          tempCD = n.distanceToCircle(o);
          //println(n + " " + o + " " + tempCD);
          if(tempCD < closeDistance) {  // Save closest pair
            closeDistance = tempCD;
            closeBase = n;
            closeNode = o;
          }
        }
      }
    }
    // Move all nodes on first graph towards other closest node
    moveVector = PVector.sub(closeNode.pv, closeBase.pv);
    moveVector.setMag(moveVector.mag() - closeBase.r - closeNode.r);
    for(Node n : graphs.get(0)) {
      n.move(moveVector);
    }
    graphs = new ArrayList<HashSet<Node>>(createGraphs(nodes));
  }
}

void drawContaining(ArrayList<PVector> vertices, HashSet<Node> nodes, float xoff, float yoff) {
  int size = vertices.size();
  int[] x = new int[size];
  int[] y = new int[size];
  float[] array;
  for(int i = 0; i < size; i++) {
    array = vertices.get(i).array();
    x[i] = (int) array[0];
    y[i] = (int) array[1];
  }
  Polygon p = new Polygon(x, y, size);
  for(Node n : nodes) {
    if(p.contains(n.x, n.y)) {
      stroke(255, 0, 0);
    } else {
      stroke(0, 255, 0);
    }
    n.drw(xoff, yoff);
  }
  stroke(0);
}

HashSet<HashSet<Node>> createGraphs(HashSet<Node> nodes) {
  /**
  Takes a list of nodes, returns the set of sets of touching nodes.
  */
  HashSet<HashSet<Node>> graphs = new HashSet<HashSet<Node>>();
  for(Node n : nodes) {
    n.resetGraph();
    n.findTouching(nodes);
  }
  for(Node n : nodes) {
    for(Node t : n.touching) {
      n.graphing(t);
    }
  }
  for(Node n : nodes) {
     graphs.add(n.graph);
  }
  return graphs;
}

ArrayList<Triangle> delaunay(HashSet<Node> nodes) {
  /**
  Accepts a set of nodes, triangulates their centres.
  */
  ArrayList<PVector> vectors = new ArrayList<PVector>();
  for(Node n : nodes) {
    vectors.add(n.pv);
  }
  return Triangulate.triangulate(vectors);
}

void updateDelaunay(HashSet<Node> nodes, ArrayList<Triangle> triangles) {
  HashMap<PVector, Node> conv = new HashMap<PVector, Node>();
  HashMap<PVector, HashSet<PVector>> dict = new HashMap<PVector, HashSet<PVector>>();
  Node base, con;
  for(Node n : nodes) {
    conv.put(n.pv, n);
    dict.put(n.pv, new HashSet<PVector>());
  }
  for(Triangle tri : triangles) {
    dict.get(tri.p1).add(tri.p2);
    dict.get(tri.p1).add(tri.p3);
    dict.get(tri.p2).add(tri.p1);
    dict.get(tri.p2).add(tri.p3);
    dict.get(tri.p3).add(tri.p1);
    dict.get(tri.p3).add(tri.p2);
  }
  for(PVector pv : dict.keySet()) {
    base = conv.get(pv);
    for(PVector connect : dict.get(pv)) {
      con = conv.get(connect);
      //if(PVector.dist(pv, connect) < (base.r + con.r) * 3) {
        base.delaunay.add(con);
      //}
    }
  }
}

void kruskal(HashSet<Node> nodes) {
  ArrayList<Edge> edges = new ArrayList<Edge>();
  for(Node b : nodes) {
    for(Node t : b.delaunay) {
      edges.add(new Edge(b, t));
    }
  }
  Collections.sort(edges);
  for(Edge e : edges) {
    e.n1.addKruskal(e.n2);
  }
}
