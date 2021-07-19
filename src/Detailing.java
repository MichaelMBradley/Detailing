import megamu.mesh.Delaunay;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
TODO: Fix triCircleAdjacent() arcs overlapping each other
TODO: Generate path such that bezier curves don't overlap arcs
*/

public class Detailing extends PApplet {
	private ArrayList<Curve> traverseArcs, traverseArcsInterior;
	private ArrayList<Circle> interiorCircumcircles, exteriorCircumcircles;
	private ArrayList<PVector> vertices;
	private ArrayList<Node> traverse;
	private HashSet<Node> nodes, interior, exterior;
	private int w, h, p, q, maxIter = 0;
	private float zoom = 2f;
	private Test test;
	private PShape shape;
	private PVector offset;
	
	private final boolean doTest = true;
	private final String commands = "acmn";
	private final float minimise = 4;
	
	private HashMap<Character, String> conv;
	private HashMap<String, Boolean> draw;
	
	@Override public void settings() {
		size(800, 800);
	}
	@Override public void setup() {
		noFill();
		frameRate(144);
		surface.setTitle("Detailing");
		initializeKeys();
		test = new Test(this);
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
		offset = Helpers.calcOffset(vertices, w, h, doTest);
		calc();
		if(doTest) {
			println("Test mode");
		}
		StringBuilder init = new StringBuilder();
		for(char c : commands.toCharArray()) {
			init.append(conv.get(c)).append(", ");
		}
		println(String.format("\nComplexity: %.1f\nInitial settings (press 'h' for full list):\n%s\n", minimise, init));
	}
	
