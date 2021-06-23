import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static processing.core.PApplet.sqrt;

public class treecreation {
    public static void kruskal(HashSet<Node> nodes) {
        kruskal(nodes, (int) sqrt(sqrt(nodes.size())));
    }

    public static void kruskal(HashSet<Node> nodes, int restrictSize) {
        /**
         Creates a minimum spanning tree of the nodes.
         */
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Node b : nodes) {
            for (Node t : b.delaunay) {
                edges.add(new Edge(b, t));
            }
        }
        Collections.sort(edges);
        for (Edge e : edges) {
            e.n1.addKruskal(e.n2, restrictSize);
        }
    }

    public static void kruskalWithin(HashSet<Node> nodes, int restrictSize) {
        /**
         Creates a minimum spanning tree of the nodes.
         */
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Node b : nodes) {
            for (Node t : b.delaunay) {
                if (nodes.contains(t)) {
                    edges.add(new Edge(b, t));
                }
            }
        }
        Collections.sort(edges);
        for (Edge e : edges) {
            e.n1.addKruskal(e.n2, restrictSize);
        }
    }

    public static void randomTreeCreate(HashSet<Node> nodes, ArrayList<PVector> vertices) {
        /**
         Creates trees by starting at the polyline and randomly adding close unclaimed nodes to itself.
         */
        HashSet<Node> touching = getNodesTouchingPolyline(nodes, vertices);
        HashSet<Node> valid;
        Node use;
        boolean add = true;
        // While there are still more nodes to add
        while (add) {
            add = false;
            for (Node n : touching) {
                valid = new HashSet<Node>();
                for (Node k : n.kruskal) {
                    for (Node d : k.delaunay) {
                        if (d.kruskal.size() == 0 && !touching.contains(d)) {
                            // Add node connected by triangulation if it is unclaimed and is not touching the polyline
                            valid.add(d);
                        }
                    }
                }
                if (valid.size() > 0) {
                    add = true;
                    use = helpers.randomFromHashSet(valid);
                    // Add random available node, if one is available
                    for (Node k : n.kruskal) {
                        if (k.delaunay.contains(use)) {
                            k.addKruskal(use, -1);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void treeNearest(HashSet<Node> nodes, ArrayList<PVector> vertices) {
        /**
         Creates trees based on the closest node touching the polyline to each node available.
         Uses a random weighting to create variability in the size of the trees.
         */
        HashSet<Node> touching = getNodesTouchingPolyline(nodes, vertices);
        HashMap<Node, Float> weights = new HashMap<Node, Float>();
        HashMap<Node, HashSet<Node>> groups = new HashMap<Node, HashSet<Node>>();
        float dist, testdist;
        boolean valid;
        Node close = new Node();
        for (Node t : touching) {
            weights.put(t, helpers.random(1, 3));
            groups.put(t, new HashSet<Node>());
            groups.get(t).add(t);
        }
        for (Node n : nodes) {
            if (!touching.contains(n)) {
                dist = Float.MAX_VALUE;
                for (Node t : touching) {
                    testdist = n.distanceToCircle(t) * weights.get(t);
                    if (testdist < dist) {
                        dist = testdist;
                        close = t;
                    }
                }
                groups.get(close).add(n);
            }
        }
        for (Node g : touching) {
            if (groups.get(g).size() == 1) {
                dist = Float.MAX_VALUE;
                for (Node t : touching) {
                    testdist = g.distanceToCircle(t);
                    if (testdist > 0 && testdist < dist && groups.keySet().contains(t)) {
                        close = t;
                        dist = testdist;
                    }
                }
                groups.remove(g);
                groups.get(close).add(g);
            }
        }
        for (HashSet<Node> g : groups.values()) {
            kruskalWithin(g, -1);
        }
        for (Node n : nodes) {
            // Checks for trees that are unconnected to nodes touching the polyline
            valid = false;
            for (Node t : touching) {
                if (n.kruskal.contains(t)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                dist = Float.MAX_VALUE;
                close = new Node();
                for (Node d : n.delaunay) {
                    if (!n.kruskal.contains(d)) {
                        testdist = n.distanceToCircle(d);
                        if (testdist < dist) {
                            close = d;
                            dist = testdist;
                        }
                    }
                }
                if (close.r != 0f) {
                    n.addKruskal(close);
                }
            }
        }
    }

    public static HashSet<Node> getNodesTouchingPolyline(HashSet<Node> nodes, ArrayList<PVector> vertices) {
        /**
         Helper function that returns the set of nodes intersected by a segment of the given polyline.
         */
        HashSet<Node> touching = new HashSet<Node>();
        for (Node n : nodes) {
            for (int i = 0; i < vertices.size(); i++) {
                if (shapefunctions.distanceToSegment(vertices.get(i), vertices.get(i + 1 == vertices.size() ? 0 : i + 1), n.pv) < n.r) {
                    touching.add(n);
                    n.kruskal.add(n);
                    //println(n.pv, distanceToSegment(vertices.get(i), vertices.get(i + 1 == vertices.size() ? 0 : i + 1), n.pv), vertices.get(i), vertices.get(i + 1 == vertices.size() ? 0 : i + 1));
                    break;
                }
            }
        }
        return touching;
    }
}
