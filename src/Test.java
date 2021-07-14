import megamu.mesh.Delaunay;
import megamu.mesh.Voronoi;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;

import static processing.core.PApplet.*;

public class Test {
	private float mouseX, mouseY, width, height, percentX, percentY;
	private boolean clicked, pressed, scrolled;
	private char key;
	private int mouseButton, mouseCount, iter, iterNext;
	private final PApplet s;
	
	public Test(PApplet sketch) {
		s = sketch;
		iter = 0;
		iterNext = 1;
		mouseCount = 0;
		update();
	}
	
	public void run() {
		test18();
		update();
	}
	public void update() {
		mouseX = (float) s.mouseX;
		mouseY = (float) s.mouseY;
		width = (float) s.pixelWidth;
		height = (float) s.pixelHeight;
		percentX = mouseX / width;
		percentY = mouseY / width;
		key = s.key;
		mouseButton = s.mouseButton;
		pressed = false;
		clicked = false;
		scrolled = false;
	}
	private void iterate(int max) {
		iter = iter == max - 1 ? 0 : iter + 1;
		iterNext = iter == max - 1 ? 0 : iter + 1;
	}
	
	public void keyPressed(char keyDown) {
		key = keyDown;
		pressed = true;
	}
	public void mouseClicked(int mouseB) {
		mouseButton = mouseB;
		clicked = true;
	}
	public void mouseWheel(int count) {
		mouseCount += count;
		scrolled = true;
	}
	
