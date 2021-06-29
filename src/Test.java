import megamu.mesh.Delaunay;
import megamu.mesh.MPolygon;
import megamu.mesh.Voronoi;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static processing.core.PApplet.*;
import static processing.core.PConstants.TWO_PI;

public class Test {
	public static void runTest(PApplet s) {
		test1(s);
	}
	
	public static void test1(PApplet s) {
        /*
        Attempts to smooth a series of circles.
        */
        /*
        iter = (int(mouseX)/5)*5 * PI / 180f;//+= 0.01f;
        Circle n1 = new Circle(400, 400, 50);
        Circle n2 = new Circle(n1.x + 100 * cos(iter), n1.y + 100 * sin(iter), 50);
        Circle n3 = new Circle(n2.x + 100 * cos(1.5 * iter), n2.y + 100 * sin(1.5 * iter), 50);
        Circle n4 = new Circle(n2.x + 100 * cos(1.5 * iter + HALF_PI), n2.y + 100 * sin(1.5 * iter + HALF_PI), 50);
        */
		Circle n1 = new Circle(300, 200, 50);
		Circle n2 = new Circle(300, 300, 50);
		Circle n3 = new Circle(200, 300, 50);
		Circle n4 = new Circle(115, 355, 50);
		Circle n5 = new Circle(300, 400, 50);
		Circle n6 = new Circle(300, 480, 30);
		Circle n7 = new Circle(400, 300, 50);
		Circle n8 = new Circle(470, 230, 50);
		Circle n9 = new Circle(s.mouseX, s.mouseY, s.mouseX / 10f);//470, 370, 50);
        /*
        mouseX, mouseY, mouseX/10);//
        fill(0);
        text(1 + "\n" + n1.x + " " + n1.y, n1.x, n1.y);
        text(2 + "\n" + n2.x + " " + n2.y, n2.x, n2.y);
        text(3 + "\n" + n3.x + " " + n3.y, n3.x, n3.y);
        text(4 + "\n" + n4.x + " " + n4.y, n4.x, n4.y);
        text(5 + "\n" + n5.x + " " + n5.y, n5.x, n5.y);
        text(6 + "\n" + n6.x + " " + n6.y, n6.x, n6.y);
        text(7 + "\n" + n7.x + " " + n7.y, n7.x, n7.y);
        text(8 + "\n" + n8.x + " " + n8.y, n8.x, n8.y);
        text(9 + "\n" + n9.x + " " + n9.y, n9.x, n9.y);
        noFill();
        */
		ArrayList<Circle> ns = new ArrayList<>(Arrays.asList(n1, n2, n7, n8, n7, n9, n7, n2, n5, n6, n5, n2, n3, n4, n3, n2, n1));
		s.strokeWeight(1);
		s.stroke(0, 255, 0);
        /*
        n1.draw();
        n2.draw();
        n3.draw();
        n4.draw();
        n5.draw();
        n6.draw();
        n7.draw();
        n8.draw();
        n9.draw();
        */
		s.strokeWeight(1);
		s.stroke(0);
		//println();
		for (Arc arc : Smoothing.surroundingArcsTree(ns)) {
			arc.draw(s);
		}
	}
	
