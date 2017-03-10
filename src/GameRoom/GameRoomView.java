package GameRoom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Game.PixelGun;
import Game.Player;
import Main.Main;
import Network.TcpNetwork;

public class GameRoomView extends JPanel {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	private Main client;
	private TcpNetwork tcpNetwork;

	private int roomNum;
	// 버튼
	private JLabel BtnStart;
	private JLabel BtnInvite;
	private JLabel BtnBack;
	private JLabel BtnSend;
	private JLabel logLabel;

	private ImageIcon bgImg = new ImageIcon("res/GameRoom/backGround.png");
	private ImageIcon startImg = new ImageIcon("res/GameRoom/StartBtn2.png");
	private ImageIcon backImg = new ImageIcon("res/GameRoom/BackBtn2.png");
	private ImageIcon sendImg = new ImageIcon("res/GameRoom/SendBtn.png");
	private ImageIcon logImg = new ImageIcon("res/GameRoom/logImg.png");

	// Invite
	private ImageIcon inviteImg = new ImageIcon("res/GameRoom/InviteBtn2.png");

	private JButton[] userBtn = new JButton[8];
	// private ImageIcon userImg = new ImageIcon("res/GameRoom/userLabel.png");

	private ImageIcon emptyImg = new ImageIcon("res/GameRoom/emptyLabel.png");
	private ImageIcon[] userImg = new ImageIcon[8];

	private int UserX[] = { 215, 475 };
	private int UserY[] = { 50, 50, 130, 130, 210, 210, 290, 290 };
	//
	private JLabel RoomStateLabel;
	private JLabel BlueTeamLabel;
	private JLabel RedTeamLabel;
	private JLabel UserStateLabel;

	private JTextArea msgArea;
	private JTextField textField;

	// 생성자
	public GameRoomView(Main client) {
		this.client = client;
		initNetwork();
		initGraphic();
	}

	public void initNetwork() {
		tcpNetwork = TcpNetwork.getInstance();
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void initGraphic() {
		this.setLayout(null);

		// Scroll
		JScrollPane msgScrollPane = new JScrollPane();

		msgScrollPane.setBounds(215, 410, 520, 140);
		this.add(msgScrollPane);

		msgArea = new JTextArea();
		msgArea.setDisabledTextColor(new Color(0, 0, 0));
		msgArea.setEnabled(false);

		msgScrollPane.setViewportView(msgArea);

		// TextField, Msg Transport Button

		textField = new JTextField();
		textField.setBounds(215, HEIGHT - 45, 460, 30);
		textField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!textField.getText().toString().equals("")) {
						tcpNetwork.send_Message(
								"/SENDGMSG " + getRoomNum() + " " + client.getName() + " " + textField.getText());
					}

					textField.setText("");
				}
			}
		});
		this.add(textField);

		BtnSend = new JLabel();
		BtnSend.setIcon(sendImg);
		BtnSend.setBounds(WIDTH - 115, HEIGHT - 45, sendImg.getIconWidth(), sendImg.getIconHeight());

		BtnSend.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				System.out.println("TRANS BUTTON");
			}
		});

		this.add(BtnSend);

		// Start, Invite, Back Button
		BtnStart = new JLabel();
		BtnStart.setText("Start");
		BtnStart.setIcon(startImg);
		BtnStart.setBounds(53, 85, startImg.getIconWidth(), startImg.getIconHeight());

		BtnStart.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				int index = client.getRooms().get(client.getClients().get(0).getRoomNum()).getIndex(client.getName());				
				client.getClients().get(0).getPvector().get(0).setIndex(index);
				client.getClients().get(0).setIndex(index);				
				System.out.println("SET INDEX : " + index);
				
//				client.getClients().get(0).initPlayer();
				
				tcpNetwork.send_Message("/GAMEENTER " + client.getName() + " " + index );
				client.getClients().get(0).networkStart();
				
				System.out.println("전송완료");
				PixelGun game = new PixelGun();
				game.setClient(client.getClients().get(0));
				game.gameStart();
				
			}
		});

		this.add(BtnStart);

		BtnInvite = new JLabel();
		BtnInvite.setIcon(inviteImg);
		BtnInvite.setBounds(50, 170, inviteImg.getIconWidth(), inviteImg.getIconHeight());

		BtnInvite.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {

			}
		});

		this.add(BtnInvite);

		BtnBack = new JLabel();
		BtnBack.setIcon(backImg);
		BtnBack.setBounds(45, 265, backImg.getIconWidth(), backImg.getIconHeight());

		BtnBack.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				tcpNetwork.send_Message("/ROOMEXIT " + roomNum + " " + client.getClients().get(0).getName());				
				client.getRooms().get(roomNum).removePlayer(client.getName());
				client.setLobbyView();
				client.getLobbyView().updateRooms();
				client.getLobbyView().printRooms();
				client.getLobbyView().printClients();

			}
		});

		this.add(BtnBack);
		logLabel = new JLabel();
		logLabel.setIcon(logImg);
		logLabel.setBounds(30, 410, logImg.getIconWidth(), logImg.getIconHeight());
		this.add(logLabel);
		// UserButton
		for (int i = 0; i < userBtn.length; i++) {
			userBtn[i] = new JButton("", emptyImg);
			userBtn[i].setHorizontalTextPosition(SwingConstants.CENTER);
			userBtn[i].setVerticalTextPosition(SwingConstants.CENTER);
			userBtn[i].setBorderPainted(false);
			userBtn[i].setContentAreaFilled(false);
		}

		for (int i = 0; i < userBtn.length; i += 2)
			userBtn[i].setBounds(UserX[0], UserY[i], emptyImg.getIconWidth(), emptyImg.getIconHeight());
		for (int i = 1; i < userBtn.length; i += 2)
			userBtn[i].setBounds(UserX[1], UserY[i], emptyImg.getIconWidth(), emptyImg.getIconHeight());

		for (int i = 0; i < userBtn.length; i++) {
			userBtn[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent me) {
					System.out.println("User Click");
				}
			});
		}

		for (int i = 0; i < userBtn.length; i++)
			this.add(userBtn[i]);
		for( int i=0; i<userImg.length; i++){
			String path = "res/GameRoom/p"+(i+1)+"Label.png";
			userImg[i] = new ImageIcon(path);
		}
		userBtn[0].setIcon(userImg[0]);

		setVisible(true);

	}
	public void printPlayer(int index){
		System.out.println(client.getRooms().get(index).getNumPlayer());
		for(int j=0; j<client.getRooms().get(index).getNumPlayer(); j++){
			userBtn[j].setIcon(userImg[j]);
			
		}
	}

	public void printMsg(String id, String msg) {
		String temp = "[" + id + "] : " + msg + "\n";
		msgArea.append(temp);
		msgArea.setCaretPosition(msgArea.getText().length());
	}

	public void paintComponent(Graphics g) {
		g.drawImage(bgImg.getImage(), 0, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	}

}