	@Override public void draw() {
		// calc();
		background(255);
		gridZoom();
		if (doTest) {
			test.run();
		} else {
			if (draw.get("numCircles")) {
				fill(0);
				text(String.format("Circles: %d", nodes.size()), 30 - offset.x, 30 - offset.y);
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
					if (!traverse.get(i).getKruskal().contains(traverse.get(i + 1))) {
						stroke(0, 0, 255);
					} else {
						stroke(255, 0, 0);
					}
					Helpers.drawLine(traverse.get(i).getPV(), traverse.get(i + 1).getPV(), this);
				}
				stroke(0, 0, 255);
				Helpers.drawLine(traverse.get(traverse.size() - 1).getPV(), traverse.get(0).getPV(), this);
			}
			if (draw.get("traversalArcs")) {
				strokeWeight(1);
				if (draw.get("gradient")) {
					float h = 0f;
					float chn = 360f / (float) traverseArcsInterior.size();
					colorMode(HSB, 360, 100, 100);
					for (Curve c : traverseArcsInterior) {
						stroke(h, 100, 100);
						h += chn;
						c.draw(this);
					}
					colorMode(RGB, 255, 255, 255);
				}
				stroke(0);
				traverseArcs.forEach(c -> c.draw(this));
			}
			if (draw.get("iterate")) {
				// things can be done with p and q
				// keyPressed(); // ?
				maxIter = traverseArcs.size();
				stroke(0, 255, 0);
				traverseArcs.get(p).draw(this);
			}
			//noLoop();
		}
	}
	public void drawNodes(HashSet<Node> circles, ArrayList<Circle> circumcircles, boolean drawCircles) {
		// Nodes may be stored in multiple discrete sets, this function draws relevant information for all nodes in a given set
		if(!doTest) {
			for(Node n : circles) {
				if(drawCircles) {
					stroke(0);
					strokeWeight(1);
					n.draw(this);
				}
				if(draw.get("touching")) {
					stroke(0, 0, 255);
					strokeWeight(1);
					for (Node t : n.getTouching()) {
						Helpers.drawLine(n.getPV(), t.getPV(), this);
					}
				}
				if(draw.get("delaunay")) {
					stroke(255, 0, 0);
					strokeWeight(1);
					for(Node t : n.getDelaunay()) {
						Helpers.drawLine(n.getPV(), t.getPV(), this);
					}
				}
				if(draw.get("kruskal")) {
					stroke(0, 255, 0);
					strokeWeight(1);
					for(Node t : n.getKruskalAdjacent()) {
						Helpers.drawLine(n.getPV(), t.getPV(), this);
					}
				}
			}
			if (draw.get("circumcircles")) {
				stroke(255, 0, 0);
				strokeWeight(1);
				circumcircles.forEach(c -> c.draw(this));
			}
		}
	}
	public void gridZoom() {
		// Manages drawing the debug grid and the zoom/pan
		int mx, my, og_mx = mouseX, og_my = mouseY;
		scale(draw.get("zoom") ? zoom : 1);
		translate(offset.x, offset.y);
		if (draw.get("zoom")) {
			translate(w / (zoom * 2) - mouseX, h / (zoom * 2) - mouseY);
			fill(0);
			text(String.format("Zoom: %.2f", zoom), 30 - offset.x, 50 - offset.y);
			noFill();
		}
		if (draw.get("grid")) {
			String msg = "";
			if (draw.get("snap")) {
				mx = mouseX - (int) offset.x;
				my = mouseY - (int) offset.y;
				if(draw.get("interior") || draw.get("exterior")) {
					for (Node n : nodes) {
						if (n.distanceToCenter(mx, my) < n.getR()) {
							mouseX = (int) (n.getX() + offset.x);
							mouseY = (int) (n.getY() + offset.y);
							msg = n.toString();
							break;
						}
					}
				} else if(draw.get("traversalArcs")) {
					Arc arc;
					float h;
					for(Curve a : traverseArcs) {
						if(a instanceof Arc) {
							arc = (Arc) a;
							h = PVector.sub(new PVector(mouseX - offset.x, mouseY - offset.y), arc.getPV()).heading();
							if (abs(arc.distanceToRadius(mx, my)) < 5 / 2f && arc.inRange(h)) {
								mouseX = (int) (arc.getX() + cos(h) * arc.getR() + offset.x);
								mouseY = (int) (arc.getY() + sin(h) * arc.getR() + offset.y);
								msg = a.toString();
								break;
							}
						}
					}
				}
			}
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
			text(!msg.equals("") ? msg : "(" + (int) (mouseX - offset.x) + ", " + (int) (mouseY - offset.y) + ")", mouseX + 2 - offset.x, mouseY - 2 - offset.y);
			noFill();
			line(mouseX - offset.x, -offset.y, mouseX - offset.x, h - offset.y);
			line(-offset.x, mouseY - offset.y, w - offset.x, mouseY - offset.y);
		}
		mouseX = og_mx;
		mouseY = og_my;
	}
	
	public ArrayList<Circle> analyze(HashSet<Node> aCircles) {
		// The Delaunay Triangulation and tree generation is done separately for the interior and exterior circles
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
		// Completes all relevant calculations
		if (!doTest) {
			int start;
			start = millis();
			nodes = CirclePacking.randomFillAware(vertices, minimise);
			CirclePacking.reduce(nodes);
			println(String.format("Packing (rejection): %.3f\tCircles: %d\tCirc/Sec: %.2f", (float) (millis() - start) / 1000, nodes.size(), nodes.size() / ((float) (millis() - start) / 1000)));
			if (draw.get("condense")) {
				start = millis();
				Adjacent.condense(nodes);  // Takes a similar amount of time as the circle packing. Only use if you need to ensure all circles are touching.
				println(String.format("Condensing: %.3f", (float) (millis() - start) / 1000));
			}
			if (draw.get("getTouching") && !draw.get("condense")) {
				start = millis();
				Adjacent.createTouchingGraphs(nodes);
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
			//traverse = TreeSelection.traverseTreesHull(nodes, vertices);
			traverseArcs = Smoothing.surroundingArcs(traverse, exterior);
			traverseArcsInterior = Smoothing.interiorCurves(traverseArcs);
			println(String.format("Traversal: %.3f", (float) (millis() - start) / 1000));
		} else {
			nodes = new HashSet<>();
			interiorCircumcircles = new ArrayList<>();
			exteriorCircumcircles = new ArrayList<>();
			traverse = new ArrayList<>();
			traverseArcs = new ArrayList<>();
		}
	}
	
	// Input
	@Override public void keyPressed() {
		if(doTest) {
			test.keyPressed(key);
		}
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
	@Override public void keyReleased() {
		test.keyReleased();
	}
	@Override public void mouseClicked() {
		if(!doTest) {
			calc();
			loop();
			p = 0;
			q = 1;
			//altTreeCreate(circles, vertices);
		}
	}
	@Override public void mouseWheel(MouseEvent event) {
		zoom *= pow(0.9f, event.getCount());
		if (doTest) {
			test.mouseWheel(event.getCount());
		}
	}
	@Override public void mousePressed() {
		test.mousePressed(mouseButton);
	}
	@Override public void mouseReleased() {
		test.mouseReleased();
	}
	
	public void initializeKeys() {
		// key, name, value
		Object[][] cmd = new Object[][]{
				{'a', "traversalArcs"},
				{'c', "numCircles"},
				{'d', "delaunay"},
				{'e', "exterior"},
				{'g', "grid"},
				{'i', "interior"},
				{'k', "kruskal"},
				{'m', "gradient"},
				{'n', "snap"},
				{'o', "condense"},
				{'p', "iterate"},
				{'q', "getTouching"},
				{'r', "traversal"},
				{'s', "shape"},
				{'t', "touching"},
				{'u', "circumcircles"},
				{'z', "zoom"},
		};
		conv = new HashMap<>();
		draw = new HashMap<>();
		for (Object[] arr : cmd) {
			conv.put((char) arr[0], (String) arr[1]);
			draw.put((String) arr[1], !doTest && (commands.indexOf((char) arr[0]) != -1));
		}
	}
	public static void main(String[] args) {
		PApplet.runSketch(new String[] {"Detailing"}, new Detailing());
	}
}
