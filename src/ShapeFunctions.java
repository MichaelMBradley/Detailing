import org.processing.wiki.triangulate.Triangle;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import java.awt.geom.Line2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashSet;

import static processing.core.PApplet.*;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

public class ShapeFunctions {
    public static Arc arcLine(PVector p1, PVector p2) {
        float minX = min(p1.x, p2.x) * (2f / 3f) + max(p1.x, p2.x) * (1f / 3f);
        float minY = min(p1.y, p2.y) * (2f / 3f) + max(p1.y, p2.y) * (1f / 3f);
        float maxX = min(p1.x, p2.x) * (1f / 3f) + max(p1.x, p2.x) * (2f / 3f);
        float maxY = min(p1.y, p2.y) * (1f / 3f) + max(p1.y, p2.y) * (2f / 3f);
        Circle circ = triangleToCircle(p1.x, p1.y, p2.x, p2.y, Helpers.random(minX, maxX), Helpers.random(minY, maxY));
        float[] se = Smoothing.order(p1, circ.pv, p2, true);
        if (se[1] - se[0] > PI) {
            return new Arc(circ, se[1] - TWO_PI, se[0]);
        } else {
            return new Arc(circ, se[0], se[1]);
        }
    }

    public static boolean circleNearLine(float cutoff, Node c, ArrayList<PVector> vertices) {
        /*
        Returns if a given node is suitably close (cutoff) to any
        of the line segments on the polyline described by the vertices.
        */
        for (int i = 0; i < vertices.size(); i++) {
            int j = i + 1 == vertices.size() ? 0 : i + 1;  // Wraps index of next vertex to 0 to avoid index out of range
            if (distanceToSegment(vertices.get(i), vertices.get(j), c.pv) - c.r <= cutoff) {
                return true;
            }
        }
        return false;
    }

    public static float distanceToSegment(PVector v1, PVector v2, PVector test) {
        return (float) Line2D.ptSegDist(v1.x, v1.y, v2.x, v2.y, test.x, test.y);
    }

    public static PVector[] extremes(ArrayList<PVector> vertices) {
        /*
        Returns two PVectors bounding a list of PVectors.
        [min, max]
        */
        float[][] ends = {{vertices.get(0).x, vertices.get(0).y}, {vertices.get(0).x, vertices.get(0).y}};
        for (PVector pv : vertices) {
            if (pv.x < ends[0][0]) {
                ends[0][0] = pv.x;
            } else if (pv.x > ends[1][0]) {
                ends[1][0] = pv.x;
            }
            if (pv.y < ends[0][1]) {
                ends[0][1] = pv.y;
            } else if (pv.y > ends[1][1]) {
                ends[1][1] = pv.y;
            }
        }
        return new PVector[]{new PVector(ends[0][0], ends[0][1]), new PVector(ends[1][0], ends[1][1])};
    }

    public static Arc getArc(Circle n1, Circle n2, Circle n3) {
        /*
        Returns data about the arc between n1 and n2, passing through n3.
        [x, y, w, h, start, end]
        w = h
        */
        Circle arcInfo = triangleToCircle(n1.x, n1.y, n2.x, n2.y, n3.x, n3.y);
        float ang1 = PVector.sub(n1.pv, arcInfo.pv).heading();
        float ang2 = PVector.sub(n2.pv, arcInfo.pv).heading();
        if (ang1 > ang2) {
            ang2 += TWO_PI;
        }
        return new Arc(arcInfo, ang1, ang2);
    }

    public static Arc[] getArcKruskal(Node n1, Node n2) {
        ArrayList<Node> n3arr = new ArrayList<>();
        for (Node d : n1.delaunay) {
            if (d.delaunay.contains(n2)) {
                n3arr.add(d);
            }
        }
        Arc[] arcs = new Arc[n3arr.size()];
        for (int i = 0; i < n3arr.size(); i++) {
            arcs[i] = getArc(n1, n2, n3arr.get(i));
        }
        return arcs;
    }

    public static ArrayList<PVector> getPVectors(HashSet<Node> nodes) {
        ArrayList<PVector> vectors = new ArrayList<>();
        for (Node n : nodes) {
            vectors.add(n.pv);
        }
        return vectors;
    }

