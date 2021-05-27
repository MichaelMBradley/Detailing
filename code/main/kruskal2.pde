ArrayList<Node> kruskalTraverse2(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  /**
  Returns one node of each MST. Each node is
  in order around the perimeter of the shape.
  */
  ArrayList<HashMap<PVector, Node>> options;
  ArrayList<Node> traversal = new ArrayList<Node>();
  HashSet<HashSet<Node>> MSTs = new HashSet<HashSet<Node>>();
  HashMap<PVector, Node> current;
  float distance, testdist;
  int j;
  PVector vi, vj;
  PVector next = new PVector();
  for(Node n : nodes) {
    MSTs.add(n.kruskal);
  }
  options = MSTClosestNode2(MSTs, vertices);
  // Finding closest edge to node
  for(int i = 0; i < vertices.size(); i++) {
    j = i + 1;
    if(j == vertices.size()) {
      j = 0;
    }
    vi = vertices.get(i);
    vj = vertices.get(j);
    current = options.get(i);
    while(!current.isEmpty()) {
      distance = 1e6;
      for(PVector pv : current.keySet()) {
        testdist = PVector.dist(pv, vi);
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

private ArrayList<HashMap<PVector, Node>> MSTClosestNode2 (HashSet<HashSet<Node>> MSTs, ArrayList<PVector> vertices) {
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
    close = Float.MAX_VALUE;  // Arbitrary big number
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

ArrayList<Node> traverseKruskalTrees2(HashSet<Node> nodes, ArrayList<PVector> vertices, boolean includeParents) {
  /**
  Visits every kruskal-esque tree in order, and traverses those trees in a relevant manner.
  */
  ArrayList<Node> traverse = new ArrayList<Node>();
  ArrayList<Node> order = new ArrayList<Node>();
  ArrayList<Node> kruskal = kruskalRecursive(nodes, vertices);//Traverse2(nodes, vertices);
  //return kruskal;
  Node edge = new Node();
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
    traverse.addAll(order.get(i).kruskalTreeTraverse(edge, shape.contains(order.get(i).x, order.get(i).y), includeParents, order.get(i + 1 == order.size() ? 0 : i + 1)));
  }
  //println(order);
  return traverse;
}

ArrayList<Node> kruskalRecursive(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  HashSet<Node> base = new HashSet<Node>();
  HashMap<PVector, Node> conv = new HashMap<PVector, Node>();
  HashSet<HashSet<Node>> MSTs = new HashSet<HashSet<Node>>();
  ArrayList<Node> traverse;
  float t;
  int i;
  Node temp = new Node();
  for(Node n : nodes) {
    MSTs.add(n.kruskal);
  }
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
  traverse = delaunayTraverse(base, vertices);
  //kruskal(base);
  //traverse = traverseKruskalTrees(base, vertices, false);
  for(i = 0; i < traverse.size(); i++) {
    traverse.set(i, conv.get(traverse.get(i).pv));
  }
  return traverse;
}
