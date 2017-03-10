
package Main;

import java.awt.CardLayout;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Client.Client;
import Game.PixelGun;
import GameRoom.GameRoomView;
import GameRoom.Room;
import Lobby.LobbyView;
import Login.LoginView;
import Login.SignUpView;
import Network.TcpNetwork;

public class Main extends JFrame {
	public static final int WIDTH = 815; // 너비 설정
	public static final int HEIGHT = 635; // 높이 설정

	private String name;
	private JPanel nowView;
	private LoginView loginView;
	private SignUpView signUpView;
	private LobbyView lobbyView;
	private CardLayout cardLayout;
	private GameRoomView gameRoomView;
	private TcpNetwork tcpNetwork;
	private Client me = new Client("");

	private boolean isRoom;
	private Vector<Client> cVector;
	private Vector<Room> rVector;

	public Main() {
		initState();
		initGraphic();
		initNetwork();
	}

	private void initState() {
		cVector = new Vector<Client>();
		rVector = new Vector<Room>();
		cVector.add(me);
		isRoom = false;
	}

	private void initNetwork() {
		this.tcpNetwork = TcpNetwork.getInstance();
		tcpNetwork.setPlayerVector(me.getPlayerVector());
		tcpNetwork.setClient(this);
	}

	public static void main(String[] args) {
		new Main();
	}

	private void initGraphic() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(WIDTH / 2, HEIGHT / 2, WIDTH, HEIGHT);
		cardLayout = new CardLayout();
		loginView = new LoginView(this);
		lobbyView = new LobbyView(this);
		signUpView = new SignUpView(this);
		gameRoomView = new GameRoomView(this);

		setLayout(cardLayout);
		this.add("LoginView", loginView);
		this.add("LobbyView", lobbyView);
		this.add("SignUpView", signUpView);
		this.add("GameRoomView", gameRoomView);

		setLoginView();

		setVisible(true);

	}

	public String getNowPanel() {
		if (nowView instanceof LoginView)
			return "LoginView";
		else if (nowView instanceof LobbyView)
			return "LobbyView";
		else if (nowView instanceof SignUpView)
			return "SignUpView";
		else
			return "GameRoomView";
	}

	public void setting() {
		me.setName(name);
		me.getPlayerVector().get(0).setId(name);
	}

	public void GameStart() {
		System.out.println("게임호출");
		Thread gameThread = new Thread(new PixelGun());

		gameThread.start();
		System.out.println("쓰렉드 시작");
	}

	public void setLoginView() {
		nowView = loginView;
		cardLayout.show(getContentPane(), "LoginView");
	}

	public void setSignUpView() {
		nowView = signUpView;
		cardLayout.show(getContentPane(), "SignUpView");
	}

	public void setLobbyView() {
		nowView = lobbyView;
		cardLayout.show(getContentPane(), "LobbyView");
	}

	public void setGameRoomView() {
		nowView = gameRoomView;	
		cardLayout.show(getContentPane(), "GameRoomView");
	}

	public LobbyView getLobbyView() {
		return lobbyView;
	}

	public GameRoomView getGameRoomView() {
		return gameRoomView;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Vector<Client> getClients() {
		return cVector;
	}

	public int getClientIndex(String id) {
		int i;
		for (i = 0; i < cVector.size(); i++) {
			if (cVector.get(i).getName().equals(id)) {
				break;
			}
		}
		if (i == cVector.size())
			return -1;
		else
			return i;
	}

	public Vector<Room> getRooms() {
		return rVector;
	}

	public boolean existRoom(int roomNum) {
		for (int i = 0; i < rVector.size(); i++) {
			if (rVector.get(i).getRoomNum() == roomNum)
				return true;
		}
		return false;
	}

	public void makeRoom(int roomNum, String name) {

		rVector.add(new Room(roomNum, name));
	}

}
