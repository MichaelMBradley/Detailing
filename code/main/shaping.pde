import java.util.List;

float[][] extremes(ArrayList<PVector> vertices) {
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
  ArrayList<PVector> proper = new ArrayList<PVector>();
  for(int i = 0; i < vertices.length; i++) {
    proper.add(new PVector(vertices[i][0], vertices[i][1]));
  }
  return proper;
}

PShape toShape(ArrayList<PVector> vertices) {
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
  float x, y, r, x1, x2, x3, y1, y2, y3;
  ArrayList<float[]> info = new ArrayList<float[]>();
  for(Triangle tri : triangles) {
    x1 = tri.p1.x;
    y1 = tri.p1.y;
    x2 = tri.p2.x;
    y2 = tri.p2.y;
    x3 = tri.p3.x;
    y3 = tri.p3.y;
    // Just a representation of the point of intersection of to lines perpendicular to two of the edges of the triangle, each cutting it's edge in half
    if(y1 == y2) {
      x = (x1 + x2) / 2;
      y = -((x3 - x2) / (y3 - y2)) * (x - ((x2 + x3) / 2)) + ((y2 + y3) / 2);
    } else if(y2 == y3) {
      x = (x2 + x3) / 2;
      y = -((x2 - x1) / (y2 - y1)) * (x - ((x1 + x2) / 2)) + ((y1 + y2) / 2);
    } else {
      x = (((x2 * x2 - x1 * x1) / (y2 - y1)) - ((x3 * x3 - x2 * x2) / (y3 - y2)) + (y1 - y3)) / (2.0f * (((x2 - x1) / (y2 - y1)) - ((x3 - x2) / (y3 - y2))));
      y = -((x2 - x1) / (y2 - y1)) * (x - ((x1 + x2) / 2)) + ((y1 + y2) / 2);
    }
    r = dist(x, y, x1, y1);
    info.add(new float[] {x, y, r});
  }
  return info;
}
