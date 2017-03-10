package Client;

import java.util.Vector;

import Game.MainGameState;
import Game.Player;
import Network.TcpNetwork;
import Network.UdpNetwork;

public class Client {

	private String name;
	private boolean isRoom;
	private int roomNum;
	private int index;

	// UDP
	private TcpNetwork tcpThread = TcpNetwork.getInstance();;
	private UdpNetwork udpThread;

	// GAME
	private MainGameState mgs;
	private Vector<Player> pv = new Vector<Player>();

	public Client(String name) {
		this.name = name;
		init();
		initPlayer();

	}
	public Vector<Player> getPvector(){
		return pv;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public int getRoomNum() {
		return this.roomNum;
	}

	public void init() {
		isRoom = false;
	}

	public boolean isRoom() {
		return isRoom;
	}

	public void setisRoom() {
		this.isRoom = true;
	}

	public void unSetIsRoom() {
		this.isRoom = false;
	}

	public void setName(String name) {
		this.name = name;
		
	}
	

	public void networkStart() {

		udpNetwork();
	}

	public Vector<Player> getPlayerVector() {
		return pv;
	}

	public void initPlayer() {
		System.out.println(name);
		pv.add(Player.makeClient(name, index));
		System.out.println("»õ·Î »ý±ä ³ð : " + pv.get(0).getId());
		

	}

	public void setMgsVector() {
		this.mgs.setVector(pv);
	}

	public void setMgs(MainGameState mgs) {
		this.mgs = mgs;
	}

	public void udpNetwork() {
		udpThread = new UdpNetwork(pv, mgs,this);
		udpThread.start();

	}

	public String getName() {
		return name;
	}

	public int getClientIndex(String id) {
		int i;
		if(name.equals(id))
			return 0;
		for (i = 0; i < pv.size(); i++) {
			if (pv.get(i).getId().equals(id)) {
				break;
			}
		}
		if (i == pv.size())
			return -1;
		else
			return i;
	}

}
