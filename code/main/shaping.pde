import java.util.List;

float[][] extremes(ArrayList<PVector> vertices) {
  /**
  Returns the highest and lowest x and y values of a list of PVectors.
  [[LowX, LowY], [HighX, HighY]]
  TODO: Return two new PVectors?
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
  return ends;
}

void scaleVertices(float scalingfactor, ArrayList<PVector> vertices) {
  for(PVector pv : vertices) {
    pv.mult(scalingfactor);
  }
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
  Calculates the circumcircle of a triangle.
  In short, it calculates the intersection point
  of the line perpendicular to (p1, p2)
  splitting (p1, p2) in half and the same line for
  (p2, p3).
  */
  float x, y, r, x1, x2, x3, y1, y2, y3;
  ArrayList<float[]> info = new ArrayList<float[]>();
  for(Triangle tri : triangles) {
    x1 = tri.p1.x;
    y1 = tri.p1.y;
    x2 = tri.p2.x;
    y2 = tri.p2.y;
    x3 = tri.p3.x;
    y3 = tri.p3.y;
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
    info.add(new float[] {x, y, r});
  }
  return info;
}
