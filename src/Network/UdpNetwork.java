package Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Vector;

import Client.Client;
import Game.MainGameState;
import Game.Player;

public class UdpNetwork extends Thread {

	private final static String INET_ADDR = "224.0.0.3";
	private final static int PORT = 8888;
	InetAddress address;
	private Vector<Player> pv;
	private Client client;
	private int index;
	private MainGameState mgs;

	public UdpNetwork(Vector<Player> pv, MainGameState mgs, Client client) {
		this.pv = pv;
		this.mgs = mgs;
		this.client = client;
		
	}


	public int getClientIndex(String id) {
		int i;
		if(client.getName().equals(id))
			return 0;
		for (i = 0; i < pv.size(); i++) {
			System.out.println("1:"+pv.get(i).getId());
			System.out.println("2:"+id);
			if (pv.get(i).getId().equals(id)) {
				break;
			}
		}
		if (i == pv.size())
			return -1;
		else
			return i;
	}
	public synchronized void addPlayer(String id, int index){
		Player temp1 = Player.makeClient(id, index);
		pv.add(temp1);
	}

	@Override
	public synchronized void run() {
		
		try {
			address = InetAddress.getByName(INET_ADDR);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try (MulticastSocket clientSocket = new MulticastSocket(PORT)) {
			// Joint the Multicast group.
			clientSocket.joinGroup(address);

			while (true) {
				// Receive the information and print it.
				byte[] buf = new byte[256];
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				clientSocket.receive(msgPacket);

				String msg = new String(buf, 0, buf.length);				
				msg = msg.trim();
				System.out.println(msg);
				String token[] = msg.split(" ");				
				switch (token[0]) {
				
				case "/UP":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).getPlayer().x = Float.parseFloat(token[2]);
						pv.get(index).getPlayer().y = Float.parseFloat(token[3]);
						pv.get(index).UnsetMoveDown();
						pv.get(index).setMoveUp();
					}
					break;
				case "/DP":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).getPlayer().x = Float.parseFloat(token[2]);
						pv.get(index).getPlayer().y = Float.parseFloat(token[3]);
						pv.get(index).UnsetMoveUp();
						pv.get(index).setMoveDown();
					}
					break;
				case "/LP":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).getPlayer().x = Float.parseFloat(token[2]);
						pv.get(index).getPlayer().y = Float.parseFloat(token[3]);
						pv.get(index).UnsetMoveRight();
						pv.get(index).setMoveLeft();
					}
					break;
				case "/RP":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).getPlayer().x = Float.parseFloat(token[2]);
						pv.get(index).getPlayer().y = Float.parseFloat(token[3]);
						pv.get(index).UnsetMoveLeft();
						pv.get(index).setMoveRight();
					}
					break;
				case "/UR":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).UnsetMoveUp();
						pv.get(index).getPlayer().x = Float.parseFloat(token[2]);
						pv.get(index).getPlayer().y = Float.parseFloat(token[3]);

					}
					break;
				case "/DR":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).getPlayer().x = Float.parseFloat(token[2]);
						pv.get(index).getPlayer().y = Float.parseFloat(token[3]);
						pv.get(index).UnsetMoveDown();
					}
					break;
				case "/LR":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).getPlayer().x = Float.parseFloat(token[2]);
						pv.get(index).getPlayer().y = Float.parseFloat(token[3]);
						pv.get(index).UnsetMoveLeft();
					}
					break;
				case "/RR":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).getPlayer().x = Float.parseFloat(token[2]);
						pv.get(index).getPlayer().y = Float.parseFloat(token[3]);
						pv.get(index).UnsetMoveRight();
					}
					break;
				case "/MV":
					if ((index = getClientIndex(token[1])) != -1) {
						pv.get(index).setAngle(Float.parseFloat(token[2]));
					}
					break;
				case "/SHOOT":					
					mgs.shoot(token[1], Float.parseFloat(token[2]), Float.parseFloat(token[3]),
								Float.parseFloat(token[4]), Float.parseFloat(token[5]), false);
					
					break;
				case "/DEAD":
					if ((index = getClientIndex(token[1])) != -1) {
							pv.get(index).playerDead = true;
							if(index !=0 && token[2].equals(pv.get(0).getId())){
								mgs.addKill();
							}
					}
					break;
				case "/GAMEOVER":
					//게임이 끝날때 !!!!!!!!!!!!
					System.exit(1);

				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	public static void sendMsg(String msg) throws UnknownHostException, InterruptedException {

		InetAddress addr = InetAddress.getByName(INET_ADDR);

		try (DatagramSocket serverSocket = new DatagramSocket()) {

			DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, PORT);
			serverSocket.send(msgPacket);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
