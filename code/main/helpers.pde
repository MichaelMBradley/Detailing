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
