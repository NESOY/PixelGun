package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

class ServerUser extends Thread {
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket user_socket;
	private Vector<ServerUser> user_vc;
	private Server server;
	private String name;

	public ServerUser(Socket soc, Vector<ServerUser> vc, Server server) {
		// 매개변수로 넘어온 자료 저장
		this.server = server;
		this.user_socket = soc;
		this.user_vc = vc;
		this.name = "";
		User_network();

	}

	public void User_network() {
		try {
			is = user_socket.getInputStream();
			dis = new DataInputStream(is);
			os = user_socket.getOutputStream();
			dos = new DataOutputStream(os);
			// 바꾸기
			// byte[] b = new byte[128];
			// dis.read(b);
			// String msg = new String(b);
			// String[] token = msg.split(" ");
			// name = token[1]; // 이름 설정
			// server.writeTextArea("ID " + token[1] + " 접속\n");
			// // 서버의 들어온 순서에 따라 위치 및 캐릭터 선정.
			// // 바꾸기
			// //String convertMsg = token[0] + " " + token[1] + " " +
			// user_vc.size() + " ";
			// //send_Message(convertMsg);
			// //broad_cast(convertMsg);
		} catch (Exception e) {
			server.writeTextArea("스트링셋팅에러\n");
		}

	}
	public boolean checkLogin(String name) {
		int i;
		for (i = 0; i < user_vc.size(); i++) {
			if (user_vc.get(i).getUserName().equals(name)) {
				break;
			}
		}
		if (i == user_vc.size())
			return true;
		else
			return false;
	}
	public int getClientIndex(String name) {
		int index = 0;
		for (int i = 0; i < user_vc.size(); i++) {
			if (user_vc.get(i).getUserName().equals(name)) {
				index = i;
				break;
			}
		}
		return index;
	}

	public String getUserName() {
		return name;
	}
	public void setUserName(String name) {
		this.name = name;
	}

	public void InMessage(String str) {
		// 사용자 메세지 처리
		broad_cast(str);

	}

	public void broad_cast(String str) {
		for (int i = 0; i < user_vc.size(); i++) {
			ServerUser imsi = user_vc.elementAt(i);
			imsi.send_Message(str);
		}
	}

	public void send_Message(String str) {
		try {
			byte[] bb;
			bb = str.getBytes();
			dos.write(bb);
		} catch (IOException e) {
			server.writeTextArea("메시지 송신 에러 발생\n");
		}
	}

	public void run() // 스레드 정의
	{

		while (true) {
			try {

				// 사용자에게 받는 메세지
				byte[] b = new byte[128];
				dis.read(b);
				String msg = new String(b);
				msg = msg.trim();
				String[] token = msg.split(" ");
				switch (token[0]) {				
				case "/LOGIN":
					if (server.loginCheck(token[1], token[2]) && checkLogin(token[1])) {
						setUserName(token[1]);
						send_Message("/SUCCESS");
						broad_cast("/NEW "+token[1]);
					} else
						send_Message("/FAIL");
					break;				
				case "/SIGNUP":
					if(server.signUpClient(token[1], token[2])){
						send_Message("/CONFIRM");
					} else{
						send_Message("/FAIL");
					}					
					break;
				case "/ENTER":
					name = new String(token[1]); // 이름 설정
					String convertMsg = token[0] + " " + token[1] + " " + getClientIndex(name) + " ";
					broad_cast(convertMsg);
					break;
				default:
					broad_cast(msg);
					break;
				}

				server.writeTextArea("사용자로부터 들어온 메세지 : " + msg + "\n");
			} catch (IOException e) {
				try {
					dos.close();
					dis.close();
					user_socket.close();
					user_vc.removeElement(this); // 에러가난 현재 객체를 벡터에서 지운다
					server.writeTextArea(user_vc.size() + " : 현재 접속한 사용자 수\n");
					server.writeTextArea("사용자 접속 끊어짐 자원 반납\n");
					String str = "/EXIT " + name;					
					broad_cast(str);
					break;

				} catch (Exception ee) {

				}
			}

		}

	}

}