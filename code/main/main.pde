import java.awt.geom.Line2D;
import java.awt.Polygon;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.processing.wiki.triangulate.*;

ArrayList<float[]> traverseArcs, interiorCircumcircles, exteriorCircumcircles;
ArrayList<PVector> vertices;
ArrayList<Node> traverse;
HashSet<Node> circles, interior, exterior;
int w, h, p, q;
float iter = 0f;
PShape shape;
PVector offset;
boolean blue = true;

final float minimise = 3;
final boolean iterate = false;
final boolean noDraw = false;

HashMap<Character, String> conv;
HashMap<String, Boolean> draw;

void setup() {
  size(800, 800);
  noFill();
  surface.setTitle("Detailing");
  initializeKeys();
  w = 800;
  h = w;
  if(iterate) {
    p = 0;
    q = 1;
  }
  // Tilted Square:
  // float m = 0; // 0 for no tilt
  // float[][] initvertices = {{0, m}, {100 - m, 0}, {100, 100 - m}, {m, 100}};
  // Mouse input example:
   float[][] initvertices = {{12, 8}, {25, 8}, {16, 20}, {0, 10}, {8, 0}, {25, 2}};
  // More complex vertices:
  //float[][] initvertices = {{0, 0}, {12, 0}, {12, 9}, {18, 9}, {12, 15}, {3, 12}};
  vertices = toPVector(initvertices);
  scaleVertices((float) w / 40, vertices);
  shape = toShape(vertices);
  calcOffset();
  calc();
}

void draw() {
  background(255);
  if(draw.get("grid")) {
    fill(127);
    stroke(127);
    strokeWeight(1);
    int freq = 25;
    for(int i = ((int) offset.x / freq) * -freq; i < w - offset.x; i += freq) {
      line(i + offset.x, 0, i + offset.x, h);
      text("" + i, i + offset.x + 2, 10);
    }
    for(int i = ((int) offset.y / freq) * -freq; i < h - offset.y; i += freq) {
      line(0, i + offset.y, w, i + offset.y);
      text("" + i, 0, i + offset.y + 12);
    }
    noFill();
  }
  if(draw.get("numCircles")) {
    fill(0);
    text(String.format("Circles: %d", circles.size()), 30, 30);
    noFill();
  }
  if(draw.get("shape")) {
    stroke(0);
    strokeWeight(1);
    shape(shape, offset.x, offset.y);
  }
  if(draw.get("interior")) {
    drawNodes(interior, interiorCircumcircles);
  }
  if(draw.get("exterior")) {
    drawNodes(exterior, exteriorCircumcircles);
  }
  if(draw.get("traversal")) {
    stroke(255, 0, 0);
    strokeWeight(1);
    for(int i = 0; i < traverse.size() - 1; i++) {
      if(!traverse.get(i).kruskal.contains(traverse.get(i+1))) {
        stroke(0, 0, 255);
      } else {
        stroke(255, 0, 0);
      }
      drawLineOffset(traverse.get(i).pv, traverse.get(i+1).pv);
    }
    stroke(0, 0, 255);
    drawLineOffset(traverse.get(traverse.size()-1).pv, traverse.get(0).pv);
  }
  if(draw.get("traversalArcs")) {
    strokeWeight(1);
    stroke(255, 0, 0);
    for(float[] a : traverseArcs) {
      stroke(255, 0, 0);
      if(blue) {
        //stroke(0, 0, 255);
      }
      blue = !blue;
      if(a.length == 6) {
        arc(a[0] + offset.x, a[1] + offset.y, a[2] * 2, a[3] * 2, a[4], a[5]);
      } else {
        line(a[0] + offset.x, a[1] + offset.y, a[2] + offset.x, a[3] + offset.y);
      }
    }
  }
  if(iterate) {
    // codestuffs with p and q (will be valid for traverse indices)
    // keyPressed(); // ?
  }
  if(noDraw) {
    test3();
  } else {
    noLoop();
  }
}

void drawNodes(HashSet<Node> circles, ArrayList<float[]> circumcircles) {
  /**
  Nodes may be stored in multiple discrete sets.
  This function draws relevant information for all nodes in a given set.
  */
  for(Node n : circles) {
    stroke(0);
    strokeWeight(1);
    n.draw(offset);
    if(draw.get("touching")) {
      stroke(0, 0, 255);
      strokeWeight(1);
      for(Node t : n.touching) {
        drawLineOffset(n.pv, t.pv);
      }
    }
    if(draw.get("delaunay")) {
      stroke(255, 0, 0);
      strokeWeight(1);
      for(Node t : n.delaunay) {
        drawLineOffset(n.pv, t.pv);
      }
    }
    if(draw.get("kruskal")) {
      stroke(0, 255, 0);
      strokeWeight(1);
      for(Node t : n.kruskalAdjacent) {
        drawLineOffset(n.pv, t.pv);
      }
    }
  }
  if(draw.get("circumcircles")) {
    stroke(255, 0, 0);
    strokeWeight(1);
    for(float[] info : circumcircles) {
      circle(info[0] + offset.x, info[1] + offset.y, info[2] * 2);
    }
  }
}

