package Server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


public class ClientData {
	private Vector<String> ClientId;
	private  Vector<String> ClientPw;
	public ClientData(){
		ClientId = new Vector<String>();
		ClientPw = new Vector<String>();
		readData();
	}
	private void readData(){
		try {
			FileReader fr = new FileReader("res/Server/ClientData.txt");
			BufferedReader br = new BufferedReader(fr);
			String s;
			String[] token;
			while((s=br.readLine())!=null){
				token = s.split(" ");
				ClientId.add(token[0]);
				ClientPw.add(token[1]);
			}
			br.close();
			fr.close();			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	private void writeData(String msg){
		try {
			FileWriter fw = new FileWriter("res/Server/ClientData.txt",true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(msg);					
			bw.close();
			fw.close();			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	public boolean checkClient(String inputId){
		int i;
		for(i=0; i< this.ClientId.size(); i++){			
			if(inputId.equals(this.ClientId.get(i)))				
				break;
		}		
		if(i==this.ClientId.size()){	//ID/PW가 안맞을때			
			return false;
		}
		else{						//ID/PW가 맞을떄			
			return true;			
		}
	}
	public boolean loginCheck(String inputId, String inputPw){
		int i;
		for(i=0; i< this.ClientId.size(); i++){			
			if(inputId.equals(this.ClientId.get(i)) && inputPw.equals(this.ClientPw.get(i)))				
				break;
		}		
		if(i==this.ClientId.size()){	//ID/PW가 안맞을때			
			return false;
		}
		else{						//ID/PW가 맞을떄			
			return true;			
		}
			
	}
	public void makeClient(String inputId,String inputPw){
		writeData(inputId + " " + inputPw+"\n");
		readData();
	}
}
