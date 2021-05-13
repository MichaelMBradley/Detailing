import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Stack;

boolean circleNearLine(float cutoff, Node c, ArrayList<PVector> vertices) {
  /**
  Returns if a given node is suitably close (cutoff) to any
  of the line segments on the polyline described by the vertices.
  */
  for(int i = 0; i < vertices.size(); i++) {
    int j = i + 1 == vertices.size() ? 0 : i + 1;  // Wraps index of next vertex to 0 to avoid index out of range
    if(distanceToSegment(vertices.get(i), vertices.get(j), c.pv) - c.r <= cutoff) {
      return true;
    }
  }
  return false;
}

float distanceToSegment(PVector v1, PVector v2, PVector test) {
  return (float) Line2D.ptSegDist(v1.x, v1.y, v2.x, v2.y, test.x, test.y);
}

HashSet<Node> randomFillAware(ArrayList<PVector> vertices) {
  return randomFillAware(vertices, 3.0f);
}

HashSet<Node> randomFillAware(ArrayList<PVector> vertices, float minimise) {
  /**
  Creates a circle packing of the given vertices.
  */
  HashSet<Node> nodes = new HashSet<Node>();
  float x, y, r, closestCircle;
  Node current;
  float[] maxs = extremes(vertices)[1];
  float minradius = max(maxs[0], maxs[1]) / (60 * minimise);
  float maxradius = minradius * 4;
  float cutoff = ((maxs[0] + maxs[1] + maxradius * 8) / 60) * (2 * minimise / 3);
  float offset = cutoff + maxradius;
  int consecutiveFailed = 0;
  while(consecutiveFailed < 1000) {
    r = random(minradius, maxradius);
    x = random(r - offset, maxs[0] + offset - r);
    y = random(r - offset, maxs[1] + offset - r);
    closestCircle = 1e6;
    for(Node n : nodes) {
      // Find overall closest circle (the actual node is irrelevant)
      closestCircle = min(closestCircle, n.distanceToRadius(x, y));
    }
    if(closestCircle < minradius) {
      // Fails if chosen position would require a node to be too small
      consecutiveFailed++;
    } else {
      current = new Node(x, y, min(maxradius, closestCircle));
      if(circleNearLine(cutoff, current, vertices)) {
        // Adds new node if given circle is near any line
        nodes.add(current);
        consecutiveFailed = 0;
      } else {
        consecutiveFailed++;
      }
    }
  }
  return nodes;
}

HashSet<Node> randomFill(int w, int h, float minimise) {
  /**
  Creates a circle packing of the given vertices.
  */
  HashSet<Node> nodes = new HashSet<Node>();
  float x, y, r, closestCircle;
  float minradius = (w + h) / (60 * minimise);
  float maxradius = minradius * 4;
  int consecutiveFailed = 0;
  while(consecutiveFailed < 1000) {
    r = random(minradius, maxradius);
    x = random(r, w - r);
    y = random(r, h - r);
    closestCircle = min(new float[] {x, y, w-x, h-y});
    for(Node n : nodes) {
      // Find overall closest circle (the actual node is irrelevant)
      closestCircle = min(closestCircle, n.distanceToRadius(x, y));
    }
    if(closestCircle < minradius) {
      // Fails if chosen position would require a node to be too small
      consecutiveFailed++;
    } else {
      nodes.add(new Node(x, y, min(maxradius, closestCircle)));
      consecutiveFailed = 0;
    }
  }
  return nodes;
}

HashSet<Node> randomFillPoisson(int w, int h, float minimise) {
  /**
  This implementation is worse than dart throwing.
  It attempts to use a 2D array to determine the
  closest other node.
  */
  ArrayList<Node> nodes = new ArrayList<Node>();
  float minradius = (w + h) / (60 * minimise);
  float maxradius = minradius * 4;
  ArrayList<ArrayList<ArrayList<Integer>>> available = new ArrayList<ArrayList<ArrayList<Integer>>>();
  for(int i = 0; i < (int) w / maxradius; i++) {
    available.add(new ArrayList<ArrayList<Integer>>());
    for(int j = 0; j < (int) h / maxradius; j++) {
      available.get(i).add(new ArrayList<Integer>());
    }
  }
  Node curr;
  Stack<Node> test = new Stack<Node>();
  HashSet<Node> nearby;
  test.push(new Node(random(minradius, w - minradius), random(minradius, h-minradius), 0));
  int locx, locy;
  float nearest;
  while(!test.empty()) {
    curr = test.pop();
    nearby = new HashSet<Node>();
    locx = (int) curr.x / w;
    locy = (int) curr.y / h;
    for(int i = max(0, locx - 1); i <= min(available.size(), locx + 1); i++) {
      for(int j = max(0, locy - 1); j <= min(available.get(0).size(), locy + 1); j++) {
        for(int a : available.get(i).get(j)) {
          nearby.add(nodes.get(a));
        }
      }
    }
    nearest = min(new float[] {curr.x, curr.y, w - curr.x, h - curr.y}) - curr.r;
    for(Node n : nearby) {
      nearest = min(nearest, n.distanceToCircle(curr));
    }
    if(nearest >= minradius) {
      curr.r = min(nearest, maxradius);
      nodes.add(curr);
      for(int i = max(0, locx - 1); i <= min(available.size(), locx + 1); i++) {
        for(int j = max(0, locy - 1); j <= min(available.get(0).size(), locy + 1); j++) {
          available.get(i).get(j).add(nodes.size()-1);
        }
      }
      for(int _ = 0; _ < 10; _++) {  // number is arbitrary
        test.push(new Node(max(0, min(w, curr.x + random(-2 * maxradius, 2 * maxradius))), max(0, min(h, curr.y + random(-2 * maxradius, 2 * maxradius))), 0));
      }
    }
  }
  return new HashSet<Node>(nodes);
}