void drawArc(float[] arc) {
  arc(arc[0], arc[1], arc[2] * 2, arc[3] * 2, arc[4], arc[5]);
}

ArrayList<float[]> analyze(HashSet<Node> aCircles) {
  /**
  The Delaunay Triangulation and tree generation is done seperately for the interior and exterior circles.
  This was made into a function to avoid repeating code.
  
  Accepts:
    HashSet<Node> representing interior/exterior circles
  Returns:
    ArrayList<float[]> representing the circumcircles of the delaunay triangulation
  */
  ArrayList<float[]> circum;
  int start;
  start = millis();
  ArrayList<Triangle> triangles = delaunay(aCircles);  // Triangulate.triangulate(vertices);
  circum = triangleToCircle(triangles);
  updateDelaunay(aCircles, triangles);
  println(String.format("\tTriangulation: %.3f", (float) (millis() - start) / 1000));
  start = millis();
  kruskal(aCircles);
  println(String.format("\tKruskal: %.3f", (float) (millis() - start) / 1000));
  return circum;
}

void calc() {
  /**
  Completes all relevant calculations.
  */
  if(!noDraw) {
    int start;
    start = millis();
    circles = randomFillAware(vertices, minimise);
    println(String.format("Packing (rejection): %.3f\tCircles: %d\tCirc/Sec: %.2f", (float) (millis() - start) / 1000, circles.size(), circles.size()/((float) (millis() - start) / 1000)));
    start = millis();
    condense(circles);  // Takes a similar amount of time as the circle packing. Only use if you need to ensure all circles are touching.
    println(String.format("Condensing: %.3f", (float) (millis() - start) / 1000));
    println("-Interior-");
    interior = containing(vertices, circles, true);
    interiorCircumcircles = analyze(interior);
    println("-Exterior-");
    exterior = containing(vertices, circles, false);
    exteriorCircumcircles = analyze(exterior);
    start = millis();
    traverse = traverseKruskalTreesSmart(circles, exterior, vertices, false);
    traverseArcs = delaunayTraversalToArcs(traverse);
    //traverseArcs = surroundingArcs(traverse);
    println(String.format("Traversal: %.3f", (float) (millis() - start) / 1000));
    println("\n");
  }
}

void calcOffset() {
  /**
  Calculates the amount all geometry should be offset to center it.
  */
  PVector[] ends = extremes(vertices);
  offset = new PVector((w - (ends[1].x - ends[0].x)) / 2, (h - (ends[1].y - ends[0].y)) / 2);
}

void drawLineOffset(PVector p1, PVector p2) {
  line(p1.x + offset.x, p1.y + offset.y, p2.x + offset.x, p2.y + offset.y);
}

// Input
void keyPressed() {
  String cmd;
  if(iterate) {
    p++;
    q++;
    if(p >= traverse.size()) {
      p = 0;
    }
    if(q >= traverse.size()) {
      q = 0;
    }
  } else {
    if(key == 'h') {
      String out = "Draw:\n";
      for(char c : conv.keySet()) {
        out += c + ": " + conv.get(c) + "\n";
      }
      print(out);
    } else if(conv.containsKey(key)) {
      cmd = conv.get(key);
      draw.replace(cmd, !draw.get(cmd));
      println(cmd + ": " + draw.get(cmd));
      loop();
    } else {
      //mouseClicked();
      boolean s = random(0, 2) > 1.0f;
      traverse = s ? traverseKruskalTrees(circles, exterior, vertices, false) : traverseKruskalTreesSmart(circles, exterior, vertices, false);
      println(s);
      loop();
    }
  }
}

void mouseClicked() {
  if(iterate) {
    p = 0;
    q = 1;
  } else {
    calc();
  }
  loop();
}

void initializeKeys() {
  // key, name, value
  Object[][] cmd = new Object[][] {
  {'g', "grid", false},
  {'n', "numCircles", true},
  {'s', "shape", true},
  {'i', "interior", false},
  {'e', "exterior", false},
  {'t', "touching", false},
  {'d', "delaunay", false},
  {'c', "circumcircles", false},
  {'r', "traversal", false},
  {'a', "traversalArcs", true},
  {'k', "kruskal", false}
  };
  conv = new HashMap<Character, String>();
  draw = new HashMap<String, Boolean>();
  for(Object[] arr : cmd) {
    conv.put((char) arr[0], (String) arr[1]);
    draw.put((String) arr[1], noDraw ? false : (boolean) arr[2]);
  }
}
