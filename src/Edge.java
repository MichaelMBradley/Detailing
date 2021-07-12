import lombok.Getter;
import processing.core.PVector;

// Just for Kruskal's MST algorithm
public final class Edge implements Comparable<Edge> {
	@Getter private final Node n1, n2;
	@Getter private final float len;
	
	public Edge(Node first, Node second) {
		n1 = first;
		n2 = second;
		len = PVector.dist(n1.getPV(), n2.getPV());
	}
	
	@Override
	public int compareTo(Edge edge) {
		return Float.compare(len, edge.len);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			Edge edge = (Edge) obj;
			return (n1.getPV().equals(edge.n1.getPV()) && n2.getPV().equals(edge.n2.getPV())) ||
					(n1.getPV().equals(edge.n2.getPV()) && n2.getPV().equals(edge.n1.getPV()));
		}
		return false;
	}
	@Override
	public int hashCode() {
		int hash = 13;
		hash = 37 * hash + n1.hashCode();
		hash = 37 * hash * n2.hashCode();
		return hash;
	}
}
