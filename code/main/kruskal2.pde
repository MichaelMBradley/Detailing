ArrayList<Node> traverseKruskalTrees2(HashSet<Node> nodes, ArrayList<PVector> vertices, boolean includeParents) {
  /**
  Visits every kruskal-esque tree in order, and traverses those trees in a relevant manner.
  */
  ArrayList<Node> traverse = new ArrayList<Node>();
  ArrayList<Node> order = new ArrayList<Node>();
  ArrayList<Node> kruskal = kruskalRecursive(nodes, vertices);//kruskalTraverse(nodes, vertices);//
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
  traverse = traverseKruskalTrees(base, vertices, true);
  for(i = 0; i < traverse.size(); i++) {
    traverse.set(i, conv.get(traverse.get(i).pv));
  }
  return traverse;
}

HashSet<HashSet<Node>> getMSTs(HashSet<Node> nodes) {
  HashSet<HashSet<Node>> MSTs = new HashSet<HashSet<Node>>();
  for(Node n : nodes) {
    MSTs.add(n.kruskal);
  }
  return MSTs;
}
