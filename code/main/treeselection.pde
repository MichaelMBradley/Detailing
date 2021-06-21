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
  //println(traversal);
  return traversal;
}

private ArrayList<HashMap<PVector, Node>> MSTClosestNode (HashSet<HashSet<Node>> MSTs, ArrayList<PVector> vertices) {
  /**
  Returns a list of pairs of points, where each HashMap on the list corresponds to a line, and each entry in the hashmap
  represents the closest point on that line to a minimum spanning tree.
  */
  ArrayList<HashMap<PVector, Node>> options = new ArrayList<HashMap<PVector, Node>>();
  float close, test;
  int j;
  PVector vi, vj, testpv;
  PVector touch = new PVector();
  Node closest = new Node();
  for(int m = 0; m < vertices.size(); m++) {
    options.add(new HashMap<PVector, Node>());
  }
  // Finding closest node to edge
  for(HashSet<Node> MST : MSTs) {
    q = 0;
    close = Float.MAX_VALUE;
    for(Node n : MST) {
      for(int i = 0; i < vertices.size(); i++) {
        j = i + 1 == vertices.size() ? 0 : i + 1;
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
    if(close == Float.MAX_VALUE) {
      println("broke");
    }
    options.get(q).put(touch, closest);
  }
  return options;
}

ArrayList<Node> traverseTreesBase(HashSet<Node> nodes, ArrayList<PVector> vertices, boolean includeParents) {
  /**
  Visits every kruskal-esque tree in order, and traverses those trees in a relevant manner.
  */
  ArrayList<Node> traverse = new ArrayList<Node>();
  ArrayList<Node> kruskal = kruskalTraverse(nodes, vertices);
  Node edge = new Node();
  PVector j, k = new PVector(), l = new PVector();
  float dist;
  Polygon shape = toPolygon(vertices);
  for(Node n : kruskal) {
    dist = Float.MAX_VALUE;
    for(int i = 0; i < vertices.size(); i++) {
      j = vertices.get(i + 1 == vertices.size() ? 0 : i + 1);  // Next vertex w/ wraparound
      if(distanceToSegment(vertices.get(i), j, n.pv) < dist) {
        k = vertices.get(i);
        l = j;
      }
    }
    edge = new Node(PVector.sub(n.pv, closestPoint2(k, l, n.pv)));
    //println("\n" + edge + "\t" + PVector.sub(n.pv, edge.pv).heading());
    traverse.addAll(n.kruskalTreeTraverse(edge, !shape.contains(n.x, n.y), includeParents));
  }
  return traverse;
}

ArrayList<Node> traverseTreesSkip(HashSet<Node> nodes, ArrayList<PVector> vertices, boolean includeParents) {
  /**
  Visits every kruskal-esque tree in order, and traverses those trees in a relevant manner.
  This method starts and stops on the closest nodes to the adjacent trees.
  */
  ArrayList<Node> traverse = new ArrayList<Node>();
  ArrayList<Node> order = new ArrayList<Node>();
  ArrayList<Node> kruskal = kruskalTraverse(nodes, vertices);//kruskalRecursive(nodes, vertices);//
  Node empty = new Node();
  Node next = new Node();
  Node end = new Node();
  float dist;
  Polygon shape = toPolygon(vertices);
  for(int i = 0; i < kruskal.size(); i++) {
    dist = Float.MAX_VALUE;
    for(Node n : kruskal.get(i).kruskal) {
      for(Node m : kruskal.get(i + 1 == kruskal.size() ? 0 : i + 1).kruskal) {
        if(m.distanceToCircle(n) < dist) {
          next = m;
          end = n;
          dist = m.distanceToCircle(n);
        }
      }
    }
    order.add(end);
    order.add(next);
  }
  for(int i = 1; i < order.size(); i+=2) {
    traverse.addAll(order.get(i).kruskalTreeTraverse(empty, shape.contains(order.get(i).x, order.get(i).y), includeParents, order.get(i + 1 == order.size() ? 0 : i + 1)));
  }
  //println(order);
  return traverse;
}

ArrayList<Node> kruskalRecursive(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  /**
  Traverses the minimum spanning trees by traversing the minimum spanning tree of the minimum spanning trees.
  */
  HashSet<Node> base = new HashSet<Node>();
  HashMap<PVector, Node> conv = new HashMap<PVector, Node>();
  HashSet<HashSet<Node>> MSTs = getMSTs(nodes);
  ArrayList<Node> traverse;
  float t;
  int i;
  Node temp = new Node();
  for(HashSet<Node> MST : MSTs) {
    t = random(0, MST.size());
    i = 0;
    for(Node n : MST) {
      temp = n; //random element
      i++;
      if(i>=t) {
        break;
      }
    }
    conv.put(temp.pv, temp);
    base.add(new Node(temp.x, temp.y, temp.r));
  }
  updateDelaunay(base);
  kruskal(base);
  traverse = traverseTreesBase(base, vertices, true);
  for(i = 0; i < traverse.size(); i++) {
    traverse.set(i, conv.get(traverse.get(i).pv));
  }
  return traverse;
}
