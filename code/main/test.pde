void test1() {
  /*
  Attempts to smooth a series of circles.
  */
  ArrayList<Circle> ns = new ArrayList<Circle>();
  //iter = (int(mouseX)/5)*5 * PI / 180f;//+= 0.01f;
  //Circle n1 = new Circle(400, 400, 50);
  //Circle n2 = new Circle(n1.x + 100 * cos(iter), n1.y + 100 * sin(iter), 50);
  //Circle n3 = new Circle(n2.x + 100 * cos(1.5 * iter), n2.y + 100 * sin(1.5 * iter), 50);
  //Circle n4 = new Circle(n2.x + 100 * cos(1.5 * iter + HALF_PI), n2.y + 100 * sin(1.5 * iter + HALF_PI), 50);
  Circle n1 = new Circle(300, 200, 50);
  Circle n2 = new Circle(300, 300, 50);
  Circle n3 = new Circle(200, 300, 50);
  Circle n4 = new Circle(115, 355, 50);
  Circle n5 = new Circle(300, 400, 50);
  Circle n6 = new Circle(300, 480, 30);
  Circle n7 = new Circle(400, 300, 50);
  Circle n8 = new Circle(470, 230, 50);
  Circle n9 = new Circle(mouseX, mouseY, mouseX/10);//470, 370, 50);
  //mouseX, mouseY, mouseX/10);//
  //fill(0);
  //text(1 + "\n" + n1.x + " " + n1.y, n1.x, n1.y);
  //text(2 + "\n" + n2.x + " " + n2.y, n2.x, n2.y);
  //text(3 + "\n" + n3.x + " " + n3.y, n3.x, n3.y);
  //text(4 + "\n" + n4.x + " " + n4.y, n4.x, n4.y);
  //text(5 + "\n" + n5.x + " " + n5.y, n5.x, n5.y);
  //text(6 + "\n" + n6.x + " " + n6.y, n6.x, n6.y);
  //text(7 + "\n" + n7.x + " " + n7.y, n7.x, n7.y);
  //text(8 + "\n" + n8.x + " " + n8.y, n8.x, n8.y);
  //text(9 + "\n" + n9.x + " " + n9.y, n9.x, n9.y);
  //noFill();
  ns.add(n1);
  ns.add(n2);
  ns.add(n7);
  ns.add(n8);
  ns.add(n7);
  ns.add(n9);
  ns.add(n7);
  ns.add(n2);
  ns.add(n5);
  ns.add(n6);
  ns.add(n5);
  ns.add(n2);
  ns.add(n3);
  ns.add(n4);
  ns.add(n3);
  ns.add(n2);
  ns.add(n1);
  strokeWeight(1);
  stroke(0, 255, 0);
  //n1.draw();
  //n2.draw();
  //n3.draw();
  //n4.draw();
  //n5.draw();
  //n6.draw();
  //n7.draw();
  //n8.draw();
  //n9.draw();
  strokeWeight(1);
  stroke(0);
  println();
  for(Arc arc : surroundingArcsTree(ns)) {
    arc.draw();
  }
  //ArrayList<float[]> arr = surroundingArcsTree(ns);
  //for(int i = 0; i < arr.size() * (float) mouseX / w; i++) {
  //  drawArc(arr.get(i));
  //}
  //fill(0);
  //text((int) (arr.size() * (float) mouseX / w), mouseX, mouseY);
  //noFill();
}

void test2() {
  /*
  Attempts to create a safe (non-overlapping) intermediate circle.
  */
  Circle n1 = new Circle(mouseX, mouseY, 50);
  Circle n2 = new Circle(300, 300, 50);
  strokeWeight(1);
  stroke(0);
  n1.draw();
  n2.draw();
  for(Circle n : getExterior(n1, n2)) {
    stroke(255, 0, 0);
    n.draw();
    for(Circle j : triCircleAdjacent(n1, n2, n)) {
      stroke(0, 0, 255);
      j.draw();
    }
  }
}

void test3() {
  /*
  Creates a slight curve.
  */
  strokeWeight(5);
  stroke(255, 0, 0);
  point(100, 100);
  point(mouseX, mouseY);
  strokeWeight(1);
  stroke(0);
  randomSeed(0l);
  arcLine(new PVector(100, 100), new PVector(mouseX, mouseY)).draw();
}

