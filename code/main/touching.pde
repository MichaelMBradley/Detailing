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
  for(ArrayList<Node> tree : trees) {
    arcs.addAll(surroundingArcsTree(tree));
  }
  return arcs;
}

ArrayList<float[]> surroundingArcsTree(ArrayList<Node> nodes) {
  ArrayList<float[]> arcs = new ArrayList<float[]>();
  Node ni, nj, nc;
  float[] ah, ai, aj, se, arc;
  int choose, index;
  float start, end;
  for(int i = 0; i < nodes.size(); i++) {
    ni = nodes.get(i);
    nj = nodes.get(i == 0 ? nodes.size() - 1 : i - 1);
    // Choosing which side of the circle to put the arc on based on the direction
    if(ni.x == nj.x) {
      choose = 0;
    } else if(ni.y == nj.y) {
      choose = 1;
    } else if(nj.y > ni.y) {
      choose = 1;
    } else {
      choose = 0;
    }
    nc = getAdjacent(ni, nj)[choose];
    arcs.add(new float[] {nc.x, nc.y, nc.r, nc.r, 0, TWO_PI});
    arcs.add(new float[] {ni.x, ni.y, ni.r, ni.r, 0, TWO_PI});
  }
  for(int i = 0; i < arcs.size(); i++) {
    ah = arcs.get(i == arcs.size() - 1 ? 0 : i + 1);
    ai = arcs.get(i);
    aj = arcs.get(i == 0 ? arcs.size() - 1 : i - 1);
    if(ai[4] != -1f) {
      // Start / stop for the arc
      se = order(new PVector(aj[0], aj[1]), new PVector(ai[0], ai[1]), new PVector(ah[0], ah[1]), true, i);
      start = se[0]; end = se[1];
      if(abs(start - end) > (3f / 4f) * TWO_PI) {
        index = (i - 1) / 2;
        index = index < 0 ? nodes.size() - index : index;
        ni = triCircleAdjacent(nodes.get(index - 1), nodes.get(index), nodes.get(index + 1))[1];
        arcs.set(i, new float[] {ni.x, ni.y, ni.r, ni.r, 0, TWO_PI});
        se = order(nodes.get(index - 1).pv, ni.pv, nodes.get(index + 1).pv, false, i);
        start = se[0]; end = se[1];
        ah[4] = -1f;
        ah[5] = -1f;
        aj[4] = -1f;
        aj[5] = -1f;
      }
      arcs.get(i)[4] = start;
      arcs.get(i)[5] = end;
    }
  }
  for(int i = 2; i < arcs.size() - 2; i++) {
    if(arcs.get(i-1)[5] == -1 && arcs.get(i+1)[5] == -1) {
      arc = arcs.get(i-2);
      arc[5] = PVector.sub(new PVector(arcs.get(i)[0], arcs.get(i)[1]), new PVector(arc[0], arc[1])).heading();
      arc[4] = bindStart(arc[4], arc[5]);
      arc = arcs.get(i+2);
      arc[4] = PVector.sub(new PVector(arcs.get(i)[0], arcs.get(i)[1]), new PVector(arc[0], arc[1])).heading();
      arc[4] = bindStart(arc[4], arc[5]);
    }
  }
  return arcs;
}

private float[] order(PVector pre, PVector curr, PVector next, boolean clockwise, int i) {
  float start = PVector.sub(pre, curr).heading();
  float end = PVector.sub(next, curr).heading();
  if(clockwise == (i % 2 == 0)) {
    float temp = start;
    start = end;
    end = temp;
  }
  start = bindStart(start, end);
  return new float[] {start, end};
}

private float bindStart(float start, float end) {
  while(start > end) {
    start -= TWO_PI;
  }
  while(start < end - TWO_PI) {
    start += TWO_PI;
  }
  return start;
}
