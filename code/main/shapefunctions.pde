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
