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

boolean inLine(PVector p1, PVector p2, PVector test) {
  /**
  Tests if PVector test is in the line described by p1, p2
  by determining of it's distance to the closest point on that
  line is approximately 0 (for rounding errors).
  */
  return PVector.dist(test, closestPoint2(p1, p2, test)) < 1;
}

ArrayList<float[]> traversalToArcs(ArrayList<Node> traversal) {
  ArrayList<float[]> arcs = new ArrayList<float[]>();
  for(int i = 0; i < traversal.size() - 1; i++) {
    if(traversal.get(i).kruskalAdjacent.contains(traversal.get(i+1))) {
      arcs.add(getArcKruskal(traversal.get(i), traversal.get(i+1)));
    } else {
      arcs.add(new float[] {traversal.get(i).x, traversal.get(i).y, traversal.get(i+1).x, traversal.get(i+1).y});
    }
  }
  arcs.add(new float[] {traversal.get(traversal.size()-1).x, traversal.get(traversal.size()-1).y, traversal.get(0).x, traversal.get(0).y});
  return arcs;
}
