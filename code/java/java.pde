import java.util.HashSet;

ArrayList<PVector> vertices, traverse;
HashSet<Node> circles;
HashSet<HashSet<Node>> graphs;
PShape shape;
int w, h;
float[][] ends;

void setup() {
  size(800, 800);
  w = 800;
  h = w;
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
  fill(0);
  text(String.format("Circles: %d", circles.size()), 10, 10);
  noFill();
  stroke(0);
  shape(shape, xoff, yoff);
  for(Node c : circles) {
    c.drw(xoff, yoff);
  }
  stroke(0, 0, 255);
  for(Node n : circles) {
    for(Node t : n.touching) {
      line(n.x + xoff, n.y + yoff, t.x + xoff, t.y + yoff);
    }
  }
}

void keyPressed() {
  calc();
}

void calc() {
  int start;
  start = millis();
  circles = randomFillAware(vertices);
  println(String.format("Circle packing: %.2f", (float) (millis() - start) / 1000));
  start = millis();
  condense(circles);
  println(String.format("Condensing: %.3f", (float) (millis() - start) / 1000));
}
