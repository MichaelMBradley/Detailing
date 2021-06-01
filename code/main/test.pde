void test1() {
  ArrayList<Node> ns = new ArrayList<Node>();
  //iter = (int(mouseX)/5)*5 * PI / 180f;//+= 0.01f;
  //Node n1 = new Node(400, 400, 50);
  //Node n2 = new Node(n1.x + 100 * cos(iter), n1.y + 100 * sin(iter), 50);
  //Node n3 = new Node(n2.x + 100 * cos(1.5 * iter), n2.y + 100 * sin(1.5 * iter), 50);
  //Node n4 = new Node(n2.x + 100 * cos(1.5 * iter + HALF_PI), n2.y + 100 * sin(1.5 * iter + HALF_PI), 50);
  Node n1 = new Node(300, 200, 50);
  Node n2 = new Node(300, 300, 50);
  Node n3 = new Node(200, 300, 50);
  Node n4 = new Node(115, 355, 50);
  Node n5 = new Node(mouseX, mouseY, 50);//300, 400, 50);
  Node n6 = new Node(300, 480, 30);
  Node n7 = new Node(400, 300, 50);
  Node n8 = new Node(470, 230, 50);
  Node n9 = new Node(470, 370, 50);
  //
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
  for(float[] arr : surroundingArcsTree(ns)) {
    drawArc(arr);
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
  Node n1 = new Node(mouseX, mouseY, 50);
  Node n2 = new Node(300, 300, 50);
  strokeWeight(1);
  stroke(0);
  n1.draw();
  n2.draw();
  for(Node n : getExterior(n1, n2)) {
    stroke(255, 0, 0);
    n.draw();
    for(Node j : triCircleAdjacent(n1, n2, n)) {
      stroke(0, 0, 255);
      j.draw();
    }
  }
}

void test3() {
  strokeWeight(5);
  stroke(255, 0, 0);
  point(100, 100);
  point(mouseX, mouseY);
  strokeWeight(1);
  stroke(0);
  randomSeed(0l);
  float[] a = arcLine(new PVector(100, 100), new PVector(mouseX, mouseY));
  drawArc(a);
}

void test4() {
  strokeWeight(1);
  stroke(0);
  //Node n1 = new Node(w / 2, h / 2, (w + h) / 4);//new Node(mouseY, mouseX, (w + h) / 4);//
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
      pixels[x * w + y] = color(constrain((log(abs(t)) * t / abs(t)) * 150 + 127, 0, 255));//getInterior(n1, new Node(x, y, (w + h) / 4))[0].x == 0f ? color(0) : color(255);
      //println(getInterior(n1, new Node(x, y, 100))[0].x);
      //println((log(abs(t)) * t / abs(t)) * 5 + 127);
    }
  }
  updatePixels();
  //Node n1 = new Node(400, 400, 100);
  //Node n2 = new Node(mouseX, mouseY, 100);
  //n1.draw();
  //n2.draw();
  //for(Node n : getInterior(n1, n2)) {
  //  n.draw();
  //}
  //noLoop();
}