	public static void test2(PApplet s) {
        /*
        Attempts to create a safe (non-overlapping) intermediate circle.
        */
		Circle n1 = new Circle(s.pixelWidth / 2f, s.pixelHeight / 2f, 50);
		Circle n2 = new Circle(s.mouseX, s.mouseY, n1.r);
		Circle n3 = new Circle(n2.x + cos(TWO_PI * ((float) s.mouseX / s.pixelWidth)) * (n2.r * 2), n2.y + sin(TWO_PI * ((float) s.mouseX / s.pixelWidth)) * (n2.r * 2), n2.r);
		s.strokeWeight(1);s.stroke(127);
		n1.draw(s);n2.draw(s);n3.draw(s);
		/*for (Circle n : Smoothing.getExterior(n1, n2)) {
			s.stroke(255, 0, 0);
			n.draw(s);
			for (Circle j : Smoothing.triCircleAdjacent(n1, n2, n)) {
				s.stroke(0, 0, 255);
				j.draw(s);
			}
		}*/
		Circle first = Smoothing.getExterior(n1, n2)[0];
		Circle second = Smoothing.getExterior(n2, n1)[1];
		Circle first2 = Smoothing.getExterior(n2, n3)[0];
		Circle second2 = Smoothing.getExterior(n3, n2)[1];
		first.draw(s);second.draw(s);first2.draw(s);second2.draw(s);
		// a1 -> aFirst -> a2 -> aFirst2 -> a3 -> aSecond2 -> a2 -> aSecond -> a1
		Arc a1 = new Arc(n1, second, first, false);
		Arc aFirst = new Arc(first, n1, n2, true);
		Arc a2 = new Arc(n2, first, first2, false);
		Arc aFirst2 = new Arc(first2, n2, n3, true);
		Arc a3 = new Arc(n3, first2, second2, false);
		Arc aSecond2 = new Arc(second2, n3, n2, true);
		Arc a22 = new Arc(n2, second2, second, false);
		Arc aSecond = new Arc(second, n2, n1, true);
		s.strokeWeight(3);s.stroke(0);
		a1.draw(s);a2.draw(s);a3.draw(s);a22.draw(s);aFirst.draw(s);aSecond.draw(s);aFirst2.draw(s);aSecond2.draw(s);
		s.fill(0);
		s.text("a22: "+a22,a22.x,a22.y);
		//s.text("a1: "+a1,a1.x,a1.y);s.text("a2: "+a2,a2.x,a2.y);s.text("a3: "+a3,a3.x,a3.y);
		//s.text("aFirst: "+aFirst,aFirst.x,aFirst.y);s.text("aSecond: "+aSecond,aSecond.x,aSecond.y);s.text("aFirst2: "+aFirst2,aFirst2.x,aFirst2.y);s.text("aSecond2: "+aSecond2,aSecond2.x,aSecond2.y);
		s.noFill();
	}
	
	public static void test3(PApplet s) {
        /*
        Creates a slight curve.
        */
		s.strokeWeight(5);
		s.stroke(255, 0, 0);
		s.point(100, 100);
		s.point(s.mouseX, s.mouseY);
		s.strokeWeight(1);
		s.stroke(0);
		s.randomSeed(0L);
		ShapeFunctions.arcLine(new PVector(100, 100), new PVector(s.mouseX, s.mouseY)).draw(s);
	}
	
	public static void test4(PApplet s) {
        /*
        Creates a circle in the overlapping area two existing circles.
        */
		s.strokeWeight(1);
		s.stroke(0);
        /*
        Circle n1 = new Circle(w / 2, h / 2, (w + h) / 4);//new Circle(mouseY, mouseX, (w + h) / 4);//
        loadPixels();
        float r0, ml, bl, aq, bq, cq;
        float r1 = (w + h) / 4;
        float r2 = (w + h) / 4;
        float x1 = w / 2;
        float y1 = h / 2;
        float t;
        for(int x = 0; x < w; x++) {
          for(int y = 0; y < h; y++) {
            r0 = r1 + r2 - dist(x1, y1, x, y);;
            if(abs(y1 - y) > 1) {
              ml = - (x - x1) / (y - y1);
              bl = (-pow(x1, 2) + pow(x, 2) - pow(y1, 2) + pow(y, 2) + pow(r1, 2) - pow(r2, 2) - (2 * r0 * (r1 - r2))) / (2 * (y - y1));
              aq = 1 + pow(ml, 2);
              bq = 2 * (ml * (bl - y1) - x1);
              cq = pow(x1, 2) + pow(bl - y1, 2) - pow(r1 - r0, 2);
            } else {
              ml = - (y - y1) / (x - x1);
              bl = (-pow(x1, 2) + pow(x, 2) - pow(y1, 2) + pow(y, 2) + pow(r1, 2) - pow(r2, 2) - (2 * r0 * (r1 - r2))) / (2 * (x - x1));
              aq = 1 + pow(ml, 2);
              bq = 2 * (ml * (bl - x1) - y1);
              cq = pow(y1, 2) + pow(bl - x1, 2) - pow(r1 - r0, 2);
            }
            t = pow(bq, 2) - 4 * aq * cq;
            pixels[x * w + y] = color(constrain((log(abs(t)) * t / abs(t)) * 150 + 127, 0, 255));//getInterior(n1, new Circle(x, y, (w + h) / 4))[0].x == 0f ? color(0) : color(255);
            //println(getInterior(n1, new Circle(x, y, 100))[0].x);
            //println((log(abs(t)) * t / abs(t)) * 5 + 127);
          }
        }
        updatePixels();
        */
		Circle n1 = new Circle(400, 400, 100);
		Circle n2 = new Circle(s.mouseX, s.mouseY, 100);
		n1.draw(s);
		n2.draw(s);
		for (Circle n : Smoothing.getInterior(n1, n2)) {
			n.draw(s);
		}
		//noLoop();
	}
	
