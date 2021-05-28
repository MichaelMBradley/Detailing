float[] arcLine(PVector p1, PVector p2) {
  float minx = min(p1.x, p2.x) * (2.0f / 3.0f) + max(p1.x, p2.x) * (1.0f / 3.0f);
  float miny = min(p1.y, p2.y) * (2.0f / 3.0f) + max(p1.y, p2.y) * (1.0f / 3.0f);
  float maxx = min(p1.x, p2.x) * (1.0f / 3.0f) + max(p1.x, p2.x) * (2.0f / 3.0f);
  float maxy = min(p1.y, p2.y) * (1.0f / 3.0f) + max(p1.y, p2.y) * (2.0f / 3.0f);
  float[] circ = triangleToCircle(p1.x, p1.y, p2.x, p2.y, random(minx, maxx), random(miny, maxy));
  float[] se = order(p1, new PVector(circ[0], circ[1]), p2, true);
  if(se[1] - se[0] > PI) {
    return new float[] {circ[0], circ[1], circ[2], circ[2], se[1] - TWO_PI, se[0]};
  } else {
    return new float[] {circ[0], circ[1], circ[2], circ[2], se[0], se[1]};
  }
}

boolean circleNearLine(float cutoff, Node c, ArrayList<PVector> vertices) {
  /**
  Returns if a given node is suitably close (cutoff) to any
  of the line segments on the polyline described by the vertices.
  */
  for(int i = 0; i < vertices.size(); i++) {
    int j = i + 1 == vertices.size() ? 0 : i + 1;  // Wraps index of next vertex to 0 to avoid index out of range
    if(distanceToSegment(vertices.get(i), vertices.get(j), c.pv) - c.r <= cutoff) {
      return true;
    }
  }
  return false;
}

float distanceToSegment(PVector v1, PVector v2, PVector test) {
  return (float) Line2D.ptSegDist(v1.x, v1.y, v2.x, v2.y, test.x, test.y);
}

PVector[] extremes(ArrayList<PVector> vertices) {
  /**
  Returns two PVectors bounding a list of PVectors.
  [min, max]
  */
  float[][] ends = {{vertices.get(0).x, vertices.get(0).y}, {vertices.get(0).x, vertices.get(0).y}};
  for(PVector pv : vertices) {
    if(pv.x < ends[0][0]) {
      ends[0][0] = pv.x;
    } else if(pv.x > ends[1][0]) {
      ends[1][0] = pv.x;
    }
    if(pv.y < ends[0][1]) {
      ends[0][1] = pv.y;
    } else if(pv.y > ends[1][1]) {
      ends[1][1] = pv.y;
    }
  }
  return new PVector[] {new PVector(ends[0][0], ends[0][1]), new PVector(ends[1][0], ends[1][1])};
}

void scaleVertices(float scalingfactor, ArrayList<PVector> vertices) {
  for(PVector pv : vertices) {
    pv.mult(scalingfactor);
  }
}

Polygon toPolygon(ArrayList<PVector> vertices) {
  /**
  Returns a polygon object with given vertices.
  */
  int size = vertices.size();
  int[] x = new int[size];
  int[] y = new int[size];
  float[] array;
  for(int i = 0; i < size; i++) {
    array = vertices.get(i).array();
    x[i] = (int) array[0];
    y[i] = (int) array[1];
  }
  return new Polygon(x, y, size);
}

ArrayList<PVector> toPVector(float[][] vertices) {
  /**
  Takes a list of vertices as an array of floats
  and turns it into a list of PVectors. Personally
  I just find it easier to enter vertices this way.
  */
  ArrayList<PVector> proper = new ArrayList<PVector>();
  for(int i = 0; i < vertices.length; i++) {
    proper.add(new PVector(vertices[i][0], vertices[i][1]));
  }
  return proper;
}

PShape toShape(ArrayList<PVector> vertices) {
  /**
  Takes a list of PVectors and turns it into a PShape
  */
  PShape polygon = createShape();
  polygon.beginShape();
  for(PVector pv : vertices) {
    polygon.vertex(pv.x, pv.y);
  }
  polygon.vertex(vertices.get(0).x, vertices.get(0).y);
  polygon.endShape();
  return polygon;
}

ArrayList<float[]> triangleToCircle(ArrayList<Triangle> triangles) {
  /**
  Return list of circumcircles for the triangles.
  */
  ArrayList<float[]> info = new ArrayList<float[]>();
  for(Triangle tri : triangles) {
    info.add(triangleToCircle(tri.p1.x, tri.p1.y, tri.p2.x, tri.p2.y, tri.p3.x, tri.p3.y));
  }
  return info;
}

