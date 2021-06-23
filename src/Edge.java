import processing.core.PVector;

// Just for Kruskal's MST algorithm
public class Edge implements Comparable<Edge> {
    public Node n1, n2;
    public float len;

    public Edge(Node first, Node second) {
        n1 = first;
        n2 = second;
        len = PVector.dist(n1.pv, n2.pv);
    }

    @Override
    public int compareTo(Edge edge) {
        return Float.compare(len, edge.len);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge edge = (Edge) obj;
            return (n1.pv.equals(edge.n1.pv) && n2.pv.equals(edge.n2.pv)) || (n1.pv.equals(edge.n2.pv) && n2.pv.equals(edge.n1.pv));
        }
        return false;
    }
}
