import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static processing.core.PApplet.sqrt;

public class TreeCreation {
	public static void kruskal(HashSet<Node> nodes) {
		kruskal(nodes, (int) sqrt(sqrt(nodes.size())));
	}
	public static void kruskal(HashSet<Node> nodes, int restrictSize) {
		/*
		Creates a minimum spanning tree of the nodes.
		*/
		ArrayList<Edge> edges = new ArrayList<>();
		for(Node b : nodes) {
			for(Node t : b.getDelaunay()) {
				edges.add(new Edge(b, t));
			}
		}
		Collections.sort(edges);
		for(Edge e : edges) {
			e.getN1().addKruskal(e.getN2(), restrictSize);
		}
	}
	
	public static void kruskalWithin(HashSet<Node> nodes, int restrictSize) {
		/*
		Creates a minimum spanning tree of the nodes.
		*/
		ArrayList<Edge> edges = new ArrayList<>();
		for(Node b : nodes) {
			for(Node t : b.getDelaunay()) {
				if(nodes.contains(t)) {
					edges.add(new Edge(b, t));
				}
			}
		}
		Collections.sort(edges);
		for(Edge e : edges) {
			e.getN1().addKruskal(e.getN2(), restrictSize);
		}
	}
	
	public static void randomTreeCreate(HashSet<Node> nodes, ArrayList<PVector> vertices) {
		/*
		Create trees by starting at the polyline and randomly adding close unclaimed nodes to itself.
		*/
		HashSet<Node> touching = getNodesTouchingPolyline(nodes, vertices);
		HashSet<Node> valid;
		Node use;
		boolean add = true;
		// While there are still more nodes to add
		while(add) {
			add = false;
			for(Node n : touching) {
				valid = new HashSet<>();
				for(Node k : n.getKruskal()) {
					for(Node d : k.getDelaunay()) {
						if(d.getKruskal().size() == 0 && !touching.contains(d)) {
							// Add node connected by triangulation if it is unclaimed and is not touching the polyline
							valid.add(d);
						}
					}
				}
				if(valid.size() > 0) {
					add = true;
					use = Helpers.randomFromHashSet(valid);
					// Add random available node, if one is available
					for(Node k : n.getKruskal()) {
						if(k.getDelaunay().contains(use)) {
							k.addKruskal(use, -1);
							break;
						}
					}
				}
			}
		}
	}
	
	public static void separateBranches(HashSet<Node> nodes) {
		ArrayList<Node> lstNodes = new ArrayList<>(nodes);
		Node a, b;
		float mult = 0.5f;
		for (int i = 0; i < nodes.size() - 1; i++) {
			for (int j = i + 1; j < nodes.size(); j++) {
				a = lstNodes.get(i);
				b = lstNodes.get(j);
				if (!a.getKruskal().contains(b) && a.distanceToCircle(b) < (a.getR() + b.getR()) / 8) {
					//System.out.printf("%s\t%s\n", a, b);
					a.move(PVector.sub(a.getPV(), b.getPV()).setMag(a.getR() * (1 - mult)));
					b.move(PVector.sub(b.getPV(), a.getPV()).setMag(b.getR() * (1 - mult)));
					a.setR(a.getR() * mult);
					b.setR(b.getR() * mult);
				}
			}
		}
	}
	
	public static void treeNearest(HashSet<Node> nodes, ArrayList<PVector> vertices) {
		/*
		Create trees based on the closest node touching the polyline to each node available.
		Uses a random weighting to create variability in the size of the trees.
		*/
		HashSet<Node> touching = getNodesTouchingPolyline(nodes, vertices);
		HashMap<Node, Float> weights = new HashMap<>();
		HashMap<Node, HashSet<Node>> groups = new HashMap<>();
		float dist, testDist;
		boolean valid;
		Node close = new Node();
		for(Node t : touching) {
			weights.put(t, Helpers.random(1, 3));
			groups.put(t, new HashSet<>());
			groups.get(t).add(t);
		}
		for(Node n : nodes) {
			if(!touching.contains(n)) {
				dist = Float.MAX_VALUE;
				for(Node t : touching) {
					testDist = n.distanceToCircle(t) * weights.get(t);
					if(testDist < dist) {
						dist = testDist;
						close = t;
					}
				}
				groups.get(close).add(n);
			}
		}
		for(Node g : touching) {
			if(groups.get(g).size() <= 2) {
				dist = Float.MAX_VALUE;
				for(Node t : touching) {
					testDist = g.distanceToCircle(t);
					if(testDist > 0 && testDist < dist && groups.containsKey(t)) {
						close = t;
						dist = testDist;
					}
				}
				groups.remove(g);
				groups.get(close).add(g);
			}
		}
		for(HashSet<Node> g : groups.values()) {
			HashSet<Node> t = new HashSet<>();
			Node c = new Node(), b = new Node();
			for(Node n : g) {
				if(!touching.contains(n)) {
					t.add(n);
				} else {
					b = n;
				}
			}
			kruskalWithin(t, -1);
			for(Node n : g) {
				if(n.distanceToCircle(b) < c.distanceToCircle(b) && n != b) {
					c = n;
				}
			}
			if(c.getR() != 0f) {
				c.addKruskal(b);
			}
			//kruskalWithin(g, -1);
		}
		for(Node n : nodes) {
			// Checks for trees that are unconnected to nodes touching the polyline
			valid = false;
			for(Node t : touching) {
				if(n.getKruskal().contains(t)) {
					valid = true;
					break;
				}
			}
			if(!valid) {
				dist = Float.MAX_VALUE;
				close = new Node();
				for(Node d : n.getDelaunay()) {
					if(!n.getKruskal().contains(d)) {
						testDist = n.distanceToCircle(d);
						if(testDist < dist) {
							close = d;
							dist = testDist;
						}
					}
				}
				if(close.getR() != 0f) {
					n.addKruskal(close);
				}
			}
		}
	}
	
	public static HashSet<Node> getNodesTouchingPolyline(HashSet<Node> nodes, ArrayList<PVector> vertices) {
		/*
		Helper function that returns the set of nodes intersected by a segment of the given polyline.
		*/
		HashSet<Node> touching = new HashSet<>();
		for(Node n : nodes) {
			for(int i = 0; i < vertices.size(); i++) {
				if(Geometry.distanceToSegment(vertices.get(i), vertices.get(i + 1 == vertices.size() ? 0 : i + 1), n.getPV()) < n.getR()) {
					touching.add(n);
					n.getKruskal().add(n);
					//println(n.getPV(), distanceToSegment(vertices.get(i), vertices.get(i + 1 == vertices.size() ? 0 : i + 1), n.getPV()), vertices.get(i), vertices.get(i + 1 == vertices.size() ? 0 : i + 1));
					break;
				}
			}
		}
		return touching;
	}
}
