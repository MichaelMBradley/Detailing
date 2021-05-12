Node closestNode(HashSet<Node> nodes, PVector vertex) {
  Node close = new Node();
  float tempdist;
  float distance = 1e6;
  for(Node n : nodes) {
    tempdist = PVector.dist(vertex, n.pv);
    if(tempdist < distance) {
      distance = tempdist;
      close = n;
    }
  }
  return close;
}

PVector closestPoint(PVector p1, PVector p2, Node node) {
  PVector p0 = node.pv;
  if(p1.x == p2.x) {
    return new PVector(p1.x, p0.y);
  }
  if(p1.y == p2.y) {
    return new PVector(p0.x, p1.y);
  }
  float m = (p2.y-p1.y)/(p2.x-p1.x);
  float mi = -1/m;
  float x = (p1.x * m - p0.x * mi + p0.y - p1.y)/(m - mi);
  float y = m * (x - p1.x) + p1.y;
  return new PVector(x, y);
}

HashSet<Node> containing(ArrayList<PVector> vertices, HashSet<Node> nodes, boolean inside) {
  /**
  Accepts a set of nodes, returns the subset
  that is either inside or outside the shape.
  */
  HashSet<Node> side = new HashSet<Node>();
  Polygon shape = toPolygon(vertices);
  for(Node n : nodes) {
    if(shape.contains(n.x, n.y) == inside) {
      side.add(n);
    }
  }
  return side;
}

ArrayList<Node> delaunayTraverse(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  ArrayList<Node> traversal = new ArrayList<Node>();
  float closest, angle;
  Node goal;
  Node next = new Node();
  Node current = closestNode(nodes, vertices.get(vertices.size() - 1));
  for(int i = 0; i < vertices.size(); i++) {
    goal = closestNode(nodes, vertices.get(i));
    while(current != goal) {
      if(current.delaunay.contains(goal)) {
        next = goal;
      } else {
        closest = PI * 3;
        for(Node d : current.delaunay) {
          angle = PVector.angleBetween(PVector.sub(d.pv, current.pv), PVector.sub(goal.pv, current.pv));
          if(angle < closest && !traversal.contains(d)) {
            closest = angle;
            next = d;
          }
        }
        if(closest == PI * 3) {
          println("skip");
          next = goal;
        }
      }
      traversal.add(current);
      current = next;
    }
  }
  return traversal;
}

ArrayList<Node> kruskalTraverse(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  /**
  Returns one node of each MST. Each node is
  in order around the perimeter of the shape.
  */
  HashMap<PVector, Node> options = new HashMap<PVector, Node>();
  ArrayList<Node> traversal = new ArrayList<Node>();
  HashSet<HashSet<Node>> MSTs = new HashSet<HashSet<Node>>();
  float test, close;
  int j;
  Node closest = new Node();
  PVector testpv;
  PVector touch = new PVector();
  for(Node n : nodes) {
    MSTs.add(n.kruskal);
  }
  for(HashSet<Node> MST : MSTs) {
    close = 1e6;  // Arbitrary big number
    for(Node n : MST) {
      for(int i = 0; i < vertices.size(); i++) {
        j = i + 1;
        if(j == vertices.size()) {
          j = 0;
        }
        testpv = closestPoint(vertices.get(i), vertices.get(j), n);
        test = PVector.dist(testpv, n.pv);
        if(test<close) {
          touch = testpv;
          close = test;
          closest = n;
        }
      }
    }
    options.put(touch, closest);
  }
  //Iterate through edges, iterate through PVector keys, find those ons same line, order by distance in direction of next vertex
  return traversal;
}

boolean inLine(PVector p1, PVector p2, PVector test) {
  return (p2.y-p1.y)/(p2.x-p1.x)==(test.y-p1.y)/(test.x-p1.x);
}
