package com.acelerometro;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	
	private ServerSocket server;
	private Socket client;
	private BufferedReader in;
    private String line;
    
    static InputStreamReader inputStream;
    static BufferedReader buffer;
    static String mensagem = "";
    static ArrayList<String> arrayList = new ArrayList();
    static String dados;//campo que recebera os valores da mensagem para separar no array de String acelerometro_dados
    static String acelerometro_dados[] = new String[2000];//array onde armazena os dados separados
    
    public Server(){
    }
    
    public class Dados implements Serializable {
        String coordX, coordY, coordZ, speedS;
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			ServerSocket Servidor = new ServerSocket(4500);
            
            while(true){
                Socket socket = Servidor.accept();
                
                if(socket.isConnected()) {
                	
                	inputStream = new InputStreamReader(socket.getInputStream());
                	
                	buffer = new BufferedReader(inputStream);
                    mensagem = buffer.readLine();
                	
                	arrayList.add(mensagem);//arraylist onde recebera o pacote de com os dados do android app
                    int n = arrayList.size();//pega o tamanho do arraylist para ultilizar no "for" a seguir
                    
                    for(int i=0;i<n;i++){
                        dados=arrayList.get(i);//recebe os dados sem separar-los
                        acelerometro_dados = dados.split(" ");//separa os dados recebidos da variavel "dados" para separar onde a quebra de linha(space)
                    }
                    
                    System.out.println("Coord X         Coord Y       Coord Z        Speed m/s");
                    for(int i=0;i<acelerometro_dados.length;i++){
                        int j=i+1;
                        
                    	System.out.print(acelerometro_dados[i] + "      ");
                    	if(j%4==0) {
                    		System.out.println("\n \n");
                    		System.out.println("Coord X         Coord Y        Coord Z       Speed m/s");
                    	}
  
                    }
                    
                }else {
                	System.out.println("Não foi possível aceitar a conexão do cliente");
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
}
