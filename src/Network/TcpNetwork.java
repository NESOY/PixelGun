package Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import Client.*;
import Game.MainGameState;
import Game.Player;
import Main.Main;

public class TcpNetwork extends Thread {
	private static final String SERVERIP = "113.198.81.86";
	private static final int SERVERPORT = 30080;
	private static TcpNetwork instance = new TcpNetwork();

	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Vector<Player> pv;
	private MainGameState mgs;
	private Main client;

	// public TcpNetwork(String ip, int port, Vector<Player> pv, MainGameState
	// mgs) {
	private TcpNetwork() {

		// this.pv = pv;
		// this.mgs = mgs;
		try {
			socket = new Socket(SERVERIP, SERVERPORT);
			if (socket != null) {
				Connection();
			}
		} catch (IOException e) {
		}
	}

	public synchronized static TcpNetwork getInstance() {
		return instance;
	}

	public void setClient(Main client) {
		this.client = client;
	}

	public void setPlayerVector(Vector<Player> pv) {
		this.pv = pv;
	}

	public void setMainGameState(MainGameState mgs) {
		this.mgs = mgs;
	}

	public void Connection() {
		try {
			is = socket.getInputStream();
			dis = new DataInputStream(is);

			os = socket.getOutputStream();
			dos = new DataOutputStream(os);

		} catch (IOException e) {
		}
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					try {
						int index;
						byte[] b = new byte[128];
						dis.read(b);
						String msg = new String(b);
						msg = msg.trim();
						String cmdToken[] = msg.split("/");
						for (int i = 0; i < cmdToken.length; i++) {
							String token[] = cmdToken[i].split(" ");
							switch (token[0]) {
							case "GAMEENTER":								
								if (!client.getClients().get(0).getName().equals(token[1])) {
									if (((index = getPlayerIndex(token[1])) == -1)) {
										Player temp1 = Player.makeClient(token[1], Integer.parseInt(token[2]));
										temp1.setId(token[1]);
										temp1.setIndex(Integer.parseInt(token[2]));
										
										pv.add(temp1);
										System.out.println("한명추가요 : " + token[1]);
										send_Message("/GAMEENTER " + pv.get(0).getId() + " " + pv.get(0).getIndex());

										while (true) {
											if (mgs != null)
												break;
										}
										mgs.initLevel(0);
									}
								} else {
									System.out.println("자기자신");
									pv.get(0).setId(client.getClients().get(0).getName());
									getPlayerIndex(token[1]);
									
									pv.get(0).getPlayer().x = Player.playerX[Integer.parseInt(token[2])];
									pv.get(0).getPlayer().y = Player.playerY[Integer.parseInt(token[2])];
									pv.get(0).setIndex(Integer.parseInt(token[2]));
								}
								break;

							case "GAMEEXIT":
								if ((index = getPlayerIndex(token[1])) != -1) {
									pv.remove(index);
									mgs.initLevel(0);
								}
								break;
							case "NEW":
								if (client.getName() != null) {
									if ((index = client.getClientIndex(token[1])) == -1) {
										if (client.getName().equals(token[1])) {
											client.getClients().get(0).setName(token[1]);
										} else {
											client.getClients().add(new Client(token[1]));
											send_Message("/NEW " + client.getName());
										}
										client.getLobbyView().printClients();
									}
								}
								break;
							case "EXIT":
								if (token.length >= 2) {
									if ((index = client.getClientIndex(token[1])) != -1) {
										client.getClients().removeElementAt(index);
										client.getLobbyView().printRooms();
										client.getLobbyView().printClients();
									}
								}
								break;

							case "SUCCESS":								
								client.setLobbyView();
								client.getLobbyView().printClients();
								break;
							case "FAIL":
								client.setName("");
								break;
							case "CONFIRM":
								client.setLoginView();
								break;
							case "SENDMSG":
								client.getLobbyView().printMsg(token[1], token[2]);
								break;
							case "SENDGMSG":
								System.out.println("★" + client.getClients().get(0).isRoom());
								System.out.println("★" + client.getClients().get(0).getRoomNum());
								if (client.getClients().get(0).isRoom()
										&& (client.getClients().get(0).getRoomNum() == Integer.parseInt(token[1]))) {
									client.getGameRoomView().printMsg(token[2], token[3]);
								}
								break;
							case "MAKEROOM":
								if (!client.getName().equals(token[2])) {
									client.getClients().get(client.getClientIndex(token[2])).setisRoom();
									client.makeRoom(Integer.parseInt(token[1]), token[2]);
									
									client.getLobbyView().printRooms();
									client.getLobbyView().printClients();
								}
								break;
							case "ENTERROOM":
								client.getClients().get(client.getClientIndex(token[2])).setisRoom();
								if (client.getClients().get(0).getRoomNum() == Integer.parseInt(token[1])
										&& !client.getName().equals(token[2])) {
									client.getRooms().get(0).addPlayer(token[2]);
									client.getGameRoomView().printPlayer(0);

								} else {
									client.getLobbyView().printRooms();
									client.getLobbyView().printClients();
								}
								break;
							case "ROOMEXIT":
								System.out.println(msg);
								if ((index = client.getClientIndex(token[2])) != -1) {
									if (client.getRooms().size() > 0 && !client.getName().equals(token[2])) {
										client.getRooms().get(Integer.parseInt(token[1])).removePlayer(token[2]);
										client.getGameRoomView().printPlayer(Integer.parseInt(token[1]));
										System.out.println("시팔 여기야 "
												+ client.getRooms().get(Integer.parseInt(token[1])).getNumPlayer());

									}
									client.getClients().get(index).unSetIsRoom();
									client.getLobbyView().updateRooms();
									client.getLobbyView().printRooms();
									client.getLobbyView().printClients();

								}

								break;
							}
						}
					} catch (IOException e) {
						System.out.println("READ ERROR");
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							break;
						} catch (IOException e1) {

						}

					}

				}

			}
		});
		th.start();
		// 바꾸기
		// 나중에 게임 접속할때 보내야해!!
		// send_Message("/ENTER " + pv.get(0).getId() + " ");

	}

	public int getPlayerIndex(String id) {
		int i;
		for (i = 0; i < pv.size(); i++) {
			System.out.println(pv.get(i).getId() + " : " + id);
			if (pv.get(i).getId().equals(id)) {
				break;
			}
		}
		if (i == pv.size())
			return -1;
		else
			return i;
	}

	public void send_Message(String str) {
		try {
			byte[] bb;
			bb = str.getBytes();
			dos.write(bb); // .writeUTF(str);
		} catch (IOException e) {
			System.out.println("Send Error");
		}
	}
}