    public static void scaleVertices(float scalingFactor, ArrayList<PVector> vertices) {
        for (PVector pv : vertices) {
            pv.mult(scalingFactor);
        }
    }

    public static ArrayList<Circle> triangleToCircle(ArrayList<Triangle> triangles) {
        /*
        Return list of circumcircles for the triangles.
        */
        ArrayList<Circle> info = new ArrayList<>();
        for (Triangle tri : triangles) {
            info.add(triangleToCircle(tri.p1.x, tri.p1.y, tri.p2.x, tri.p2.y, tri.p3.x, tri.p3.y));
        }
        return info;
    }

    public static Circle triangleToCircle(float x1, float y1, float x2, float y2, float x3, float y3) {
        /*
        Calculates the circumcircle of a triangle.
        In short, it calculates the intersection point
        of the line perpendicular to (p1, p2)
        splitting (p1, p2) in half and the same line for
        (p2, p3).
        */
        float x, y, r;
        if ((x1 == x2 && x2 == x3) || (y1 == y2 && y2 == y3)) {
            // Impossible to find circumcircle for points in a straight line
            x = Float.NaN;
            y = Float.NaN;
        } else if (y1 == y2) {
            // Preventing div/0 errors for when points
            // 1 and 2 have the same y value
            x = (x1 + x2) / 2;
            y = -((x3 - x2) / (y3 - y2)) * (x - ((x2 + x3) / 2)) + ((y2 + y3) / 2);
        } else if (y2 == y3) {
            // Preventing div/0 errors for when points
            // 2 and 3 have the same y value
            x = (x2 + x3) / 2;
            y = -((x2 - x1) / (y2 - y1)) * (x - ((x1 + x2) / 2)) + ((y1 + y2) / 2);
        } else {
            x = (((x2 * x2 - x1 * x1) / (y2 - y1)) - ((x3 * x3 - x2 * x2) / (y3 - y2)) + (y1 - y3)) / (2 * (((x2 - x1) / (y2 - y1)) - ((x3 - x2) / (y3 - y2))));
            y = -((x2 - x1) / (y2 - y1)) * (x - ((x1 + x2) / 2)) + ((y1 + y2) / 2);
        }
        r = dist(x, y, x1, y1);
        return new Circle(x, y, r);
    }

    public static Polygon toPolygon(ArrayList<PVector> vertices) {
        /*
        Returns a polygon object with given vertices.
        */
        int size = vertices.size();
        int[] x = new int[size];
        int[] y = new int[size];
        float[] array;
        for (int i = 0; i < size; i++) {
            array = vertices.get(i).array();
            x[i] = (int) array[0];
            y[i] = (int) array[1];
        }
        return new Polygon(x, y, size);
    }

    public static ArrayList<PVector> toPVector(float[][] vertices) {
        /*
        Takes a list of vertices as an array of floats
        and turns it into a list of PVectors. Personally
        I just find it easier to enter vertices this way.
        */
        ArrayList<PVector> proper = new ArrayList<>();
        for(float[] vertex : vertices) {
            proper.add(new PVector(vertex[0], vertex[1]));
        }
        return proper;
    }

    public static float[][] toFloatArray(ArrayList<PVector> vertices) {
        float[][] proper = new float[vertices.size()][2];
        for(int i = 0; i < vertices.size(); i++) {
            proper[i] = PVectorToFloat(vertices.get(i));
        }
        return proper;
    }

    public static float[] PVectorToFloat(PVector pv) {
        return new float[] {pv.x, pv.y};
    }

    public static PShape toShape(ArrayList<PVector> vertices, PApplet sketch) {
        /*
        Takes a list of PVectors and turns it into a PShape
        */
        PShape polygon = sketch.createShape();
        polygon.beginShape();
        for (PVector pv : vertices) {
            polygon.vertex(pv.x, pv.y);
        }
        polygon.vertex(vertices.get(0).x, vertices.get(0).y);
        polygon.endShape();
        return polygon;
    }
}
