import java.util.HashSet;

ArrayList<PVector> vertices;
ArrayList<Node> traverse;
ArrayList<float[]> interiorCircumcircles, exteriorCircumcircles, traverseArcs;
HashSet<Node> circles, interior, exterior;
PShape shape;
int w, h;
int p, q;
float[][] ends;

final float minimise = 3;
final boolean iterate = false;
final boolean drawGrid = false;
final boolean drawNumCircles = true;
final boolean drawShape = true;
final boolean drawInterior = true;
final boolean drawExterior = true;
final boolean drawTouching = false;
final boolean drawDelaunay = false;
final boolean drawCircumcircles = false;
final boolean drawTraversal = false;
final boolean drawTraversalArcs = true;
final boolean drawKruskal = false;

void setup() {
  size(800, 800);
  w = 800;
  h = w;
  if(iterate) {
    p = 0;
    q = 1;
  }
  noFill();
  // Tilted Square:
  // float m = 0; // 0 for no tilt
  // float[][] initvertices = {{0, m}, {100 - m, 0}, {100, 100 - m}, {m, 100}};
  // Mouse input example:
  // float[][] initvertices = {{12, 8}, {25, 8}, {16, 20}, {0, 10}, {8, 0}, {25, 2}};
  // More complex vertices:
  float[][] initvertices = {{0, 0}, {12, 0}, {12, 9}, {18, 9}, {12, 15}, {3, 12}};
  vertices = toPVector(initvertices);
  scaleVertices((float) w / 40, vertices);
  ends = extremes(vertices);
  shape = toShape(vertices);
  calc();
}

void draw() {
  background(255);
  float xoff, yoff;
  xoff = (w - (ends[1][0] - ends[0][0])) / 2;
  yoff = (h - (ends[1][1] - ends[0][1])) / 2;
  if(drawGrid) {
    fill(127);
    stroke(127);
    strokeWeight(1);
    int freq = 25;
    for(int i = ((int) xoff / freq) * -freq; i < w - xoff; i += freq) {
      line(i + xoff, 0, i + xoff, h);
      text("" + i, i + xoff + 2, 10);
    }
    for(int i = ((int) yoff / freq) * -freq; i < h - yoff; i += freq) {
      line(0, i + yoff, w, i + yoff);
      text("" + i, 0, i + yoff + 12);
    }
    noFill();
  }
  if(drawNumCircles) {
    fill(0);
    text(String.format("Circles: %d", circles.size()), 30, 30);
    noFill();
  }
  if(drawShape) {
    stroke(0);
    strokeWeight(1);
    shape(shape, xoff, yoff);
  }
  if(drawInterior) {
    drawNodes(interior, xoff, yoff);
  }
  if(drawExterior) {
    drawNodes(exterior, xoff, yoff);
  }
  if(drawCircumcircles) {
    stroke(255, 0, 0);
    strokeWeight(1);
    for(float[] info : interiorCircumcircles) {
      circle(info[0] + xoff, info[1] + yoff, info[2] * 2);
    }
    for(float[] info : exteriorCircumcircles) {
      circle(info[0] + xoff, info[1] + yoff, info[2] * 2);
    }
  }
  if(drawTraversal) {
    stroke(255, 0, 0);
    strokeWeight(1);
    for(int i = 0; i < traverse.size() - 1; i++) {
      if(!traverse.get(i).kruskal.contains(traverse.get(i+1))) {
        stroke(0, 0, 255);
      } else {
        stroke(255, 0, 0);
      }
      line(traverse.get(i).x + xoff, traverse.get(i).y + yoff, traverse.get(i+1).x + xoff, traverse.get(i+1).y + yoff);
    }
    stroke(0, 0, 255);
    line(traverse.get(traverse.size()-1).x + xoff, traverse.get(traverse.size()-1).y + yoff, traverse.get(0).x + xoff, traverse.get(0).y + yoff);
  }
  if(drawTraversalArcs) {
    strokeWeight(1);
    stroke(255, 0, 0);
    for(float[] a : traverseArcs) {
      if(a.length == 6) {
        arc(a[0] + xoff, a[1] + yoff, a[2] * 2, a[3] * 2, a[4], a[5]);
      } else {
        line(a[0] + xoff, a[1] + yoff, a[2] + xoff, a[3] + yoff);
      }
    }
  }
  if(iterate) {
    //codestuffs with p and q
    //keyPressed();
  }
}

void keyPressed() {
  if(iterate) {
    p++;
    q++;
    if(p >= traverse.size()) {
      p = 0;
    }
    if(q >= traverse.size()) {
      q = 0;
    }
  }
}

void mouseClicked() {
  if(iterate) {
    p = 0;
    q = 1;
  }
  calc();
}

void drawNodes(HashSet<Node> circles, float xoff, float yoff) {
  for(Node n : circles) {
    stroke(0);
    strokeWeight(1);
    n.drw(xoff, yoff);
    if(drawTouching) {
      stroke(0, 0, 255);
      strokeWeight(1);
      for(Node t : n.touching) {
        line(n.x + xoff, n.y + yoff, t.x + xoff, t.y + yoff);
      }
    }
    if(drawDelaunay) {
      stroke(255, 0, 0);
      strokeWeight(1);
      for(Node t : n.delaunay) {
        line(n.x + xoff, n.y + yoff, t.x + xoff, t.y + yoff);
      }
    }
    if(drawKruskal) {
      stroke(0, 0, 255);
      //stroke(0, 255, 0);
      strokeWeight(1);
      for(Node t : n.kruskalAdjacent) {
        line(n.x + xoff, n.y + yoff, t.x + xoff, t.y + yoff);
      }
    }
  }
}

void calc() {
  int start;
  println();
  start = millis();
  circles = randomFillAware(vertices, minimise);
  println(String.format("Packing (rejection): %.3f\tCircles: %d\tCirc/Sec: %.2f", (float) (millis() - start) / 1000, circles.size(), circles.size()/((float) (millis() - start) / 1000)));
  //start = millis();
  //condense(circles);
  //println(String.format("Condensing: %.3f", (float) (millis() - start) / 1000));
  interior = containing(vertices, circles, true);
  exterior = containing(vertices, circles, false);
  interiorCircumcircles = analyze(interior);
  exteriorCircumcircles = analyze(exterior);
  start = millis();
  traverse = traverseKruskalTree(kruskalTraverse(circles, vertices), exterior, vertices);
  traverseArcs = traversalToArcs(traverse);
  println(String.format("Traversal: %.3f", (float) (millis() - start) / 1000));
}

ArrayList<float[]> analyze(HashSet<Node> aCircles) {
  ArrayList<float[]> circum;
  int start;
  start = millis();
  ArrayList<Triangle> triangles = delaunay(aCircles);  // Triangulate.triangulate(vertices);
  circum = triangleToCircle(triangles);
  updateDelaunay(aCircles, triangles);
  println(String.format("Triangulation: %.3f", (float) (millis() - start) / 1000));
  start = millis();
  kruskal(aCircles);
  println(String.format("Kruskal: %.3f", (float) (millis() - start) / 1000));
  return circum;
}
