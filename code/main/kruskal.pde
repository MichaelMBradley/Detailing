void kruskal(HashSet<Node> nodes) {
  /**
  Creates a minimum spanning tree of the nodes.
  */
  int restrictSize = (int) nodes.size() / 100;
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
  
  TODO: Break up into multiple functions?
  */
  ArrayList<HashMap<PVector, Node>> options = new ArrayList<HashMap<PVector, Node>>();
  ArrayList<Node> traversal = new ArrayList<Node>();
  HashSet<HashSet<Node>> MSTs = new HashSet<HashSet<Node>>();
  float test, close, distance, testdist, mult;
  int j, q;
  Node closest = new Node();
  PVector testpv, vi, vj;
  PVector next = new PVector();
  PVector touch = new PVector();
  for(Node n : nodes) {
    MSTs.add(n.kruskal);
  }
  for(int _=0;_<vertices.size();_++) {
    options.add(new HashMap<PVector, Node>());
  }
  // Finding closest node to edge
  for(HashSet<Node> MST : MSTs) {
    q = 0;
    close = 1e6;  // Arbitrary big number
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
  // Finding closest edge to node
  for(int i = 0; i < vertices.size(); i++) {
    j = i + 1;
    if(j == vertices.size()) {
      j = 0;
    }
    vi = vertices.get(i);
    vj = vertices.get(j);
    HashMap<PVector, Node> current = options.get(i);
    while(!current.isEmpty()) {
      distance = 1e6;
      for(PVector pv : current.keySet()) {
        if(PVector.angleBetween(PVector.sub(vj, vi), PVector.sub(pv, vi)) < 1) {
          mult = 1;
        } else {
          mult = -1;
        }
        testdist = PVector.dist(pv, vi) * mult;
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

ArrayList<Node> traverseKruskalTrees(HashSet<Node> nodes, HashSet<Node> exterior, ArrayList<PVector> vertices, boolean includeParents) {
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
    traverse.addAll(n.kruskalTreeTraverse(edge, exterior.contains(n), includeParents));
  }
  return traverse;
}

ArrayList<Node> traverseKruskalTreesSmart(HashSet<Node> nodes, HashSet<Node> exterior, ArrayList<PVector> vertices, boolean includeParents) {
  /**
  Visits every kruskal-esque tree in order, and traverses those trees in a relevant manner.
  */
  ArrayList<Node> traverse = new ArrayList<Node>();
  ArrayList<Node> order = new ArrayList<Node>();
  ArrayList<Node> kruskal = kruskalTraverse(nodes, vertices);
  Node edge = new Node();
  Node close = new Node();
  float dist;
  for(int i = 0; i < kruskal.size(); i++) {
    dist = 1e6;
    for(Node n : kruskal.get(i).kruskal) {
      for(Node m : kruskal.get(i + 1 == kruskal.size() ? 0 : i + 1).kruskal) {
        if(m.distanceToCircle(n) < dist) {
          close = m;
        }
      }
    }
    order.add(close);
  }
  for(int i = 0; i < order.size(); i++) {
    traverse.addAll(order.get(i).kruskalTreeTraverse(edge, exterior.contains(order.get(i)), includeParents, order.get(i + 1 == order.size() ? 0 : i + 1)));
  }
  //print(order);
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
