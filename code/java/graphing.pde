import java.util.ArrayList;
import java.util.HashSet;

HashSet<HashSet<Node>> createGraphs(ArrayList<Node> nodes) {
  HashSet<HashSet<Node>> graphs = new HashSet<HashSet<Node>>();
  for(Node n : nodes) {
    for(Node t : n.touching) {
      n.graphing(t);
    }
  }
  for(Node n : nodes) {
    graphs.add(n.graph);
  }
  return graphs;
}
