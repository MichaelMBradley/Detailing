import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static java.util.Objects.isNull;
import static processing.core.PConstants.*;

public class Smoothing {
	public static ArrayList<Curve> surroundingArcs(ArrayList<Node> nodes, HashSet<Node> exterior) {
		ArrayList<Curve> arcTree, arcs = new ArrayList<>();
		Arc arc, newArc;
		boolean out;
		Node base = nodes.get(0);
		ArrayList<ArrayList<Node>> trees = new ArrayList<>(Collections.singletonList(new ArrayList<>(Collections.singletonList(base))));
		for(Node n : nodes.subList(1, nodes.size())) {
			if(trees.get(trees.size() - 1).get(0).getKruskal().contains(n)) {
				trees.get(trees.size() - 1).add(n);
			} else {
				trees.add(new ArrayList<>(Collections.singletonList(n)));
			}
		}
		for(ArrayList<Node> tree : trees) {
			arcs.add(null);
			out = exterior.contains(tree.get(0));
			arcs.addAll(surroundingArcsTree(tree, out));
		}
		for(int i = 0; i < arcs.size(); i++) {
			if(isNull(arcs.get(i))) {
				if(isNull(arcs.get(i == arcs.size() - 1 ? 0 : i + 1))) {
					arcs.remove(i);
					i--;
				}
			}
		}
		return getBezier(arcs);
	}
	public static ArrayList<Arc> surroundingArcsTree(ArrayList<? extends Circle> nodes, boolean clockwise) {
		ArrayList<Arc> arcs = new ArrayList<>();
		ArrayList<Circle> baseCircles = new ArrayList<>();
		Circle closeCircle;
		Arc prev, next;
		float closeDist, test;
		boolean tri, overlap = true;
		int rel;
		if(nodes.size() == 0) {
			return arcs;
		}
		for(int i = 0; i < nodes.size(); i++) {
			baseCircles.add(nodes.get(i));
			baseCircles.add(Adjacent.getExterior(nodes.get(i), nodes.get(i == nodes.size() - 1 ? 0 : i + 1))[clockwise ? 1 : 0]);
		}
		baseCircles.remove(baseCircles.size() - 1);
		while(overlap) {
			overlap = false;
			for(int i = 2; i < baseCircles.size() - 2; i += 2) {
				//If circle(prev, this) overlaps circle(this, next)
				prev = new Arc(baseCircles.get(i - 1), baseCircles.get(i - 2), baseCircles.get(i), !clockwise, !clockwise);
				next = new Arc(baseCircles.get(i + 1), baseCircles.get(i), baseCircles.get(i + 2), !clockwise, !clockwise);
				if(prev.overlaps(next)) {//baseCircles.get(i - 1).overlaps(baseCircles.get(i + 1))) {
					// If the average distance from a circle to the adjacent circles is less than the distance between the circles
					tri = (baseCircles.get(i).distanceToCircle(baseCircles.get(i + 2)) + baseCircles.get(i).distanceToCircle(baseCircles.get(i - 2))) / 3f
							< baseCircles.get(i + 2).distanceToCircle(baseCircles.get(i - 2));
					if(tri) {
						baseCircles.set(i, Adjacent.triCircleAdjacentSafer(baseCircles.get(i - 2), baseCircles.get(i), baseCircles.get(i + 2))[1]);
						//baseCircles.set(i, Adjacent.getExteriorSafe(baseCircles.get(i - 2), baseCircles.get(i + 2), clockwise));
					}
					// else, or current r too big: current.r > (prev.r + next.r) * 2
					if(!tri || baseCircles.get(i).getR() > (baseCircles.get(i - 2).getR() + baseCircles.get(i + 2).getR()) * 2) {
						baseCircles.set(i, Adjacent.getExterior(baseCircles.get(i - 2), baseCircles.get(i + 2))[clockwise ? 1 : 0]);
					}
					baseCircles.remove(i + 1);
					baseCircles.remove(i - 1);
					i -= 2;
					overlap = true;
				}
			}
		}
		// Swap 1 -> 3 for a somewhat cleaner curve, but with less detail in the centre
		int help = 2;
		for(int i = help; i < baseCircles.size() - help; i++) {
			arcs.add(new Arc(baseCircles.get(i), baseCircles.get(i - 1), baseCircles.get(i + 1), clockwise == (i % 2 == 0), clockwise == (i % 2 == 0)));
		}
		return arcs;
	}
	private static ArrayList<Curve> getBezier(ArrayList<Curve> arcs) {
		int prev, next;
		for(int i = 0; i < arcs.size(); i++) {
			if(isNull(arcs.get(i))) {
				prev = i == 0 ? arcs.size() - 1 : i - 1;
				next = i == arcs.size() - 1 ? 0 : i + 1;
				((Arc) arcs.get(prev)).setEndAngle((Arc) arcs.get(next), false);
				((Arc) arcs.get(next)).setStartAngle((Arc) arcs.get(prev), false);
				arcs.set(i, new Bezier(arcs.get(prev), arcs.get(next)));
				//arcs.set(i, new Clothoid(-8 * HALF_PI, 8 * HALF_PI, arcs.get(prev).getEndPVector(), arcs.get(next).getStartPVector()));
				//arcs.set(i, ShapeFunctions.connectArcs(arcs.get(prev), arcs.get(next)));
				//println(arcs.get(i) + "\t" + arcs.get(prev) + "\t" + arcs.get(next));
			}
		}
		return arcs;
	}
	
	public static ArrayList<Curve> interiorCurves(ArrayList<Curve> curves) {
		ArrayList<Curve> interior = new ArrayList<>();
		Arc newArc;
		for(Curve curve : curves) {
			if(curve instanceof Arc) {
				newArc = new Arc((Arc) curve);
				newArc.setR(newArc.getR() + (newArc.isConnecting() ? -0.5f : 0.5f));
				interior.add(newArc);
			} else if(curve instanceof Bezier) {
				Collections.addAll(interior, ((Bezier) curve).getAdjacent(0.5f, 10));
			}
			
		}
		return interior;
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
