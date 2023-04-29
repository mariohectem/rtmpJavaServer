//package com.video.servflv;
import java.io.*;
import java.net.*;
import java.util.*;
 
/**
 * This is the chat server program.
 * Press Ctrl + C to terminate the program.
 *
 * @author www.codejava.net
 */
public class ChatVideo {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserVideo> userThreads = new HashSet<>();
    private Rtmp rtmp=new Rtmp();
     public Map<String, amfh.AmfObject> meta_data_=new HashMap<>();
        public List avc_sequence_header_= new ArrayList();
    public ChatVideo(int port) {
        this.port = port;
    }
 
 
 
 
 
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
      while (true) {
                Socket socket = serverSocket.accept();
               UserVideo newUser = new UserVideo(socket, this);
                userThreads.add(newUser);
                newUser.start();
 
            }
 
        } catch (IOException ex) {
          
            ex.printStackTrace();
        }
    }
 

void guardarMetaData( Map<String, amfh.AmfObject> meta_data_){
this.meta_data_=meta_data_;
}

void guardarAVC(byte[] arr){
if(avc_sequence_header_.size()>0)
 avc_sequence_header_.remove(0);
 avc_sequence_header_.add(avc_sequence_header_);

}


    void broadcast(String message, UserVideo excludeUser) {
     for (UserVideo aUser : userThreads) {
  
        }
    }
 
 

 void SendMediaDataRes(byte type, int timestamp, byte[]payload,int payload_size,String tipo) {
	
     for (UserVideo aUser : userThreads) {
     	
     	
     	
     	
          if(tipo.equals("avc")){   	
            if (aUser.bf.is_player_) {
            if (!(aUser.bf.is_playing_))
                  aUser.bf.SendMediaData(type,timestamp,payload,payload_size);
          }else    
             aUser.bf.SendMediaData(type,timestamp,payload,payload_size);
        
            }
        }
    }
 
 
void SendMediaData(byte type, int timestamp, byte[]payload,int payload_size,String tipo,byte[]avc,Map<String, amfh.AmfObject> meta_data,String path) {
	
     for (UserVideo aUser : userThreads) {
      if (aUser.bf!=null /*&& path!=null && aUser.bf.stream_path_.equals(path)*/){
      	
     	if (aUser.bf.is_player_&& aUser.bf.stream_path_.equals(path)) {
            if(aUser.bf.metaDataY==false){
               System.out.println("Me envio envio metadata");       
               
               
                for(Map.Entry<String,amfh.AmfObject>entry:meta_data.entrySet()){

System.out.println("clave decodificada "+"--"+entry.getKey()+"----"+entry.getValue().amf_string+"----"+entry.getValue().amf_number);
                //     y++;

               }

               
                    	
            	aUser.bf.SendMetaData(meta_data);aUser.bf.metaDataY=true;}
 
           if(aUser.bf.avcSequenceY==false){
      
   
            
	      aUser.bf.SendMediaData((byte)rtmp.RTMP_AVC_SEQUENCE_HEADER,timestamp,avc,avc.length);            
             //  aUser.bf.SendMediaData((byte)rtmp.RTMP_AVC_SEQUENCE_HEADER,timestamp,avc,avc.length);    
               System.out.println("Me envio envio av!!!!!!!!!!!!!!!!!!!!c");            	
            	System.out.println("con un size de "+Integer.toString(avc.length));
              aUser.bf.avcSequenceY=true;
                 
            }
            if(aUser.bf.avcSequenceY==true){
            	aUser.bf.SendMediaData(type,timestamp,payload,payload_size);}
             
                 	
     	    
     	}    
     	    
        }
    }}
  
  
 
 void cerrarSocket(String ruta) {
	
     for (UserVideo aUser : userThreads) {
     	if(!aUser.bf.brwh && aUser.sockopen && aUser.bf.stream_path_.equals(ruta)&&aUser.bf.is_player_){
     	          aUser.bf.brwh=true;
     	          aUser.cerrarSocket();
     	            }
     	            
     	            
     	
          
        }
    }
 
 
void SendMediaDataa(byte type, int timestamp, byte[]payload,int payload_size,String tipo,String path) {
  	
     for (UserVideo aUser : userThreads) {
     	
     	
     	    if (aUser.bf!=null /*&& aUser.bf.stream_path_.equals(path)*/){	
     	
            if (aUser.bf.is_player_&& aUser.bf.stream_path_.equals(path)) {
            
          if(tipo.equals("aac")){   	
            if (!(aUser.bf.is_playing_))
                  aUser.bf.SendMediaData(type,timestamp,payload,payload_size);
          }else    
             aUser.bf.SendMediaData(type,timestamp,payload,payload_size);
             
            }
        }}
    } 
 



 
 
void SendMetaData( Map<String, amfh.AmfObject> meta_data)
{


     for (UserVideo aUser : userThreads) {
 	
    	
            if (aUser.bf.is_player_) {
                 
                if (!(aUser.bf.is_playing_))                
                    aUser.bf.SendMetaData(meta_data);
           
         }
        }


} 
   void broadcastt( byte bres[],UserVideo excludeUser) {
       for (UserVideo aUser : userThreads) {
       
        }
    }
 
    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }
 
    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, UserVideo aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser); //  System.out.println("The user " + userName + " quitted");
        }
    }
 
    Set<String> getUserNames() {
        return this.userNames;
    }
 
    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();}
        
        

        
        
      
        
     public static void main(String[] args) {
    
        int port=1935;
        ChatVideo server = new ChatVideo(port);
        server.execute();
    }  }
