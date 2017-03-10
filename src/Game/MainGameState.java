package Game;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;

import Client.Client;
import Network.UdpNetwork;

public class MainGameState extends SimpleGameState {

	private final static int EMITTER_BOOM = 0; /* 폭탄 번호 */
	private final static int EMITTER_BLOOD = 1; /* 피 */
	private final static int EMITTER_BARREL = 2; /* 통 */

	private final static int DAMAGE = 5;
	
	private final static int RESPONETIME = 5000;
	private final static int MAXKILL = 2;
	private final static int NUM_EMITTERS = 15;
	private final static int FIRING_DELAY = 200;
	private final static int FIRING_DELAY_SHOTGUN = 500;
	private final static int RELOAD_TIME = 1;
	private final static int MAX_AMMO = 20;
	private final static int QUOTIENT = 3;

	private Client client;

	private boolean soundEnabled = true;

	private int id;
	private int cnt;

	private Vector<Player> pv;

	private float camX = 0;
	private float camY = 0;

	private Image crosshair;

	private Polygon outer;

	
	private ArrayList<Polygon> inners = new ArrayList<Polygon>();
	private ArrayList<Line> lines = new ArrayList<Line>();
	private ArrayList<Movable> movables = new ArrayList<Movable>();
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();	

	private Image enemyTop;
	private Image enemyBottom;
	private Image enemyBottomFlipped;
	private Image[] playerTop = new Image[8];
	private Image[] playerBottom = new Image[8];
	private Image[] playerBottomFlipped = new Image[8];
	private Image bullet;
	private Image groundTexture;
	private Image groundTexture2;
	private Image barrel;
	private Image medpack;
	private Image extraAmmo;
	private Image logo;

	private Sound soundHit;
	private Sound soundHit2;
	private Sound soundShoot;
	private Sound soundExplosion;
	private Sound soundPickup;
	private Sound soundWin;
	
	private int deltaCount = 0;	

	private TrueTypeFont hpFont;
	private TrueTypeFont messageFont;
	private TrueTypeFont smallFont;
	private TrueTypeFont buttonFont;

	private ParticleSystem particleSystem;
	private ConfigurableEmitter[] boom = new ConfigurableEmitter[NUM_EMITTERS];
	private ConfigurableEmitter[] blood = new ConfigurableEmitter[NUM_EMITTERS];
	private ConfigurableEmitter[] barrelboom = new ConfigurableEmitter[NUM_EMITTERS / 2];

	private int boomEmitterCount = 0;
	private int bloodEmitterCount = 0;
	private int barrelEmitterCount = 0;

	private int shootTimer = 0;
	private int reloadTimer = 0;
	private int ammo = MAX_AMMO; // 총알 개수
	private int shotgunAmmo = 0;
	private int lastLevelShotgunAmmo = 0;

	private int level = 0;

	private float goalX;
	private float goalY;

	private boolean won;
	private boolean titleScreen = true;

	
	private int kills = 0;
	private int killsTotal = 0;

	private int animationCounter = 0;
	private boolean animationStep = true;

	private int hpTimer = 0;






	private static String[] levels = {
			"250,250,2000,250,2000,2750,250,2750,//405,2245,640,2250,910,2250,910,2585,730,2580,720,2375,410,2380,//690,1430,850,1430,845,1850,510,1850,510,1680,695,1675,//1390,1025,1730,1020,1730,795,1880,795,1885,1135,1390,1130,//715,495,865,490,875,1190,710,1185,705,890,385,895,380,735,715,730,//255,1240,505,1080,430,1530,250,1345,//1005,1245,1225,1400,1225,1500,1075,1500,//945,1795,1145,1645,1095,1895,945,1895,//1310,2060,1540,2035,1675,2185,1675,2285,1140,2215,//990,605,1390,605,1390,705,995,705,//1550,1385,1895,1565,1660,1820,1360,1715,//1685,2675,1255,2660,1260,2455,1415,2480,//1855,1910,2005,1910,1850,2155,1720,2010,//1080,870,1230,870,1230,970,1080,970,//415,2580//170,300//&&" };

	/* 생성자 */
	public MainGameState(Client client) {
		this.client = client;
		id = (int) System.currentTimeMillis();
	}

	/* 생성된 시간을 얻는 함수 */
	public int getID() {
		return id;
	}

