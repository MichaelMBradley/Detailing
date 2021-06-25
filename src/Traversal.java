import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Traversal {
	public static Node closestNode(HashSet<Node> nodes, PVector vertex) {
		Node close = new Node();
		float tempDist;
		float distance = 1e6f;
		for (Node n : nodes) {
			tempDist = PVector.dist(vertex, n.pv);
			if (tempDist < distance) {
				distance = tempDist;
				close = n;
			}
		}
		return close;
	}
	
	public static PVector closestPoint(PVector p1, PVector p2, PVector p0) {
        /*
        Uses basic point-slope formulas to find the intersection of (p1, p2)
        and the line perpendicular to (p1, p2) passing through the node.
        Slightly slower.
        */
		if (p1.x == p2.x) {
			return new PVector(p1.x, p0.y);
		}
		if (p1.y == p2.y) {
			return new PVector(p0.x, p1.y);
		}
		float m = (p2.y - p1.y) / (p2.x - p1.x);
		float mi = -1 / m;
		float x = (p1.x * m - p0.x * mi + p0.y - p1.y) / (m - mi);
		float y = m * (x - p1.x) + p1.y;
		return new PVector(x, y);
	}
	
	public static PVector closestPoint2(PVector p1, PVector p2, PVector p0) {
        /*
        Projects the node onto the vector between (p1, p2) to find the closest point.
        Slightly faster.
        */
		PVector y = PVector.sub(p0, p1);
		PVector u = PVector.sub(p2, p1);
		return u.mult(y.dot(u) / u.magSq()).add(p1);
	}
	
	public static HashSet<Node> containing(ArrayList<PVector> vertices, HashSet<Node> nodes, boolean inside) {
        /*
        Accepts a set of nodes, returns the subset
        that is either inside or outside the shape.
        */
		HashSet<Node> side = new HashSet<>();
		Polygon shape = ShapeFunctions.toPolygon(vertices);
		for (Node n : nodes) {
			if (shape.contains(n.x, n.y) == inside) {
				side.add(n);
			}
		}
		return side;
	}
	
	public static boolean inLine(PVector p1, PVector p2, PVector test) {
        /*
        Tests if PVector test is in the line described by p1, p2
        by determining of it's distance to the closest point on that
        line is approximately 0 (for rounding errors).
        */
		return PVector.dist(test, closestPoint2(p1, p2, test)) < 1;
	}
	
	public static ArrayList<Arc> delaunayTraversalToArcs(ArrayList<Node> traversal) {
		ArrayList<Arc> arcs = new ArrayList<>();
		System.out.println(traversal.size());
		for (int i = 0; i < traversal.size() - 1; i++) {
			if (traversal.get(i).kruskalAdjacent.contains(traversal.get(i + 1))) {
				arcs.add(ShapeFunctions.getArcKruskal(traversal.get(i), traversal.get(i + 1))[0]);//All(Arrays.asList(ShapeFunctions.getArcKruskal(traversal.get(i), traversal.get(i + 1))));//
				System.out.println(arcs.get(arcs.size() - 1));
			} else {
				arcs.add(ShapeFunctions.arcLine(traversal.get(i).pv, traversal.get(i + 1).pv));
			}
		}
		arcs.add(ShapeFunctions.arcLine(traversal.get(traversal.size() - 1).pv, traversal.get(0).pv));
		return arcs;
	}
}
