package Lobby;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Login.LoginView;
import Login.MyTextArea;
import Main.Main;
import Network.TcpNetwork;

public class LobbyView extends JPanel {

	public static final int WIDTH = 815; // 너비 설정
	public static final int HEIGHT = 635; // 높이 설정

	private JLabel btnSendMsg; // 전송버튼
	private JLabel btnMakeRoom;
	private JLabel btnAllRoom;
	private JLabel btnWaitingRoom;
	private JLabel btnPrev;
	private JLabel btnNext;
	private TcpNetwork tcpNetwork;
	private Main client;

	private MyTextArea chatArea;
	private MyTextArea listArea;

	private JButton RoomBtn[] = new JButton[6];

	private int RoomX[] = { 50, 315 };
	private int RoomY[] = { 110, 110, 190, 190, 270, 270 };
	private ImageIcon backGround = new ImageIcon("res/Lobby/Backgound_v.png");
	private ImageIcon showAllPImage = new ImageIcon("res/Lobby/AllRoom_ClickButton.png");
	private ImageIcon showAllRImage = new ImageIcon("res/Lobby/AllRoom_Button.png");
	private ImageIcon showWaitPImage = new ImageIcon("res/Lobby/WaitRoom_Button.png");
	private ImageIcon showWaitRImage = new ImageIcon("res/Lobby/WaitRoom_ClickButton.png");
	private ImageIcon makeRoomPImage = new ImageIcon("res/Lobby/MakeRoom_Button.png");
	private ImageIcon makeRoomRImage = new ImageIcon("res/Lobby/MakeRoom_ClickButton.png");
	private ImageIcon nextRBtnPImage = new ImageIcon("res/Lobby/Next_ClickButton.png");
	private ImageIcon nextRBtnRImage = new ImageIcon("res/Lobby/Next_Button.png");
	private ImageIcon nextLBtnPImage = new ImageIcon("res/Lobby/Pre_ClickButton.png");
	private ImageIcon nextLBtnRImage = new ImageIcon("res/Lobby/Pre_Button.png");
	private ImageIcon userListImage = new ImageIcon("res/Lobby/userList.png");
	private ImageIcon sendImage = new ImageIcon("res/Lobby/Send_Button.png");
	private ImageIcon roomImage = new ImageIcon("res/Lobby/RoomLabel2.png");
	private String chatPath = "res/Lobby/chatImg.png";
	private String listPath = "res/Lobby/chatList.png";
	private JLabel userListLabel;

	private JTextArea msgArea; // 수신된 메세지를 나타낼 변수
	private JTextArea UserList;
	private JTextField textField; // 보낼 메세지 쓰는곳

	public LobbyView(Main client) {
		this.client = client;
		initNetwork();
		initGraphic();
	}

	private void initNetwork() {
		this.tcpNetwork = TcpNetwork.getInstance();
	}

	private void initGraphic() { // 화면구성 메소드
		this.setLayout(null);

		chatArea = new MyTextArea(chatPath);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(25, HEIGHT - HEIGHT / 3 + 20, WIDTH - 50, HEIGHT / 5);
		scrollPane.setViewportView(chatArea);
		this.add(scrollPane);

		listArea = new MyTextArea(listPath);
		JScrollPane userListScrollPane = new JScrollPane();
		userListScrollPane.setBounds(WIDTH - WIDTH / 4, 40, 180, HEIGHT / 2 + 50);
		userListScrollPane.setViewportView(listArea);
		this.add(userListScrollPane);

		msgArea = new JTextArea();
		msgArea.setDisabledTextColor(new Color(0, 0, 0));
		msgArea.setEnabled(false); // 사용자가 수정못하게 막는다

		UserList = new JTextArea();
		UserList.setDisabledTextColor(new Color(0, 0, 0));
		UserList.setEnabled(false); // 사용자가 수정못하게 막는다

		userListScrollPane.setViewportView(UserList);

		// textField
		textField = new JTextField();
		textField.setBackground(Color.YELLOW);
		textField.setBounds(22, 600 - 38, LoginView.WIDTH - 100, 35);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!textField.getText().toString().equals("")) {
						tcpNetwork.send_Message("/SENDMSG " + client.getName() + " " + textField.getText());
					}

