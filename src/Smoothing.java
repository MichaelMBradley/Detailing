import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static java.util.Objects.isNull;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

public class Smoothing {
	public static ArrayList<Circle> spacedOutBezier(Bezier b, int num) {
		// At least 4 recommended
		ArrayList<Circle> circles = new ArrayList<>();
		ArrayList<Float> dists = new ArrayList<>();
		float r = b.distance() / (num * 2f);
		for(int i = 0; i < num; i++) {
			dists.add(r * ((2 * i) + 1));
		}
		LinkedHashMap<Float, Float> lhm = b.speed(20);
		//int curr = 0;
		float past = -Float.MAX_VALUE;
		for(Float next : lhm.keySet()) {
			if(past != -Float.MAX_VALUE) {
				for(Float dist : dists) {
					if(lhm.get(past) < dist && dist < lhm.get(next)) {
						circles.add(new Circle(PVector.lerp(b.pointAt(past), b.pointAt(next),
								(dist - lhm.get(past)) / (lhm.get(next) - lhm.get(past))), r));
					}
				}
			}
			past = next;
		}
		return circles;
	}
	public static ArrayList<Arc> connectArcs(Arc from, Arc to) {
		ArrayList<Arc> arcs = new ArrayList<>();
		int num = (from.isClockwise() == to.isClockwise()) ? 3 : 2;
		ArrayList<Circle> circles = spacedOutBezier(new Bezier(from, to), num);
		circles.set(0, Adjacent.getAdjacent(from, circles.get(1), circles.get(0).getR() * 1.1f, true)[from.isClockwise() ? 1 : 0]);
		circles.set(circles.size() - 1, Adjacent.getAdjacent(circles.get(circles.size() - 2), to, circles.get(circles.size() - 2).getR() * 1.5f, true)[to.isClockwise() ? 1 : 0]);
		from.setEndAngle(circles.get(0), false);
		to.setStartAngle(circles.get(circles.size() - 1), false);
		circles.add(0, from);
		circles.add(to);
		for(int i = 1; i < circles.size() - 1; i++) {
			arcs.add(new Arc(circles.get(i), circles.get(i - 1), circles.get(i + 1), (i % 2 == 0) == from.isClockwise(), (i % 2 == 0) == from.isClockwise()));
		}
		return arcs;
	}
	
	public static void doubleCheck(ArrayList<Curve> curves) {
		Arc prev, next;
		PVector end, beg;
		for(int i = 0; i < curves.size(); i++) {
			if(curves.get(i) instanceof Arc && curves.get((i + 2) % curves.size()) instanceof Arc) {
				prev = (Arc) curves.get(i);
				next = (Arc) curves.get((i + 2) % curves.size());
				if(prev.overlaps(next)) {
					/*end = prev.getEndPVector();
					beg = next.getStartPVector();*/
					curves.set(i, new Bezier(prev, next));//Adjacent.getAdjacent(new Circle(end), new Circle(beg), end.dist(beg), true)[0]);
					curves.remove(i + 1);
					curves.remove(i + 1);
				}
			}
		}
	}
	
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
	public static ArrayList<Arc> surroundingArcsTree(ArrayList<Node> nodes, boolean clockwise) {
		ArrayList<Arc> arcs = new ArrayList<>();
		ArrayList<Circle> baseCircles = new ArrayList<>();
		Circle closeCircle, p, q;
		Arc prev, next;
		float closeDist, test;
		boolean tri, overlap = true;
		int rel;
		if(nodes.size() == 0) {
			return arcs;
		}
		for(int i = 0; i < nodes.size(); i++) {
			baseCircles.add(nodes.get(i));
			baseCircles.add(Adjacent.getExterior(nodes.get(i), nodes.get((i + 1) % nodes.size()))[clockwise ? 1 : 0]);
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
		ArrayList<Arc> bezNew;
		for(int i = 0; i < arcs.size(); i++) {
			if(isNull(arcs.get(i))) {
				prev = i == 0 ? arcs.size() - 1 : i - 1;
				next = i == arcs.size() - 1 ? 0 : i + 1;
				((Arc) arcs.get(prev)).setEndAngle((Arc) arcs.get(next), false);
				((Arc) arcs.get(next)).setStartAngle((Arc) arcs.get(prev), false);
				//arcs.set(i, new Bezier(arcs.get(prev), arcs.get(next)));
				bezNew = connectArcs((Arc) arcs.get(prev), (Arc) arcs.get(next));
				arcs.remove(i);
				arcs.addAll(i, bezNew);
				i += bezNew.size() - 1;
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
