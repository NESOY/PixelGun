package GameRoom;

import java.util.Vector;

import Game.Player;

public class Room {
	private int roomNum;
	private String roomName;
	private String superUser;
	private boolean playing;
	private Vector<Player> pVector;

	public Room(int roomNum, String superUser) {
		this.roomNum = roomNum;
		this.superUser = superUser;
		init();
		addPlayer(superUser);
	}

	private void init() {
		pVector = new Vector<Player>();
	}

	public void addPlayer(String id) {
		pVector.add(new Player(id, pVector.size()));
	}

	public void removePlayer(String id) {
		
		for(int i=0; i<pVector.size(); i++){
			if(pVector.get(i).getId().equals(id)){
				pVector.remove(i);
				break;
			}
		}
		

	}

	public int getRoomNum() {
		return this.roomNum;
	}

	public void setRoomNum(int num) {
		this.roomNum = num;
	}

	public int getNumPlayer() {
		return pVector.size();
	}
	public int getIndex(String name){
		for(int i=0; i<pVector.size(); i++){
			if(pVector.get(i).getId().equals(name))
				return i; 
		}
		return -1;
	}

}
