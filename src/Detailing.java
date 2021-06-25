/*
TODO: Implement smoothing for connections between delaunay arcs
TODO: Fix smoothing for arcs connecting touching circles
*/

import megamu.mesh.Delaunay;

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
	HashSet<Node> nodes, interior, exterior;
	int w, h, p, q, maxIter = 0;
	float zoom = 2f;
	PShape shape;
	PVector offset;
	
	final float minimise = 5;
	final boolean doTests = false;
	
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
		// float[][] initVertices = {{0, m}, {100 - m, 0}, {100, 100 - m}, {m, 100}};
		// Mouse input example:
		// float[][] initVertices = {{12, 8}, {25, 8}, {16, 20}, {0, 10}, {8, 0}, {25, 2}};
		// More complex vertices:
		float[][] initVertices = {{0, 0}, {12, 0}, {12, 9}, {18, 9}, {12, 15}, {3, 12}};
		vertices = ShapeFunctions.toPVector(initVertices);
		ShapeFunctions.scaleVertices((float) w / 30, vertices);
		shape = ShapeFunctions.toShape(vertices, this);
		offset = Helpers.calcOffset(vertices, w, h, doTests);
		calc();
	}
	
	public void draw() {
		background(255);
		if (doTests) {
			Test.runTest(this);
		}
		gridZoom();
		if (draw.get("numCircles")) {
			fill(0);
			text(String.format("Circles: %d", nodes.size()), 30 - offset.x, 30 - offset.y);
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
				Helpers.drawLine(traverse.get(i).pv, traverse.get(i + 1).pv, this);
			}
			stroke(0, 0, 255);
			Helpers.drawLine(traverse.get(traverse.size() - 1).pv, traverse.get(0).pv, this);
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
			// things can be done with p and q
			// keyPressed(); // ?
			maxIter = traverseArcs.size();
			stroke(0, 255, 0);
			traverseArcs.get(p).draw(this);
		}
	}
	
	public void drawNodes(HashSet<Node> circles, ArrayList<Circle> circumcircles, boolean drawCircles) {
        /*
        Nodes may be stored in multiple discrete sets.
        This function draws relevant information for all nodes in a given set.
        */
		if (!doTests) {
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
						Helpers.drawLine(n.pv, t.pv, this);
					}
				}
				if (draw.get("delaunay")) {
					stroke(255, 0, 0);
					strokeWeight(1);
					for (Node t : n.delaunay) {
						Helpers.drawLine(n.pv, t.pv, this);
					}
				}
				if (draw.get("kruskal")) {
					stroke(0, 255, 0);
					strokeWeight(1);
					for (Node t : n.kruskalAdjacent) {
						Helpers.drawLine(n.pv, t.pv, this);
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
	
	public void gridZoom() {
		/*
		Manages drawing the debug grid and the zoom/pan.
		*/
		int mx, my, og_mx = mouseX, og_my = mouseY;
		scale(draw.get("zoom") ? zoom : 1);
		translate(offset.x, offset.y);
		if (draw.get("zoom")) {
			translate(w / (zoom * 2) - mouseX, h / (zoom * 2) - mouseY);
		}
		if (draw.get("snap") && draw.get("grid")) {
			mx = mouseX - (int) offset.x;
			my = mouseY - (int) offset.y;
			for (Node n : nodes) {
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
		mouseX = og_mx;
		mouseY = og_my;
	}
	
	public ArrayList<Circle> analyze(HashSet<Node> aCircles) {
        /*
        The Delaunay Triangulation and tree generation is done separately for the interior and exterior circles.
        */
		ArrayList<Circle> circumcircles;
		int start;
		start = millis();
		Delaunay d = DelaunayMethods.delaunayMesh(aCircles);
		circumcircles = ShapeFunctions.delaunayMeshToCircle(d, aCircles);
		println(String.format("\tTriangulation: %.3f", (float) (millis() - start) / 1000));
		start = millis();
		// kruskal(aCircles);
		// altTreeCreate(aCircles, vertices);
		TreeCreation.treeNearest(aCircles, vertices);
		println(String.format("\tKruskal: %.3f", (float) (millis() - start) / 1000));
		return circumcircles;
	}
	
	public void calc() {
        /*
        Completes all relevant calculations.
        */
		if (!doTests) {
			int start;
			start = millis();
			nodes = CirclePacking.randomFillAware(vertices, minimise);
			println(String.format("Packing (rejection): %.3f\tCircles: %d\tCirc/Sec: %.2f", (float) (millis() - start) / 1000, nodes.size(), nodes.size() / ((float) (millis() - start) / 1000)));
			if (draw.get("condense")) {
				start = millis();
				Touching.condense(nodes);  // Takes a similar amount of time as the circle packing. Only use if you need to ensure all circles are touching.
				println(String.format("Condensing: %.3f", (float) (millis() - start) / 1000));
			}
			if (draw.get("getTouching") && !draw.get("condense")) {
				start = millis();
				Touching.createTouchingGraphs(nodes);
				println(String.format("Touching: %.3f", (float) (millis() - start) / 1000));
			}
			println("-Interior-");
			interior = Traversal.containing(vertices, nodes, true);
			interiorCircumcircles = analyze(interior);
			println("-Exterior-");
			exterior = Traversal.containing(vertices, nodes, false);
			exteriorCircumcircles = analyze(exterior);
			start = millis();
			traverse = TreeSelection.traverseTreesBase(nodes, vertices, true);
			traverseArcs = Traversal.delaunayTraversalToArcs(traverse);
			//traverse = TreeSelection.traverseTreesSkip(circles, vertices, true);
			//traverseArcs = Smoothing.surroundingArcs(traverse);
			println(String.format("Traversal: %.3f", (float) (millis() - start) / 1000));
			println("\n");
		} else {
			nodes = new HashSet<>();
			interiorCircumcircles = new ArrayList<>();
			exteriorCircumcircles = new ArrayList<>();
			traverse = new ArrayList<>();
			traverseArcs = new ArrayList<>();
		}
	}
	
	// Input
	public void keyPressed() {
		String cmd;
		if (key == 'h') {
			StringBuilder out = new StringBuilder("Draw:\n");
			for (char c : conv.keySet()) {
				out.append(c).append(": ").append(conv.get(c)).append("\n");
			}
			print(out.toString());
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
		conv = new HashMap<>();
		draw = new HashMap<>();
		for (Object[] arr : cmd) {
			conv.put((char) arr[0], (String) arr[1]);
			draw.put((String) arr[1], !doTests && (boolean) arr[2]);
		}
	}
	
	public static void main(String[] args) {
		PApplet.runSketch(new String[] {"Detailing"}, new Detailing());
	}
}