	/*
	 * org.newdawn.slick.state.GameState의 init() Initialise the state. It should
	 * load any resources it needs at this stage 이미지, 소리, 맵정보, 플레이어 정보, Object
	 * 추가 및 초기화하는 함수.
	 */
	@Override
	public void init(GameContainer container, StateBasedGame game) {
		ConfigurableEmitter boomEmitter = null;
		ConfigurableEmitter bloodEmitter = null;
		ConfigurableEmitter barrelEmitter = null;
		try {
			crosshair = new Image("res/Item/crosshair.gif");
			enemyTop = new Image("res/Item/enemyTop.gif");
			enemyBottom = new Image("res/Item/enemyBottom.gif");
			enemyBottomFlipped = enemyBottom.getFlippedCopy(true, false); // 이미지
																			// 복사하는
																			// 과정.
																			// (flipHorizontal,flipVertical)

			for (int i = 0; i < 8; i++) {
				playerTop[i] = new Image("res/Player/playerTop" + (i + 1) + ".png");
				playerBottom[i] = new Image("res/Player/playerBottom" + (i + 1) + ".png");
				playerBottomFlipped[i] = playerBottom[i].getFlippedCopy(true, false);
			}
			bullet = new Image("res/Item/bullet.gif");
			groundTexture = new Image("res/Item/groundTexture3.gif");
			groundTexture2 = new Image("res/Item/groundTexture2.gif");
			barrel = new Image("res/Item/barrel.gif");
			medpack = new Image("res/Item/medpack.gif");
			extraAmmo = new Image("res/Item/ammo.gif");
			logo = new Image("res/Item/cavernousshooter.png");

			soundHit = new Sound("res/Sound/hit.ogg");
			soundHit2 = new Sound("res/Sound/hit3.ogg");
			soundShoot = new Sound("res/Sound/shoot.ogg");
			soundExplosion = new Sound("res/Sound/explosion3.ogg");
			soundPickup = new Sound("res/Sound/pickup2.ogg");
			soundWin = new Sound("res/Sound/win3.ogg");
			// lose 소리 추가할 것.

			/* xml 추가 */
			boomEmitter = ParticleIO.loadEmitter("res/XML/boom.xml");
			bloodEmitter = ParticleIO.loadEmitter("res/XML/blood.xml");
			barrelEmitter = ParticleIO.loadEmitter("res/XML/barrelboom.xml");

			/* 폰트 지정 */
			hpFont = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 48), true);
			messageFont = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48), true);
			smallFont = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16), true);
			buttonFont = new TrueTypeFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24), true);

			/* 입자 시스템 */

			particleSystem = new ParticleSystem("res/Item/particle2.png");
			particleSystem.setRemoveCompletedEmitters(true); // Indicate if completed emitters should be removed
			particleSystem.setUsePoints(true); // Indicate if this engine should use points to render the particles particleSystem.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);

			// container.setMouseCursor(crosshair,31,31);
		} catch (Exception se) {
			se.printStackTrace();
		}
		container.setMouseGrabbed(true);

		// 복사 하는 과정
		for (int i = 0; i < NUM_EMITTERS; i++) {
			boom[i] = boomEmitter.duplicate();
			blood[i] = bloodEmitter.duplicate();
		}
		// 통 복사.
		for (int i = 0; i < NUM_EMITTERS / 2; i++) {
			barrelboom[i] = barrelEmitter.duplicate();
		}


		initLevel(level);

	}

	public void addPlayer(Movable player) {
		movables.add(player);
	}

	public void initLevel(int level) {
		inners.clear();
		movables.clear();
		projectiles.clear();
		lines.clear();
		won = false;
		pv.get(0).getPlayer().x = Player.playerX[pv.get(0).getIndex()];
		pv.get(0).getPlayer().y = Player.playerY[pv.get(0).getIndex()];
		for (int i = 0; i < pv.size(); i++) {
			System.out.println("추가중");
			pv.get(i).initPlayer();
			pv.get(i).getPlayer().setName(pv.get(i).getId());
			movables.add(pv.get(i).getPlayer());
		}

		ammo = MAX_AMMO;
		reloadTimer = 0;
		cnt = 0;
		shotgunAmmo = lastLevelShotgunAmmo;

		// MAP정보 / PLAYER좌표 /GOAL좌표 // OBJECT
		// 좌표 해제하는 작업.
		String[] levelParts = levels[level].split("&&");// stage 해체작업
		String[] tmp = levelParts[0].split("//"); //
		String[] coords = tmp[0].split(",");
		outer = new Polygon();
		// 맵 좌표 입력
		for (int i = 0; i < coords.length - 1; i += 2) {
			outer.addPoint(Integer.parseInt(coords[i]), Integer.parseInt(coords[i + 1]));
		}
		inners.clear();

		for (int i = 1; i < tmp.length - 2; i++) {
			Polygon p = new Polygon();
			coords = tmp[i].split(",");
			for (int j = 0; j < coords.length - 1; j += 2) {
				p.addPoint(Integer.parseInt(coords[j]), Integer.parseInt(coords[j + 1]));
			}
			inners.add(p);
		}

		// 플레이어 좌표 추가
		coords = tmp[tmp.length - 2].split(",");
		// player.x = Integer.parseInt(coords[0]);
		// player.y = Integer.parseInt(coords[1]);
		// for (int i = 0; i < pv.size(); i++) {
		// pv.get(i).getPlayer().x = Integer.parseInt(coords[0])+i*10;
		// pv.get(i).getPlayer().y = Integer.parseInt(coords[1])+i*10;
		// }

		// 골인 좌표 추가.
		coords = tmp[tmp.length - 1].split(",");
		goalX = Integer.parseInt(coords[0]);
		goalY = Integer.parseInt(coords[1]);

		// Object생성
		if (levelParts.length > 1) {
			String[] objs = levelParts[1].split("//");
			for (String obj : objs) {
				String[] objDetails = obj.split(",");
				if (objDetails[0].equals("E")) {
					Enemy enemy = new Enemy("Enemy", Integer.parseInt(objDetails[1]), Integer.parseInt(objDetails[2]),
							new Circle(0, 0, 15));
					double angle = Math.random() * 2 * Math.PI;
					enemy.dx = (float) Math.cos(angle) * 0.76f;
					enemy.dy = (float) Math.sin(angle) * 0.76f;
					movables.add(enemy);
				}
				if (objDetails[0].equals("B")) {
					movables.add(new Barrel("Barrel", Integer.parseInt(objDetails[1]), Integer.parseInt(objDetails[2]),
							new Circle(0, 0, 20)));
					////////////////////////////////////////////// ADD
					////////////////////////////////////////////// BREAKABLES
					////////////////////////////////////////////// HERE
				}
			}
		}

		for (int i = 0; i < outer.getPointCount() - 1; i++) {
			lines.add(new Line(outer.getPoint(i)[0], outer.getPoint(i)[1], outer.getPoint(i + 1)[0],
					outer.getPoint(i + 1)[1]));
			lines.add(new Line(outer.getPoint(outer.getPointCount() - 1)[0],
					outer.getPoint(outer.getPointCount() - 1)[1], outer.getPoint(0)[0], outer.getPoint(0)[1]));
		}
		for (int j = 0; j < inners.size(); j++) {
			Polygon p = inners.get(j);
			for (int i = 0; i < p.getPointCount() - 1; i++) {
				lines.add(new Line(p.getPoint(i + 1)[0], p.getPoint(i + 1)[1], p.getPoint(i)[0], p.getPoint(i)[1]));
				lines.add(new Line(p.getPoint(0)[0], p.getPoint(0)[1], p.getPoint(p.getPointCount() - 1)[0],
						p.getPoint(p.getPointCount() - 1)[1]));
			}
		}
		/*
		 * for(Polygon p : inners) { for(int i=0; i<p.getPointCount()-1; i++) {
		 * lines.add(new
		 * Line(p.getPoint(i)[0],p.getPoint(i)[1],p.getPoint(i+1)[0],p.getPoint(
		 * i+1)[1])); lines.add(new
		 * Line(p.getPoint(p.getPointCount()-1)[0],p.getPoint(p.getPointCount()-
		 * 1)[1],p.getPoint(0)[0],p.getPoint(0)[1])); } }
		 */
	}

	/*
	 * Update the game logic here.
	 */
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {		
		delta = 3;
		
		particleSystem.update(delta);
		

		deltaCount += delta;

		animationCounter += delta;

		for (int i = 0; i < pv.size(); i++) {
			pv.get(i).getPlayer().dx = 0;
			pv.get(i).getPlayer().dy = 0;
		}
		if (killsTotal == MAXKILL) {
			won = true;
			try {
				UdpNetwork.sendMsg("/GAMEOVER");
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (!won && !pv.get(0).playerDead && !titleScreen) {
			for (int i = 0; i < pv.size(); i++) {
				if (pv.get(i).isMoveLeft()) {
					pv.get(i).getPlayer().dx = -0.8f;
					// pv.get(i).getPlayer().x += -0.8f * delta / 5f;

				} else if (pv.get(i).isMoveRight()) {
					pv.get(i).getPlayer().dx = 0.8f;
					// pv.get(i).getPlayer().x += 0.8f * delta / 5f;
				}
				if (pv.get(i).isMoveUp()) {
					pv.get(i).getPlayer().dy = -0.8f;
					// pv.get(i).getPlayer().y += -0.8f * delta / 5f;
				} else if (pv.get(i).isMoveDown()) {
					pv.get(i).getPlayer().dy = 0.8f;
					// pv.get(i).getPlayer().y += 0.8f * delta / 5f;
				}
			}

			ArrayList<Movable> removeList = new ArrayList<Movable>();
			ArrayList<Movable> addList = new ArrayList<Movable>();

			for (int i = 0; i < projectiles.size(); i++) {
				Projectile p = projectiles.get(i);
				// 총알 사거리.
				// if (Math.abs(p.x - pv.get(0).getPlayer().x) > 700 ||
				// Math.abs(p.y - pv.get(0).getPlayer().y) > 700) {
				// removeList.add(p);
				// continue;
				// }
				p.x += p.dx * delta / 1f;
				p.y += p.dy * delta / 1f;
				for (Line line : lines) {
					if (p.getShape().intersects(line)) {
						if (soundEnabled)
							soundHit.play(1f, 0.3f);
						removeList.add(p);
						break;
					}
				}
				
				for (int j = 0; j < movables.size(); j++) {
					Movable m = movables.get(j);					
					if (m instanceof Enemy) {
						if (p.getShape().intersects(m.getShape()) && !p.hostile) {
							if (soundEnabled)
								soundHit2.play(1f, 0.3f);
							removeList.add(p);
							boolean dead = ((Enemy) m).hurt(10);
							if (dead) {
								removeList.add(m);
								killsTotal++;
								double rand = Math.random();
								if (rand < 0.15) {
									addList.add(new Medpack("Medpack", m.x, m.y, new Circle(0, 0, 20)));
								} else if (rand < 0.3) {
									addList.add(new Ammo("Ammo", m.x, m.y, new Circle(0, 0, 20)));
								}
							}
							ConfigurableEmitter emitter = getEmitter(EMITTER_BLOOD);
							emitter.setPosition(m.x, m.y);
							emitter.replay();
							emitter.setEnabled(true);
							particleSystem.addEmitter(emitter);

						}
					} else if (m instanceof Barrel) {
						if (p.getShape().intersects(m.getShape())) {
							if (soundEnabled)
								soundHit.play(1f, 0.3f);
							removeList.add(p);
							boolean dead = ((Barrel) m).hurt(10);
							if (dead) {
								if (soundEnabled)
									soundExplosion.play(1f, 0.5f);
								removeList.add(m);
								ConfigurableEmitter emitter = getEmitter(EMITTER_BARREL);
								emitter.setPosition(m.x, m.y);
								emitter.replay();
								emitter.setEnabled(true);
								particleSystem.addEmitter(emitter);
								for (Movable m2 : movables) {
									if (m2.distance(m) < 100) {
										if (m2 instanceof Enemy) {
											dead = ((Enemy) m2).hurt(100);
											if (dead) {
												removeList.add(m2);
												killsTotal++;
												double rand = Math.random();
												if (rand < 0.15) {
													addList.add(new Medpack("MedPack", m.x, m.y, new Circle(0, 0, 20)));
												} else if (rand < 0.3) {
													addList.add(new Ammo("Ammo", m.x, m.y, new Circle(0, 0, 20)));
												}
											}
										}
										if (!(m2 instanceof Enemy) && !(m2 instanceof Barrel)) {
											pv.get(0).hp -= 100;
											if (pv.get(0).hp <= 0) {
												pv.get(0).hp = 0;
												removeList.add(pv.get(0).getPlayer());
												pv.get(0).playerDead = true;
											}
										}
									}
								}
							}
						}
					} else if (m instanceof Medpack) {

					} else {
						if (p.getShape().intersects(m.getShape()) && !p.getName().equals(m.getName()) && !m.getName().equals("")) {
							System.out.println(m.getName());
							System.out.println(p.getName());
							if (soundEnabled)
								soundHit2.play(1f, 0.3f);
							removeList.add(p);
							int index = client.getClientIndex(m.getName());
							pv.get(index).hp -= DAMAGE;
							ConfigurableEmitter emitter = getEmitter(EMITTER_BLOOD);
							emitter.setPosition(m.x, m.y);
							emitter.replay();
							emitter.setEnabled(true);
							particleSystem.addEmitter(emitter);
							if (pv.get(0).hp <= 0) {
								removeList.add(pv.get(0).getPlayer());
								pv.get(0).playerDead = true;
								try {
									UdpNetwork.sendMsg("/DEAD " + pv.get(0).getId() + " " + p.getName());
								} catch (UnknownHostException | InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < movables.size(); i++) {
				Movable m = movables.get(i);
				m.x += m.dx * delta / 4f;
				m.y += m.dy * delta / 4f;

				float distance = m.distance(pv.get(0).getPlayer());

				if (m instanceof Enemy) {

					((Enemy) m).deltaCount += delta;

					if (distance < 300 && ((Enemy) m).deltaCount >= 800) {
						if (soundEnabled)
							soundShoot.play(1f, 0.3f);
						Projectile projectile = new Projectile("Enemy", m.x, m.y, new Circle(0, 0, 4), true);
						double angle = Math.atan2(m.y - pv.get(0).getPlayer().y, m.x - pv.get(0).getPlayer().x);
						projectile.dx = (float) (-Math.cos(angle));
						projectile.dy = (float) (-Math.sin(angle));
						projectiles.add(projectile);
						((Enemy) m).deltaCount = 0;
					}

					if (distance < 250 && distance > 150) {
						double angle = Math.atan2(m.y - pv.get(0).getPlayer().y, m.x - pv.get(0).getPlayer().x);
						m.dx = (float) -Math.cos(angle) * 0.76f;
						m.dy = (float) -Math.sin(angle) * 0.76f;
					}

					for (Movable m2 : movables) {
						// 밀어내기 코드
						if (m2 instanceof Enemy && m2.getShape().intersects(m.getShape())) {
							double angle = Math.atan2(m.y - m2.y, m.x - m2.x);
							float pushX = (float) Math.cos(angle) * 0.1f;
							float pushY = (float) Math.sin(angle) * 0.1f;

							//////////////// NEEDS TO BE FIXED, ENEMIES CAN BE
							//////////////// PUSHED OFF THE MAP
							if (outer.contains(m.x + pushX, m.y + pushY)) {
								m.x += pushX;
								m.y += pushY;
							}
						}
					}
				} else if (m instanceof Medpack) {
					if (m.distance(pv.get(0).getPlayer()) < 20) {
						removeList.add(m);
						pv.get(0).hp += 30;
						soundPickup.play(1.0f, 0.3f);
					}
				} else if (m instanceof Ammo) {
					if (m.distance(pv.get(0).getPlayer()) < 20) {
						removeList.add(m);
						shotgunAmmo += 6;
						soundPickup.play(1.0f, 0.3f);
					}
				} else if (!(m instanceof Enemy) && !(m instanceof Barrel) && !(m instanceof Medpack)
						&& !(m instanceof Ammo)) {
					for (Movable m2 : movables) {
						if (m2 == m)
							continue;
						if (!(m2 instanceof Medpack) && !(m2 instanceof Ammo)
								&& m2.getShape().intersects(m.getShape())) {
							double angle = Math.atan2(m.y - m2.y, m.x - m2.x);
							float pushX = (float) Math.cos(angle) * 5f;
							float pushY = (float) Math.sin(angle) * 5f;
							m.x += pushX;
							m.y += pushY;
						}
					}
				}

				for (int j = 0; j < lines.size(); j++) {
					Line line = lines.get(j);
					if (m.getShape().intersects(line)) {

						m.x -= m.dx * delta / 4f;
						m.y -= m.dy * delta / 4f;

						/*
						 * if(m instanceof Enemy && distance > 200) { double
						 * angle = Math.random()*2*Math.PI; m.dx =
						 * (float)Math.cos(angle)*0.76f; m.dy =
						 * (float)Math.sin(angle)*0.76f; continue; }
						 */

						// ENEMIES DON'T SLIDE CORRECTLY

						float ldx = line.getX1() - line.getX2();
						if (Math.abs(ldx) > 0.1) {
							ldx /= Math.abs(ldx);
						}
						float ldy = line.getY1() - line.getY2();
						if (Math.abs(ldy) > 0.1) {
							ldy /= Math.abs(ldy);
						}

						float ndx = ldy;
						float ndy = -ldx;

						float dot = ndx * m.dx + ndy * m.dy;

						m.x = m.x + ndx * 0.31f;
						m.y = m.y + ndy * 0.31f;

						m.dx -= ndx * dot;
						m.dy -= ndy * dot;

						m.x += m.dx * 0.3f;
						m.y += m.dy * 0.3f;

					}
				}
			}

			for (int i = 0; i < removeList.size(); i++) {
				Movable m = removeList.get(i);
				if (m instanceof Projectile) {
					ConfigurableEmitter emitter = getEmitter(EMITTER_BOOM);
					emitter.setPosition(m.x, m.y);
					emitter.replay();
					emitter.setEnabled(true);
					particleSystem.addEmitter(emitter);
				}
				projectiles.remove(m);
				movables.remove(m);
			}
			for (Movable m : addList) {
				movables.add(m);
			}
			if (keys[Input.KEY_R]) {
				ammo = 0;
				reloadTimer = RELOAD_TIME;
			}

			if (reloadTimer > 0) {
				reloadTimer -= delta;
				if (reloadTimer <= 0) {
					ammo = MAX_AMMO;
				}
			}
			shootTimer += delta;
			// if (mouse[0]) {
			// sendShoot();
			// }
			// if (mouse[1]) {
			// sendShoot();
			// }

			hpTimer += delta;

			if (hpTimer > 1000 && pv.get(0).hp > 100) {
				pv.get(0).hp--;
				hpTimer = 0;
			}
		}

		if (keys[Input.KEY_ESCAPE]) {
			System.exit(0);
		}

		camX += (pv.get(0).getPlayer().x - camX - 400) * 0.006f * delta / 1f;
		camY += (pv.get(0).getPlayer().y - camY - 300) * 0.006f * delta / 1f;

		if (!won && Math.sqrt(Math.pow(pv.get(0).getPlayer().x - goalX, 2) + Math.pow(pv.get(0).getPlayer().y - goalY, 2)) < 40) {
			soundWin.play(1.0f, 0.3f);
			won = true;
			killsTotal += kills;
			lastLevelShotgunAmmo = shotgunAmmo;
		}

		if (animationCounter > 170) {
			animationStep = !animationStep;
			animationCounter = 0;
		}

	}

	/*
	 * org.newdawn.slick.state.GameState의 render() Render this state to the
	 * game's graphics context Render : 어떤 상태가 되게 만들다. 그래픽 업데이트 함수.
	 */
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		g.setAntiAlias(false);

		g.setColor(new Color(45, 35, 20));
		g.fillRect(0, 0, 800, 600); // 배경색

		g.resetTransform();

		g.translate(-camX, -camY);

		// g.setColor(new Color(40,30,20));
		g.setColor(new Color(225, 210, 160));
		// g.texture(outer,groundTexture,3f,3f,true);
		g.texture(outer, groundTexture, 1 / 1024f, 1 / 1024f); // 바닥 그리는 함수.
		/*
		 * if(fpsDropped < 2000) {
		 * g.texture(outer,groundTexture,1/1024f,1/1024f); } else {
		 * g.fill(outer); }
		 */

		g.setAntiAlias(true);

		g.setColor(Color.black);
		g.setLineWidth(4); // 외곽선 굵기dddw
		g.draw(outer); // 외곽선 그리기
		/*
		 * g.setColor(Color.white); for(Line line : lines) { g.draw(line); }
		 */

		// 내부 벽 그리기
		for (int i = 0; i < inners.size(); i++) {
			Polygon p = inners.get(i);
			g.setColor(new Color(45, 35, 20));
			g.fill(p);
			g.setColor(Color.black);
			g.draw(p);
		}

//		// 말 떠들기
//		if (!pv.get(0).playerDead && !titleScreen && !won) {
//			if (level == 0) {
//				g.setColor(new Color(200, 180, 100, 150));
//				g.setFont(buttonFont);
//				g.setAntiAlias(false);
//				g.drawString("MADE BY YOUNG JAE & MIN SEOK.", 280, 1290);
//				g.drawString("Try to defeat them!", 360, 1320);
//				g.drawString("Exploding barrels", 1170, 1062);
//				g.drawString("are deadly.", 1210, 1092);
//				g.drawString("Press R to do a manual reload.", 1150, 420);
//				g.drawString("Right click to fire your shotgun", 1150, 480);
//				g.drawString("(if you've found ammo for it!).", 1150, 510);
//				g.setAntiAlias(true);
//			}
//			if (level == 1) {
//				g.setColor(new Color(200, 180, 100, 150));
//				g.setFont(buttonFont);
//				g.setAntiAlias(false);
//				// g.drawString("Explore the cave!",723,385);
//				g.drawString("Explore the cave!", 1100, 700);
//				g.setAntiAlias(true);
//
//			}
//		}

		g.setLineWidth(2);

		// 골 그리기
		g.setColor(new Color(150, 200, 100, 100));
		g.drawOval(goalX - 40, goalY - 40, 80, 80);
		g.drawOval(goalX - 36, goalY - 36, 72, 72);
		g.drawLine(goalX, goalY - 22, goalX, goalY + 22);
		g.drawLine(goalX - 22, goalY, goalX + 22, goalY);

		// 총알 그리기
		g.setLineWidth(1);
		g.setColor(new Color(200, 200, 150));
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = projectiles.get(i);
			// if (Math.abs(p.x - pv.get(0).getPlayer().x) > 500 || Math.abs(p.y
			// - pv.get(0).getPlayer().y) > 400) {
			// continue;
			// }
			float angle = (float) Math.toDegrees(Math.atan2(p.dy, p.dx));
			g.rotate(p.x, p.y, angle);
			g.drawImage(bullet, p.x, p.y);
			g.rotate(p.x, p.y, -angle);
			// g.fill(p.getShape());

		}

		// 캐릭터, 배럴, 아이템, 적그리기
		for (Movable m : movables) {
			if (Math.abs(m.x - pv.get(0).getPlayer().x) > 500 || Math.abs(m.y - pv.get(0).getPlayer().y) > 400) {
				continue;
			}
			// g.resetTransform();
			// g.translate(-camX,-camY);
			if (m instanceof Enemy) {
				float angle = (float) Math.toDegrees(Math.atan2(m.dy, m.dx)) + 90;
				float angle2 = (float) Math
						.toDegrees(Math.atan2(m.y - pv.get(0).getPlayer().y, m.x - pv.get(0).getPlayer().x)) - 90;
				if (m.distance(pv.get(0).getPlayer()) > 300) {
					g.rotate(m.x, m.y, angle);
				} else {
					g.rotate(m.x, m.y, angle2);
				}
				// g.rotate(m.x,m.y,angle);
				if (won || pv.get(0).playerDead || titleScreen) {
					g.drawImage(enemyBottom, m.x - m.getShape().getWidth() / 2, m.y - m.getShape().getHeight() / 2);
				} else {
					if (animationStep) {// ((deltaCount+((Enemy)m).offset)/8)%2
										// == 0) {
						g.drawImage(enemyBottom, m.x - m.getShape().getWidth() / 2, m.y - m.getShape().getHeight() / 2);
					} else {
						g.drawImage(enemyBottomFlipped, m.x - m.getShape().getWidth() / 2,
								m.y - m.getShape().getHeight() / 2);
					}
				}
				// g.rotate(m.x,m.y,-angle);
				g.drawImage(enemyTop, m.x - m.getShape().getWidth() / 2, m.y - m.getShape().getHeight() / 2);
				if (m.distance(pv.get(0).getPlayer()) > 300) {
					g.rotate(m.x, m.y, -angle);
				} else {
					g.rotate(m.x, m.y, -angle2);
				}
			} else if (m instanceof Barrel) {
				g.drawImage(barrel, m.x - barrel.getWidth() / 2, m.y - barrel.getHeight() / 2);
			} else if (m instanceof Medpack) {
				g.drawImage(medpack, m.x - medpack.getWidth() / 2, m.y - medpack.getHeight() / 2);
			} else if (m instanceof Ammo) {
				g.drawImage(extraAmmo, m.x - extraAmmo.getWidth() / 2, m.y - extraAmmo.getHeight() / 2);
			} else {
			
				int index;
				float angle;			
				if ((index = client.getClientIndex(m.getName())) != -1) {
					
					// Player 그리기
					if (index == 0) {
						angle = (float) Math.toDegrees(Math.atan2(pv.get(0).getPlayer().y - (camY + mouseY),
								pv.get(0).getPlayer().x - (camX + mouseX))) - 90;
					} else {
						angle = pv.get(index).getAngel();
					}
					g.rotate(m.x, m.y, angle);
					// g.rotate(m.x,m.y,angle);
					if ((m.dx == 0 && m.dy == 0) || (won || pv.get(0).playerDead || titleScreen)) {						
						g.drawImage(playerBottom[pv.get(index).getIndex()], m.x - m.getShape().getWidth() / 2,
								m.y - m.getShape().getHeight() / 2);
					} else {
						if (animationStep) {// (deltaCount/8)%2 == 0) {
							g.drawImage(playerBottom[pv.get(index).getIndex()], m.x - m.getShape().getWidth() / 2,

							m.y - m.getShape().getHeight() / 2);
						} else {
							g.drawImage(playerBottomFlipped[pv.get(index).getIndex()],
									m.x - m.getShape().getWidth() / 2, m.y - m.getShape().getHeight() / 2);
						}
					}
					// g.rotate(m.x,m.y,-angle);

					g.drawImage(playerTop[pv.get(index).getIndex()], m.x - m.getShape().getWidth() / 2,
							m.y - m.getShape().getHeight() / 2);
					g.rotate(m.x, m.y, -angle);
					// g.setColor(Color.white);
					// g.draw(m.getShape());
				}
			}
		}

		// 효과 그리기
		
		particleSystem.render();
		
		// 화면 추적
		g.translate(camX, camY);
		// emitters[0].reset();
		/*
		 * emitters[0].replay(); emitters[0].setPosition(400,400);
		 * emitters[0].setEnabled(true); particleSystem.addEmitter(emitters[0]);
		 */

		g.setAntiAlias(false);
		g.setColor(new Color(120, 100, 50, 100));
		g.fillRoundRect(703, 528, 89, 65, 12);
		g.fillRoundRect(635, 558, 59, 35, 6);
		g.fillRoundRect(655, 529, 39, 24, 6);
		// 스타트 버튼이요
		if (won || pv.get(0).playerDead || titleScreen) {
			g.setColor(new Color(30, 20, 10, 220));
			g.fillRoundRect(50, 50, 700, 450, 24); // 타이틀 색깔
			Rectangle r = new Rectangle(315, 400, 160, 50);
			if (r.contains(mouseX, mouseY)) {
				g.setColor(new Color(70, 50, 30, 220));
			} else {
				g.setColor(new Color(10, 10, 5, 220));
			}
			g.fillRoundRect(315, 400, 160, 50, 12);
		}
		g.setAntiAlias(true);
		g.setColor(new Color(200, 180, 100, 100));
		g.drawRoundRect(703, 528, 89, 65, 12);
		g.drawRoundRect(635, 558, 59, 35, 6);
		g.drawRoundRect(655, 529, 39, 24, 6);
		if (won || pv.get(0).playerDead || titleScreen) {
			// g.setColor(new Color(110,100,100,150));
			g.drawRoundRect(50, 50, 700, 450, 24); // 타이틀 외곽선
			g.drawRoundRect(315, 400, 160, 50, 12); // 타이틀 외곽선
		}

		g.setColor(new Color(200, 180, 100));
		g.setAntiAlias(false);
		if (ammo > 0) {
			g.fillRoundRect(638, 562, 53 * ammo / MAX_AMMO, 28, 4); // 총알 탄창 표현
		} else {
			g.fillRoundRect(638, 562, 53 * (RELOAD_TIME - reloadTimer) / RELOAD_TIME, 28, 4); // 총알
																								// 탄창
																								// 표현
		}
		g.setAntiAlias(true);

		g.setFont(hpFont);
		g.drawString("" + pv.get(0).hp, 800 - 54 - hpFont.getWidth("" + pv.get(0).hp) / 2, 532); // HP
		// 부분

		g.setFont(messageFont);
		String message;
		if (pv.get(0).playerDead) {
			message = "DEFEAT!";
			g.drawString(message, 400 - messageFont.getWidth(message) / 2 - 10, 190);
		}
		int statsYoff = 0;
		if (won) {
			if (level == levels.length - 1) {
				message = "VICTORY!";
				statsYoff = -40;
			} else {
				message = "LEVEL COMPLETE!";
			}
			g.drawString(message, 400 - messageFont.getWidth(message) / 2 - 10, 190 + statsYoff);
		}
		if (titleScreen) {
			g.drawImage(logo, 400 - logo.getWidth() / 2 - 10, 90); // 타이틀 로고
		}
		g.setFont(smallFont);
		if (pv.get(0).playerDead) {
			message = "You are dead.";
			g.drawString(message, 400 - smallFont.getWidth(message) / 2 - 10, 260);
		}
		if (won) {
			message = "You KILL EVERYONE!";
			g.setFont(smallFont);
			g.drawString(message, 400 - smallFont.getWidth(message) / 2 - 10, 260 + statsYoff);

			if (level == levels.length - 1) {
				statsYoff = -15;
			} else {
				statsYoff = 0;
			}
			message = "Enemies killed: ";
			g.drawString(message, 400 - smallFont.getWidth(message) / 2 - 10 - 8, 325 + statsYoff);
			g.setFont(buttonFont);
			g.drawString("" + killsTotal, 400 + smallFont.getWidth(message) / 2 - 15, 318 + statsYoff);

//			if (level == levels.length - 1) {
//				g.setFont(smallFont);
//				message = "Kills total: ";
//				g.drawString(message, 400 - smallFont.getWidth(message) / 2 - 10 - 8, 325 + 15);
//				g.setFont(buttonFont);
//				g.drawString("" + killsTotal, 400 + smallFont.getWidth(message) / 2 - 15, 318 + 15);
//			}

		}
		if (titleScreen) {
			message = "Move and aim with WASD and mouse.";
			g.drawString(message, 400 - smallFont.getWidth(message) / 2 - 10, 296);
			message = "[Q] toggles the three particle settings.";
			g.drawString(message, 400 - smallFont.getWidth(message) / 2 - 10, 316);
			message = "Click below to begin your journey!";
			g.drawString(message, 400 - smallFont.getWidth(message) / 2 - 10, 336);
			g.drawString("Made by Young Jae, Min Seok Network", 10, 570);
		}
		g.setFont(buttonFont);
		message = "" + shotgunAmmo;
		g.drawString(message, 675 - buttonFont.getWidth(message) / 2, 527);
		if (pv.get(0).playerDead) {
			message = "Restart";
			g.drawString(message, 400 - buttonFont.getWidth(message) / 2 - 7, 410);
		}
		if (won) {
			if (level == levels.length - 1) {
				message = "Main menu";
			} else {
				message = "Continue";
			}
			g.drawString(message, 400 - buttonFont.getWidth(message) / 2 - 7, 410);

		}
		if (titleScreen) {
			message = "START!";
			g.drawString(message, 400 - buttonFont.getWidth(message) / 2 - 7, 410);
		}
		/*
		 * if(fpsDropped > 2000 && fpsDropped < 1000) { message =
		 * "Low FPS! Quality decreased.";
		 * g.drawString(message,400-buttonFont.getWidth(message)/2-7,290); }
		 */

		g.drawImage(crosshair, mouseX - crosshair.getWidth() / 2, mouseY - crosshair.getHeight() / 2);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		Rectangle r = new Rectangle(315, 400, 160, 50);
		if (r.contains(x, y) && (won || pv.get(0).playerDead || titleScreen)) {
			if (pv.get(0).playerDead) {
				initLevel(level);
			}
			if (titleScreen) {
				titleScreen = false;
			}
			if (won) {
				level = (level + 1) % levels.length;
				if (level == 0) {
					titleScreen = true;
					killsTotal = 0;
					shotgunAmmo = 0;
					lastLevelShotgunAmmo = 0;
				}
				initLevel(level);
			}
		} else {

			super.mousePressed(button, x, y);

			if (button == 0) {
				sendShoot();
				// shoot(x, y, false);
			}
			// if (button == 1) {
			// sendShoot();
			// // shoot(x,y,true);
			// }

		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		super.mouseMoved(oldx, oldy, newx, newy);
		cnt++;
		if (cnt % QUOTIENT == 0) {
			float angle = (float) Math.toDegrees(
					Math.atan2(pv.get(0).getPlayer().y - (camY + mouseY), pv.get(0).getPlayer().x - (camX + mouseX)))
					- 90;
			try {
				UdpNetwork.sendMsg("/MV " + client.getName() + " " + angle);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendShoot() {
		if (ammo > 0 && !won && !pv.get(0).playerDead) {
			double angle1 = Math.atan2(pv.get(0).getPlayer().y - (camY + mouseY),
					pv.get(0).getPlayer().x - (camX + mouseX));
			float addX = (float) (20 * Math.cos(angle1 - 70));
			float addY = (float) (20 * Math.sin(angle1 - 70));
			double angle = Math.atan2(pv.get(0).getPlayer().y + addY - (camY + mouseY),
					pv.get(0).getPlayer().x + addX - (camX + mouseX));
			try {
				UdpNetwork.sendMsg("/SHOOT " + client.getName() + " " + (pv.get(0).getPlayer().x + addX) + " "
						+ (pv.get(0).getPlayer().y + addY) + " " + (float) (-Math.cos(angle)) + " "
						+ (float) (-Math.sin(angle)));
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
			ammo--;
			shootTimer = 0;

			if (ammo == 0) {
				reloadTimer = RELOAD_TIME;
			}
		}
	}

	public void shoot(String id, float x, float y, float dx, float dy, boolean shotgun) {
		// if (shootTimer > FIRING_DELAY && ammo > 0 && !won &&
		// !pv.get(0).playerDead) {
		Projectile projectile;
		projectile = new Projectile(id, x, y, new Circle(0, 0, 5), false);
		projectile.dx = dx;
		projectile.dy = dy;
		projectiles.add(projectile);
		if (soundEnabled)
			soundShoot.play(1f, 0.3f);

		// double angle1 = Math.atan2(pv.get(0).getPlayer().y - (camY + mouseY),
		// pv.get(0).getPlayer().x - (camX + mouseX));

		// float addX = (float) (10 * Math.cos(angle1 - 90));
		// float addY = (float) (10 * Math.sin(angle1 - 90));

		// double angle = Math.atan2(pv.get(0).getPlayer().y + addY - (camY +
		// mouseY),
		// pv.get(0).getPlayer().x + addX - (camX + mouseX));
		// Projectile projectile = new Projectile(pv.get(0).getPlayer().x +
		// addX, pv.get(0).getPlayer().y + addY,
		// new Circle(0, 0, 5), false);

		// projectile.dx = (float) (-Math.cos(angle));
		// projectile.dy = (float) (-Math.sin(angle));

		// }
		// if (shotgun && shotgunAmmo > 0 && shootTimer > FIRING_DELAY_SHOTGUN
		// && !won && !playerDead) {
		// if (soundEnabled)
		// soundShoot.play(1f, 0.3f);
		//
		// double angle1 = Math.atan2(pv.get(0).getPlayer().y - (camY + mouseY),
		// pv.get(0).getPlayer().x - (camX + mouseX));
		// float addX = (float) (10 * Math.cos(angle1 - 90));
		// float addY = (float) (10 * Math.sin(angle1 - 90));
		//
		// double angle = Math.atan2(pv.get(0).getPlayer().y + addY - (camY +
		// mouseY),
		// pv.get(0).getPlayer().x + addX - (camX + mouseX));
		// for (int i = -2; i < 3; i++) {
		// Projectile projectile = new Projectile(pv.get(0).getPlayer().x +
		// addX, pv.get(0).getPlayer().y + addY,
		// new Circle(0, 0, 5), false);
		// projectile.dx = (float) (-(0.8 + Math.random() * 0.6) *
		// Math.cos(angle + (Math.PI * i) / 24));
		// projectile.dy = (float) (-(0.8 + Math.random() * 0.6) *
		// Math.sin(angle + (Math.PI * i) / 24));
		// projectiles.add(projectile);
		// }
		//
		// shootTimer = 0;
		// shotgunAmmo--;
		// // System.out.println(particleSystem.getEmitterCount());
		// }
	}

	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
		if (key == Input.KEY_UP || key == Input.KEY_W) {
			try {
				UdpNetwork.sendMsg(
						"/UP " + client.getName() + " " + pv.get(0).getPlayer().x + " " + pv.get(0).getPlayer().y);
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (key == Input.KEY_DOWN || key == Input.KEY_S) {
			try {
				UdpNetwork.sendMsg(
						"/DP " + client.getName() + " " + pv.get(0).getPlayer().x + " " + pv.get(0).getPlayer().y);
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (key == Input.KEY_LEFT || key == Input.KEY_A) {
			try {
				UdpNetwork.sendMsg(
						"/LP " + client.getName() + " " + pv.get(0).getPlayer().x + " " + pv.get(0).getPlayer().y);
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (key == Input.KEY_RIGHT || key == Input.KEY_D) {
			try {
				UdpNetwork.sendMsg(
						"/RP " + client.getName() + " " + pv.get(0).getPlayer().x + " " + pv.get(0).getPlayer().y);
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		
	}

	@Override
	public void keyReleased(int key, char c) {
		super.keyReleased(key, c);
		if (key == Input.KEY_UP || key == Input.KEY_W) {
			try {
				UdpNetwork.sendMsg(
						"/UR " + client.getName() + " " + pv.get(0).getPlayer().x + " " + pv.get(0).getPlayer().y);
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (key == Input.KEY_DOWN || key == Input.KEY_S) {
			try {
				UdpNetwork.sendMsg(
						"/DR " + client.getName() + " " + pv.get(0).getPlayer().x + " " + pv.get(0).getPlayer().y);
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (key == Input.KEY_LEFT || key == Input.KEY_A) {
			try {
				UdpNetwork.sendMsg(
						"/LR " + client.getName() + " " + pv.get(0).getPlayer().x + " " + pv.get(0).getPlayer().y);
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (key == Input.KEY_RIGHT || key == Input.KEY_D) {
			try {
				UdpNetwork.sendMsg(
						"/RR " + client.getName() + " " + pv.get(0).getPlayer().x + " " + pv.get(0).getPlayer().y);
			} catch (UnknownHostException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void reset() {

	}

	public ConfigurableEmitter getEmitter(int type) {
		if (type == EMITTER_BOOM) {
			boomEmitterCount = (boomEmitterCount + 1) % boom.length;
			return boom[boomEmitterCount];
		}
		if (type == EMITTER_BLOOD) {
			bloodEmitterCount = (bloodEmitterCount + 1) % blood.length;
			return blood[bloodEmitterCount];
		}
		if (type == EMITTER_BARREL) {
			barrelEmitterCount = (barrelEmitterCount + 1) % barrelboom.length;
			return barrelboom[barrelEmitterCount];
		}
		return null;
	}

	public void setVector(Vector<Player> pv) {
		this.pv = pv;
	}
	public void addKill(){
		killsTotal += 1;
	}
}