	public void test1() {
		/*
		Attempts to smooth a series of circles.
		*/
		/*
		iter = (int(mouseX)/5)*5 * PI / 180f;//+= 0.01f;
		Circle n1 = new Circle(400, 400, 50);
		Circle n2 = n1.adjacent(iter, 50);
		Circle n3 = n2.adjacent(1.5f * iter, 50);
		Circle n4 = n3.adajcent(1.5f * iter + HALF_PI, 50);
		*/
		Circle n1 = new Circle(300, 200, 50);
		Circle n2 = new Circle(300, 300, 50);
		Circle n3 = new Circle(200, 300, 50);
		Circle n4 = new Circle(115, 355, 50);
		Circle n5 = new Circle(300, 400, 50);
		Circle n6 = new Circle(300, 480, 30);
		Circle n7 = new Circle(400, 300, 50);
		Circle n8 = new Circle(470, 230, 50);
		Circle n9 = new Circle(470, 370, 50);//mouseX, mouseY, mouseX / 10f);//
		/*
		s.fill(0);
		s.text(1 + "\n" + n1.x + " " + n1.y, n1.x, n1.y);
		s.text(2 + "\n" + n2.x + " " + n2.y, n2.x, n2.y);
		s.text(3 + "\n" + n3.x + " " + n3.y, n3.x, n3.y);
		s.text(4 + "\n" + n4.x + " " + n4.y, n4.x, n4.y);
		s.text(5 + "\n" + n5.x + " " + n5.y, n5.x, n5.y);
		s.text(6 + "\n" + n6.x + " " + n6.y, n6.x, n6.y);
		s.text(7 + "\n" + n7.x + " " + n7.y, n7.x, n7.y);
		s.text(8 + "\n" + n8.x + " " + n8.y, n8.x, n8.y);
		s.text(9 + "\n" + n9.x + " " + n9.y, n9.x, n9.y);
		s.noFill();
		*/
		ArrayList<Circle> ns = new ArrayList<>(Arrays.asList(n1, n2, n7, n8, n7, n9, n7, n2, n5, n6, n5, n2, n3, n4, n3, n2, n1));
		s.strokeWeight(1);
		s.stroke(0, 255, 0);
		/*n1.draw(s);n2.draw(s);n3.draw(s);n4.draw(s);n5.draw(s);n6.draw(s);n7.draw(s);n8.draw(s);n9.draw(s);*/
		s.strokeWeight(1);
		s.stroke(0);
		ArrayList<Curve> arcs = Smoothing.fixedSurroundingArcsTree(ns, true);
		for (int i = 0; i < arcs.size() * percentX; i++) {
			arcs.get(i).draw(s);
		}
	}
	public void test2() {
		/*
		Attempts to create a safe (non-overlapping) intermediate circle.
		*/
		Circle n1 = new Circle(width / 2, height / 2, 50);
		Circle n2 = new Circle(mouseX, mouseY, n1.getR());
		Circle n3 = n2.adjacent(TWO_PI * mouseX / width, n2.getR());
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
		Circle first = Adjacent.getExterior(n1, n2)[0];
		Circle second = Adjacent.getExterior(n2, n1)[0];
		Circle first2 = Adjacent.getExterior(n2, n3)[0];
		Circle second2 = Adjacent.getExterior(n3, n2)[0];
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
		s.text("a22: "+a22, a22.getX(), a22.getY());
		//s.text("a1: "+a1,a1.x,a1.y);s.text("a2: "+a2,a2.x,a2.y);s.text("a3: "+a3,a3.x,a3.y);
		//s.text("aFirst: "+aFirst,aFirst.x,aFirst.y);s.text("aSecond: "+aSecond,aSecond.x,aSecond.y);s.text("aFirst2: "+aFirst2,aFirst2.x,aFirst2.y);s.text("aSecond2: "+aSecond2,aSecond2.x,aSecond2.y);
		s.noFill();
	}
	public void test3() {
		/*
		Creates a slight curve.
		*/
		s.strokeWeight(5);
		s.stroke(255, 0, 0);
		s.point(100, 100);
		s.point(mouseX, mouseY);
		s.strokeWeight(1);
		s.stroke(0);
		s.randomSeed(0L);
		Geometry.arcLine(new PVector(100, 100), new PVector(mouseX, mouseY)).draw(s);
	}
	public void test4() {
		/*
		Creates a circle in the overlapping area two existing circles.
		*/
		s.strokeWeight(1);
		s.stroke(0);
		/*
		Circle n1 = new Circle(w / 2, h / 2, (w + h) / 4);//new Circle(mouseY, mouseX, (w + h) / 4);//
		s.loadPixels();
		float r0, ml, bl, aq, bq, cq;
		float r1 = (width + height) / 4;
		float r2 = (width + height) / 4;
		float x1 = width / 2;
		float y1 = height / 2;
		float t;
		for(int x = 0; x < width; x++) {
		  for(int y = 0; y < height; y++) {
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
			s.pixels[x * width + y] = color(constrain((log(abs(t)) * t / abs(t)) * 150 + 127, 0, 255));//getInterior(n1, new Circle(x, y, (w + h) / 4))[0].x == 0f ? color(0) : color(255);
			//println(getInterior(n1, new Circle(x, y, 100))[0].x);
			//println((log(abs(t)) * t / abs(t)) * 5 + 127);
		  }
		}
		s.updatePixels();
		*/
		Circle n1 = new Circle(400, 400, 100);
		Circle n2 = new Circle(mouseX, mouseY, 100);
		n1.draw(s);
		n2.draw(s);
		for (Circle n : Adjacent.getInterior(n1, n2)) {
			n.draw(s);
		}
		//noLoop();
	}
	public void test5() {
		/*
		Tests out the Arc class.
		*/
		float t_s = (mouseX - width / 2) / 20;
		float arc = mouseY * 1.25f * TWO_PI / height;
		new Arc(new PVector(width / 2, height / 2), 50, t_s, t_s + arc).draw(s);
		s.fill(0);
		s.text(String.format("Start: %.2f\nLength: %.2f\nEnd: %.2f\n", t_s, arc, t_s + arc), s.mouseX, s.mouseY);
		s.noFill();
	}
	public void test6() {
		/*
		Tests out safe exteriors.
		*/
		Circle n1 = new Circle(400, 400, 50);
		Circle n2 = new Circle(400, 500, 50);
		float r = mouseX / 20;
		Circle n3 = n2.adjacent(mouseX / 100, r);
		Circle n4 = n2.adjacent(mouseY / 100, r);
		n1.draw(s);
		n2.draw(s);
		n3.draw(s);
		n4.draw(s);
		for (Circle n : Adjacent.getExterior(n1, n2)) {
			n.draw(s);
		}
		for (Circle n : Adjacent.getExterior(n2, n3)) {
			n.draw(s);
		}
		for (Circle n : Adjacent.getExterior(n2, n4)) {
			n.draw(s);
		}
	}
	public void test7() {
		/*
		Mixing safe connections with good looking ones.
		*/
		Circle n1 = new Circle(width / 2, height / 2, 50);
		Circle n2 = new Circle(mouseX, mouseY, 25);
		n1.draw(s);
		n2.draw(s);
		Circle[] ext = Adjacent.getExterior(n1, n2);
		for (Circle n : ext) {
			n.draw(s);
		}
		s.fill(0);
		s.text("" + ext[0].overlaps(ext[1]), mouseX, mouseY);
		s.noFill();
		/*
		color f;
		s.loadPixels();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (smoothing.getExterior(n1, new Circle(x, y, 100 * mouseX / width))[0].x > Float.MIN_VALUE) {
					f = color(0, 255, 0);
				} else {
					f = color(255, 0, 0);
				}
				s.pixels[x * width + y] = f;
			}
		}
		s.updatePixels();
		*/
	}
	public void test8() {
		PVector p1 = new PVector(0, 1), p2 = new PVector(1, 2);
		float j = 0f;
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
		println(j);
		s.exit();
	}
	public void test9() {
		s.randomSeed(0L);
		int max = 500;
		float[][] points = new float[max + 1][2];
		for(int i = 0; i < max; i++) {
			points[i][0] = s.random(width);
			points[i][1] = s.random(height);
		}
		points[max][0] = mouseX;
		points[max][1] = mouseY;
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
	public void test10() {
		s.randomSeed(10L);
		Circle c1 = new Circle(200, 200, 100);
		Circle c2 = new Circle(700, 200, 100);
		Circle c3 = new Circle(200, 700, 100);
		Circle c4 = new Circle(700, 700, 100);
		Circle c5 = new Circle(400, 400, 100);
		Circle c6 = new Circle(mouseX, mouseY, 50);
		int max = 10;
		ArrayList<ArrayList<Circle>> nodes = new ArrayList<>();
		nodes.add(new ArrayList<>(Arrays.asList(c1, c2, c3, c4, c5, c6)));
		for(int i = 0; i < max; i++) {
			nodes.add(new ArrayList<>(nodes.get(nodes.size()-1)));
			CirclePacking.voronoiPacking(nodes.get(nodes.size()-1));
			s.strokeWeight(1);//2 * (max - i));
			s.stroke(s.random(255), s.random(255), s.random(255));
			for(Circle n : nodes.get(nodes.size()-1)) {
				n.draw(s);
			}
		}
	}
	public void test11() {
		s.stroke(0);
		s.strokeWeight(1);
		Circle c1 = new Circle(200, 100, 50);
		Circle c2 = new Circle(300, 300, 40);
		Circle c3 = new Circle(mouseX, mouseY, 30);
		c1.draw(s);c2.draw(s);c3.draw(s);
		for(Circle c : Adjacent.triCircleAdjacent(c1, c2, c3)) { c.draw(s); }
		for(Circle c : Adjacent.getAdjacent(c1, c2, (c1.getR() + c2.getR()) / 2, true)) { c.draw(s); }
		for(Circle c : Adjacent.getAdjacent(c1, c3, (c1.getR() + c3.getR()) / 2, true)) { c.draw(s); }
		for(Circle c : Adjacent.getAdjacent(c2, c3, (c2.getR() + c3.getR()) / 2, true)) { c.draw(s); }
	}
	public void test12() {
		float angle = new PVector(mouseX - width / 2f, mouseY - height / 2f).heading();
		Circle c1 = new Circle(width / 2 + cos(angle) * width / 2, height / 2 + sin(angle) * height / 2, 5);
		angle += HALF_PI;
		Circle c2 = new Circle(width / 2 + cos(angle) * width / 2, height / 2 + sin(angle) * height / 2, 5);
		c1.draw(s);c2.draw(s);
		s.stroke(0);
		s.strokeWeight(1);
		new Arc(new Circle(new PVector(width / 2, height / 2), 50), c1, c2, false).draw(s);
		new Arc(new Circle(new PVector(width / 2, height / 2), 40), c2, c1, true).draw(s);
	}
	public void test13() {
		// Testing Arc collision
		Arc a1 = new Arc(new PVector(width / 2 - 25, height / 2), 50,
				percentX * TWO_PI * 2, percentX * TWO_PI * 2 + HALF_PI);
		Arc a2 = new Arc(new PVector(width / 2 + 25, height / 2), 50,
				percentY * TWO_PI * 2, percentY * TWO_PI * 2 + HALF_PI);
		s.strokeWeight(1f);
		s.stroke(a1.overlaps(a2) ? 255: 0, 0, 0);
		a1.draw(s);
		a2.draw(s);
	}
	public void test14() {
		float angle1 = -HALF_PI;//-2 * PI * percentX;
		float angle2 = HALF_PI;//2 * PI * percentY;
		float fixedAngle1 = -HALF_PI;
		float fixedAngle2 = HALF_PI;
		PVector p1 = new PVector(200, 450);
		PVector p2 = new PVector(700, 450);
		PVector c1 = new PVector(p1.x + 100 * cos(fixedAngle1), p1.y + 100 * sin(fixedAngle1));
		PVector c2 = new PVector(p2.x + 100 * cos(fixedAngle2), p2.y + 100 * sin(fixedAngle2));
		Clothoid clothoid = new Clothoid(angle1, angle2, p1, p2);
		Bezier bezier = new Bezier(p1, c1, c2, p2);
		s.stroke(0);
		clothoid.draw(s);
		s.stroke(255, 0, 0);
		bezier.draw(s);
		s.stroke(0, 255, 0);
		Helpers.drawLine(p1, c1, s);
		Helpers.drawLine(p2, c2, s);
		s.stroke(0);
		s.fill(0);
		s.text(String.format("%.2f", PVector.sub(c1, p1).heading()), c1.x, c1.y);
		s.text(String.format("%.2f", PVector.sub(c2, p2).heading()), c2.x, c2.y);
		s.text(String.format("%.2f", clothoid.getStartAngle()), p1.x, p1.y);
		s.text(String.format("%.2f", clothoid.getEndAngle()), p2.x, p2.y);
		s.text(angle2 - angle1, 0, 0);
		s.noFill();
	}
	public void test15() {
		// Testing test class
		s.fill(0);
		s.text(String.format("width: %.0f\theight: %.0f\n" +
				"mouseX: %.0f\tmouseY: %.0f\n" +
				"percentX: %.2f\tpercentY: %.2f\n" +
				"clicked: %s\tpressed: %s\tscrolled: %s\n" +
				"key: %s\t num: %d\n" +
				"mouseButton: %d\tmouseCount: %d\n" +
				"iter: %d",
				width, height, mouseX, mouseY, percentX, percentY, clicked, pressed, scrolled, key, (int) key, mouseButton, mouseCount, iter), 10, 10);
	}
	public void test16() {
		mouseCount = max(-9, min(mouseCount, 10));
		float lerp = (mouseCount + 10) / 20f;
		Circle c1 = new Circle(300, 300, 50);
		Circle c2 = new Circle(mouseX, mouseY, 50);
		Circle c3 = new Circle(600, 600, 50);
		s.stroke(0);
		c1.draw(s);c2.draw(s);c3.draw(s);
		s.fill(0);
		s.text(lerp, mouseX, mouseY);
		s.noFill();
		s.stroke(255, 0, 0);
		Adjacent.triCircleAdjacent(c1, c2, c3)[0].draw(s);
		s.stroke(0, 255, 0);
		Adjacent.triCircleAdjacent(c1, c2, c3)[1].draw(s);
		s.stroke(255, 0, 0);
		Adjacent.triCircleAdjacentSafer(c1, c2, c3, lerp)[0].draw(s);
		s.stroke(0, 255, 0);
		Adjacent.triCircleAdjacentSafer(c1, c2, c3, lerp)[1].draw(s);
	}
	public void test17() {
		PVector p1 = new PVector(100, 100);
		PVector p2 = new PVector(mouseX, mouseY);
		PVector p3 = new PVector(100, height - 100);
		PVector p4 = new PVector(width - 300, 100);
		PVector con = Traversal.crossover(p1, p2, p3, p4);
		s.stroke(0);
		s.strokeWeight(2);
		Helpers.drawLine(p1, p2, s);
		Helpers.drawLine(p3, p4, s);
		s.stroke(255, 0, 0);
		s.strokeWeight(8);
		Helpers.drawPoint(con, s);
	}
	public void test18() {
		int mouseC = max(1, mouseCount);
		PVector p1 = new PVector(100, 100);
		PVector c1 = new PVector(mouseX, mouseY);
		PVector c2 = new PVector(width - mouseX, width - mouseY);
		PVector p2 = new PVector(width - 100, height - 100);
		Bezier b = new Bezier(p1, c1, c2, p2);
		Bezier bAdj = b.getAdjacent(1);
		Bezier[] bAdjG = b.getAdjacent(1, mouseC);
		s.strokeWeight(2);
		s.stroke(0);
		b.draw(s);
		s.strokeWeight(8);
		b.drawPoints(s);
		s.strokeWeight(2);
		s.stroke(255, 0, 0);
		bAdj.draw(s);
		s.strokeWeight(8);
		bAdj.drawPoints(s);
		s.stroke(0, 255, 0);
		for(Bezier bez : bAdjG) {
			s.strokeWeight(2);
			bez.draw(s);
			s.strokeWeight(8);
			bez.drawPoints(s);
		}
		s.fill(0);
		s.text(mouseC, mouseX, mouseY);
		s.noFill();
	}
}