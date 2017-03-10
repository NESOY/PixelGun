package Game;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.BasicGameState;

public abstract class SimpleGameState extends BasicGameState {

	int mouseX;
	int mouseY;
	int mouseOldX;
	int mouseOldY;
	int mouseLastEventX;
	int mouseLastEventY;
	boolean[] mouse = new boolean[8];
	boolean[] keys = new boolean[512];

	public void mousePressed(int button, int x, int y) {
		mouseX = x;
		mouseY = y;
		mouseLastEventX = x;
		mouseLastEventY = y;
		mouse[button] = true;
	}
	public void mouseReleased(int button, int x, int y) {
		mouseX = x;
		mouseY = y;
		mouseLastEventX = x;
		mouseLastEventY = y;
		mouse[button] = false;
	}
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		mouseOldX = oldx;
		mouseOldY = oldy;
		mouseX = newx;
		mouseY = newy;		
	}	
	public void keyPressed(int key, char c) {
		keys[key] = true;
	}
	public void keyReleased(int key, char c) {
		keys[key] = false;
	}

	public abstract void reset();

}