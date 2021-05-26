void test1() {
  ArrayList<Node> ns = new ArrayList<Node>();
  iter = (int(mouseX)/5)*5 * PI / 180f;//+= 0.01f;
  //Node n1 = new Node(400, 400, 50);
  //Node n2 = new Node(n1.x + 100 * cos(iter), n1.y + 100 * sin(iter), 50);
  //Node n3 = new Node(n2.x + 100 * cos(1.5 * iter), n2.y + 100 * sin(1.5 * iter), 50);
  //Node n4 = new Node(n2.x + 100 * cos(1.5 * iter + HALF_PI), n2.y + 100 * sin(1.5 * iter + HALF_PI), 50);
  Node n1 = new Node(300, 200, 50);
  Node n2 = new Node(300, 300, 50);
  Node n3 = new Node(200, 300, 50);
  Node n4 = new Node(115, 355, 50);
  Node n5 = new Node(300, 400, 50);
  Node n6 = new Node(300, 480, 30);
  Node n7 = new Node(400, 300, 50);
  Node n8 = new Node(470, 230, 50);
  Node n9 = new Node(mouseX, mouseY, 50);//470, 370, 50);
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
  for(Node n : getAdjacent(n1, n2)) {
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
