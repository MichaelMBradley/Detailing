import lombok.Getter;
import lombok.Setter;
import processing.core.PApplet;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.round;
import static processing.core.PConstants.*;

public class Slider {
	@Getter @Setter private float value;
	private final float minValue, maxValue, width, height, baseX, baseY, roundTo, defaultValue;
	private final String message;
	public Slider(float min, float init, float max, float w, float h, float x, float y, float round, String msg) {
		minValue = min;
		value = init;
		defaultValue = init;
		maxValue = max;
		width = w;
		height = h;
		baseX = x;
		baseY = y;
		roundTo = round;
		message = msg;
	}
	
	public boolean update(float x, float y) {
		if(abs(x - baseX) <= width && y >= baseY && y <= baseY + height) {
			value = round((minValue + (maxValue - minValue) * (y - baseY) / height) / roundTo) * roundTo;
			return true;
		}
		return false;
	}
	
	public void draw(PApplet s) {
		s.push();
		s.stroke(255, 0, 0);
		float dy = baseY + height * (defaultValue - minValue) / (maxValue - minValue);
		s.line(baseX - width / 4, dy, baseX + width / 4, dy);
		s.stroke(0);
		s.line(baseX - width / 2, baseY, baseX + width / 2, baseY);
		s.line(baseX, baseY, baseX, baseY + height);
		s.line(baseX - width / 2, baseY + height, baseX + width / 2, baseY + height);
		float y = baseY + height * (value - minValue) / (maxValue - minValue);
		s.line(baseX - width / 4, y, baseX + width / 4, y);
		s.fill(0);
		s.textAlign(LEFT, CENTER);
		s.text(String.format("%.1f", minValue), baseX + 3 + width / 2, baseY);
		s.text(String.format("%.1f", value), baseX + 3 + width / 4, y);
		s.text(String.format("%.1f", maxValue), baseX + 3 + width / 2, baseY + height);
		s.textAlign(CENTER, BOTTOM);
		s.text(message, baseX, baseY);
		s.pop();
	}
	public String toString() {
		return String.format("Slider at (%.1f, %.1f) with value %.2f.", baseX, baseY, value);
	}
}
