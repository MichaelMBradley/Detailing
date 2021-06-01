Node[] getAdjacent(Node n1, Node n2, float r0, boolean exterior) {
  /**
  Returns the two nodes touching the both the two given nodes.
  if sign(x2-x1) != sign(y2-y1):  # +/-, -/+
    [0] is (higher y than) line passing through centres
  if sign(x2-x1) == (signy2-y1):  # +/+, -/-
    [0] is (lower y than) line passing through centres
  if x1==x2:
    [0] is to the right
  if y1==y2:
    [0] is the lower circle (higher y)
  */
  float dist = PVector.dist(n1.pv, n2.pv);
  if(dist + n2.r < n1.r || dist + n1.r < n2.r || dist > n1.r + n2.r + (r0 * 2)) {
    // node 1 contains node 2 || node 2 contains node 1 || nodes are too far apart
    return new Node[] {new Node(), new Node()};
  }
  float ml, bl, aq, bq, cq, xa1, ya1, xa2, ya2;
  float x1 = n1.x;
  float y1 = n1.y;
  float r1 = n1.r;
  float x2 = n2.x;
  float y2 = n2.y;
  float r2 = n2.r;
  int inv = exterior ? 1 : -1;
  if(abs(y1 - y2) > 1) {
    ml = - (x2 - x1) / (y2 - y1);
    bl = (-pow(x1, 2) + pow(x2, 2) - pow(y1, 2) + pow(y2, 2) + pow(r1, 2) - pow(r2, 2) + (2 * r0 * (r1 - r2)) * inv) / (2 * (y2 - y1));
    aq = 1 + pow(ml, 2);
    bq = 2 * (ml * (bl - y1) - x1);
    cq = pow(x1, 2) + pow(bl - y1, 2) - pow(r1 + r0 * inv, 2);
    if(pow(bq, 2) < 4 * aq * cq) {
      return new Node[] {new Node(), new Node()};
    }
    xa1 = (-bq + sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
    ya1 = ml * xa1 + bl;
    xa2 = (-bq - sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
    ya2 = ml * xa2 + bl;
  } else {
    ml = - (y2 - y1) / (x2 - x1);
    bl = (-pow(x1, 2) + pow(x2, 2) - pow(y1, 2) + pow(y2, 2) + pow(r1, 2) - pow(r2, 2) + (2 * r0 * (r1 - r2)) * inv) / (2 * (x2 - x1));
    aq = 1 + pow(ml, 2);
    bq = 2 * (ml * (bl - x1) - y1);
    cq = pow(y1, 2) + pow(bl - x1, 2) - pow(r1 + r0 * inv, 2);
    if(pow(bq, 2) < 4 * aq * cq) {
      return new Node[] {new Node(), new Node()};
    }
    ya1 = (-bq + sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
    xa1 = ml * ya1 + bl;
    ya2 = (-bq - sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
    xa2 = ml * ya2 + bl;
  }
  return new Node[] {new Node(xa1, ya1, r0), new Node(xa2, ya2, r0)};
}

Node[] getExteriorFix(Node n1, Node n2) {
  float r = max((n1.r + n2.r) / 4, n1.distanceToCircle(n2) + n1.r / 2, n2.distanceToCircle(n1) + n2.r / 2);
  Node[] test = getAdjacent(n1, n2, r, true);
  while(test[0].distanceToCircle(test[1]) < 0) {
    r *= 1.1;
    test = getAdjacent(n1, n2, r, true);
  }
  return test;
}

Node[] getExterior(Node n1, Node n2) {
  return getAdjacent(n1, n2, max((n1.r + n2.r) / 4, n1.distanceToCircle(n2) + n1.r / 2, n2.distanceToCircle(n1) + n2.r / 2), true);
}

Node[] getExterior(Node n1, Node n2, float r0) {
  return getAdjacent(n1, n2, r0, true);
}

Node[] getInterior(Node n1, Node n2) {
  return getAdjacent(n1, n2, (n1.r + n2.r - PVector.dist(n1.pv, n2.pv)) / 2f, false);
}

Node[] getInterior(Node n1, Node n2, float r0) {
  return getAdjacent(n1, n2, r0, false);
}

Node[] triCircleAdjacent(Node n1, Node n2, Node n3) {
  // John Alexiou (https://math.stackexchange.com/users/3301/john-alexiou), Calculate the circle that touches three other circles, URL (version: 2019-07-18): https://math.stackexchange.com/q/3290944
  // [0] is on opposite side of line (n1, n2) as n3
  float x1 = n1.x;
  float y1 = n1.y;
  float r1 = n1.r;
  float x2 = n2.x;
  float y2 = n2.y;
  float r2 = n2.r;
  float x3 = n3.x;
  float y3 = n3.y;
  float r3 = n3.r;
  float Ka = -pow(r1, 2) + pow(r2, 2) + pow(x1, 2) - pow(x2, 2) + pow(y1, 2) - pow(y2, 2);
  float Kb = -pow(r1, 2) + pow(r3, 2) + pow(x1, 2) - pow(x3, 2) + pow(y1, 2) - pow(y3, 2);
  float D = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
  float A0 = (Ka * (y1 - y3) + Kb * (y2 - y1)) / (2 * D);
  float B0 = - (Ka * (x1 - x3) + Kb * (x2 - x1)) / (2 * D);
  float A1 = - (r1 * (y2 - y3) + r2 * (y3 - y1) + r3 * (y1 - y2)) / D;
  float B1 = (r1 * (x2 - x3) + r2 * (x3 - x1) + r3 * (x1 - x2)) / D;
  float C0 = pow(A0 - x1, 2) + pow(B0 - y1, 2) - pow(r1, 2);
  float C1 = A1 * (A0 - x1) + B1 * (B0 - y1) - r1;
  float C2 = pow(A1, 2) + pow(B1, 2) - 1;
  float r4 = (-C1 + sqrt(pow(C1, 2) - C0 * C2)) / C2;
  float r5 = (-C1 - sqrt(pow(C1, 2) - C0 * C2)) / C2;
  float x4 = A0 + A1 * r4;
  float y4 = B0 + B1 * r4;
  float x5 = A0 + A1 * r5;
  float y5 = B0 + B1 * r5;
  return new Node[] {new Node(x4, y4, r4), new Node(x5, y5, r5)};
}


ArrayList<float[]> surroundingArcs(ArrayList<Node> nodes) {
  ArrayList<ArrayList<Node>> trees = new ArrayList<ArrayList<Node>>();
  ArrayList<float[]> arcs = new ArrayList<float[]>();
  trees.add(new ArrayList<Node>());
  trees.get(0).add(new Node());
  for(Node n : nodes) {
    if(!trees.get(trees.size() - 1).get(0).kruskal.contains(n)) {
      trees.add(new ArrayList<Node>());
    }
    trees.get(trees.size() - 1).add(n);
  }
  trees.get(0).remove(0);
  for(int i = 0; i < trees.size(); i++) {
    if(i == trees.size() - 1) {
      arcs.addAll(surroundingArcsTree(trees.get(i)));
    } else {
      arcs.addAll(surroundingArcsTree(trees.get(i), trees.get(i+1).get(0)));
    }
  }
  return arcs;
}

ArrayList<float[]> surroundingArcsTree(ArrayList<Node> nodes, Node next) {
  ArrayList<Node> n = (ArrayList<Node>) nodes.clone();
  n.add(next);
  return surroundingArcsTree(n);
}

ArrayList<float[]> surroundingArcsTree(ArrayList<Node> nodes) {
  if(nodes.size() == 0) {
    return new ArrayList<float[]>();
  }
  ArrayList<float[]> arcs = new ArrayList<float[]>();
  ArrayList<Node> arcNodes = new ArrayList<Node>();
  ArrayList<Integer> tri = new ArrayList<Integer>();
  Node ni, nj, nc, n;
  float[] se;
  int choose;
  for(int i = 1; i < nodes.size() - 1; i++) {
    ni = nodes.get(i);
    nj = nodes.get(i == 0 ? nodes.size() - 1 : i - 1);
    // Choosing which side of the circle to put the arc on based on the direction
    if(ni.x == nj.x) {
      choose = ni.y > nj.y ? 0 : 1;
    } else if(ni.y == nj.y) {
      choose = ni.x > nj.x ? 1 : 0;
    } else if(nj.y > ni.y) {
      choose = 1;
    } else {
      choose = 0;
    }
    nc = getExterior(ni, nj)[choose];
    arcNodes.add(nc);
    arcNodes.add(ni);
  }
  for(int i = 3; i <= arcNodes.size() - 3; i+=2) {
    //println(PVector.angleBetween(PVector.sub(arcNodes.get(i+2).pv, arcNodes.get(i).pv), PVector.sub(arcNodes.get(i-2).pv, arcNodes.get(i).pv)) + "\t" + 
    //(PVector.angleBetween(PVector.sub(arcNodes.get(i+2).pv, arcNodes.get(i).pv), PVector.sub(arcNodes.get(i-2).pv, arcNodes.get(i).pv)) < HALF_PI * 1.25f && arcNodes.get(i+2) != arcNodes.get(i-2)));
    //println("\t" + arcNodes.get(i-2).pv + "\t" + arcNodes.get(i).pv + "\t" + arcNodes.get(i+2).pv);
    if(PVector.angleBetween(PVector.sub(arcNodes.get(i+2).pv, arcNodes.get(i).pv), PVector.sub(arcNodes.get(i-2).pv, arcNodes.get(i).pv)) < HALF_PI * 1.25f && arcNodes.get(i+2) != arcNodes.get(i-2)) {
      arcNodes.set(i, triCircleAdjacent(arcNodes.get(i-2), arcNodes.get(i+2), arcNodes.get(i))[1]);
      arcNodes.remove(i+1);
      arcNodes.remove(i-1);
      tri.add(i - 1);
    }
  }
  for(int i = 0; i < arcNodes.size(); i++) {
    n = arcNodes.get(i);
    ni = arcNodes.get(i == 0 ? arcNodes.size() - 2 : i - 1);
    nj = arcNodes.get(i >= arcNodes.size() - 2 ? arcNodes.size() - i : i + 1);
    se = order(ni.pv, n.pv, nj.pv, (i % 2 == 0));
    arcs.add(new float[] {n.x, n.y, n.r, n.r, se[0], se[1]});
  }
  return arcs;
}

private float[] order(PVector pre, PVector curr, PVector next, boolean crossing) {
  float start = PVector.sub(pre, curr).heading();
  float end = PVector.sub(next, curr).heading();
  if(PVector.angleBetween(PVector.sub(pre, curr), PVector.sub(next, curr)) < PI == crossing) {
    float temp = start;
    start = end;
    end = temp;
  }
  return bindStart(start, end);
}

private float[] bindStart(float start, float end) {
  while(start > end) {
    start -= TWO_PI;
  }
  while(start < end - TWO_PI) {
    start += TWO_PI;
  }
  while(end < 0) {
    start += TWO_PI;
    end += TWO_PI;
  }
  return new float[] {start, end};
}
