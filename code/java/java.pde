import java.util.ArrayList;

ArrayList<PVector> vertices, traverse;
//ArrayList<circ> circles;
//ArrayList<node> graph;
PShape shape;
int w, h;

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
  shape = toShape(vertices);
  calc();
}

void draw() {
  background(255);
  float xoff, yoff;
  xoff = 0;
  yoff = 0;
  shape(shape, xoff, yoff);
}

void keyPressed() {
  calc();
}

void calc() {
  System.out.println(vertices);
}
