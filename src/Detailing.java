/*
Things to do:
- Implement smoothing for connections between delaunay arcs
- Fix smoothing for arcs connecting touching circles
- Add more space in between trees
*/

import org.processing.wiki.triangulate.*;

import java.util.*;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.event.MouseEvent;

public class Detailing extends PApplet {
    ArrayList<Arc> traverseArcs;
    ArrayList<Circle> interiorCircumcircles, exteriorCircumcircles;
    ArrayList<PVector> vertices;
    ArrayList<Node> traverse;
    HashSet<Node> circles, interior, exterior;
    int w, h, p, q, mx, my, ogmx, ogmy, maxIter = 0;
    float zoom = 2f;
    PShape shape;
    PVector offset;

    final float minimise = 5;
    final boolean noDraw = true;

    HashMap<Character, String> conv;
    HashMap<String, Boolean> draw;

    public void settings() {
        size(900, 900);
    }

    public void setup() {
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
        vertices = shapefunctions.toPVector(initvertices);
        shapefunctions.scaleVertices((float) w / 30, vertices);
        shape = shapefunctions.toShape(vertices, this);
        offset = helpers.calcOffset(vertices, w, h, noDraw);
        calc();
    }

    public void draw() {
        ogmx = mouseX;
        ogmy = mouseY;
        scale(draw.get("zoom") ? zoom : 1);
        translate(offset.x, offset.y);
        if (draw.get("zoom")) {
            translate(w / (zoom * 2) - mouseX, h / (zoom * 2) - mouseY);
        }
        background(255);
        if (noDraw) {
            test.test1(this);
        }
        if (draw.get("snap") && draw.get("grid")) {
            mx = mouseX - (int) offset.x;
            my = mouseY - (int) offset.y;
            for (Node n : circles) {
                if (n.distanceToCenter(mx, my) < n.r) {
                    mouseX = (int) (n.x + offset.x);
                    mouseY = (int) (n.y + offset.y);
                    break;
                }
            }
        }
        if (draw.get("grid")) {
            fill(127);
            stroke(127);
            strokeWeight(1);
            int freq = 25;
            for (int i = ((int) offset.x / freq) * -freq; i < w - offset.x; i += freq) {
                line(i, -offset.y, i, h - offset.y);
                text("" + i, i + 2, 10 - offset.y);
            }
            for (int i = ((int) offset.y / freq) * -freq; i < h - offset.y; i += freq) {
                line(-offset.x, i, w - offset.x, i);
                text("" + i, -offset.x, i + 12);
            }
            fill(0);
            stroke(0);
            text("(" + (int) (mouseX - offset.x) + ", " + (int) (mouseY - offset.y) + ")", mouseX + 2 - offset.x, mouseY - 2 - offset.y);
            line(mouseX - offset.x, -offset.y, mouseX - offset.x, h - offset.y);
            line(-offset.x, mouseY - offset.y, w - offset.x, mouseY - offset.y);
            noFill();
        }
        if (draw.get("numCircles")) {
            fill(0);
            text(String.format("Circles: %d", circles.size()), 30 - offset.x, 30 - offset.y);
            if (draw.get("zoom")) {
                text(String.format("Zoom: %.2f", zoom), 30 - offset.x, 50 - offset.y);
            }
            noFill();
        }
        if (draw.get("shape")) {
            stroke(0);
            strokeWeight(1);
            shape(shape, 0, 0);
        }
        drawNodes(interior, interiorCircumcircles, draw.get("interior"));
        drawNodes(exterior, exteriorCircumcircles, draw.get("exterior"));
        if (draw.get("traversal")) {
            stroke(255, 0, 0);
            strokeWeight(1);
            for (int i = 0; i < traverse.size() - 1; i++) {
                if (!traverse.get(i).kruskal.contains(traverse.get(i + 1))) {
                    stroke(0, 0, 255);
                } else {
                    stroke(255, 0, 0);
                }
                helpers.drawLine(traverse.get(i).pv, traverse.get(i + 1).pv, this);
            }
            stroke(0, 0, 255);
            helpers.drawLine(traverse.get(traverse.size() - 1).pv, traverse.get(0).pv, this);
        }
        if (draw.get("traversalArcs")) {
            strokeWeight(1);
            stroke(255, 0, 0);
            float r = 255;
            float b = 0;
            float chn = 255.0f / (float) traverseArcs.size();
            for (Arc a : traverseArcs) {
                if (draw.get("gradient")) {
                    stroke(r, 0, b);
                    r -= chn;
                    b += chn;
                }
                a.draw(this);
            }
        }
        if (draw.get("iterate")) {
            // codestuffs with p and q
            // keyPressed(); // ?
            maxIter = traverseArcs.size();
            stroke(0, 255, 0);
            traverseArcs.get(p).draw(this);
        }
        mouseX = ogmx;
        mouseY = ogmy;
    }

