import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static java.util.Objects.isNull;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

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
			arcTree = fixedSurroundingArcsTree(tree, out);
			arcs.addAll(arcTree);
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
	public static ArrayList<Curve> fixedSurroundingArcsTree(ArrayList<? extends Circle> nodes, boolean clockwise) {
		ArrayList<Curve> arcs = new ArrayList<>();
		ArrayList<Circle> ev = new ArrayList<>();
		Circle closeCircle;
		Curve prev, next;
		float closeDist, test;
		boolean tri, overlap = true;
		int rel;
		if(nodes.size() == 0) {
			return arcs;
		}
		for(int i = 0; i < nodes.size(); i++) {
			ev.add(nodes.get(i));
			ev.add(Adjacent.getExterior(nodes.get(i), nodes.get(i == nodes.size() - 1 ? 0 : i + 1))[clockwise ? 1 : 0]);
		}
		ev.remove(ev.size() - 1);
		while(overlap) {
			overlap = false;
			for (int i = 2; i < ev.size() - 2; i += 2) {
				if (ev.get(i - 1).overlaps(ev.get(i + 1))) {
					tri = (ev.get(i).distanceToCircle(ev.get(i + 2)) + ev.get(i).distanceToCircle(ev.get(i - 2))) / 2f < ev.get(i + 2).distanceToCircle(ev.get(i - 2));
					if(tri) {
						ev.set(i, Adjacent.triCircleAdjacentSafer(ev.get(i - 2), ev.get(i), ev.get(i + 2))[1]);
					}
					if(!tri || ev.get(i).getR() > (ev.get(i - 2).getR() + ev.get(i + 2).getR()) * 2) {
						ev.set(i, Adjacent.getExterior(ev.get(i - 2), ev.get(i + 2))[clockwise ? 1 : 0]);
					}
					ev.remove(i + 1);
					ev.remove(i - 1);
					i -= 2;
					overlap = true;
				}
			}
		}
		/*overlap = true;
		while(overlap) {
			overlap = false;
			for(int i = 1; i < ev.size() - 1; i += 2) {
				closeCircle = new Circle();
				closeDist = Float.MAX_VALUE;
				for(Circle c : nodes) {
					test = Geometry.distanceToSegment(ev.get(i - 1).getPV(), ev.get(i + 1).getPV(), c.getPV());
					if(ev.get(i).overlaps(c) && test < closeDist && !ev.subList(i - 1, i + 2).contains(c)) {
						closeCircle = c;
						closeDist = test;
					}
				}
				if(closeDist != Float.MAX_VALUE) {
					ev.set(i, Adjacent.triCircleAdjacent(ev.get(i - 1), ev.get(i + 1), closeCircle)[0]);
				}
			}
		}*/
		// Swap 1 -> 3 for a somewhat cleaner curve, but with less detail in the centre
		int help = 1;
		for(int i = help; i < ev.size() - help; i++) {
			arcs.add(new Arc(ev.get(i), ev.get(i - 1), ev.get(i + 1), clockwise == (i % 2 == 0), clockwise == (i % 2 == 0)));
		}
		return arcs;
	}
	
	public static ArrayList<Curve> interiorCurves(ArrayList<Curve> curves) {
		ArrayList<Curve> interior = new ArrayList<>();
		Arc newArc;
		for(Curve curve : curves) {
			if (curve instanceof Arc) {
				newArc = new Arc((Arc) curve);
				newArc.setR(newArc.getR() + (newArc.isConnecting() ? -0.5f : 0.5f));
			} else {
				newArc = null;
			}
			interior.add(newArc);
		}
		return getBezier(interior);
	}
	private static ArrayList<Curve> getBezier(ArrayList<Curve> arcs) {
		for(int i = 0; i < arcs.size(); i++) {
			if (isNull(arcs.get(i))) {
				//arcs.set(i, ShapeFunctions.connectArcs(arcs.get(i == 0 ? arcs.size() - 1 : i - 1), arcs.get(i == arcs.size() - 1 ? 0 : i + 1)));
				arcs.set(i, new Bezier(arcs.get(i == 0 ? arcs.size() - 1 : i - 1), arcs.get(i == arcs.size() - 1 ? 0 : i + 1)));
				//println(arcs.get(i) + "\t" + arcs.get(i == 0 ? arcs.size() - 1 : i - 1) + "\t" + arcs.get(i == arcs.size() - 1 ? 0 : i + 1));
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
