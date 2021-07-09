import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashSet;

import static processing.core.PApplet.*;

public class Touching {
	public static void condense(HashSet<Node> nodes) {
        /*
        Combine many graphs into one by moving the first graph
        to be touching it's closest neighbour. Keeps doing this
        until there is only one graph left.
        */
		ArrayList<HashSet<Node>> graphs = createTouchingGraphs(nodes);
		Node closeBase, closeNode;
		PVector moveVector;
		float closeDistance, tempCD;
		while (graphs.size() > 1) {
			closeBase = new Node();
			closeNode = new Node();
			closeDistance = 1e6f;  // Arbitrary large number
			// Find closest node outside the first graph to the first graph
			for (Node n : graphs.get(0)) {  // For every node in the first graph
				for (int i = 1; i < graphs.size(); i++) {
					for (Node o : graphs.get(i)) {  // For every node not in the first graph
						tempCD = n.distanceToCircle(o);
						//println(n + " " + o + " " + tempCD);
						if (tempCD < closeDistance) {  // Save closest pair
							closeDistance = tempCD;
							closeBase = n;
							closeNode = o;
						}
					}
				}
			}
			// Move all nodes on first graph towards other closest node
			moveVector = PVector.sub(closeNode.pv, closeBase.pv);
			moveVector.setMag(moveVector.mag() - closeBase.r - closeNode.r);
			for (Node n : graphs.get(0)) {
				n.move(moveVector);
			}
			graphs = createTouchingGraphs(nodes);
		}
	}
	
	public static ArrayList<HashSet<Node>> createTouchingGraphs(HashSet<Node> nodes) {
        /*
        Takes a list of nodes, returns the set of sets of touching nodes.
        */
		ArrayList<HashSet<Node>> graphs = new ArrayList<>();
		HashSet<Node> used = new HashSet<>();
		for (Node n : nodes) {
			n.resetGraph();
			n.findTouching(nodes);
		}
		for (Node n : nodes) {
			for (Node t : n.touching) {
				n.graphing(t);
			}
		}
		for (Node n : nodes) {
			if (!used.contains(n)) {
				graphs.add(n.graph);
				used.addAll(n.graph);
			}
		}
		return graphs;
	}
	
