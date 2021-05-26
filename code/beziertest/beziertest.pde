// Just to see how they might act around circles
// LMB = Place vertex
// RMB = Remove last vertex

ArrayList<PVector> create = new ArrayList<PVector>();

void setup() {
  size(800, 800);
}
  
void draw() {
  int l = create.size();
  background(255);
  stroke(127);
  if(l == 0) {
    // Do nothing
  } else if (l == 1) {
    point(create.get(0).x, create.get(0).y);
  } else {
    for(int i = 0; i < l - 1; i++) {
      line(create.get(i).x, create.get(i).y, create.get(i + 1).x, create.get(i + 1).y);
    }
    stroke(0);
    bezierMain(create, 10);
  }
  noLoop();
}

void mouseClicked() {
  if(mouseButton == LEFT) {
    create.add(new PVector(mouseX, mouseY));
  } else if(mouseButton == RIGHT) {
    if(create.size() >= 1) {
      create.remove(create.size() - 1);
    }
  }
  loop();
}

void bezierMain(ArrayList<PVector> points, int mult) {
  ArrayList<PVector> nodes = new ArrayList<PVector>();
  int detail = mult * points.size();
  for(int i = 0; i <= detail; i++) {
    nodes.add(bezierGet(points, i, detail));
    if(i != 0) {
      line(nodes.get(i - 1).x, nodes.get(i - 1).y, nodes.get(i).x, nodes.get(i).y);
    }
  }
}

PVector bezierGet(ArrayList<PVector> points, int through, int detail) {
  if(points.size() == 1) {
    return points.get(0);
  } else {
    ArrayList<PVector> newPoints = new ArrayList<PVector>();
    for(int i = 0; i < points.size() - 1; i++) {
      float x = points.get(i).x * ((float) through / detail) + points.get(i + 1).x * (1 - ((float) through / detail));
      float y = points.get(i).y * ((float) through / detail) + points.get(i + 1).y * (1 - ((float) through / detail));
      newPoints.add(new PVector(x, y));
    }
    return bezierGet(newPoints, through, detail);
  }
}
