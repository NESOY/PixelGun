package Login;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Main.Main;
import Network.TcpNetwork;

public class LoginView extends JPanel {

	public static final int WIDTH = 815; // 너비 설정
	public static final int HEIGHT = 635; // 높이 설정
	private TcpNetwork tcpNetwork;
	private Main client;


	private ImageIcon backGroundImage= new ImageIcon("res/LoginView/loginView.png");;

	private JTextField Id;
	private JPasswordField pw;

	private JLabel btnSignUp;
	private JLabel btnLogin;

	public LoginView(Main client) {
		this.client = client;
		initNetwork();
		initGraphic();
	}

	private void initNetwork() {
		this.tcpNetwork = TcpNetwork.getInstance();
	}

	// 화면 초기화
	private void initGraphic() {

		this.setLayout(null);
		Id = new JTextField();
		Id.setDocument(new JTextFieldLimit(10));
		Id.setBounds(800 / 2 - 100, 600 - 600 / 2, 230, 30);
		this.add(Id);

		pw = new JPasswordField();
		pw.setDocument(new JTextFieldLimit(10));
		pw.setBounds(800 / 2 - 100, 600 - 600 / 2 + 95, 230, 30);
		pw.addKeyListener(new KeyAdapter(){

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					if (!Id.getText().equals("") && !pw.getText().equals("")) {
						client.setName(Id.getText().toString());						
						client.setting();
						String msg = "/LOGIN " + Id.getText() + " " + pw.getText();
						tcpNetwork.send_Message(msg);
					}
				}				
			}			
		});
		this.add(pw);

		btnSignUp = new JLabel();
		btnSignUp.setBounds(800 / 2 + 45, 600 - 600 / 3 + 70, 140, 40);
		btnSignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				client.setSignUpView();
			}
		});
		this.add(btnSignUp);

		btnLogin = new JLabel();
		btnLogin.setBounds(800 / 2 - 170, 600 - 600 / 3 + 70, 110, 40);
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if (!Id.getText().equals("") && !pw.getText().equals("")) {
					client.setName(Id.getText());
					String msg = "/LOGIN " + Id.getText() + " " + pw.getText();
					tcpNetwork.send_Message(msg);
				}
			}
		});
		this.add(btnLogin);

	
		setVisible(true);

	}
	   public void paintComponent(Graphics g) {
		      g.drawImage(backGroundImage.getImage(), 0, 0, null);
		      setOpaque(false);
		      super.paintComponent(g);
	   }

}
