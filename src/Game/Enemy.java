package Game;

import org.newdawn.slick.geom.Shape;

public class Enemy extends Movable {

	private int hitpoints = 50;
	public int offset = (int) (Math.random() * 1000);
	public int deltaCount = offset;

	public Enemy(String name, float x, float y, Shape s) {
		super(name, x, y, s);
	}

	public boolean hurt(int damage) {
		hitpoints -= damage;
		if (hitpoints <= 0)
			return true;
		return false;
	}

}