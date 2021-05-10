import java.util.HashSet;

boolean circleNearLine(float cutoff, Node c, ArrayList<PVector> vertices) {
  float distance;
  for(int i = 0; i < vertices.size(); i++) {
    int j = i + 1 == vertices.size() ? 0 : i + 1;
    PVector vi = vertices.get(i);
    PVector vj = vertices.get(j);
    /*
    Calculates the distance to the infinite line through v[i], v[j]
    If the angle (node, v[i], v[j]) is greater than 90deg it calculates the distance to the endpoint instead
    */
    if(PVector.angleBetween(PVector.sub(vj, vi), PVector.sub(c.pv, vi)) > HALF_PI) {
      distance = PVector.dist(vi, c.pv);
    } else if(PVector.angleBetween(PVector.sub(vi, vj), PVector.sub(c.pv, vj)) > HALF_PI) {
      distance = PVector.dist(vj, c.pv);
    } else {
      float x1 = vi.array()[0];
      float y1 = vi.array()[1];
      float x2 = vj.array()[0];
      float y2 = vj.array()[1];
      distance = abs(((x2-x1)*(y1-c.y)-(x1-c.x)*(y2-y1)))/PVector.dist(vi, vj);
    }
    if(distance - c.r <= cutoff) {
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