    public void drawNodes(HashSet<Node> circles, ArrayList<Circle> circumcircles, boolean drawCircles) {
        /**
         Nodes may be stored in multiple discrete sets.
         This function draws relevant information for all nodes in a given set.
         */
        if (!noDraw) {
            for (Node n : circles) {
                if (drawCircles) {
                    stroke(0);
                    strokeWeight(1);
                    n.draw(this);
                }
                if (draw.get("touching")) {
                    stroke(0, 0, 255);
                    strokeWeight(1);
                    for (Node t : n.touching) {
                        helpers.drawLine(n.pv, t.pv, this);
                    }
                }
                if (draw.get("delaunay")) {
                    stroke(255, 0, 0);
                    strokeWeight(1);
                    for (Node t : n.delaunay) {
                        helpers.drawLine(n.pv, t.pv, this);
                    }
                }
                if (draw.get("kruskal")) {
                    stroke(0, 255, 0);
                    strokeWeight(1);
                    for (Node t : n.kruskalAdjacent) {
                        helpers.drawLine(n.pv, t.pv, this);
                    }
                }
            }
            if (draw.get("circumcircles")) {
                stroke(255, 0, 0);
                strokeWeight(1);
                for (Circle c : circumcircles) {
                    c.draw(this);
                }
            }
        }
    }

    public ArrayList<Circle> analyze(HashSet<Node> aCircles) {
        /**
         The Delaunay Triangulation and tree generation is done seperately for the interior and exterior circles.
         This was made into a function to avoid repeating code.
         */
        ArrayList<Circle> circum;
        int start;
        start = millis();
        ArrayList<Triangle> triangles = delaunay.delaunayTriangulation(aCircles);
        circum = shapefunctions.triangleToCircle(triangles);
        delaunay.updateDelaunay(aCircles, triangles);
        println(String.format("\tTriangulation: %.3f", (float) (millis() - start) / 1000));
        start = millis();
        //kruskal(aCircles);
        //altTreeCreate(aCircles, vertices);
        treecreation.treeNearest(aCircles, vertices);
        println(String.format("\tKruskal: %.3f", (float) (millis() - start) / 1000));
        return circum;
    }

    public void calc() {
        /**
         Completes all relevant calculations.
         */
        if (!noDraw) {
            int start;
            start = millis();
            circles = circlepacking.randomFillAware(vertices, minimise);
            println(String.format("Packing (rejection): %.3f\tCircles: %d\tCirc/Sec: %.2f", (float) (millis() - start) / 1000, circles.size(), circles.size() / ((float) (millis() - start) / 1000)));
            if (draw.get("condense")) {
                start = millis();
                touching.condense(circles);  // Takes a similar amount of time as the circle packing. Only use if you need to ensure all circles are touching.
                println(String.format("Condensing: %.3f", (float) (millis() - start) / 1000));
            }
            if (draw.get("getTouching") && !draw.get("condense")) {
                start = millis();
                touching.createTouchingGraphs(circles);
                println(String.format("Touching: %.3f", (float) (millis() - start) / 1000));
            }
            println("-Interior-");
            interior = traversal.containing(vertices, circles, true);
            interiorCircumcircles = analyze(interior);
            println("-Exterior-");
            exterior = traversal.containing(vertices, circles, false);
            exteriorCircumcircles = analyze(exterior);
            start = millis();
            traverse = treeselection.traverseTreesBase(circles, vertices, true);
            //traverseArcs = smoothing.delaunayTraversalToArcs(traverse, vertices);
            //traverse = treeselection.traverseTreesSkip(circles, vertices, true);
            traverseArcs = smoothing.surroundingArcs(traverse);
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

    // Input
    public void keyPressed() {
        String cmd;
        if (key == 'h') {
            String out = "Draw:\n";
            for (char c : conv.keySet()) {
                out += c + ": " + conv.get(c) + "\n";
            }
            print(out);
        } else if (conv.containsKey(key)) {
            cmd = conv.get(key);
            draw.replace(cmd, !draw.get(cmd));
            println(cmd + ": " + draw.get(cmd));
            loop();
        } else if (draw.get("iterate")) {
            p = p + 1 >= maxIter ? 0 : p + 1;
            q = q + 1 >= maxIter ? 0 : q + 1;
        } else {
            mouseClicked();
        }

    }

    public void mouseClicked() {
        calc();
        loop();
        p = 0;
        q = 1;
        //altTreeCreate(circles, vertices);
    }

    public void mouseMoved() {
        //traverse = traverseKruskalTrees2(circles, vertices, false);
        //traverseArcs = delaunayTraversalToArcs(traverse);
        //loop();
    }

    public void mouseWheel(MouseEvent event) {
        zoom *= pow(0.9f, event.getCount());
    }

    public void initializeKeys() {
        // key, name, value
        Object[][] cmd = new Object[][]{
                {'g', "grid", false},
                {'c', "numCircles", true},
                {'s', "shape", true},
                {'i', "interior", true},
                {'e', "exterior", true},
                {'t', "touching", false},
                {'d', "delaunay", false},
                {'u', "circumcircles", false},
                {'r', "traversal", true},
                {'a', "traversalArcs", false},
                {'k', "kruskal", false},
                {'m', "gradient", true},
                {'q', "getTouching", false},
                {'o', "condense", false},
                {'p', "iterate", false},
                {'n', "snap", true},
                {'z', "zoom", false},
        };
        conv = new HashMap<Character, String>();
        draw = new HashMap<String, Boolean>();
        for (Object[] arr : cmd) {
            conv.put((char) arr[0], (String) arr[1]);
            draw.put((String) arr[1], !noDraw && (boolean) arr[2]);
        }
    }

    public static void main(String[] args) {
        PApplet.runSketch(new String[] {"Detailing"}, new Detailing());
    }
}
