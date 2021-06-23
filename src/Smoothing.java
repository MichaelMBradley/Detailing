import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Smoothing {
    public static Circle[] getAdjacent(Circle n1, Circle n2, float r0, boolean exterior) {
        /*
        Returns the two Circles touching the both the two given Circles.
        if sign(x2-x1) != sign(y2-y1):  # +/-, -/+
           [0] is (higher y than) line passing through centres
        if sign(x2-x1) == sign(y2-y1):  # +/+, -/-
           [0] is (lower y than) line passing through centres
        if x1==x2:
           [0] is to the right
        if y1==y2:
           [0] is the lower circle (higher y)
        */
        float dist = PVector.dist(n1.pv,n2.pv);
        if(dist + n2.r < n1.r || dist + n1.r < n2.r || dist > n1.r + n2.r + (r0 * 2)) {
            // Circle 1 contains Circle 2 || Circle 2 contains Circle 1 || circles are too far apart
            return new Circle[]{ new Circle(), new Circle() };
        }
        float ml, bl, aq, bq, cq, xa1, ya1, xa2, ya2;
        float x1 = n1.x;
        float y1 = n1.y;
        float r1 = n1.r;
        float x2 = n2.x;
        float y2 = n2.y;
        float r2 = n2.r;
        int inv = exterior ? 1 : -1;
        if(abs(y1 - y2) > 1){
            ml = - (x2 - x1) / (y2 - y1);
            bl = (-pow(x1, 2) + pow(x2, 2) - pow(y1, 2) + pow(y2, 2) + pow(r1, 2) - pow(r2, 2) + (2 * r0 * (r1 - r2)) * inv) / (2 * (y2 - y1));
            aq = 1 + pow(ml, 2);
            bq = 2 * (ml * (bl - y1) - x1);
            cq = pow(x1, 2) + pow(bl - y1, 2) - pow(r1 + r0 * inv, 2);
            if(pow(bq, 2) < 4 * aq * cq) {
                return new Circle[] { new Circle(), new Circle() };
            }
            xa1 = (-bq + sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
            ya1 = ml * xa1 + bl;
            xa2 = (-bq - sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
            ya2 = ml * xa2 + bl;
        } else {
            ml = - (y2 - y1) / (x2 - x1);
            bl = (-pow(x1, 2) + pow(x2, 2) - pow(y1, 2) + pow(y2, 2) + pow(r1, 2) - pow(r2, 2) + (2 * r0 * (r1 - r2)) * inv) / (2 * (x2 - x1));
            aq = 1 + pow(ml, 2);
            bq = 2 * (ml * (bl - x1) - y1);
            cq = pow(y1, 2) + pow(bl - x1, 2) - pow(r1 + r0 * inv, 2);
            if(pow(bq, 2) < 4 * aq * cq){
                return new Circle[] { new Circle(), new Circle() };
            }
            ya1 = (-bq + sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
            xa1 = ml * ya1 + bl;
            ya2 = (-bq - sqrt(pow(bq, 2) - 4 * aq * cq)) / (2 * aq);
            xa2 = ml * ya2 + bl;
        }
        return new Circle[]{new Circle(xa1,ya1,r0),new Circle(xa2,ya2,r0)};
    }

    public static Circle[]getExterior(Circle n1, Circle n2) {
        Circle[] adj = getAdjacent(n1, n2, (float) (triCircleAdjacent(n1, n2, getAdjacent(n1, n2, min(n1.r,n2.r), true)[0])[1].r * 0.9), true);
        if(adj[0].distanceToCircle(adj[1]) > adj[0].r && n1.distanceToCircle(n2) < min(n1.r, n2.r) / 2) {
            return adj;
        }
        float angle = PVector.sub(n2.pv, n1.pv).heading() + 0.01f;
        return getAdjacent(n1, n2, triCircleAdjacent(n1, n2, new Circle((n1.x + n1.r * cos(angle) + n2.x + n2.r * cos(PI + angle)) / 2f,(n1.y + n1.r * sin(angle) + n2.y + n2.r * sin(PI + angle)) / 2f, (n1.r + n2.r) / 8))[0].r, true);
    }

    public static Circle[] getInterior(Circle n1, Circle n2) {
        return getAdjacent(n1, n2, (n1.r + n2.r - PVector.dist(n1.pv, n2.pv)) / 2f, false);
    }

    public static Circle[] triCircleAdjacent(Circle n1, Circle n2, Circle n3) {
        // John Alexiou (https://math.stackexchange.com/users/3301/john-alexiou), Calculate the circle that touches three other circles, URL (version: 2019-07-18): https://math.stackexchange.com/q/3290944
        // [0] is on opposite side of line (n1, n2) as n3
        float x1 = n1.x;
        float y1 = n1.y;
        float r1 = n1.r;
        float x2 = n2.x;
        float y2 = n2.y;
        float r2 = n2.r;
        float x3 = n3.x;
        float y3 = n3.y;
        float r3 = n3.r;
        float Ka = -pow(r1, 2) + pow(r2, 2) + pow(x1, 2) - pow(x2, 2) + pow(y1, 2) - pow(y2, 2);
        float Kb = -pow(r1, 2) + pow(r3, 2) + pow(x1, 2) - pow(x3, 2) + pow(y1, 2) - pow(y3, 2);
        float D = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
        float A0 = (Ka * (y1 - y3) + Kb * (y2 - y1 )) / (2 * D);
        float B0 = -(Ka * (x1 - x3) + Kb * (x2 - x1)) / (2 * D);
        float A1 = -(r1 * (y2 - y3) + r2 * (y3 - y1) + r3 * (y1 - y2)) / D;
        float B1 = (r1 * (x2 - x3) + r2 * (x3 - x1) + r3 * (x1 - x2)) / D;
        float C0 = pow(A0 - x1, 2) + pow(B0 - y1, 2) - pow(r1, 2);
        float C1 = A1 * (A0 - x1) + B1 * (B0 - y1) - r1;
        float C2 = pow(A1, 2) + pow(B1, 2) - 1;
        float r4 = (-C1 + sqrt(pow(C1, 2) - C0 * C2)) / C2;
        float r5 = (-C1 - sqrt(pow(C1, 2) - C0 * C2)) / C2;
        float x4 = A0 + A1 * r4;
        float y4 = B0 + B1 * r4;
        float x5 = A0 + A1 * r5;
        float y5 = B0 + B1 * r5;
        return new Circle[] { new Circle(x4, y4, r4), new Circle(x5, y5, r5) };
    }

    public static ArrayList<Arc> surroundingArcs(ArrayList<Node> nodes) {
        ArrayList<ArrayList<Node>>trees = new ArrayList<ArrayList<Node>>();
        ArrayList<Arc> arcs = new ArrayList<Arc>();
        trees.add(new ArrayList<Node>());
        trees.get(0).add(new Node());
        for(Node n : nodes){
            if(!trees.get(trees.size() - 1).get(0).kruskal.contains(n)){
                trees.add(new ArrayList<Node>());
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
        ArrayList<Circle> n = (ArrayList<Circle>) nodes.clone();
        n.add(next);
        return surroundingArcsTree(n);
    }

    public static ArrayList<Arc> surroundingArcsTree(ArrayList<?extends Circle> nodes) {
        if(nodes.size()==0){
            return new ArrayList<Arc>();
        }
        ArrayList<Arc> arcs = new ArrayList<Arc>();
        ArrayList<Circle> arcCircles = new ArrayList<Circle>();
        ArrayList<Integer> tri = new ArrayList<Integer>();
        Circle ni, nj, nc, n;
        float[] se;
        int choose;
        for(int i = 1; i < nodes.size() - 1; i++) {
            ni=nodes.get(i);
            nj=nodes.get(i == 0 ? nodes.size() - 1 : i - 1);
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
            nc = getExterior(ni, nj)[choose];
            arcCircles.add(nc);
            arcCircles.add(ni);
        }
        /*
        for(int i = 3; i <= arcCircles.size() - 3; i += 2) {
          //println(PVector.angleBetween(PVector.sub(arcCircles.get(i+2).pv, arcCircles.get(i).pv), PVector.sub(arcCircles.get(i-2).pv, arcCircles.get(i).pv)) + "\t" +
          //(PVector.angleBetween(PVector.sub(arcCircles.get(i + 2).pv, arcCircles.get(i).pv), PVector.sub(arcCircles.get(i - 2).pv, arcCircles.get(i).pv)) < HALF_PI * 1.25f && arcCircles.get(i + 2) != arcCircles.get(i - 2)));
          //println("\t" + arcCircles.get(i - 2).pv + "\t" + arcCircles.get(i).pv + "\t" + arcCircles.get(i+2).pv);
          if(PVector.angleBetween(PVector.sub(arcCircles.get(i + 2).pv, arcCircles.get(i).pv), PVector.sub(arcCircles.get(i - 2).pv, arcCircles.get(i).pv)) < HALF_PI * 1.25f && arcCircles.get(i + 2) != arcCircles.get(i - 2)) {
            arcCircles.set(i, triCircleAdjacent(arcCircles.get(i - 2), arcCircles.get(i + 2), arcCircles.get(i))[1]);
            arcCircles.remove(i + 1);
            arcCircles.remove(i - 1);
            tri.add(i - 1);
          }
        }
        */
        if(arcCircles.size() > 2){
            for(int i = 0; i < arcCircles.size(); i++){
                n = arcCircles.get(i);
                ni = arcCircles.get(i == 0 ? arcCircles.size() - 2 : i - 1);
                nj = arcCircles.get(i >= arcCircles.size() - 2 ? arcCircles.size() - i : i + 1);
                se = order(ni.pv, n.pv, nj.pv, (i % 2 == 0));
                arcs.add(new Arc(n, se[0], se[1]));
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
