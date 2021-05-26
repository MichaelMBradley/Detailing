void condense(HashSet<Node> nodes) {
  /**
  Combine many graphs into one by moving the first graph
  to be touching it's closest neighbour. Keeps doing this
  until there is only one graph left.
  */
  ArrayList<HashSet<Node>> graphs = createTouchingGraphs(nodes);
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
    graphs = createTouchingGraphs(nodes);
  }
}

ArrayList<HashSet<Node>> createTouchingGraphs(HashSet<Node> nodes) {
  /**
  Takes a list of nodes, returns the set of sets of touching nodes.
  */
  ArrayList<HashSet<Node>> graphs = new ArrayList<HashSet<Node>>();
  HashSet<Node> used = new HashSet<Node>();
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
    if(!used.contains(n)) {
      graphs.add(n.graph);
      used.addAll(n.graph);
    }
  }
  return graphs;
}

ArrayList<float[]> surroundingArcs(ArrayList<Node> nodes) {
  ArrayList<ArrayList<Node>> trees = new ArrayList<ArrayList<Node>>();
  ArrayList<float[]> arcs = new ArrayList<float[]>();
  trees.add(new ArrayList<Node>());
  trees.get(0).add(new Node());
  for(Node n : nodes) {
    if(!trees.get(trees.size() - 1).get(0).kruskal.contains(n)) {
      trees.add(new ArrayList<Node>());
    }
    trees.get(trees.size() - 1).add(n);
  }
  trees.get(0).remove(0);
  for(int i = 0; i < trees.size(); i++) {
    if(i == trees.size() - 1) {
      arcs.addAll(surroundingArcsTree(trees.get(i)));
    } else {
      arcs.addAll(surroundingArcsTree(trees.get(i), trees.get(i+1).get(0)));
    }
  }
  return arcs;
}

ArrayList<float[]> surroundingArcsTree(ArrayList<Node> nodes, Node next) {
  ArrayList<Node> n = (ArrayList<Node>) nodes.clone();
  n.add(next);
  return surroundingArcsTree(n);
}

ArrayList<float[]> surroundingArcsTree(ArrayList<Node> nodes) {
  if(nodes.size() == 0) {
    return new ArrayList<float[]>();
  }
  ArrayList<float[]> arcs = new ArrayList<float[]>();
  ArrayList<Node> arcNodes = new ArrayList<Node>();
  ArrayList<Integer> tri = new ArrayList<Integer>();
  Node ni, nj, nc, n;
  float[] se;
  int choose;
  boolean in;
  //nodes.add(0, nodes.get(0));
  //nodes.add(0, nodes.get(0));
  //nodes.add(nodes.size(), nodes.get(nodes.size() - 1));
  //nodes.add(nodes.size(), nodes.get(nodes.size() - 1));
  for(int i = 1; i < nodes.size() - 1; i++) {
    ni = nodes.get(i);
    nj = nodes.get(i == 0 ? nodes.size() - 1 : i - 1);
    // Choosing which side of the circle to put the arc on based on the direction
    if(ni.x == nj.x) {
      choose = ni.y > nj.y ? 0 : 1;
    } else if(ni.y == nj.y) {
      choose = ni.x > nj.x ? 1 : 0;
    } else if(nj.y > ni.y) {
      choose = 1;
    } else {
      choose = 0;
    }
    nc = getAdjacent(ni, nj)[choose];
    arcNodes.add(nc);
    arcNodes.add(ni);
  }
  for(int i = 3; i < arcNodes.size() - 3; i+=2) {
    if(PVector.angleBetween(PVector.sub(arcNodes.get(i+2).pv, arcNodes.get(i).pv), PVector.sub(arcNodes.get(i-2).pv, arcNodes.get(i).pv)) < HALF_PI * 1.25f && arcNodes.get(i+2) != arcNodes.get(i-2)) {
      arcNodes.set(i, triCircleAdjacent(arcNodes.get(i-2), arcNodes.get(i+2), arcNodes.get(i))[1]);
      arcNodes.remove(i+1);
      arcNodes.remove(i-1);
      tri.add(i);
    }
  }
  for(int i = 0; i < arcNodes.size(); i++) {
    n = arcNodes.get(i);
    in = (i % 2 == 0);
    if(tri.contains(i+1)) {
      in = true;
    }
    se = order(arcNodes.get(i == 0 ? arcNodes.size() - 2 : i - 1).pv, n.pv, arcNodes.get(i >= arcNodes.size() - 2 ? arcNodes.size() - i : i + 1).pv, in);
    arcs.add(new float[] {n.x, n.y, n.r, n.r, se[0], se[1]});
  }
  return arcs;
}

private float[] order(PVector pre, PVector curr, PVector next, boolean crossing) {
  float start = PVector.sub(pre, curr).heading();
  float end = PVector.sub(next, curr).heading();
  if(PVector.angleBetween(PVector.sub(pre, curr), PVector.sub(next, curr)) < PI == crossing) {
    float temp = start;
    start = end;
    end = temp;
  }
  return bindStart(start, end);
}

private float[] bindStart(float start, float end) {
  while(start > end) {
    start -= TWO_PI;
  }
  while(start < end - TWO_PI) {
    start += TWO_PI;
  }
  while(end < 0) {
    start += TWO_PI;
    end += TWO_PI;
  }
  return new float[] {start, end};
}
