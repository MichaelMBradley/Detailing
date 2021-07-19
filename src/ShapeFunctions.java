import megamu.mesh.Delaunay;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ShapeFunctions {
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
		return new PVector[]{ new PVector(ends[0][0], ends[0][1]), new PVector(ends[1][0], ends[1][1]) };
	}
	
	public static ArrayList<PVector> getPVectors(HashSet<Node> nodes) {
		ArrayList<PVector> vectors = new ArrayList<>();
		for (Node n : nodes) {
			vectors.add(n.getPV());
		}
		return vectors;
	}
	
	public static void scaleVertices(float scalingFactor, ArrayList<PVector> vertices) {
		for (PVector pv : vertices) {
			pv.mult(scalingFactor);
		}
	}
	
	public static ArrayList<Circle> delaunayMeshToCircle(Delaunay d, HashSet<Node> nodes) {
		ArrayList<Circle> info = new ArrayList<>();
		HashMap<PVector, HashSet<PVector>> link = new HashMap<>();
		HashSet<HashSet<PVector>> tris = new HashSet<>();
		ArrayList<PVector> triInfo;
		for(Node n : nodes) {
			link.put(n.getPV(), new HashSet<>());
		}
		for(float[] line : d.getEdges()) {
			link.get(new PVector(line[0], line[1])).add(new PVector(line[2], line[3]));
			link.get(new PVector(line[2], line[3])).add(new PVector(line[0], line[1]));
		}
		for(PVector pv : link.keySet()) {
			for(PVector con : link.get(pv)) {
				for(PVector con2 : link.get(con)) {
					if (link.get(con2).contains(pv)) {
						tris.add(new HashSet<>(Arrays.asList(pv, con, con2)));
					}
				}
			}
		}
		for(HashSet<PVector> tri : tris) {
			triInfo = new ArrayList<>(tri);
			info.add(Geometry.triangleToCircle(triInfo.get(0), triInfo.get(1), triInfo.get(2)));
		}
		return info;
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
			proper.add(toPVector(vertex));
		}
		return proper;
	}
	public static PVector toPVector(float[] vertices) {
		return new PVector(vertices[0], vertices[1]);
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
