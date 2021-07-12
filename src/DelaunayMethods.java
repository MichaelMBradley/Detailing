import megamu.mesh.Delaunay;
import megamu.mesh.Voronoi;
import org.processing.wiki.triangulate.*;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.println;
import static processing.core.PConstants.QUARTER_PI;

public class DelaunayMethods {
	public static ArrayList<Triangle> delaunayTriangulation(HashSet<Node> nodes) {
        /*
        Accepts a set of nodes, creates a triangulation of their centres.
        */
		return Triangulate.triangulate(ShapeFunctions.getPVectors(nodes));
	}
	
	public static void updateDelaunay(HashSet<Node> nodes) {
		updateDelaunay(nodes, delaunayTriangulation(nodes));
	}
	public static void updateDelaunay(HashSet<Node> nodes, ArrayList<Triangle> triangles) {
        /*
        Adds the delaunay triangulation information to the nodes.
        */
		HashMap<PVector, Node> conv = new HashMap<>();
		HashMap<PVector, HashSet<PVector>> dict = new HashMap<>();
		Node base, con;
		for (Node n : nodes) {
			conv.put(n.getPV(), n);
			dict.put(n.getPV(), new HashSet<>());
		}
		for (Triangle tri : triangles) {
			dict.get(tri.p1).add(tri.p2);
			dict.get(tri.p1).add(tri.p3);
			dict.get(tri.p2).add(tri.p1);
			dict.get(tri.p2).add(tri.p3);
			dict.get(tri.p3).add(tri.p1);
			dict.get(tri.p3).add(tri.p2);
		}
		addDelaunay(conv, dict);
	}
	
	public static Delaunay delaunayMesh(HashSet<Node> nodes) {
		PVector p1, p2;
		Node base, con;
		HashMap<PVector, Node> conv = new HashMap<>();
		HashMap<PVector, HashSet<PVector>> dict = new HashMap<>();
		for(Node n : nodes) {
			conv.put(n.getPV(), n);
			dict.put(n.getPV(), new HashSet<>());
		}
		Delaunay d = new Delaunay(ShapeFunctions.toFloatArray(new ArrayList<>(conv.keySet())));
		for(float[] p : d.getEdges()) {
			p1 = new PVector(p[0], p[1]);
			p2 = new PVector(p[2], p[3]);
			dict.get(p1).add(p2);
			dict.get(p2).add(p1);
		}
		addDelaunay(conv, dict);
		return d;
	}
	
	private static void addDelaunay(HashMap<PVector, Node> conv, HashMap<PVector, HashSet<PVector>> dict) {
		Node base, con;
		for (PVector pv : dict.keySet()) {
			base = conv.get(pv);
			for (PVector connect : dict.get(pv)) {
				con = conv.get(connect);
				if (PVector.dist(pv, connect) < (base.getR() + con.getR()) * 3) {
					base.getDelaunay().add(con);
				}
			}
		}
	}
	
	public static ArrayList<Node> delaunayTraverse(HashSet<Node> nodes, ArrayList<PVector> vertices) {
		return delaunayTraverse(nodes, vertices, 1f);
		//return delaunayTraverse(nodes, vertices, (float) mouseX * 20f / w);
	}
	public static ArrayList<Node> delaunayTraverse(HashSet<Node> nodes, ArrayList<PVector> vertices, float distanceWeight) {
        /*
        Visits many nodes in order around the polyline, based on the delaunay triangulation.
        */
		ArrayList<Node> comb, traverse = new ArrayList<>();
		float closest, angle, distance;
		int i;
		Node goal, next = new Node(), current = Traversal.closestNode(nodes, vertices.get(vertices.size() - 1));
		for (PVector vertex : vertices) {
			goal = Traversal.closestNode(nodes, vertex);
			while (current != goal) {
				if (current.getDelaunay().contains(goal)) {
					next = goal;
				} else {
					closest = Float.MAX_VALUE;
					comb = new ArrayList<>(current.getKruskal());
					comb.addAll(current.getDelaunay());
					i = 0;
					for (Node k : comb) {
						if(i == current.getKruskal().size() && closest != Float.MAX_VALUE) {
							break;
						}
						angle = abs(PVector.angleBetween(PVector.sub(k.getPV(), current.getPV()), PVector.sub(goal.getPV(), current.getPV())) - QUARTER_PI);
						distance = PVector.dist(k.getPV(), goal.getPV());
						if (angle + distance / ((goal.getR() + k.getR()) / 2) * distanceWeight < closest && !traverse.contains(k)) {
							closest = angle + distance / ((goal.getR() + k.getR()) / 2) * distanceWeight;
							next = k;
						}
						i++;
					}
					if (closest == Float.MAX_VALUE) {
						println("skip");
						next = goal;
					}
				}
				traverse.add(current);
				current = next;
			}
		}
		return traverse;
	}
	
	public static Voronoi getVoronoi(HashSet<Node> nodes) {
		return new Voronoi(ShapeFunctions.toFloatArray(ShapeFunctions.getPVectors(nodes)));
	}
	
	public static void compareDelaunaySpeeds(HashSet<Node> nodes, PApplet sketch) {
		ArrayList<PVector> pv = ShapeFunctions.getPVectors(nodes);
		float[][] po = ShapeFunctions.toFloatArray(pv);
		int t = sketch.millis();
		Delaunay test = new Delaunay(po);
		println(String.format("Mesh: %dms", sketch.millis() - t));
		t = sketch.millis();
		Triangulate.triangulate(pv);
		println(String.format("Triangulate: %dms", sketch.millis() - t));
		sketch.millis();
		Voronoi testV = new Voronoi(po);
		println(String.format("Voronoi: %dms", sketch.millis() - t));
	}
}
