import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashSet;

public class Helpers {
    public static PVector calcOffset(ArrayList<PVector> vertices, int w, int h, boolean noDraw) {
        /*
        Calculates the amount all geometry should be offset to center it.
        */
        PVector[] ends = ShapeFunctions.extremes(vertices);
        if (noDraw) {
            return new PVector();
        } else {
            return new PVector((w - (ends[1].x - ends[0].x)) / 2, (h - (ends[1].y - ends[0].y)) / 2);
        }
    }

    public static void drawLine(PVector p1, PVector p2, PApplet sketch) {
        sketch.line(p1.x, p1.y, p2.x, p2.y);
    }

    public static HashSet<HashSet<Node>> getMSTs(HashSet<Node> nodes) {
        /*
        Returns a HashSet of the minimum spanning trees.
        */
        HashSet<HashSet<Node>> MSTs = new HashSet<HashSet<Node>>();
        for (Node n : nodes) {
            MSTs.add(n.kruskal);
        }
        return MSTs;
    }

    public static float random(float max) {
        return random(0f, max);
    }

    public static float random(float max, float min) {
        return (float) (Math.random() * (max - min) + min);
    }

    public static Node randomFromHashSet(HashSet<Node> h) {
        float r = random(h.size());
        int i = 0;
        Node node = new Node();
        for (Node n : h) {
            node = n;
            if (i > r) {
                break;
            }
            i++;
        }
        return node;
    }
}