	public static void test5(PApplet s) {
        /*
        Tests out the Arc class.
        */
		int w = s.pixelWidth;
		int h = s.pixelHeight;
		float t_s = (s.mouseX - w / 2f) / 20f;
		float arc = (float) (s.mouseY * 1.25 * TWO_PI / h);
		new Arc(new PVector(w / 2f, h / 2f), 50, t_s, t_s + arc).draw(s);
		s.fill(0);
		s.text(String.format("Start: %.2f\nLength: %.2f\nEnd: %.2f\n", t_s, arc, t_s + arc), s.mouseX, s.mouseY);
		s.noFill();
	}
	
	public static void test6(PApplet s) {
        /*
        Tests out safe exteriors.
        */
		Circle n1 = new Circle(400, 400, 50);
		Circle n2 = new Circle(400, 500, 50);
		float r = s.mouseX / 20f;
		Circle n3 = new Circle(n2.x + (r + n2.r) * cos(s.mouseX / 100f), n2.y + (r + n2.r) * sin(s.mouseX / 100f), r);
		Circle n4 = new Circle(n2.x + (r + n2.r) * cos(s.mouseY / 100f), n2.y + (r + n2.r) * sin(s.mouseY / 100f), r);
		n1.draw(s);
		n2.draw(s);
		n3.draw(s);
		n4.draw(s);
		for (Circle n : Smoothing.getExterior(n1, n2)) {
			n.draw(s);
		}
		for (Circle n : Smoothing.getExterior(n2, n3)) {
			n.draw(s);
		}
		for (Circle n : Smoothing.getExterior(n2, n4)) {
			n.draw(s);
		}
	}
	
	public static void test7(PApplet s) {
        /*
        Mixing safe connections with good looking ones.
        */
		int w = s.pixelWidth;
		int h = s.pixelHeight;
		Circle n1 = new Circle(w / 2f, h / 2f, 50);
		Circle n2 = new Circle(s.mouseX, s.mouseY, 25);
		n1.draw(s);
		n2.draw(s);
		Circle[] ext = Smoothing.getExterior(n1, n2);
		for (Circle n : ext) {
			n.draw(s);
		}
		s.fill(0);
		s.text("" + ext[0].overlaps(ext[1]), s.mouseX, s.mouseY);
		s.noFill();
        /*
        color f;
        s.loadPixels();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (smoothing.getExterior(n1, new Circle(x, y, 100f * s.mouseX / w))[0].x > Float.MIN_VALUE) {
                    f = color(0, 255, 0);
                } else {
                    f = color(255, 0, 0);
                }
                s.pixels[x * w + y] = f;
            }
        }
        s.updatePixels();
        */
	}
	
	public static void test8(PApplet s) {
		PVector p1 = new PVector(0, 1), p2 = new PVector(1, 2);
		float j;
		float num = 1e9f;
		int t = s.millis();
		for (int i = 0; i < num; i++) {
			j = PVector.dist(p1, p2);
		}
		println(".dist: " + (s.millis() - t));
		t = s.millis();
		for (int i = 0; i < num; i++) {
			j = PVector.sub(p1, p2).magSq();
		}
		println(".sub.magSq: " + (s.millis() - t));
		t = s.millis();
		for (int i = 0; i < num; i++) {
			j = pow(p1.x - p2.x, 2) + pow(p1.y - p2.y, 2);
		}
		println("custom: " + (s.millis() - t));
		s.exit();
	}
	
