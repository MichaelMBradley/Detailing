import processing.core.PApplet;
import processing.core.PVector;

public interface Curve {
	boolean isEmpty();
	boolean isConnecting();
	float getStartAngle();
	float getEndAngle();
	PVector getStartPVector();
	PVector getEndPVector();
	float getSize();
	float getRange();
	
	void draw(PApplet sketch);
}
