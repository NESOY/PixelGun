package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame {

	private JPanel contentPane;
	JTextArea textArea; // Ŭ���̾�Ʈ �� ���� �޽��� ���

	private ServerSocket socket; // ��������
	private Socket soc; // �������
	private static final int Port = 30080; // ��Ʈ��ȣ

	private Vector<ServerUser> vc;// ����� ����ڸ� ������ ����
	private Server me = this;
	private ClientData clientData;

	public static void main(String[] args) throws Exception {

		Server frame = new Server();
		frame.setVisible(true);

	}

	public Server() {
		init();
		initGraphic();
		serverStart();
		makeClientData();
	}
	private void init(){
		 vc = new Vector<ServerUser>();
	}

	private void initGraphic() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 400);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane js = new JScrollPane();

		textArea = new JTextArea();
		textArea.setColumns(20);
		textArea.setRows(5);
		js.setBounds(0, 0, 264, 254);
		contentPane.add(js);
		js.setViewportView(textArea);			
		
		textArea.setEditable(false);
	}
	public void writeTextArea(String msg) {
		textArea.append(msg);
		textArea.setCaretPosition(textArea.getText().length());
	}
	private void serverStart() {
		try {
			socket = new ServerSocket(Port); // ������ ��Ʈ ���ºκ�		

			if (socket != null) // socket �� ���������� ��������
			{
				Connection();
			}

		} catch (IOException e) {
			textArea.append("������ �̹� ������Դϴ�...\n");

		}

	}

	private void Connection() {
		Thread th = new Thread(new Runnable() { 
			@Override
			public void run() {
				while (true) {
					try {
						writeTextArea("����� ���� �����...\n");
						soc = socket.accept(); 
						writeTextArea("����� ����!!\n");
						ServerUser user = new ServerUser(soc, vc, me);			
						vc.add(user);
						user.start();
					} catch (IOException e) {
						writeTextArea("!!!! accept ���� �߻�... !!!!\n");
					}
				}
			}
		});
		th.start();
	}
	/* ClientData */
	private void makeClientData(){
		clientData = new ClientData();		
	}
	public boolean loginCheck(String inputId, String inputPw){
		return clientData.loginCheck(inputId, inputPw);
	}
	public boolean signUpClient(String inputId, String inputPw){
		if(clientData.checkClient(inputId)){
			return false;
		}
		else{
			clientData.makeClient(inputId, inputPw);
			return true;
		}
		
	}

//	class UserInfo extends Thread {
//		private InputStream is;
//		private OutputStream os;
//		private DataInputStream dis;
//		private DataOutputStream dos;
//		private Socket user_socket;
//		private Vector user_vc;
//		private String msg;
//		private String name;
//
//		public UserInfo(Socket soc, Vector vc) // �����ڸ޼ҵ�
//		{
//			// �Ű������� �Ѿ�� �ڷ� ����
//			this.user_socket = soc;
//			this.user_vc = vc;
//
//			User_network();
//
//		}
//		public String getUserName(){
//			return name;
//		}
//
//		public void User_network() {
//			try {
//				is = user_socket.getInputStream();
//				dis = new DataInputStream(is);
//				os = user_socket.getOutputStream();
//				dos = new DataOutputStream(os);
//
//				byte[] b = new byte[128];
//				dis.read(b);			
//
//				msg = new String(b);
//				String[] token = msg.split(" ");
//				name = token[1]; // �̸� ����
//				textArea.append("ID " + token[1] + " ����\n");
//				textArea.setCaretPosition(textArea.getText().length());
//				// ������ ���� ������ ���� ��ġ �� ĳ���� ����.
//				String convertMsg = token[0]+" "+ token[1] + " " + vc.size() + " ";
//				b = convertMsg.getBytes();
//				dos.write(b);
//				broad_cast(b);
//
//			} catch (Exception e) {
//				textArea.append("��Ʈ�� ���� ����\n");
//				textArea.setCaretPosition(textArea.getText().length());
//			}
//
//		}
//
//
//		public void broad_cast(byte[] b) {
//			for (int i = 0; i < user_vc.size(); i++) {
//				UserInfo imsi = (UserInfo) user_vc.elementAt(i);
//				imsi.send_Message(b);
//			}	
//		}
//
//		public void send_Message(byte[] bb) {
//			try {
//				// dos.writeUTF(str);
//				// byte[] bb;
//				// bb = str.getBytes();
//				dos.write(bb); // .writeUTF(str);
//			} catch (IOException e) {
//				textArea.append("�޽��� �۽� ���� �߻�\n");
//				textArea.setCaretPosition(textArea.getText().length());
//			}
//		}
//
//		public void run() // ������ ����
//		{
//
//			while (true) {
//				try {
//
//					// ����ڿ��� �޴� �޼���
//					byte[] b = new byte[128];
//					dis.read(b);				
//					msg = new String(b);
//					textArea.append(msg + "\n");
//					textArea.setCaretPosition(textArea.getText().length());
//					String[] token = msg.split(" ");
//					switch (token[0]) {
//						case "/ENTER":							  
//							   name = token[1];   //�̸� ����
//							   int index = 0;							   
//							   for(int i=0; i<vc.size(); i++){
//								   if(vc.get(i).getUserName().equals(name)){									   
//									   index = i;
//									   break;
//								   }
//							   }				              
//							   String convertMsg = token[0]+" "+ token[1] + " " + index + " ";
//								b = convertMsg.getBytes();
//							break;
//					}
//					
//					broad_cast(b);
//
//				} catch (IOException e) {
//
//					try {
//						dos.close();
//						dis.close();
//						user_socket.close();
//						vc.removeElement(this); // �������� ���� ��ü�� ���Ϳ��� �����
//						writeTextArea(vc.size() + " : ���� ���Ϳ� ����� ����� ��\n");
//						writeTextArea("����� ���� ������ �ڿ� �ݳ�\n");						
//						String str = "/EXIT " + name;
//
//						byte[] bb = new byte[128];
//						bb = str.getBytes();
//						broad_cast(bb);
//						break;
//
//					} catch (Exception ee) {
//
//					}
//				}
//			}
//		}
//	} 

}