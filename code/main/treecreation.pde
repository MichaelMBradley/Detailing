void altTreeCreate(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  /**
  Creates trees by starting at the polyline and randomly adding close unclaimed nodes to itself.
  */
  HashSet<Node> touching = getNodesTouchingPolyline(nodes, vertices);
  HashSet<Node> valid;
  Node use;
  boolean add = true;
  // While there are still more nodes to add
  while(add) {
    add = false;
    for(Node n : touching) {
      valid = new HashSet<Node>();
      for(Node k : n.kruskal) {
        for(Node d : k.delaunay) {
          if(d.kruskal.size() == 0 && !touching.contains(d)) {
            // Add node connected by triangulation if it is unclaimed and is not touching the polyline
            valid.add(d);
          }
        }
      }
      if(valid.size() > 0) {
        add = true;
        use = randomFromHashSet(valid);
        // Add random available node, if one is available
        for(Node k : n.kruskal) {
          if(k.delaunay.contains(use)) {
            k.addKruskal(use, -1);
            break;
          }
        }
      }
    }
  }
}

void kruskal(HashSet<Node> nodes) {
  kruskal(nodes, (int) sqrt(sqrt(nodes.size())));
}

void kruskal(HashSet<Node> nodes, int restrictSize) {
  /**
  Creates a minimum spanning tree of the nodes.
  */
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

void kruskalWithin(HashSet<Node> nodes, int restrictSize) {
  /**
  Creates a minimum spanning tree of the nodes.
  */
  ArrayList<Edge> edges = new ArrayList<Edge>();
  for(Node b : nodes) {
    for(Node t : b.delaunay) {
      if(nodes.contains(t)) {
        edges.add(new Edge(b, t));
      }
    }
  }
  Collections.sort(edges);
  for(Edge e : edges) {
    e.n1.addKruskal(e.n2, restrictSize);
  }
}

void treeNearest(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  /**
  Creates trees based on the closest node touching the polyline to each node available.
  */
  HashSet<Node> touching = getNodesTouchingPolyline(nodes, vertices);;
  HashMap<Node, HashSet<Node>> groups = new HashMap<Node, HashSet<Node>>();
  float dist, testdist;
  Node close = new Node();
  for(Node t : touching) {
    groups.put(t, new HashSet<Node>());
    groups.get(t).add(t);
  }
  for(Node n : nodes) {
    if(!touching.contains(n)) {
      dist = Float.MAX_VALUE;
      for(Node t : touching) {
        testdist = n.distanceToCircle(t);
        if(testdist < dist) {
          dist = testdist;
          close = t;
        }
      }
      groups.get(close).add(n);
    }
  }
  for(HashSet<Node> g : groups.values()) {
    //println(g);
    kruskalWithin(g, -1);
    //break;
  }
  //println(groups);
}

private HashSet<Node> getNodesTouchingPolyline(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  /**
  Helper function that returns the set of nodes intersected by a segment of the given polyline.
  */
  HashSet<Node> touching = new HashSet<Node>();
  for(Node n : nodes) {
    for(int i = 0; i < vertices.size(); i++) {
      if(distanceToSegment(vertices.get(i), vertices.get(i + 1 == vertices.size() ? 0 : i + 1), n.pv) < n.r) {
        touching.add(n);
        n.kruskal.add(n);
        break;
      }
    }
  }
  return touching;
}
