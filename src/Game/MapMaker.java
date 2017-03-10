package Game;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import java.util.*;
import javax.swing.JOptionPane;

public class MapMaker extends BasicGame {

	private static AppGameContainer container;
	private boolean[] keys = new boolean[512];

	private LinkedList<Vector2f> outer = new LinkedList<Vector2f>();
	private ArrayList<LinkedList<Vector2f>> inners = new ArrayList<LinkedList<Vector2f>>();
	private ArrayList<Object> objects = new ArrayList<Object>();

	private Vector2f selected;

	private Vector2f goal = new Vector2f();
	private Vector2f start = new Vector2f();

	private int mouseX;
	private int mouseY;

	private boolean drag = false;

	private TrueTypeFont arial;

	public MapMaker() {
		super("ModifiedEditor (based on TowerEdit)");
	}

	public static void main(String[] args) {
		try {
			container = new AppGameContainer(new MapMaker());

			//container.setTargetFrameRate(60);
			//container.setMinimumLogicUpdateInterval(10);
			//container.setMaximumLogicUpdateInterval(10);

			container.setDisplayMode(800,600,false);
			container.start();

		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void init(GameContainer cont) {
		outer.add(new Vector2f(50f,50f));
		outer.add(new Vector2f(400f,50f));
		outer.add(new Vector2f(400f,550f));
		selected = new Vector2f(50f,550f);
		outer.add(selected);
		goal.set(120,70);
		start.set(120,540);

		arial = new TrueTypeFont(new java.awt.Font("Arial",java.awt.Font.PLAIN,12), false);
	}

	public void update(GameContainer cont, int delta) {
		if(drag) {
			selected.set(mouseX,mouseY);
		}
		if(keys[Input.KEY_S]) {
			StringBuilder level = new StringBuilder();
			for(Vector2f v : outer) {
				level.append((int)(v.x*5) + "," + (int)(v.y*5));
				level.append(",");
			}
			level.append("//");
			for(LinkedList<Vector2f> l : inners) {
				for(Vector2f v : l) {
					level.append((int)(v.x*5) + "," + (int)(v.y*5));
					level.append(",");
				}
					level.append("//");
			}
			level.append((int)(start.x*5) + "," + (int)(start.y*5) + "//");
			level.append((int)(goal.x*5) + "," + (int)(goal.y*5) + "//");
			level.append("&&");
			for(Object o : objects) {
				level.append(o.s+","+(int)(o.x*5) + "," + (int)(o.y*5) + "//");
			}

			JOptionPane.showInputDialog("Save level:",level.toString());
			keys[Input.KEY_S] = false;
		}
		if(keys[Input.KEY_L]) {
			String level = JOptionPane.showInputDialog("Load level:");
			String[] divided = level.split("&&");
			String[] polys = divided[0].split("//");
			String[] coords = polys[0].split(",");
			System.out.println(polys.length);
			System.out.println(coords.length);
			outer = new LinkedList<Vector2f>();
			for(int i=0; i<coords.length-1; i+=2) {
				outer.add(new Vector2f(Integer.parseInt(coords[i])/5f,Integer.parseInt(coords[i+1])/5f));
			}
			selected = outer.get(0);
			inners.clear();
			for(int i=1; i<polys.length-2; i++) {
				coords = polys[i].split(",");
				LinkedList<Vector2f> l = new LinkedList<Vector2f>();
				for(int j=0; j<coords.length-1; j+=2) {
					l.add(new Vector2f(Integer.parseInt(coords[j])/5f,Integer.parseInt(coords[j+1])/5f));
				}
				inners.add(l);
			}

			coords = polys[polys.length-2].split(",");
			start.set(Integer.parseInt(coords[0])/5,Integer.parseInt(coords[1])/5);

			coords = polys[polys.length-1].split(",");
			goal.set(Integer.parseInt(coords[0])/5,Integer.parseInt(coords[1])/5);

			if(divided.length > 1) {
				String[] objs = divided[1].split("//");
				for(String obj : objs) {
					String[] objDetails = obj.split(",");
					objects.add(new Object(objDetails[0],(int)(Integer.parseInt(objDetails[1])/5f),(int)(Integer.parseInt(objDetails[2])/5f)));
					//System.out.println(objDetails[0]);
					//System.out.println(objDetails[0].equals("B"));
				}
			}

			keys[Input.KEY_L] = false;
		}
	}

	public void render(GameContainer cont, Graphics g) {

		g.setColor(Color.gray.darker());
		g.setFont(arial);
		g.drawString("Press LMB to drag a node or insert a new one.",500,40);
		g.drawString("Press RMB to delete a node.",500,60);
		g.drawString("Press Mouse3 create a new polygon (platform).",500,80);
		g.drawString("Press Q to move Goal to cursor.",500,100);
		g.drawString("Press W to move Start to cursor.",500,120);
		g.drawString("Press S to Save map (copy it from dialogue).",500,180);
		g.drawString("Press L to Load map (paste into dialogue).",500,200);
		g.drawString("Map size is scaled up times 5 in-game.",500,260);
		g.drawString("Green polygon is the outer map bounds.",500,280);
		g.drawString("Press L in-game to load your map.",500,340);
		g.drawString("Press Z to place breakable.",500,400);
		g.drawString("Press X to place enemy.",500,420);

		g.setColor(Color.green);

		drawPolygon(outer,g);

		for(LinkedList<Vector2f> l : inners) {
			g.setColor(Color.white);
			drawPolygon(l,g);
		}

		for(Object o : objects) {
			if(o.s.startsWith("B")) {
				g.setColor(Color.orange);
				g.fillOval(o.x-3,o.y-3,6,6);
			}
			if(o.s.startsWith("E")) {
				g.setColor(Color.red);
				g.fillOval(o.x-3,o.y-3,6,6);
			}
		}

		g.setColor(Color.white);
		g.drawOval(start.x-5,start.y-5,10,10);

		g.setColor(Color.yellow);
		g.drawOval(goal.x-5,goal.y-5,10,10);


	}

	private void drawPolygon(LinkedList<Vector2f> list, Graphics g) {
		for(int i=0; i<list.size()-1; i++) {
			g.drawLine(list.get(i).x,list.get(i).y,list.get(i+1).x,list.get(i+1).y);
			g.fillOval(list.get(i).x-5,list.get(i).y-5,10,10);
		}
		if(list.size() >= 1) {
			g.drawLine(list.get(list.size()-1).x,list.get(list.size()-1).y,list.get(0).x,list.get(0).y);
			g.fillOval(list.get(list.size()-1).x-5,list.get(list.size()-1).y-5,10,10);
		}

		if(selected != null) {
			g.setColor(Color.red);
			g.fillOval(selected.x-5,selected.y-5,10,10);
		}

	}

	public void keyPressed(int k, char c) {
		if(k == Input.KEY_ESCAPE) {
			System.exit(0);
		}
		if(k == Input.KEY_Q) {
			goal.set(mouseX,mouseY);
		}
		if(k == Input.KEY_W) {
			start.set(mouseX,mouseY);
		}
		if(k == Input.KEY_Z) {
			objects.add(new Object("B",mouseX,mouseY));
		}
		if(k == Input.KEY_X) {
			objects.add(new Object("E",mouseX,mouseY));
		}
		keys[k] = true;
	}

	public void keyReleased(int k, char c) {
		keys[k] = false;
	}


	public void mousePressed(int button, int x, int y) {

		if(button == 1) {
			ArrayList<Object> removeList = new ArrayList<Object>();
			for(Object o : objects) {
				double dist = Math.sqrt(Math.pow(o.x-x,2)+Math.pow(o.y-y,2));
				if(dist <= 3) {
					removeList.add(o);
				}
			}
			for(Object o : removeList) {
				objects.remove(o);
			}
		}

		Iterator<Vector2f> it = outer.iterator();
		Vector2f add = new Vector2f(x,y);

		while(it.hasNext()) {
			Vector2f v = it.next();
			double dist = (v.x-x)*(v.x-x)+(v.y-y)*(v.y-y);
			if(button == 0) {
				drag = true;
				if(dist < 25) {
					selected = v;
					add = null;
					continue;
				}
			}
			if(button == 1) {
				if(dist < 25) {
					it.remove();
					if(outer.size() >= 1)
						selected = outer.get(0);
					else
						selected = null;
				}
			}
			if(button == 2) {
				LinkedList<Vector2f> l = new LinkedList<Vector2f>();
				l.add(new Vector2f(x-15,y-10));
				l.add(new Vector2f(x+15,y-10));
				l.add(new Vector2f(x+15,y+10));
				selected = new Vector2f(x-15,y+10);
				l.add(selected);
				inners.add(l);
				button = -1;
			}
		}

		if(button == 0 && add != null && selected != null) {
			int index = outer.indexOf(selected);
			if(index != -1) {
				selected = new Vector2f(x,y);
				outer.add(index+1, selected);
			}
		}

		LinkedList<Vector2f> remove = null;
		for(LinkedList<Vector2f> list : inners) {
			it = list.iterator();
			while(it.hasNext()) {
				Vector2f v = it.next();
				double dist = (v.x-x)*(v.x-x)+(v.y-y)*(v.y-y);
				if(button == 0) {
					drag = true;
					if(dist < 25) {
						selected = v;
						add = null;
					}
				}
				if(button == 1) {
					if(dist < 25) {
						it.remove();
						if(list.size() >= 1)
							selected = list.get(0);
						else {
							selected = null;
							remove = list;
						}
					}
				}
			}
		}

		for(LinkedList<Vector2f> list : inners) {
			if(button == 0 && add != null && selected != null) {
				int index = list.indexOf(selected);
				if(index != -1) {
					selected = new Vector2f(x,y);
					list.add(index+1, selected);
				}
			}
		}
		if(remove != null) {
			inners.remove(remove);
		}
	}

	public void mouseReleased(int button, int x, int y) {
		drag = false;
	}

	public void mouseMoved(int ox, int oy, int nx, int ny) {
		mouseX = nx;
		mouseY = ny;
	}
}

class Object {
	public String s;
	public int x;
	public int y;

	public Object(String s, int x, int y) {
		this.s = s;
		this.x = x;
		this.y = y;
	}
}