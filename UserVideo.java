//package com.video.servflv;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
 
/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class UserVideo extends Thread {
    private Socket socket;
    private ChatVideo server;
    private PrintWriter writer;
    boolean handsh=false;
    int length=0;
    String userName="";
    int mascinf=2;
    int mascsup=5;
    int payloadlen=0;
    int tammen=0;
    int contador=0;
    int contadorm=0;
    String serverMessage;
    public boolean sockopen=false;
    boolean cond=true;
    static byte[] mascara=new byte[4];
    OutputStream output=null;    
    boolean iden=false;
   // public BufferedReader reader
    public BufferReader bf=null;
    public UserVideo(Socket socket, ChatVideo server) {
        this.socket = socket;
        this.server = server;
        sockopen=true;
    }
 
    public void run() {
        try {
       //     InputStream input = socket.getInputStream();
       //     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
              //InputStream reader = socket.getInputStream();
             BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
              
       
            output=socket.getOutputStream();
            writer = new PrintWriter(output, true);
        
          
            int read=0;            
            boolean bc0=false;boolean bc1=false;boolean bc2=false; 
            int numMensajes=0;
            byte c1[]=new byte[1536];
            bf=new BufferReader(this.server);
            //int c1x=new int[1536];
            int suma=0;
            //Handshake handshake = new Handshake();

        while(true){ 
              if(input.available()>0 && bc0==false){
                    byte c0[]=new byte[1];c0[0]=(byte)input.read();bc0=true;
              //       System.out.println(new String(c0,"UTF-8"));
              }
            	
            int valor=0;
             if(input.available()>0 && bc1==false && bc0==true&& bc2==false ){
                   int bytesLeidos=0;
                   do{
                   	read=input.read();
                   	c1[bytesLeidos]=(byte)read;
                       //read=input.read(c1,bytesLeidos,1536-bytesLeidos);
                       //bytesLeidos+=read;
                       bytesLeidos++;  
                   }while(bytesLeidos<1536);
                 //  System.out.println("ya estan leidos los 1536 bytes");
                    bc1=true;
                                       
                   }
                   if(bc0==true&&bc1==true&&bc2==false){
                      	 byte c2[]=new byte[9];
                      	 c2[0]=(byte)0x03;
                      	  for(int i=1;i<=8;i++){c2[i]=(byte)0x00;} 
                
                      	 output.write(c2);
                      	 output.flush();
                      	 byte arrAleatorio[]=new byte[1528];
                           Random rd=new Random();                         
                         rd.nextBytes(arrAleatorio); 
                         
                        for(int i=0;i<arrAleatorio.length;i++){
                	//output.write((int)arrAleatorio[i]);
                	         output.write(arrAleatorio[i]);
                         }
                        //  output.flush();
                         
                                                   
                         bc2=true;  
                           output.write(c1);
                 output.flush();
                         //System.out.println("y ya envie lo que dice ");                         
                                             	 
                      	 }
                      	 
                        if(input.available()>0 &&bc2==true &&bc0==true &&bc1==true){
                        
             int i=0;  
               do{read=input.read();i++;/*System.out.println("recogiendo bytes");*/}while(i<1536);
               
                 bf.Read(input,output,socket);
                 break; 
                              
               }
                
               // if(input.available()>0 &&bc2==true &&bc0==true &&bc1==true){
                //  System.out.println("Ya esta el handshake paso el input al bufferReader y tengo disponibles "+input.available());
                               
                        
               //}                      	 
                      	 
                        /*c2[0]=(byte)0x03;
                        for(int i=1;i<=8;i++){c2[i]=(byte)0x00;} 
                
                output.write(c2);
                output.flush();
                       handshake.writeC1(output);
                       output.flush();     	
            	}
            	
     /*        handshake.readS0(input);
             handshake.readS1(input);
              handshake.writeC0(output);
              handshake.writeC1(output);
              output.flush();
              handshake.readS2(input);
              handshake.writeC2(output);
              output.flush();
              System.out.println("y luego");
            
            }
        */
              
             
              
              
            }
         
    
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }


 }
 
         
     

  

     public void cerrarSocket(){
     try{
     socket.close();
     sockopen=false;
     }
     catch(IOException ex){System.out.println(ex.toString());}
     }
   
     
     

     
     
 
  
}