					textField.setText("");
				}
			}

		});
		this.add(textField);

		// SEND MSG Com
		btnSendMsg = new JLabel("btnSendMsg");
		btnSendMsg.setIcon(sendImage);
		btnSendMsg.setBounds(800 - sendImage.getIconWidth() - 10, 600 - sendImage.getIconHeight(),
				sendImage.getIconWidth(), sendImage.getIconHeight());
		btnSendMsg.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if (!textField.getText().toString().equals("")) {
					tcpNetwork.send_Message("/SENDMSG " + client.getName() + " " + textField.getText());
				}

				textField.setText("");
			}
		});
		this.add(btnSendMsg);

		// MAKEROOM Com
		btnMakeRoom = new JLabel("btnMakeRoom");
		btnMakeRoom.setIcon(makeRoomPImage);
		btnMakeRoom.setBounds((800 / 3) * 2 - 140, 40, makeRoomRImage.getIconWidth(), makeRoomRImage.getIconHeight());
		btnMakeRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				int roomNum = client.getRooms().size();
				btnMakeRoom.setIcon(makeRoomRImage);
				tcpNetwork.send_Message("/MAKEROOM " + roomNum + " " + client.getName());
				client.getClients().get(0).setisRoom();
				client.getClients().get(0).setRoomNum(roomNum);
				client.makeRoom(roomNum, client.getClients().get(0).getName());
				client.getGameRoomView().setRoomNum(roomNum);
				client.setGameRoomView();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				btnMakeRoom.setIcon(makeRoomPImage);
			}
		});
		this.add(btnMakeRoom);

		// ALLROOM
		btnAllRoom = new JLabel("btnAllRoom");
		btnAllRoom.setIcon(showAllRImage);
		btnAllRoom.setBounds(50, 50, showAllRImage.getIconWidth(), showAllRImage.getIconHeight());
		btnAllRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent me) {
				btnAllRoom.setIcon(showAllRImage);
			}

			public void mousePressed(MouseEvent me) {
				btnAllRoom.setIcon(showAllPImage);
			}
		});
		btnAllRoom.setIcon(showAllRImage);
		this.add(btnAllRoom);

		// btnWaitingRoom
		btnWaitingRoom = new JLabel("btnWaitingRoom");
		btnWaitingRoom.setIcon(showWaitPImage);
		btnWaitingRoom.setBounds(800 / 3 - 50, 40, showWaitRImage.getIconWidth(), showWaitRImage.getIconHeight());
		btnWaitingRoom.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent me) {
				btnWaitingRoom.setIcon(showWaitPImage);
			}

			public void mousePressed(MouseEvent me) {
				btnWaitingRoom.setIcon(showWaitRImage);
			}
		});
		this.add(btnWaitingRoom);

		// btnPrev
		btnPrev = new JLabel();
		btnPrev.setIcon(nextLBtnRImage);
		btnPrev.setBounds(240, 355, nextLBtnRImage.getIconWidth(), nextLBtnRImage.getIconHeight());
		btnPrev.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent me) {
				btnPrev.setIcon(nextLBtnRImage);
			}

			public void mousePressed(MouseEvent me) {
				btnPrev.setIcon(nextLBtnPImage);
			}
		});
		this.add(btnPrev);

		// btnNext
		btnNext = new JLabel("btnNext");
		btnNext.setIcon(nextRBtnRImage);
		btnNext.setBounds(330, 355, nextRBtnRImage.getIconWidth(), nextRBtnRImage.getIconHeight());
		btnNext.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent me) {
				btnNext.setIcon(nextRBtnPImage);
			}

			public void mouseReleased(MouseEvent me) {
				btnNext.setIcon(nextRBtnRImage);
			}
		});
		this.add(btnNext);

		// userListLabel
		userListLabel = new JLabel("userListLabel");
		userListLabel.setIcon(userListImage);
		userListLabel.setBounds(WIDTH - userListImage.getIconWidth() - 100, 30, userListImage.getIconWidth(),
				userListImage.getIconWidth());
		this.add(userListLabel);

		for (int i = 0; i < RoomBtn.length; i++) {
			RoomBtn[i] = new JButton("", roomImage);
			RoomBtn[i].setHorizontalTextPosition(SwingConstants.CENTER);
			RoomBtn[i].setVerticalTextPosition(SwingConstants.CENTER);
			RoomBtn[i].setBorderPainted(false);
			RoomBtn[i].setContentAreaFilled(false);
		}

		for (int i = 0; i < RoomBtn.length; i += 2)
			RoomBtn[i].setBounds(RoomX[0], RoomY[i], roomImage.getIconWidth(), roomImage.getIconHeight());

		for (int i = 1; i < RoomBtn.length; i += 2)
			RoomBtn[i].setBounds(RoomX[1], RoomY[i], roomImage.getIconWidth(), roomImage.getIconHeight());

		for (int i = 0; i < RoomBtn.length; i++) {
			final int index = i;
			RoomBtn[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent me) {
					int roomNum;
					if (!RoomBtn[index].getText().toString().equals("EMPTY")
							|| !RoomBtn[index].getText().toString().equals("")) {
						String token[] = RoomBtn[index].getText().toString().split("번");
						roomNum = Integer.parseInt(token[0]);
						tcpNetwork.send_Message("/ENTERROOM " + roomNum + " " + client.getName());
						client.getClients().get(0).setisRoom();
						client.getClients().get(0).setRoomNum(roomNum);
						System.out.println("★" + roomNum);
						client.getRooms().get(roomNum).addPlayer(client.getName());
						client.setGameRoomView();
						client.getGameRoomView().printPlayer(roomNum);
					}
				}
			});
		}

		for (int i = 0; i < RoomBtn.length; i++)
			this.add(RoomBtn[i]);

		setVisible(true);
	}

	public void printClients() {
		UserList.setText(" ");
		for (int i = 0; i < client.getClients().size(); i++) {
			System.out.println(client.getClients().get(i).isRoom());
			if (!client.getClients().get(i).isRoom()) {
				UserList.append(client.getClients().get(i).getName() + "\n");
				UserList.setCaretPosition(UserList.getText().length());
			}
		}
	}

	public void printRooms() {
		System.out.println("방의 개수 : " + client.getRooms().size());
		for (int i = 0; i < RoomBtn.length; i++) {
			RoomBtn[i].setText("");
			if (client.getRooms().size() > i && client.getRooms().size() != 0) {
				String roomName = Integer.toString(client.getRooms().get(i).getRoomNum()) + "번";
				RoomBtn[i].setText(roomName);
			} else {
				RoomBtn[i].setText("EMPTY");
			}
		}
	}

	public void updateRooms() {
		System.out.println("방의 개수 : " + client.getRooms().size());
		for (int i = 0; i < client.getRooms().size(); i++) {
			System.out.println(i + "번 플레이어 수: " + client.getRooms().get(i).getNumPlayer());
			if (client.getRooms().size() != 0) {
				if (client.getRooms().get(i).getNumPlayer() == 0) {
					client.getRooms().remove(i);
				}
			}
		}
		printRooms();
	}

	public void printMsg(String id, String msg) {
		String temp = "[" + id + "] : " + msg + "\n";
		chatArea.append(temp);
		chatArea.setCaretPosition(chatArea.getText().length());
	}

	public void paintComponent(Graphics g) {
		g.drawImage(backGround.getImage(), 0, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	}
}