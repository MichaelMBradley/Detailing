import megamu.mesh.Hull;
import processing.core.PVector;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;

import static processing.core.PApplet.println;

public class TreeSelection {
	public static ArrayList<Node> kruskalTraverse(HashSet<Node> nodes, ArrayList<PVector> vertices) {
		// Returns one node of each MST. Each node is in order around the perimeter of the shape.
		ArrayList<HashMap<PVector, Node>> options;
		ArrayList<Node> traversal = new ArrayList<>();
		HashSet<HashSet<Node>> MSTs = new HashSet<>();
		HashMap<PVector, Node> current;
		float distance, testDist;
		PVector next = new PVector();
		for(Node n : nodes) {
			MSTs.add(n.getKruskal());
		}
		options = MSTClosestNode(MSTs, vertices);
		// Finding the closest edge to the node
		for(int i = 0; i < vertices.size(); i++) {
			current = options.get(i);
			while(!current.isEmpty()) {
				distance = Float.MAX_VALUE;
				for(PVector pv : current.keySet()) {
					testDist = PVector.dist(pv, vertices.get(i));
					if(testDist < distance) {
						distance = testDist;
						next = pv;
					}
				}
				traversal.add(current.get(next));
				current.remove(next);
			}
		}
		//println(traversal);
		return traversal;
	}
	
	public static ArrayList<HashMap<PVector, Node>> MSTClosestNode(HashSet<HashSet<Node>> MSTs, ArrayList<PVector> vertices) {
		/*
		Returns a list of pairs of points, where each HashMap on the list corresponds to a line, and each entry in the hashmap
		represents the closest point on that line to a minimum spanning tree.
		*/
		ArrayList<HashMap<PVector, Node>> options = new ArrayList<>();
		float close, test;
		int j, q;
		PVector vi, vj, testPV;
		PVector touch = new PVector();
		Node closest = new Node();
		for(int m = 0; m < vertices.size(); m++) {
			options.add(new HashMap<>());
		}
		// Finding the closest node to the edge
		for(HashSet<Node> MST : MSTs) {
			q = 0;
			close = Float.MAX_VALUE;
			for(Node n : MST) {
				for(int i = 0; i < vertices.size(); i++) {
					j = i + 1 == vertices.size() ? 0 : i + 1;
					vi = vertices.get(i);
					vj = vertices.get(j);
					testPV = Traversal.closestPoint2(vi, vj, n.getPV());
					test = (float) Line2D.ptSegDist(vi.x, vi.y, vj.x, vj.y, n.getX(), n.getY()) - n.getR();
					if(test < close) {
						touch = testPV;
						close = test;
						closest = n;
						q = i;
					}
				}
			}
			if(close == Float.MAX_VALUE) {
				println("broke");
			}
			options.get(q).put(touch, closest);
		}
		return options;
	}
	
	public static ArrayList<Node> traverseTreesBase(HashSet<Node> nodes, ArrayList<PVector> vertices, boolean includeParents) {
		/*
		Visits every kruskal (ish) tree in order, and traverses those trees in a relevant manner.
		*/
		ArrayList<Node> traverse = new ArrayList<>();
		ArrayList<Node> kruskal = kruskalTraverse(nodes, vertices);
		Node edge;
		PVector j, k = new PVector(), l = new PVector();
		float dist;
		Polygon shape = ShapeFunctions.toPolygon(vertices);
		for(Node n : kruskal) {
			dist = Float.MAX_VALUE;
			for(int i = 0; i < vertices.size(); i++) {
				j = vertices.get(i + 1 == vertices.size() ? 0 : i + 1);  // Next vertex w/ wraparound
				if(Geometry.distanceToSegment(vertices.get(i), j, n.getPV()) < dist) {
					k = vertices.get(i);
					l = j;
				}
			}
			edge = new Node(Traversal.closestPoint2(k, l, n.getPV()));
			//edge = new Node(PVector.sub(n.getPV(), Traversal.closestPoint2(k, l, n.getPV())));
			//println("\n" + edge + "\t" + PVector.sub(n.getPV(), edge.getPV()).heading());
			traverse.addAll(n.kruskalTreeTraverse(edge, !shape.contains(n.getX(), n.getY()), includeParents));
		}
		return traverse;
	}
	
