package Game;

import org.newdawn.slick.geom.Shape;



public class Movable {

	public float x;
	public float y;
	public float dx;
	public float dy;
	private Shape shape;
	private String name;

	public Movable(String name, float x, float y, Shape s) {
		this.x = x;
		this.y = y;
		this.name = name;
		shape = s;
	}

	public Shape getShape() {
		shape.setX(x - shape.getWidth() / 2);
		shape.setY(y - shape.getHeight() / 2);
		return shape;
	}

	public float distance(Movable m) {
		return (float) Math.sqrt(Math.pow(m.x - x, 2) + Math.pow(m.y - y, 2));
	}

	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name = name;
	}

}