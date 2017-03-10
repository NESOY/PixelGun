package Game;

import org.newdawn.slick.geom.Circle;
public class Player {
	public boolean playerDead;	
	private String id;
	private int index;
	private boolean MoveUp;
	private boolean MoveDown;
	private boolean MoveLeft;
	private boolean MoveRight;
	private float angle;
	
	public int hp;
	
	public  static int playerX[] = {400,1800,1800,1800,500,550,1250,1150};
	public static int playerY[] = {400,400,1300,2500,2500,1550,1850,1100};
	
	private Movable player;
	public Player(String id,int index){
		this.index = index;
		this.id = id;
		initPlayer();
		System.out.println("¡Ú"+id);
		player = new Movable(id,playerX[index],playerY[index],new Circle(0,0,20));
	}
	public void initPlayer(){
		playerDead = false;
		MoveUp = false;
		MoveDown = false;
		MoveLeft = false;
		MoveRight = false;
		hp = 100;
	}
	public static Player makeClient(String id, int index) {
		return new Player(id, index);
		
	}
	public void setName(){
		player.setName(id);
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	public int getIndex(){
		return index;
	}
	public void setMoveUp(){
		this.MoveUp = true;
	}
	public void setMoveDown(){
		this.MoveDown = true;
	}
	public void setMoveLeft(){
		this.MoveLeft = true;
	}
	public void setMoveRight(){
		this.MoveRight = true;
	}
	public void UnsetMoveUp(){
		this.MoveUp = false;
	}
	public void UnsetMoveDown(){
		this.MoveDown = false;
	}
	public void UnsetMoveLeft(){
		this.MoveLeft = false;
	}
	public void UnsetMoveRight(){
		this.MoveRight = false;
	}
		
	public boolean isMoveUp(){
		return MoveUp;
	}
	public boolean isMoveDown(){
		return MoveDown;
	}
	public boolean isMoveLeft(){
		return MoveLeft;
	}
	public boolean isMoveRight(){
		return MoveRight;
	}
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return id;
	}
	public Movable getPlayer(){
		return player;
	}
	public void setAngle(float angle){
		this.angle = angle;
	}
	public float getAngel(){
		return angle;
	}

	
	
	
	
	
}
