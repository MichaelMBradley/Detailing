// I want to see if using a specialised class can help clean up the code
public class Arc extends Circle {
  public float start, end;
  
  public Arc() {
    super();
    start = 0f;
    start = 0f;
  }
  public Arc(Circle c, float s, float e) {
    super(c.pv, c.r);
    start = s;
    end = e;
  }
  public Arc(PVector location, float radius, float s, float e) {
    super(location, radius);
    start = s;
    end = e;
  }
  
  public Arc(PVector loc, Node prev, Node next, boolean connect) {
    pv = loc;
    r = PVector.dist(loc, prev.pv) - prev.r;
    start = PVector.sub(prev.pv, loc).heading();
    end = PVector.sub(next.pv, loc).heading();
    float temp;
    // Allow for rounding error on cross?
    boolean cross = abs(end - start) != PVector.angleBetween(PVector.sub(prev.pv, loc), PVector.sub(next.pv, loc)) == connect;
    if(start > end) {  // Out of order
      if(cross) {
        temp = start;
        start = end;
        end = temp;
      } else {
        end += TWO_PI;
      }
    } else if(cross) {
      temp = start;
      start = end;
      end = temp;
      end += TWO_PI;
    }
  }
  
  @Override
  public void draw() {
    arc(pv.x, pv.y, r * 2, r * 2, start, end);
  }
}
