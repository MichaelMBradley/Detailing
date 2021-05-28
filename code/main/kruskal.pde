void kruskal(HashSet<Node> nodes) {
  /**
  Creates a minimum spanning tree of the nodes.
  */
  int restrictSize = (int) sqrt(sqrt(nodes.size()));
  ArrayList<Edge> edges = new ArrayList<Edge>();
  for(Node b : nodes) {
    for(Node t : b.delaunay) {
      edges.add(new Edge(b, t));
    }
  }
  Collections.sort(edges);
  for(Edge e : edges) {
    e.n1.addKruskal(e.n2, restrictSize);
  }
}

ArrayList<Node> kruskalTraverse(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  /**
  Returns one node of each MST. Each node is
  in order around the perimeter of the shape.
  */
  ArrayList<HashMap<PVector, Node>> options;
  ArrayList<Node> traversal = new ArrayList<Node>();
  HashSet<HashSet<Node>> MSTs = new HashSet<HashSet<Node>>();
  HashMap<PVector, Node> current;
  float distance, testdist;
  PVector next = new PVector();
  for(Node n : nodes) {
    MSTs.add(n.kruskal);
  }
  options = MSTClosestNode(MSTs, vertices);
  // Finding closest edge to node
  for(int i = 0; i < vertices.size(); i++) {
    current = options.get(i);
    while(!current.isEmpty()) {
      distance = 1e6;
      for(PVector pv : current.keySet()) {
        testdist = PVector.dist(pv, vertices.get(i));
        if(testdist < distance) {
          distance = testdist;
          next = pv;
        }
      }
      traversal.add(current.get(next));
      current.remove(next);
    }
  }
  return traversal;
}

private ArrayList<HashMap<PVector, Node>> MSTClosestNode (HashSet<HashSet<Node>> MSTs, ArrayList<PVector> vertices) {
  ArrayList<HashMap<PVector, Node>> options = new ArrayList<HashMap<PVector, Node>>();
  float close, test;
  int j;
  PVector vi, vj, testpv;
  PVector touch = new PVector();
  Node closest = new Node();
  for(int _=0;_<vertices.size();_++) {
    options.add(new HashMap<PVector, Node>());
  }
  // Finding closest node to edge
  for(HashSet<Node> MST : MSTs) {
    q = 0;
    close = Float.MAX_VALUE;
    for(Node n : MST) {
      for(int i = 0; i < vertices.size(); i++) {
        j = i + 1;
        if(j == vertices.size()) {
          j = 0;
        }
        vi = vertices.get(i);
        vj = vertices.get(j);
        testpv = closestPoint2(vi, vj, n.pv);
        test = (float) Line2D.ptSegDist(vi.x, vi.y, vj.x, vj.y, n.x, n.y) - n.r;
        if(test<close) {
          touch = testpv;
          close = test;
          closest = n;
          q = i;
        }
      }
    }
    options.get(q).put(touch, closest);
  }
  return options;
}

ArrayList<Node> traverseKruskalTrees(HashSet<Node> nodes, ArrayList<PVector> vertices, boolean includeParents) {
  /**
  Visits every kruskal-esque tree in order, and traverses those trees in a relevant manner.
  */
  ArrayList<Node> traverse = new ArrayList<Node>();
  ArrayList<Node> kruskal = kruskalTraverse(nodes, vertices);
  Node edge = new Node();
  float dist;
  int j;
  int k = 0;
  int l = 0;
  Polygon shape = toPolygon(vertices);
  for(Node n : kruskal) {
    dist = 1e6;
    for(int i = 0; i < vertices.size(); i++) {
      j = i + 1 == vertices.size() ? 0 : i + 1;  // Next vertex w/ wraparound
      if(distanceToSegment(vertices.get(i), vertices.get(j), n.pv) < dist) {
        k = i;
        l = j;
      }
    }
    edge = new Node(PVector.sub(n.pv, closestPoint2(vertices.get(k), vertices.get(l), n.pv)));
    //println("\n" + edge + "\t" + PVector.sub(n.pv, edge.pv).heading());
    traverse.addAll(n.kruskalTreeTraverse(edge, shape.contains(n.x, n.y), includeParents));
  }
  return traverse;
}

float[] getArcKruskal(Node n1, Node n2) {
  ArrayList<Node> n3arr = new ArrayList<Node>();
  float[] arcinfo = new float[6];
  for(Node d : n1.delaunay) {
    if(d.delaunay.contains(n2)) {
      n3arr.add(d);
    }
  }
  for(Node n3 : n3arr) {
    arcinfo = getArc(n1, n2, n3);
    if(arcinfo[5] - arcinfo[4] < PI) {
      break;
    }
  }
  return arcinfo;
}
