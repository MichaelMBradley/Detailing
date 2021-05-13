import java.awt.geom.Line2D;

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

PVector closestPoint(PVector p1, PVector p2, PVector p0) {
  /**
  Uses basic point-slope formulas to find the intersection of (p1, p2)
  and the line perpendicular to (p1, p2) passing through the node.
  Slightly slower.
  */
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

PVector closestPoint2(PVector p1, PVector p2, PVector p0) {
  /**
  Projects the node onto the vector between (p1, p2) to find the closest point.
  Slightly faster.
  */
  PVector y = PVector.sub(p0, p1);
  PVector u = PVector.sub(p2, p1);
  return u.mult(y.dot(u)/u.magSq()).add(p1);
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
  for(HashSet<Node> MST : MSTs) {
    q = -1;
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

ArrayList<Node> traverseKruskalTree(ArrayList<Node> kruskal, HashSet<Node> exterior, ArrayList<PVector> vertices) {
  ArrayList<Node> traverse = new ArrayList<Node>();
  Node edge = new Node();
  float dist, test;
  int j, k, l;
  for(Node n : kruskal) {
    dist = 1e6;
    k = 0;
    l = 0;
    for(int i = 0; i < vertices.size(); i++) {
      j = i + 1;
      if(j == vertices.size()) {
        j = 0;
      }
      test = distanceToSegment(vertices.get(i), vertices.get(j), n.pv);
      if(test<dist) {
        k = i;
        l = j;
      }
    }
    edge = new Node(PVector.sub(vertices.get(l), vertices.get(k)));
    traverse.addAll(n.getAllKruskal(edge, exterior.contains(n)));
  }
  return traverse;
}

boolean inLine(PVector p1, PVector p2, PVector test) {
  /**
  Tests if PVector test is in the line described by p1, p2
  by determining of it's distance to the closest point on that
  line is approximately 0 (for rounding errors).
  */
  return PVector.dist(test, closestPoint2(p1, p2, test)) < 1;
}
