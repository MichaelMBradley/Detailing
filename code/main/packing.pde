import java.awt.geom.Line2D;
import java.util.HashSet;

boolean circleNearLine(float cutoff, Node c, ArrayList<PVector> vertices) {
  for(int i = 0; i < vertices.size(); i++) {
    int j = i + 1 == vertices.size() ? 0 : i + 1;  // Wraps index of next vertex to 0 to avoid index out of range
    PVector vi = vertices.get(i);
    PVector vj = vertices.get(j);
    if(Line2D.ptSegDist(vi.x, vi.y, vj.x, vj.y, c.x, c.y) - c.r <= cutoff) {
      return true;
    }
  }
  return false;
}

HashSet<Node> randomFillAware(ArrayList<PVector> vertices) {
  HashSet<Node> nodes = new HashSet<Node>();
  float x, y, r, closestCircle;
  Node current;
  float[] maxs = extremes(vertices)[1];
  float minradius = max(maxs[0], maxs[1]) / 60;
  float maxradius = minradius * 4;
  float offset = maxradius * 2;
  float cutoff = ((maxs[0] + maxs[1] + offset * 4 ) / 60);
  int consecutiveFailed = 0;
  while(consecutiveFailed < 1000) {
    r = random(minradius, maxradius);
    x = random(r - offset, maxs[0] + offset - r);
    y = random(r - offset, maxs[1] + offset - r);
    closestCircle = 1e6;
    for(Node n : nodes) {
      closestCircle = min(closestCircle, n.distanceToRadius(x, y));
    }
    if(closestCircle < minradius) {
      consecutiveFailed++;
    } else {
      current = new Node(x, y, min(maxradius, closestCircle));
      if(circleNearLine(cutoff, current, vertices)) {
        nodes.add(current);
        consecutiveFailed = 0;
      } else {
        consecutiveFailed++;
      }
    }
  }
  return nodes;
}
