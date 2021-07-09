import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static processing.core.PApplet.*;

public class Smoothing {
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
						newArc = new Arc((Arc) arcs.get(i));
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
			ev.add(Touching.getExterior(nodes.get(i), nodes.get(i == nodes.size() - 1 ? 0 : i + 1))[clockwise ? 1 : 0]);
		}
		ev.remove(ev.size() - 1);
		while(overlap) {
			overlap = false;
			for (int i = 2; i < ev.size() - 2; i += 2) {
				if (ev.get(i - 1).overlaps(ev.get(i + 1))) {
					tri = (ev.get(i).distanceToCircle(ev.get(i + 2)) + ev.get(i).distanceToCircle(ev.get(i - 2))) / 2f < ev.get(i + 2).distanceToCircle(ev.get(i - 2));
					if(tri) {
						ev.set(i, Touching.triCircleAdjacentSafer(ev.get(i - 2), ev.get(i), ev.get(i + 2))[1]);
					}
					if(!tri || ev.get(i).r > (ev.get(i - 2).r + ev.get(i + 2).r) * 2) {
						ev.set(i, Touching.getExterior(ev.get(i - 2), ev.get(i + 2))[clockwise ? 1 : 0]);
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
					test = Geometry.distanceToSegment(ev.get(i - 1).pv, ev.get(i + 1).pv, c.pv);
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
		int help = 1;
		for(int i = help; i < ev.size() - help; i++) {
			arcs.add(new Arc(ev.get(i), ev.get(i - 1), ev.get(i + 1), clockwise == (i % 2 == 0), clockwise == (i % 2 == 0)));
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
	
	public static ArrayList<Arc> surroundingArcs(ArrayList<Node> nodes) {
		ArrayList<ArrayList<Node>>trees = new ArrayList<>(Collections.singletonList(new ArrayList<>(Collections.singletonList(new Node()))));
		ArrayList<Arc> arcs = new ArrayList<>();
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
			nc = Touching.getExterior(ni, nj)[choose];
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
						arcCircles.set(i, Touching.triCircleAdjacent(arcCircles.get(i - 2), arcCircles.get(i), arcCircles.get(i + 2))[1]);
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
