import org.processing.wiki.triangulate.Triangle;
import processing.core.PVector;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Geometry {
	public static Arc arcLine(PVector p1, PVector p2) {
		float minX = min(p1.x, p2.x) * (2f / 3f) + max(p1.x, p2.x) * (1f / 3f);
		float minY = min(p1.y, p2.y) * (2f / 3f) + max(p1.y, p2.y) * (1f / 3f);
		float maxX = min(p1.x, p2.x) * (1f / 3f) + max(p1.x, p2.x) * (2f / 3f);
		float maxY = min(p1.y, p2.y) * (1f / 3f) + max(p1.y, p2.y) * (2f / 3f);
		Circle circ = triangleToCircle(p1, p2, new PVector(Helpers.random(minX, maxX), Helpers.random(minY, maxY)));
		float[] se = Smoothing.order(p1, circ.getPV(), p2, true);
		if(se[1] - se[0] > PI) {
			return new Arc(circ, se[1] - TWO_PI, se[0]);
		} else {
			return new Arc(circ, se[0], se[1]);
		}
	}
	
	public static Arc connectArcs(Arc a1, Arc a2) {
		return arcLine(a1.PVectorOnCircumference(a1.getEndAngleBase()), a1.PVectorOnCircumference(a1.getStartAngleBase()));
	}
	
	public static boolean circleNearLine(float cutoff, Circle c, ArrayList<PVector> vertices) {
		/*
		Returns if a given node is suitably close (cutoff) to any
		of the line segments on the polyline described by the vertices.
		*/
		int j;
		for(int i = 0; i < vertices.size(); i++) {
			j = i + 1 == vertices.size() ? 0 : i + 1;
			if(distanceToSegment(vertices.get(i), vertices.get(j), c.getPV()) - c.getR() <= cutoff) {
				return true;
			}
		}
		return false;
	}
	
	public static float nearestLineDistance(Circle c, ArrayList<PVector> vertices) {
		int j;
		float close = Float.MAX_VALUE;
		for(int i = 0; i < vertices.size(); i++) {
			j = (i + 1) % vertices.size();
			close = min(close, distanceToSegment(vertices.get(i), vertices.get(j), c.getPV()) - c.getR());
		}
		return close;
	}
	
	public static float distanceToSegment(PVector v1, PVector v2, PVector test) {
		return (float) Line2D.ptSegDist(v1.x, v1.y, v2.x, v2.y, test.x, test.y);
	}
	public static Arc getArc(Circle n1, Circle n2, Circle n3) {
		/*
		Returns the arc between n1 and n2, passing through n3.
		*/
		Circle arcInfo = triangleToCircle(n1.getPV(), n2.getPV(), n3.getPV());
		float ang1 = PVector.sub(n1.getPV(), arcInfo.getPV()).heading();
		float ang2 = PVector.sub(n2.getPV(), arcInfo.getPV()).heading();
		if(ang1 > ang2) {
			ang2 += TWO_PI;
		}
		return new Arc(arcInfo, ang1, ang2);
	}
	
	public static Arc[] getArcKruskal(Node n1, Node n2) {
		ArrayList<Node> n3arr = new ArrayList<>();
		for(Node d : n1.getDelaunay()) {
			if(d.getDelaunay().contains(n2)) {
				n3arr.add(d);
			}
		}
		Arc[] arcs = new Arc[n3arr.size()];
		for(int i = 0; i < n3arr.size(); i++) {
			arcs[i] = new Arc(n1.getPV(), n2.getPV(), n3arr.get(i).getPV());
		}
		return arcs;
	}
	
	public static ArrayList<Circle> triangleToCircle(ArrayList<Triangle> triangles) {
		/*
		Return list of circumcircles for the triangles.
		*/
		ArrayList<Circle> info = new ArrayList<>();
		for(Triangle tri : triangles) {
			info.add(triangleToCircle(tri.p1, tri.p2, tri.p3));
		}
		return info;
	}
	public static Circle triangleToCircle(PVector pv1, PVector pv2, PVector pv3) {
		return triangleToCircle(pv1.x, pv1.y, pv2.x, pv2.y, pv3.x, pv3.y);
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
		if((x1 == x2 && x2 == x3) || (y1 == y2 && y2 == y3)) {
			// Impossible to find circumcircle for points in a straight line
			x = Float.NaN;
			y = Float.NaN;
		} else if(y1 == y2) {
			// Preventing div/0 errors for when points
			// 1 and 2 have the same y value
			x = (x1 + x2) / 2;
			y = -((x3 - x2) / (y3 - y2)) * (x - ((x2 + x3) / 2)) + ((y2 + y3) / 2);
		} else {
			if(y2 == y3) {
				// Preventing div/0 errors for when points
				// 2 and 3 have the same y value
				x = (x2 + x3) / 2;
			} else {
				x = (((x2 * x2 - x1 * x1) / (y2 - y1)) - ((x3 * x3 - x2 * x2) / (y3 - y2)) + (y1 - y3)) / (2 * (((x2 - x1) / (y2 - y1)) - ((x3 - x2) / (y3 - y2))));
			}
			y = -((x2 - x1) / (y2 - y1)) * (x - ((x1 + x2) / 2)) + ((y1 + y2) / 2);
		}
		r = dist(x, y, x1, y1);
		return new Circle(x, y, r);
	}
}