float[] triangleToCircle(float x1, float y1, float x2, float y2, float x3, float y3) {
  /**
  Calculates the circumcircle of a triangle.
  In short, it calculates the intersection point
  of the line perpendicular to (p1, p2)
  splitting (p1, p2) in half and the same line for
  (p2, p3).
  */
  float x, y, r;
  if((x1 == x2 && x2 == x3) || (y1 == y2 && y2 == y3)) {
    // Impossible to find circumcircle for points in a straight line
    x = Float.NaN;
    y = Float.NaN;
    r = Float.NaN;
  } else if(y1 == y2) {
    // Preventing div/0 errors for when points
    // 1 and 2 have the same y value
    x = (x1 + x2) / 2;
    y = -((x3 - x2) / (y3 - y2)) * (x - ((x2 + x3) / 2)) + ((y2 + y3) / 2);
  } else if(y2 == y3) {
    // Preventing div/0 errors for when points
    // 2 and 3 have the same y value
    x = (x2 + x3) / 2;
    y = -((x2 - x1) / (y2 - y1)) * (x - ((x1 + x2) / 2)) + ((y1 + y2) / 2);
  } else {
    x = (((x2 * x2 - x1 * x1) / (y2 - y1)) - ((x3 * x3 - x2 * x2) / (y3 - y2)) + (y1 - y3)) / (2 * (((x2 - x1) / (y2 - y1)) - ((x3 - x2) / (y3 - y2))));
    y = -((x2 - x1) / (y2 - y1)) * (x - ((x1 + x2) / 2)) + ((y1 + y2) / 2);
  }
  r = dist(x, y, x1, y1);
  return new float[] {x, y, r};
}

float[] getArc(Node n1, Node n2, Node n3) {
  /**
  Returns data about the arc between n1 and n2, passing through n3.
  [x, y, w, h, start, end]
  w = h
  */
  float[] arcinfo = triangleToCircle(n1.x, n1.y, n2.x, n2.y, n3.x, n3.y);
  float ang1 = PVector.sub(n1.pv, new PVector(arcinfo[0], arcinfo[1])).heading();
  float ang2 = PVector.sub(n2.pv, new PVector(arcinfo[0], arcinfo[1])).heading();
  if(ang1 > ang2) {
    ang2 += TWO_PI;
  }
  return new float[] {arcinfo[0], arcinfo[1], arcinfo[2], arcinfo[2], ang1, ang2};
}

Node[] getAdjacent(Node n1, Node n2) {
  float r = max((n1.r + n2.r) / 4, n1.distanceToCircle(n2) + n1.r / 2, n2.distanceToCircle(n1) + n2.r / 2);
  Node[] test = getAdjacent(n1, n2, r);
  while(test[0].distanceToCircle(test[1]) < 0) {
    r *= 1.1;
    test = getAdjacent(n1, n2, r);
  }
  return test;
}

Node[] getAdjacent(Node n1, Node n2, float r0) {
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
  if(abs(y1 - y2) > 1) {
    ml = - (x2 - x1) / (y2 - y1);
    bl = (-pow(x1, 2) + pow(x2, 2) - pow(y1, 2) + pow(y2, 2) + pow(r1, 2) - pow(r2, 2) + (2 * r0 * (r1 - r2))) / (2 * (y2 - y1));
    aq = 1 + pow(ml, 2);
    bq = 2 * (ml * (bl - y1) - x1);
    cq = pow(x1, 2) + pow(bl - y1, 2) - pow(r1 + r0, 2);
    xa1 = (-bq + sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
    ya1 = ml * xa1 + bl;
    xa2 = (-bq - sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
    ya2 = ml * xa2 + bl;
  } else {
    ml = - (y2 - y1) / (x2 - x1);
    bl = (-pow(x1, 2) + pow(x2, 2) - pow(y1, 2) + pow(y2, 2) + pow(r1, 2) - pow(r2, 2) + (2 * r0 * (r1 - r2))) / (2 * (x2 - x1));
    aq = 1 + pow(ml, 2);
    bq = 2 * (ml * (bl - x1) - y1);
    cq = pow(y1, 2) + pow(bl - x1, 2) - pow(r1 + r0, 2);
    ya1 = (-bq + sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
    xa1 = ml * ya1 + bl;
    ya2 = (-bq - sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
    xa2 = ml * ya2 + bl;
  }
  return new Node[] {new Node(xa1, ya1, r0), new Node(xa2, ya2, r0)};
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
