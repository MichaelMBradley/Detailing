void calcOffset() {
  /**
  Calculates the amount all geometry should be offset to center it.
  */
  PVector[] ends = extremes(vertices);
  if(noDraw) {
    offset = new PVector();
  } else {
    offset = new PVector((w - (ends[1].x - ends[0].x)) / 2, (h - (ends[1].y - ends[0].y)) / 2);
  }
}

void drawLine(PVector p1, PVector p2) {
  line(p1.x, p1.y, p2.x, p2.y);
}

HashSet<HashSet<Node>> getMSTs(HashSet<Node> nodes) {
  /**
  Returns a HashSet of the minimum spanning trees.
  */
  HashSet<HashSet<Node>> MSTs = new HashSet<HashSet<Node>>();
  for(Node n : nodes) {
    MSTs.add(n.kruskal);
  }
  return MSTs;
}

Node randomFromHashSet(HashSet<Node> h) {
  float r = random(h.size());
  int i = 0;
  Node node = new Node();
  for(Node n : h) {
    node = n;
    if(i > r) {
      break;
    }
    i++;
  }
  return node;
}
