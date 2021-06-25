import megamu.mesh.MPolygon;
import megamu.mesh.Voronoi;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import static processing.core.PApplet.max;
import static processing.core.PApplet.min;

public class CirclePacking {
	public static HashSet<Node> randomFillAware(ArrayList<PVector> vertices, float minimise) {
        /*
        Creates a circle packing of the given vertices.
        */
		HashSet<Node> nodes = new HashSet<>();
		float x, y, r, closestCircle;
		Node current;
		PVector max = ShapeFunctions.extremes(vertices)[1];
		float maxRadius = max(max.x, max.y) / (60 * minimise) * 4;
		float minRadius = maxRadius / 2;
		float cutoff = ((max.x + max.y + maxRadius * 8) / 60) * (2 * minimise / 3);
		float offset = cutoff + maxRadius;
		int consecutiveFailed = 0;
		while (consecutiveFailed < 3000 / minimise) {
			r = Helpers.random(minRadius, maxRadius);
			x = Helpers.random(r - offset, max.x + offset - r);
			y = Helpers.random(r - offset, max.y + offset - r);
			closestCircle = 1e6f;
			for (Node n : nodes) {
				// Find overall closest circle (the actual node is irrelevant)
				closestCircle = min(closestCircle, n.distanceToRadius(x, y));
			}
			if (closestCircle < minRadius) {
				// Fails if chosen position would require a node to be too small
				consecutiveFailed++;
			} else {
				current = new Node(x, y, min(maxRadius * Helpers.random(0.75f, 1.25f), closestCircle));
				if (ShapeFunctions.circleNearLine(cutoff, current, vertices)) {
					// Adds new node if given circle is near any line
					nodes.add(current);
					consecutiveFailed = 0;
				} else {
					consecutiveFailed++;
				}
			}
		}
		return nodes;
	}
	
	public static HashSet<Node> randomFill(int w, int h, float minimise) {
        /*
        Creates a circle packing in the given area.
        */
		HashSet<Node> nodes = new HashSet<>();
		float x, y, r, closestCircle;
		float minRadius = (w + h) / (60 * minimise);
		float maxRadius = minRadius * 4;
		int consecutiveFailed = 0;
		while (consecutiveFailed < 3000 / minimise) {
			r = Helpers.random(minRadius, maxRadius);
			x = Helpers.random(r, w - r);
			y = Helpers.random(r, h - r);
			closestCircle = min(new float[]{x, y, w - x, h - y});
			for (Node n : nodes) {
				// Find overall closest circle (the actual node is irrelevant)
				closestCircle = min(closestCircle, n.distanceToRadius(x, y));
			}
			if (closestCircle < minRadius) {
				// Fails if chosen position would require a node to be too small
				consecutiveFailed++;
			} else {
				nodes.add(new Node(x, y, min(maxRadius, closestCircle)));
				consecutiveFailed = 0;
			}
		}
		return nodes;
	}
	
	public static HashSet<Node> randomFillPoisson(int w, int h, float minimise) {
        /*
        This implementation is worse than dart throwing.
        It attempts to use a 2D array to determine the
        closest other node.
        */
		ArrayList<Node> nodes = new ArrayList<>();
		float minRadius = (w + h) / (60 * minimise);
		float maxRadius = minRadius * 4;
		ArrayList<ArrayList<ArrayList<Integer>>> available = new ArrayList<>();
		for (int i = 0; i < w / maxRadius; i++) {
			available.add(new ArrayList<>());
			for (int j = 0; j < h / maxRadius; j++) {
				available.get(i).add(new ArrayList<>());
			}
		}
		Node curr;
		Stack<Node> test = new Stack<>();
		HashSet<Node> nearby;
		test.push(new Node(Helpers.random(minRadius, w - minRadius), Helpers.random(minRadius, h - minRadius), 0));
		int locX, locY;
		float nearest;
		while (!test.empty()) {
			curr = test.pop();
			nearby = new HashSet<>();
			locX = (int) curr.x / w;
			locY = (int) curr.y / h;
			for (int i = max(0, locX - 1); i <= min(available.size(), locX + 1); i++) {
				for (int j = max(0, locY - 1); j <= min(available.get(0).size(), locY + 1); j++) {
					for (int a : available.get(i).get(j)) {
						nearby.add(nodes.get(a));
					}
				}
			}
			nearest = min(new float[]{curr.x, curr.y, w - curr.x, h - curr.y}) - curr.r;
			for (Node n : nearby) {
				nearest = min(nearest, n.distanceToCircle(curr));
			}
			if (nearest >= minRadius) {
				curr.r = min(nearest, maxRadius);
				nodes.add(curr);
				for (int i = max(0, locX - 1); i <= min(available.size(), locX + 1); i++) {
					for (int j = max(0, locY - 1); j <= min(available.get(0).size(), locY + 1); j++) {
						available.get(i).get(j).add(nodes.size() - 1);
					}
				}
				for (int m = 0; m < 10; m++) {  // number is arbitrary
					test.push(new Node(max(0, min(w, curr.x + Helpers.random(-2 * maxRadius, 2 * maxRadius))), max(0, min(h, curr.y + Helpers.random(-2 * maxRadius, 2 * maxRadius))), 0));
				}
			}
		}
		return new HashSet<>(nodes);
	}
	
	public static void voronoiPacking(Collection<Node> nodes) {
		voronoiPacking(nodes, -1f, -1f);
	}
	public static void voronoiPacking(Collection<Node> nodes, ArrayList<PVector> vertices, float minimise) {
		PVector max = ShapeFunctions.extremes(vertices)[1];
		float maxRadius = max(max.x, max.y) / (60 * minimise) * 4;
		float minRadius = maxRadius / 2;
		voronoiPacking(nodes, -1f, maxRadius);
	}
	public static void voronoiPacking(Collection<Node> nodes, float minR, float maxR) {
		ArrayList<PVector> pvs = new ArrayList<>();
		float close;
		for(Node n : nodes) {
			pvs.add(n.pv);
		}
		Voronoi v = new Voronoi(ShapeFunctions.toFloatArray(pvs));
		for(MPolygon m : v.getRegions()) {
			for(float[] co : m.getCoords()) {
				if(min(co) > -100 && max(co) < 1100) {
					close = Float.MAX_VALUE;
					for (Circle c : nodes) {
						close = min(close, c.distanceToRadius(co[0], co[1]));
					}
					if (close > 0 && close != Float.MAX_VALUE && (minR == -1f || close > minR) && (maxR == -1f || close < maxR)) {
						nodes.add(new Node(co[0], co[1], close));
					}
				}
			}
		}
	}
}
