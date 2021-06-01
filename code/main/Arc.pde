// I want to see if using a specialised class can help clean up the code
public class Arc {
  public PVector pv;
  public float r, t_s, t_e;
  
  public Arc() {
    pv = new PVector();
    r = 0f;
    t_s = 0f;
    t_e = 0f;
  }
  
  public Arc(PVector location, float radius, float start, float arc) {
    pv = location;
    r = radius;
    t_s = start;
    t_e = start + arc;
  }
  
  public Arc(float[] arcInfo) {
    pv = new PVector(arcInfo[0], arcInfo[1]);
    r = arcInfo[2];
    t_s = arcInfo[4];
    t_e = arcInfo[5];
  }
  
  public void draw() {
    draw(new PVector());
  }
  public void draw(PVector offset) {
    arc(pv.x + offset.x, pv.y + offset.y, r * 2, r * 2, t_s, t_e);
  }
}
