//package com.video.servflv;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.ArrayList;  
import java.util.List;
public class RtmpMessage
	{
		int timestamp = 0;
		int length = 0;
		byte  type_id = 0;
		int stream_id = 0;
		int extend_timestamp = 0;

		long _timestamp = 0;
		byte  codecId = 0;

		byte  csid = 0;
		public int index = 0;
		ByteBuffer payload = null;
	
		
		public Vector payloadx=new Vector();

     public List payloady=new ArrayList();


      public int getTimeStamp(){return timestamp;}
		public int getLength(){return length;}
		public byte getTypeId(){return type_id;}
		public int getStreamId(){return stream_id;}
		public int getExtendTimeStamp(){return extend_timestamp;}
		public long get_TimeStamp(){return _timestamp;}
		public byte getCodecId(){return codecId;}
		public byte getCsid(){return csid;}
		public int getIndex(){return index;}
		public ByteBuffer getPayload(){return payload;}
      public Vector getPayloadx(){return payloadx;}
      
      public byte[] getPayloady(){return (byte [])payloady.get(0);}      
      
      

      public void setTimeStamp(int timestamp){this.timestamp =timestamp;}
		public void setLength(int length){this.length =length;}
		public void setTypeId(byte type_id){this.type_id = type_id;}
		public void setStreamId(int stream_id){this.stream_id = stream_id;}
		public void setExtendTimeStamp(int extend_timestamp){this.extend_timestamp =extend_timestamp;}
		public void set_TimeStamp(long _timestamp){this._timestamp = _timestamp;}
		public void setCodecId(byte codecId){ this.codecId = codecId;}
		public void setCsid(byte csid){this.csid = csid;}
		public void setIndex(int index){this.index = index;}
		
      public void setPayloady(byte bytes[]){
         if(payloady.size()>0)       
          payloady.remove(0);
          payloady.add(bytes);      
      }		
		
		
    public void updatePayloady(byte bytes[],int pos){
    int j=0;
    for(int i=pos;i<bytes.length;i++){((byte [])payloady.get(0))[i]=bytes[j++];}
    }		
		
		
		public void setPayload(byte bytes[]){
                  this.payload=ByteBuffer.wrap(bytes, 0, bytes.length);

                  }

      public void setPayload(byte bytes[],int pos){
      	this.payload=ByteBuffer.wrap(bytes, 0, bytes.length);
                for(int i=0;i<bytes.length;i++){this.payload.put(pos++,bytes[i]);;}
                
                
                }



 public void setPayloadx(byte bytes[],int pos){
      	
                for(int i=0;i<bytes.length;i++)
                {this.payloadx.insertElementAt(new Byte(bytes[i]),pos++);}
                
                
                }


		void Clear()
		{
			
       			
			index = 0;
			timestamp = 0;
			extend_timestamp = 0;
			
			if (length > 0) {
			
			}
		}

		boolean IsCompleted() 
		{
			
			if (index == length && length > 0 &&
				payloadx != null) {
				return true;
			}

			return false;
		}
		

		
		
		
	}



