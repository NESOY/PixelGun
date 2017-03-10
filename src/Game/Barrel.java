package Game;

import org.newdawn.slick.geom.Shape;

public class Barrel extends Movable {
	private int hitpoints = 30;

	public Barrel(String name, float x, float y, Shape s) {
		super(name, x, y, s);
	}

	public boolean hurt(int damage) {
		hitpoints -= damage;
		if (hitpoints <= 0)
			return true;
		return false;
	}
}
