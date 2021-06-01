ArrayList<Triangle> delaunayTriangulation(HashSet<Node> nodes) {
  /**
  Accepts a set of nodes, creates a triangulation of their centres.
  */
  ArrayList<PVector> vectors = new ArrayList<PVector>();
  for(Node n : nodes) {
    vectors.add(n.pv);
  }
  return Triangulate.triangulate(vectors);
}


void updateDelaunay(HashSet<Node> nodes) {
  updateDelaunay(nodes, delaunayTriangulation(nodes));
}


void updateDelaunay(HashSet<Node> nodes, ArrayList<Triangle> triangles) {
  /**
  Adds the delaunay triangulation information to the nodes.
  */
  HashMap<PVector, Node> conv = new HashMap<PVector, Node>();
  HashMap<PVector, HashSet<PVector>> dict = new HashMap<PVector, HashSet<PVector>>();
  Node base, con;
  for(Node n : nodes) {
    conv.put(n.pv, n);
    dict.put(n.pv, new HashSet<PVector>());
  }
  for(Triangle tri : triangles) {
    dict.get(tri.p1).add(tri.p2);
    dict.get(tri.p1).add(tri.p3);
    dict.get(tri.p2).add(tri.p1);
    dict.get(tri.p2).add(tri.p3);
    dict.get(tri.p3).add(tri.p1);
    dict.get(tri.p3).add(tri.p2);
  }
  for(PVector pv : dict.keySet()) {
    base = conv.get(pv);
    for(PVector connect : dict.get(pv)) {
      con = conv.get(connect);
      //if(PVector.dist(pv, connect) < (base.r + con.r) * 3) {
        base.delaunay.add(con);
      //}
    }
  }
}

ArrayList<Node> delaunayTraverse(HashSet<Node> nodes, ArrayList<PVector> vertices) {
  return delaunayTraverse(nodes, vertices, 1f);
  //return delaunayTraverse(nodes, vertices, (float) mouseX * 20f / w);
}

ArrayList<Node> delaunayTraverse(HashSet<Node> nodes, ArrayList<PVector> vertices, float distanceWeight) {
  /**
  Visits many nodes in order around the polyline, based on the delaunay triangulation.
  */
  ArrayList<Node> traversal = new ArrayList<Node>();
  float closest, angle, distance;
  Node goal, next = new Node(), current = closestNode(nodes, vertices.get(vertices.size() - 1));
  for(int i = 0; i < vertices.size(); i++) {
    goal = closestNode(nodes, vertices.get(i));
    while(current != goal) {
      if(current.delaunay.contains(goal)) {
        next = goal;
      } else {
        closest = Float.MAX_VALUE;
        for(Node d : current.kruskal) {
          angle = abs(PVector.angleBetween(PVector.sub(d.pv, current.pv), PVector.sub(goal.pv, current.pv)) - QUARTER_PI);
          distance = PVector.dist(d.pv, goal.pv);
          if(angle + distance / ((goal.r + d.r) / 2) * distanceWeight < closest && !traversal.contains(d)) {
            closest = angle + distance / ((goal.r + d.r) / 2) * distanceWeight;
            next = d;
          }
        }
        if(closest == Float.MAX_VALUE) {
          for(Node d : current.delaunay) {
            angle = abs(PVector.angleBetween(PVector.sub(d.pv, current.pv), PVector.sub(goal.pv, current.pv)) - QUARTER_PI);
            distance = PVector.dist(d.pv, goal.pv);
            if(angle + distance / ((goal.r + d.r) / 2) * distanceWeight < closest && !traversal.contains(d)) {
              closest = angle + distance / ((goal.r + d.r) / 2) * distanceWeight;
              next = d;
            }
          }
        }
        if(closest == Float.MAX_VALUE) {
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