	public static ArrayList<Node> traverseTreesHull(HashSet<Node> nodes, ArrayList<PVector> vertices) {
		ArrayList<Node> bases = kruskalTraverse(nodes, vertices), temp = new ArrayList<>(), traverse = new ArrayList<>();
		HashMap<PVector, Node> dict = new HashMap<>();
		Polygon p = ShapeFunctions.toPolygon(vertices);
		for(Node n : nodes) {
			dict.put(n.getPV(), n);
		}
		for(Node n : bases) {
			temp.clear();
			Arrays.stream(new Hull(ShapeFunctions.toFloatArray(ShapeFunctions.getPVectors(n.getKruskal()))).getRegion().getCoords()).iterator().forEachRemaining(f -> temp.add(dict.get(ShapeFunctions.toPVector(f))));
			if(p.contains(temp.get(0).getX(), temp.get(0).getY())) {
				Collections.reverse(temp);
			}
			traverse.addAll(temp);
		}
		return traverse;
	}
	
	public static ArrayList<Node> traverseTreesSkip(HashSet<Node> nodes, ArrayList<PVector> vertices, boolean includeParents) {
		/*
		Visits every kruskal (ish) tree in order, and traverses those trees in a relevant manner.
		This method starts and stops on the closest nodes to the adjacent trees.
		*/
		ArrayList<Node> traverse = new ArrayList<>();
		ArrayList<Node> order = new ArrayList<>();
		ArrayList<Node> kruskal = kruskalTraverse(nodes, vertices);//kruskalRecursive(nodes, vertices);//
		Node empty = new Node();
		Node next = new Node();
		Node end = new Node();
		float dist;
		Polygon shape = ShapeFunctions.toPolygon(vertices);
		for(int i = 0; i < kruskal.size(); i++) {
			dist = Float.MAX_VALUE;
			for(Node n : kruskal.get(i).getKruskal()) {
				for(Node m : kruskal.get(i + 1 == kruskal.size() ? 0 : i + 1).getKruskal()) {
					if(m.distanceToCircle(n) < dist) {
						next = m;
						end = n;
						dist = m.distanceToCircle(n);
					}
				}
			}
			order.add(end);
			order.add(next);
		}
		for(int i = 1; i < order.size(); i += 2) {
			traverse.addAll(order.get(i).kruskalTreeTraverse(empty, shape.contains(order.get(i).getX(), order.get(i).getY()), includeParents, order.get(i + 1 == order.size() ? 0 : i + 1)));
		}
		//println(order);
		return traverse;
	}
	
	public static ArrayList<Node> kruskalRecursive(HashSet<Node> nodes, ArrayList<PVector> vertices) {
		/*
		Traverses the minimum spanning trees by traversing the minimum spanning tree of the minimum spanning trees.
		*/
		HashSet<Node> base = new HashSet<>();
		HashMap<PVector, Node> conv = new HashMap<>();
		HashSet<HashSet<Node>> MSTs = Helpers.getMSTs(nodes);
		ArrayList<Node> traverse;
		float t;
		int i;
		Node temp = new Node();
		for(HashSet<Node> MST : MSTs) {
			t = Helpers.random(0, MST.size());
			i = 0;
			for(Node n : MST) {
				temp = n; //random element
				i++;
				if(i >= t) {
					break;
				}
			}
			conv.put(temp.getPV(), temp);
			base.add(new Node(temp.getX(), temp.getY(), temp.getR()));
		}
		DelaunayMethods.updateDelaunay(base);
		TreeCreation.kruskal(base);
		traverse = traverseTreesBase(base, vertices, true);
		for(i = 0; i < traverse.size(); i++) {
			traverse.set(i, conv.get(traverse.get(i).getPV()));
		}
		return traverse;
	}
}