	@SuppressWarnings("SuspiciousNameCombination")
	public static Circle[] getAdjacent(Circle n1, Circle n2, float r0, boolean exterior) {
        /*
        Returns the two Circles touching both of the two given Circles.
        if sign(x2-x1) != sign(y2-y1):  # +/-, -/+
           [0] is (higher y than) line passing through centres
        if sign(x2-x1) == sign(y2-y1):  # +/+, -/-
           [0] is (lower y than) line passing through centres
        if x1==x2:
           [0] is to the right
        if y1==y2:
           [0] is the lower circle (higher y)
        */
		float dist = PVector.dist(n1.pv,n2.pv);
		if(dist + n2.r < n1.r || dist + n1.r < n2.r || dist > n1.r + n2.r + (r0 * 2)) {
			// Circle 1 contains Circle 2 || Circle 2 contains Circle 1 || circles are too far apart
			return new Circle[] { new Circle(Float.NaN, Float.NaN, Float.NaN), new Circle(Float.NaN, Float.NaN, Float.NaN) };
		}
		if(abs(n1.y - n2.y) > 1){
			return getAdjacentCalc(n1.x, n1.y, n1.r, n2.x, n2.y, n2.r, r0, exterior);
		} else {
			// Swapping x/y still returns valid circles and avoids div/0 errors
			Circle[] circles = getAdjacentCalc(n1.y, n1.x, n1.r, n2.y, n2.x, n2.r, r0, exterior);
			circles[0].setLocation(circles[0].y, circles[0].x);
			circles[1].setLocation(circles[1].y, circles[1].x);
			return new Circle[] {circles[1], circles[0]};
		}
	}
	public static Circle[] getAdjacentCalc(float x1, float y1, float r1, float x2, float y2, float r2, float r0, boolean exterior) {
		float ml, bl, aq, bq, cq, xa1, ya1, xa2, ya2;
		int inv = exterior ? 1 : -1;
		ml = - (x2 - x1) / (y2 - y1);
		bl = (-pow(x1, 2) + pow(x2, 2) - pow(y1, 2) + pow(y2, 2) + pow(r1, 2) - pow(r2, 2) + (2 * r0 * (r1 - r2)) * inv) / (2 * (y2 - y1));
		aq = 1 + pow(ml, 2);
		bq = 2 * (ml * (bl - y1) - x1);
		cq = pow(x1, 2) + pow(bl - y1, 2) - pow(r1 + r0 * inv, 2);
		if(pow(bq, 2) < 4 * aq * cq) {
			return new Circle[] { new Circle(Float.NaN, Float.NaN, Float.NaN), new Circle(Float.NaN, Float.NaN, Float.NaN) };
		}
		xa1 = (-bq + sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
		ya1 = ml * xa1 + bl;
		xa2 = (-bq - sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
		ya2 = ml * xa2 + bl;
		if(y2 < y1) {
			return new Circle[]{new Circle(xa1, ya1, r0), new Circle(xa2, ya2, r0)};
		} else {
			return new Circle[]{new Circle(xa2, ya2, r0), new Circle(xa1, ya1, r0)};
		}
	}
	
	public static Circle[] getExterior(Circle n1, Circle n2) {
		float angle = PVector.sub(n2.pv, n1.pv).heading() + 0.01f;
		float dist = n1.distanceToCenter(n2) / 2f;
		return getAdjacent(n1, n2,
				triCircleAdjacent(n1, n2,
						new Circle(n1.x + dist * cos(angle),
								n1.y + dist * sin(angle),
								(n1.r + n2.r) / 3)//8)
				)[0].r, true);
	}
	public static Circle[] getExteriorSafe(Circle n1, Circle n2) {
		return getAdjacent(n1, n2, (float) (triCircleAdjacent(n1, n2, getAdjacent(n1, n2, min(n1.r,n2.r), true)[0])[1].r * 0.9), true);
	}
	public static Circle[] getInterior(Circle n1, Circle n2) {
		return getAdjacent(n1, n2, (n1.r + n2.r - PVector.dist(n1.pv, n2.pv)) / 2f, false);
	}
	
	public static Circle[] triCircleAdjacent(Circle n1, Circle n2, Circle n3) {
		// John Alexiou (https://math.stackexchange.com/users/3301/john-alexiou), Calculate the circle that touches three other circles, URL (version: 2019-07-18): https://math.stackexchange.com/q/3290944
		// [0] is on opposite side of line (n1, n2) as n3
		float x1 = n1.x;
		float y1 = n1.y;
		float r1 = n1.r;
		float x2 = n2.x;
		float y2 = n2.y;
		float r2 = n2.r;
		float x3 = n3.x;
		float y3 = n3.y;
		float r3 = n3.r;
		float Ka = -pow(r1, 2) + pow(r2, 2) + pow(x1, 2) - pow(x2, 2) + pow(y1, 2) - pow(y2, 2);
		float Kb = -pow(r1, 2) + pow(r3, 2) + pow(x1, 2) - pow(x3, 2) + pow(y1, 2) - pow(y3, 2);
		float D = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
		float A0 = (Ka * (y1 - y3) + Kb * (y2 - y1 )) / (2 * D);
		float B0 = -(Ka * (x1 - x3) + Kb * (x2 - x1)) / (2 * D);
		float A1 = -(r1 * (y2 - y3) + r2 * (y3 - y1) + r3 * (y1 - y2)) / D;
		float B1 = (r1 * (x2 - x3) + r2 * (x3 - x1) + r3 * (x1 - x2)) / D;
		float C0 = pow(A0 - x1, 2) + pow(B0 - y1, 2) - pow(r1, 2);
		float C1 = A1 * (A0 - x1) + B1 * (B0 - y1) - r1;
		float C2 = pow(A1, 2) + pow(B1, 2) - 1;
		float r4 = (-C1 + sqrt(pow(C1, 2) - C0 * C2)) / C2;
		float r5 = (-C1 - sqrt(pow(C1, 2) - C0 * C2)) / C2;
		float x4 = A0 + A1 * r4;
		float y4 = B0 + B1 * r4;
		float x5 = A0 + A1 * r5;
		float y5 = B0 + B1 * r5;
		// println(String.format("%.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f", Ka, Kb, D, A0, B0, A1, B1, C0, C1, C2));
		// println(Ka + " " + Kb + " " + D + " " + A0 + " " + B0 + " " + A1 + " " + B1 + " " + C0 + " " + C1 + " " + C2);
		// println(String.format("%s %s %s (%.2f, %.2f, %.2f) (%.2f, %.2f, %.2f)", n1, n2, n3, x4, y4, r4, x5, y5, r5));
		return new Circle[] { new Circle(x4, y4, r4), new Circle(x5, y5, r5) };
	}
	public static Circle[] triCircleAdjacentSafer(Circle lockStart, Circle mid, Circle lockEnd) {
		return triCircleAdjacentSafer(lockStart, mid, lockEnd, 0.75f);
	}
	public static Circle[] triCircleAdjacentSafer(Circle lockStart, Circle mid, Circle lockEnd, float lerpFactor) {
		return triCircleAdjacent(lockStart, new Circle(PVector.lerp(PVector.lerp(lockStart.pv, lockEnd.pv, lockStart.r / lockStart.distanceToCenter(lockEnd)), PVector.lerp(lockEnd.pv, lockStart.pv, lockEnd.r / lockEnd.distanceToCenter(lockStart)), 0.5f).lerp(mid.pv, lerpFactor), mid.r), lockEnd);
	}
}
