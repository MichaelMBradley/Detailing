import java.awt.geom.Line2D;
import java.awt.Polygon;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.processing.wiki.triangulate.*;

ArrayList<Arc> traverseArcs;
ArrayList<Circle> interiorCircumcircles, exteriorCircumcircles;
ArrayList<PVector> vertices;
ArrayList<Node> traverse;
HashSet<Node> circles, interior, exterior;
int w, h, p, q, maxIter = 0;
PShape shape;
PVector offset;
boolean smart = false;

final float minimise = 2;
final boolean noDraw = false;

HashMap<Character, String> conv;
HashMap<String, Boolean> draw;

void setup() {
  size(1000, 1000);
  noFill();
  surface.setTitle("Detailing");
  initializeKeys();
  w = pixelWidth;
  h = pixelHeight;
  p = 0;
  q = 1;
  // Tilted Square:
  // float m = 0; // 0 for no tilt
  // float[][] initvertices = {{0, m}, {100 - m, 0}, {100, 100 - m}, {m, 100}};
  // Mouse input example:
   //float[][] initvertices = {{12, 8}, {25, 8}, {16, 20}, {0, 10}, {8, 0}, {25, 2}};
  // More complex vertices:
  float[][] initvertices = {{0, 0}, {12, 0}, {12, 9}, {18, 9}, {12, 15}, {3, 12}};
  vertices = toPVector(initvertices);
  scaleVertices((float) w / 30, vertices);
  shape = toShape(vertices);
  if(!noDraw) {
    calcOffset();
  } else {
    offset = new PVector();
  }
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
    fill(0);
    stroke(0);
    text("(" + (int) (mouseX - offset.x) + ", " + (int) (mouseY - offset.y) + ")", mouseX + 2, mouseY - 2);
    line(mouseX, 0, mouseX, h);
    line(0, mouseY, w, mouseY);
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
  drawNodes(interior, interiorCircumcircles, draw.get("interior"));
  drawNodes(exterior, exteriorCircumcircles, draw.get("exterior"));
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
    float r = 255;
    float b = 0;
    float chn = 255.0f / (float) traverseArcs.size();
    for(Arc a : traverseArcs) {
      if(draw.get("gradient")) {
        stroke(r, 0, b);
        r -= chn;
        b += chn;
      }
      a.draw(offset);
    }
  }
  if(draw.get("iterate")) {
    // codestuffs with p and q
    // keyPressed(); // ?
    maxIter = traverseArcs.size();
    stroke(0, 255, 0);
    traverseArcs.get(p).draw(offset);
  }
  if(noDraw) {
    test7();
  }
}

void drawNodes(HashSet<Node> circles, ArrayList<Circle> circumcircles, boolean drawCircles) {
  /**
  Nodes may be stored in multiple discrete sets.
  This function draws relevant information for all nodes in a given set.
  */
  if(!noDraw) {
    for(Node n : circles) {
      if(drawCircles) {
        stroke(0);
        strokeWeight(1);
        n.draw(offset);
      }
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
      for(Circle c : circumcircles) {
        c.draw(offset);;
      }
    }
  }
}

ArrayList<Circle> analyze(HashSet<Node> aCircles) {
  /**
  The Delaunay Triangulation and tree generation is done seperately for the interior and exterior circles.
  This was made into a function to avoid repeating code.
  
  Accepts:
    HashSet<Node> representing interior/exterior circles
  Returns:
    ArrayList<Arc> representing the circumcircles of the delaunay triangulation
  */
  ArrayList<Circle> circum;
  int start;
  start = millis();
  ArrayList<Triangle> triangles = delaunayTriangulation(aCircles);
  circum = triangleToCircle(triangles);
  updateDelaunay(aCircles, triangles);
  println(String.format("\tTriangulation: %.3f", (float) (millis() - start) / 1000));
  start = millis();
  //kruskal(aCircles);
  //altTreeCreate(aCircles, vertices);
  treeNearest(aCircles, vertices);
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
    if(draw.get("condense")) {
      start = millis();
      condense(circles);  // Takes a similar amount of time as the circle packing. Only use if you need to ensure all circles are touching.
      println(String.format("Condensing: %.3f", (float) (millis() - start) / 1000));
    }
    if(draw.get("getTouching") && !draw.get("condense")) {
      start = millis();
      createTouchingGraphs(circles);
      println(String.format("Touching: %.3f", (float) (millis() - start) / 1000));
    }
    println("-Interior-");
    interior = containing(vertices, circles, true);
    interiorCircumcircles = analyze(interior);
    println("-Exterior-");
    exterior = containing(vertices, circles, false);
    exteriorCircumcircles = analyze(exterior);
    start = millis();
    traverse = traverseTreesBase(circles, vertices, true);
    //traverseArcs = delaunayTraversalToArcs(traverse, vertices);
    //traverse = traverseTreesSkip(circles, vertices, true);
    traverseArcs = surroundingArcs(traverse);
    //traverse = new ArrayList<Node>();
    //traverseArcs = new ArrayList<Arc>();
    println(String.format("Traversal: %.3f", (float) (millis() - start) / 1000));
    println("\n");
  } else {
    circles = new HashSet<Node>();
    interiorCircumcircles = new ArrayList<Circle>();
    exteriorCircumcircles = new ArrayList<Circle>();
    traverse = new ArrayList<Node>();
    traverseArcs = new ArrayList<Arc>();
  }
}

void calcOffset() {
  /**
  Calculates the amount all geometry should be offset to center it.
  */
  PVector[] ends = extremes(vertices);
  if(noDraw) {
    offset = new PVector();
  } else {
    offset = new PVector((w - (ends[1].x - ends[0].x)) / 2, (h - (ends[1].y - ends[0].y)) / 2);
  }
}

void drawLineOffset(PVector p1, PVector p2) {
  line(p1.x + offset.x, p1.y + offset.y, p2.x + offset.x, p2.y + offset.y);
}

// Input
void keyPressed() {
  String cmd;
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
  } else if(draw.get("iterate")) {
    p = p + 1 >= maxIter ? 0 : p + 1;
    q = q + 1 >= maxIter ? 0 : q + 1;
  } else {
    mouseClicked();
  }
  
}

void mouseClicked() {
  calc();
  loop();
  p = 0;
  q = 1;
  //altTreeCreate(circles, vertices);
}

void mouseMoved() {
  //traverse = traverseKruskalTrees2(circles, vertices, false);
  //traverseArcs = delaunayTraversalToArcs(traverse);
  //loop();
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
  {'r', "traversal", true},
  {'a', "traversalArcs", false},
  {'k', "kruskal", false},
  {'m', "gradient", true},
  {'q', "getTouching", false},
  {'o', "condense", false},
  {'p', "iterate", false},
  };
  conv = new HashMap<Character, String>();
  draw = new HashMap<String, Boolean>();
  for(Object[] arr : cmd) {
    conv.put((char) arr[0], (String) arr[1]);
    draw.put((String) arr[1], noDraw ? false : (boolean) arr[2]);
  }
}
