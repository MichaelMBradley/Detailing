import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static processing.core.PApplet.*;

public class Smoothing {
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
	
	public static ArrayList<Arc> surroundingArcs(ArrayList<Node> nodes) {
		ArrayList<ArrayList<Node>>trees = new ArrayList<>();
		ArrayList<Arc> arcs = new ArrayList<>();
		trees.add(new ArrayList<>());
		trees.get(0).add(new Node());
		for(Node n : nodes){
			if(!trees.get(trees.size() - 1).get(0).kruskal.contains(n)){
				trees.add(new ArrayList<>());
			}
			trees.get(trees.size() - 1).add(n);
		}
		trees.get(0).remove(0);
		for(int i = 0; i < trees.size(); i++){
			if(i == trees.size() - 1) {
				arcs.addAll(surroundingArcsTree(trees.get(i)));
			} else {
				arcs.addAll(surroundingArcsTree(trees.get(i), trees.get(i + 1).get(0)));
			}
		}
		return arcs;
	}
	
	public static ArrayList<Curve> fixedSurroundingArcs(ArrayList<Node> nodes, HashSet<Node> exterior) {
		ArrayList<Arc> arcTree;
		ArrayList<Curve> arcs = new ArrayList<>(), arcInside = new ArrayList<>();
		Arc arc, newArc;
		boolean out;
		Node base = nodes.get(0);
		ArrayList<ArrayList<Node>> trees = new ArrayList<>(Collections.singletonList(new ArrayList<>(Collections.singletonList(base))));
		for(Node n : nodes.subList(1, nodes.size())) {
			if(trees.get(trees.size() - 1).get(0).kruskal.contains(n)) {
				trees.get(trees.size() - 1).add(n);
			} else {
				trees.add(new ArrayList<>(Collections.singletonList(n)));
			}
		}
		for(ArrayList<Node> tree : trees) {
			arcs.add(new Arc());
			out = exterior.contains(tree.get(0));
			arcTree = fixedSurroundingArcsTree(tree, out);
			arcs.addAll(arcTree);
			arcInside.add(new Arc());
			for(int i = 0; i < arcTree.size(); i++) {
				arc = arcTree.get(i);
				arcInside.add(new Arc(arc.pv, arc.r + (0.5f * (out == (i % 2 == 0) ? 1 : -1)), arc.drawStart, arc.drawEnd));
			}
		}
		for(int i = 0; i < arcs.size(); i++) {
			if (arcs.get(i).isEmpty()) {
				if (arcs.get(i == arcs.size() - 1 ? 0 : i + 1).isEmpty()) {
					arcs.remove(i);
					arcInside.remove(i);
					i--;
				} else {
					//arcs.set(i, ShapeFunctions.connectArcs(arcs.get(i == 0 ? arcs.size() - 1 : i - 1), arcs.get(i == arcs.size() - 1 ? 0 : i + 1)));
					arcs.set(i, new Bezier(arcs.get(i == 0 ? arcs.size() - 1 : i - 1), arcs.get(i == arcs.size() - 1 ? 0 : i + 1)));
					//println(arcs.get(i) + "\t" + arcs.get(i == 0 ? arcs.size() - 1 : i - 1) + "\t" + arcs.get(i == arcs.size() - 1 ? 0 : i + 1));
					if(arcs.get(i) instanceof Arc) {
						newArc = (Arc) ((Arc) arcs.get(i)).clone();
						newArc.r -= 0.5f;
						arcInside.set(i, newArc);
					}
					
				}
				}
		}
		arcs.addAll(arcInside);
		return arcs;
	}
	
