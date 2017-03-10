package Login;


import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Main.Main;
import Network.TcpNetwork;

public class SignUpView extends JPanel {

	public static final int WIDTH = 815; // 너비 설정
	public static final int HEIGHT = 635; // 높이 설정
	private TcpNetwork tcpNetwork;
	private Main client;

	private JLabel backGround;
	private JLabel IdLabel;
	private JLabel PwLabel;

	private ImageIcon backGroundImage = new ImageIcon("res/LoginView/BackGround.png");
	private ImageIcon idImg = new ImageIcon("res/LoginView/ID_Label.png");
	private ImageIcon pwImg = new ImageIcon("res/LoginView/PW_Label.png");
	private ImageIcon conImg = new ImageIcon("res/LoginView/confirmBtn.png");
	private ImageIcon canImg = new ImageIcon("res/LoginView/cancelBtn.png");

	private JTextField Id;
	private JTextField pw;

	private JLabel conLabel;
	private JLabel canLabel;

	public SignUpView(Main client) {
		this.client = client;
		initNetwork();
		initGraphic();
	}
	private void initNetwork() {
		this.tcpNetwork = TcpNetwork.getInstance();
	}
	public void initGraphic() {

		this.setLayout(null);

		IdLabel = new JLabel();
		IdLabel.setIcon(idImg);
		IdLabel.setBounds(WIDTH / 2 - 150, HEIGHT / 2, idImg.getIconWidth(), idImg.getIconHeight());
		this.add(IdLabel);

		Id = new JTextField();
		Id.setDocument(new JTextFieldLimit(10));
		Id.setBounds(WIDTH / 2 - 100, HEIGHT / 2 + 10, 230, 30);
		this.add(Id);


		PwLabel = new JLabel();
		PwLabel.setIcon(pwImg);
		PwLabel.setBounds(WIDTH / 2 - 173, HEIGHT - HEIGHT / 2 + 85, pwImg.getIconWidth(), pwImg.getIconHeight());
		this.add(PwLabel);

		pw = new JTextField();
		pw.setDocument(new JTextFieldLimit(10));
		pw.setBounds(WIDTH / 2 - 100, HEIGHT - HEIGHT / 2 + 95, 230, 30);
		this.add(pw);

		canLabel = new JLabel();
		canLabel.setIcon(canImg);
		canLabel.setBounds(WIDTH / 2 + 45, HEIGHT - HEIGHT / 3 + 40, canImg.getIconWidth(), canImg.getIconHeight());
		canLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				client.setLoginView();
			}
		});
		this.add(canLabel);

		conLabel = new JLabel();
		conLabel.setIcon(conImg);
		conLabel.setBounds(WIDTH / 2 - 100, HEIGHT - HEIGHT / 3 + 40, 110, 40);
		conLabel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				tcpNetwork.send_Message("/SIGNUP " + Id.getText() + " " + pw.getText());
				/*
				System.out.println(Id.getText() + " " + pw.getText());
				clientData.addClientData(Id.getText(), pw.getText());
				client.setLoginView();
				*/
			}
		});
		this.add(conLabel);

		setVisible(true);

	}

	public void paintComponent(Graphics g) {
		g.drawImage(backGroundImage.getImage(), 0, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	}

}
