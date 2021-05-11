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