	public static ArrayList<Arc> fixedSurroundingArcsTree(ArrayList<? extends Circle> nodes, boolean clockwise) {
		ArrayList<Arc> arcs = new ArrayList<>();
		ArrayList<Circle> ev = new ArrayList<>();
		Circle closeCircle;
		float closeDist, test;
		boolean tri, overlap = true;
		int rel;
		if(nodes.size() == 0) {
			return arcs;
		}
		for(int i = 0; i < nodes.size(); i++) {
			ev.add(nodes.get(i));
			ev.add(getExterior(nodes.get(i), nodes.get(i == nodes.size() - 1 ? 0 : i + 1))[clockwise ? 1 : 0]);
		}
		ev.remove(ev.size() - 1);
		while(overlap) {
			overlap = false;
			for (int i = 2; i < ev.size() - 2; i += 2) {
				if (ev.get(i - 1).overlaps(ev.get(i + 1))) {
					tri = (ev.get(i).distanceToCircle(ev.get(i + 2)) + ev.get(i).distanceToCircle(ev.get(i - 2))) / 2f < ev.get(i + 2).distanceToCircle(ev.get(i - 2));
					if(tri) {
						ev.set(i, triCircleAdjacent(ev.get(i - 2), ev.get(i), ev.get(i + 2))[1]);
					}
					if(!tri || ev.get(i).r > (ev.get(i - 2).r + ev.get(i + 2).r) * 2) {
						ev.set(i, getExterior(ev.get(i - 2), ev.get(i + 2))[clockwise ? 1 : 0]);
					}
					ev.remove(i + 1);
					ev.remove(i - 1);
					i -= 2;
					overlap = true;
				}
			}
		}
		overlap = true;
		while(overlap) {
			overlap = false;
			for(int i = 1; i < ev.size() - 1; i += 2) {
				closeCircle = new Circle();
				closeDist = Float.MAX_VALUE;
				for(Circle c : nodes) {
					test = ShapeFunctions.distanceToSegment(ev.get(i - 1).pv, ev.get(i + 1).pv, c.pv);
					if(ev.get(i).overlaps(c) && test < closeDist && !ev.subList(i - 1, i + 2).contains(c)) {
						closeCircle = c;
						closeDist = test;
					}
				}
				if(closeDist != Float.MAX_VALUE) {
					//ev.set(i, triCircleAdjacent(ev.get(i - 1), ev.get(i + 1), closeCircle)[0]);
				}
			}
		}
		// Swap 1 -> 3 for a somewhat cleaner curve, but with less detail in the centre
		for(int i = 1; i < ev.size() - 1; i++) {
			arcs.add(new Arc(ev.get(i), ev.get(i - 1), ev.get(i + 1), clockwise == (i % 2 == 0)));
		}
		/*for(int i = 1; i < arcs.size() - 1; i += 2) {
			for(int j = i + 2; j < arcs.size() - 1; j += 2) {
				if(arcs.get(i).overlaps(arcs.get(j))) {
					rel = arcs.get(i).range() > arcs.get(j).range() ? i : j;
					arcs.set(rel, new Arc(arcs.get(rel - 1), arcs.get(rel + 1)));
				}
			}
		}*/
		return arcs;
	}
	
	public static ArrayList<Arc> surroundingArcsTree(ArrayList<? extends Circle> nodes, Circle next) {
		ArrayList<Circle> n = new ArrayList<>(nodes);
		n.add(next);
		return surroundingArcsTree(n);
	}
	
	public static ArrayList<Arc> surroundingArcsTree(ArrayList<? extends Circle> nodes) {
		if(nodes.size()==0){
			return new ArrayList<>();
		}
		ArrayList<Arc> arcs = new ArrayList<>();
		ArrayList<Circle> arcCircles = new ArrayList<>();
		Circle ni, nj, nc, n;
		float[] se;
		int choose;
		for(int i = 1; i < nodes.size() - 1; i++) {
			ni = nodes.get(i);
			nj = nodes.get(i - 1);
			// Choosing which side of the circle to put the arc on based on the direction
			if(ni.x == nj.x){
				choose = ni.y > nj.y ? 0 : 1;
			}else if(ni.y == nj.y){
				choose = ni.x > nj.x ? 1 : 0;
			}else if(nj.y>ni.y){
				choose = 1;
			}else{
				choose=0;
			}
			nc = getExterior(ni, nj)[choose];
			arcCircles.add(nc);
			arcCircles.add(ni);
		}
		if(arcCircles.size() > 2) {
			boolean overlap = true;
			int j = 0;
			while(overlap) {
				j++;
				overlap = false;
				for (int i = 3; i < arcCircles.size() - 3; i += 2) {
					if (arcCircles.get(i - 1).overlaps(arcCircles.get(i + 1))) {
						arcCircles.set(i, triCircleAdjacent(arcCircles.get(i - 2), arcCircles.get(i), arcCircles.get(i + 2))[1]);
						// arcCircles.set(i, getExterior(arcCircles.get(i - 2), arcCircles.get(i + 2))[0]);
						arcCircles.remove(i + 1);
						arcCircles.remove(i - 1);
						i -= 2;
						overlap = true;
					}
				}
			}
			//println(j);
			for(int i = 1; i < arcCircles.size() - 1; i++) {
				se = order(arcCircles.get(i - 1).pv, arcCircles.get(i).pv, arcCircles.get(i + 1).pv, (i % 2 == 0));
				arcs.add(new Arc(arcCircles.get(i), se[0], se[1]));
			}
		}
		return arcs;
	}
	
	public static float[] order(PVector pre, PVector curr, PVector next, boolean crossing){
		float start = PVector.sub(pre, curr).heading();
		float end = PVector.sub(next, curr).heading();
		if(PVector.angleBetween(PVector.sub(pre, curr), PVector.sub(next, curr)) < PI == crossing) {
			float temp = start;
			start = end;
			end = temp;
		}
		return bindStart(start, end);
	}
	
	public static float[] bindStart(float start, float end) {
		while(start>end){
			start -= TWO_PI;
		}
		while(start < end - TWO_PI){
			start += TWO_PI;
		}
		while(end< 0){
			start += TWO_PI;
			end += TWO_PI;
		}
		return new float[] {start, end};
	}
}
