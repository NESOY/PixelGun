package Game;

import org.newdawn.slick.geom.Shape;

public class Projectile extends Movable {
	public boolean hostile;

	public Projectile(String name, float x, float y, Shape s, boolean hostile) {
		super(name, x, y, s);
		this.hostile = hostile;
	}
}