package Game;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

import Client.Client;


public class PixelGun extends StateBasedGame implements Runnable{
	
	private static final int WIDTH = 800;	//너비 설정
	private static final int HEIGHT = 600;	//높이 설정
	private String playerID = "";
	private static boolean flag = false;
	private Client client;

	//Game Engine
	private static AppGameContainer container;
	private static MainGameState mgs;


//	public static void main(String[] args) {
//		try {
//			PixelGun MainGame = new PixelGun();
//			container = new AppGameContainer(MainGame);
//
//			container.setDisplayMode(WIDTH,HEIGHT,false);
//			container.setShowFPS(true); 
//			container.start();
//		} catch (SlickException e) {
//			e.printStackTrace();
//		}
//	}
	public void setClient(Client client){
		this.client = client;
	}
	public void gameStart(){
		try {
			container = new AppGameContainer(this);

			container.setDisplayMode(WIDTH,HEIGHT,false);
			container.setShowFPS(true); 
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	public void gameEnd(){
		System.out.println("ESC");
		flag = true;
	}
	public void setID(String playerID){
		this.playerID = playerID;
	}
	public PixelGun() {
		super("PIXEL GUN!");		
	}
	public void initStatesList(GameContainer container) {
		mgs = new MainGameState(client);
		client.setMgs(mgs);
		client.setMgsVector();
		addState(mgs);
		enterState(mgs.getID());
		
		
		client.networkStart();

	}

	public void mousePressed(int button, int x, int y) {
		getCurrentState().mousePressed(button,x,y);
	}
	public void mouseReleased(int button, int x, int y) {
		getCurrentState().mouseReleased(button,x,y);
	}
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		getCurrentState().mouseMoved(oldx,oldy,newx,newy);
	}
	public void keyPressed(int key, char c) {
		getCurrentState().keyPressed(key,c);
	}
	public void keyReleased(int key, char c) {
		getCurrentState().keyReleased(key,c);
	}
	@Override
	public void run() {
		gameStart();
		while(true){
			System.out.println("돈다");
			if(flag){
				System.out.println("들어옴");
				break;
			}
		}
		
	}

}