	public static void test9(PApplet s) {
		s.randomSeed(0L);
		int max = 500;
		int h = s.pixelHeight;
		int w = s.pixelWidth;
		float[][] points = new float[max + 1][2];
		for(int i = 0; i < max; i++) {
			points[i][0] = s.random(w);
			points[i][1] = s.random(h);
		}
		points[max][0] = s.mouseX;
		points[max][1] = s.mouseY;
		Delaunay d = new Delaunay(points);
		Voronoi v = new Voronoi(points);
		s.stroke(255, 0, 0);
		for(float[] edge : d.getEdges()) {
			s.line(edge[0], edge[1], edge[2], edge[3]);
		}
		s.stroke(0, 0, 255);
		for(float[] edge : v.getEdges()) {
			s.line(edge[0], edge[1], edge[2], edge[3]);
		}
		s.strokeWeight(5);
		s.stroke(0, 255, 0);
		for(float[] point : points) {
			s.point(point[0], point[1]);
		}
		s.strokeWeight(1);
	}
	
	public static void test10(PApplet s) {
		s.randomSeed(10L);
		Node c1 = new Node(200, 200, 100);
		Node c2 = new Node(700, 200, 100);
		Node c3 = new Node(200, 700, 100);
		Node c4 = new Node(700, 700, 100);
		Node c5 = new Node(400, 400, 100);
		Node c6 = new Node(s.mouseX, s.mouseY, 50);
		int max = 10;
		ArrayList<ArrayList<Node>> nodes = new ArrayList<>();
		nodes.add(new ArrayList<>(Arrays.asList(c1, c2, c3, c4, c5, c6)));
		for(int i = 0; i < max; i++) {
			nodes.add((ArrayList<Node>) nodes.get(nodes.size()-1).clone());
			CirclePacking.voronoiPacking(nodes.get(nodes.size()-1));
			s.strokeWeight(1);//2 * (max - i));
			s.stroke(s.random(255), s.random(255), s.random(255));
			for(Node n : nodes.get(nodes.size()-1)) {
				n.draw(s);
			}
		}
	}
	
	public static void test11(PApplet s) {
		s.stroke(0);
		s.strokeWeight(1);
		Circle c1 = new Circle(200, 100, 50);
		Circle c2 = new Circle(300, 300, 40);
		Circle c3 = new Circle(s.mouseX, s.mouseY, 30);
		c1.draw(s);c2.draw(s);c3.draw(s);
		for(Circle c : Smoothing.triCircleAdjacent(c1, c2, c3)) { c.draw(s); }
		for(Circle c : Smoothing.getAdjacent(c1, c2, (c1.r + c2.r) / 2, true)) { c.draw(s); }
		for(Circle c : Smoothing.getAdjacent(c1, c3, (c1.r + c3.r) / 2, true)) { c.draw(s); }
		for(Circle c : Smoothing.getAdjacent(c2, c3, (c2.r + c3.r) / 2, true)) { c.draw(s); }
	}
	
	public static void test12(PApplet s) {
		int h = s.pixelHeight;
		int w = s.pixelWidth;
		float angle = new PVector(s.mouseX - w / 2f, s.mouseY - h / 2f).heading();
		Circle c1 = new Circle(w / 2f + cos(angle) * w / 2, h / 2f + sin(angle) * h / 2, 5);
		angle += HALF_PI;
		Circle c2 = new Circle(w / 2f + cos(angle) * w / 2, h / 2f + sin(angle) * h / 2, 5);
		c1.draw(s);c2.draw(s);
		s.stroke(0);
		s.strokeWeight(1);
		new Arc(new Circle(new PVector(w / 2f, h / 2f), 50), c1, c2, false).draw(s);
		new Arc(new Circle(new PVector(w / 2f, h / 2f), 40), c2, c1, true).draw(s);
	}
}