void test4() {
  /*
  Creates a circle in the overlapping area two existing circles.
  */
  strokeWeight(1);
  stroke(0);
  //Circle n1 = new Circle(w / 2, h / 2, (w + h) / 4);//new Circle(mouseY, mouseX, (w + h) / 4);//
  //loadPixels();
  //float r0, ml, bl, aq, bq, cq;
  //float r1 = (w + h) / 4;
  //float r2 = (w + h) / 4;
  //float x1 = w / 2;
  //float y1 = h / 2;
  //float t;
  //for(int x = 0; x < w; x++) {
  //  for(int y = 0; y < h; y++) {
  //    r0 = r1 + r2 - dist(x1, y1, x, y);;
  //    if(abs(y1 - y) > 1) {
  //      ml = - (x - x1) / (y - y1);
  //      bl = (-pow(x1, 2) + pow(x, 2) - pow(y1, 2) + pow(y, 2) + pow(r1, 2) - pow(r2, 2) - (2 * r0 * (r1 - r2))) / (2 * (y - y1));
  //      aq = 1 + pow(ml, 2);
  //      bq = 2 * (ml * (bl - y1) - x1);
  //      cq = pow(x1, 2) + pow(bl - y1, 2) - pow(r1 - r0, 2);
  //    } else {
  //      ml = - (y - y1) / (x - x1);
  //      bl = (-pow(x1, 2) + pow(x, 2) - pow(y1, 2) + pow(y, 2) + pow(r1, 2) - pow(r2, 2) - (2 * r0 * (r1 - r2))) / (2 * (x - x1));
  //      aq = 1 + pow(ml, 2);
  //      bq = 2 * (ml * (bl - x1) - y1);
  //      cq = pow(y1, 2) + pow(bl - x1, 2) - pow(r1 - r0, 2);
  //    }
  //    t = pow(bq, 2) - 4 * aq * cq;
  //    pixels[x * w + y] = color(constrain((log(abs(t)) * t / abs(t)) * 150 + 127, 0, 255));//getInterior(n1, new Circle(x, y, (w + h) / 4))[0].x == 0f ? color(0) : color(255);
  //    //println(getInterior(n1, new Circle(x, y, 100))[0].x);
  //    //println((log(abs(t)) * t / abs(t)) * 5 + 127);
  //  }
  //}
  //updatePixels();
  Circle n1 = new Circle(400, 400, 100);
  Circle n2 = new Circle(mouseX, mouseY, 100);
  n1.draw();
  n2.draw();
  for(Circle n : getInterior(n1, n2)) {
    n.draw();
  }
  //noLoop();
}

void test5() {
  /*
  Tests out the Arc class.
  */
  float t_s = (mouseX - w / 2) / 20f;
  float arc = mouseY * 1.25 * TWO_PI / h;
  new Arc(new PVector(w / 2, h / 2), 50, t_s, t_s + arc).draw();
  fill(0);
  text(String.format("Start: %.2f\nLength: %.2f\nEnd: %.2f\n", t_s, arc, t_s + arc), mouseX, mouseY);
  noFill();
}

void test6() {
  /*
  Tests out safe exteriors.
  */
  Circle n1 = new Circle(400, 400, 50);
  Circle n2 = new Circle(400, 500, 50);
  float r = mouseX/20f;
  Circle n3 = new Circle(n2.x + (r + n2.r) * cos(mouseX / 100f), n2.y + (r + n2.r) * sin(mouseX / 100f), r);
  Circle n4 = new Circle(n2.x + (r + n2.r) * cos(mouseY / 100f), n2.y + (r + n2.r) * sin(mouseY / 100f), r);
  n1.draw();
  n2.draw();
  n3.draw();
  n4.draw();
  for(Circle n :  getExterior(n1, n2)) {
    n.draw();
  }
  for(Circle n :  getExterior(n2, n3)) {
    n.draw();
  }
  for(Circle n :  getExterior(n2, n4)) {
    n.draw();
  }
}

void test7() {
  /*
  Mixing safe connections with good looking ones.
  */
  boolean works = !false;
  Circle n1 = new Circle(w / 2, h / 2, 50);
  if(works) {
    Circle n2 = new Circle(mouseX, mouseY, 25);
    n1.draw();
    n2.draw();
    Circle[] ext = getExterior(n1, n2);
    for(Circle n : ext) {
      n.draw();
    }
    fill(0);
    text("" + ext[0].overlaps(ext[1]), mouseX, mouseY);
    noFill();
  } else {
    color f;
    loadPixels();
    for(int x = 0; x < w; x++) {
      for(int y = 0; y < h; y++) {
        if(getExterior(n1, new Circle(x, y, 100 * mouseX / w))[0].x > Float.MIN_VALUE){
          f = color(0, 255, 0);
        } else {
          f = color(255, 0, 0);
        }
        pixels[x * w + y] = f;
      }
    }
    updatePixels();
  }
}

void test8() {
  PVector p1 = new PVector(0, 1), p2 = new PVector(1, 2);
  float j;
  float num = 1e9;
  int t = millis();
  for(int i = 0; i < num; i++) {
    j = PVector.dist(p1, p2);
  }
  println(".dist: " + (millis() - t));
  t = millis();
  for(int i = 0; i < num; i++) {
    j = PVector.sub(p1, p2).magSq();
  }
  println(".sub.magSq: " + (millis() - t));
  t = millis();
  for(int i = 0; i < num; i++) {
    j = pow(p1.x - p2.x, 2) + pow(p2.y - p2.y, 2);
  }
  println("custom: " + (millis() - t));
  exit();
}
