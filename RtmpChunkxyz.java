//package com.video.servflv;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
public class RtmpChunkxyz{
	public int chunk_stream_id_=0;
	private int stream_id_=0;
	private int in_chunk_size_=128;
	private int out_chunk_size_=60000;
	private int kDefaultStreamId=1;
	private int kChunkMessageHeaderLen[]={11,7,3,0};
	public State state_;
	private Map<Integer,RtmpMessage>rtmp_messages_=new HashMap<>();
	public enum State{PARSE_HEADER,PARSE_BODY};
	private boolean bodyfull=false;
   int byteb=-50000;
   int tr=0;
	RtmpChunkxyz(){
		state_=State.PARSE_HEADER;
		chunk_stream_id_=-1;
		stream_id_=kDefaultStreamId;

	}

	public RtmpMessage getRtmpMessage(int chunk)
	{
		return rtmp_messages_.get(chunk);

	}


	/*Parse*/

	String Parse(InputStream in_buffer)
	{


		int ret = 0;
		if (state_ == State.PARSE_HEADER) {
			ret = ParseChunkHeader(in_buffer);
		}
		else if (state_ == State.PARSE_BODY)
		{
			ret = ParseChunkBody(in_buffer);
			if (ret > 0 && chunk_stream_id_ >= 0) {
				RtmpMessage rtmp_msg = rtmp_messages_.get(chunk_stream_id_);
				if (rtmp_msg.getIndex() == rtmp_msg.getLength()) {
					if (rtmp_msg.getTimeStamp() >= 0xffffff) {
						rtmp_msg.set_TimeStamp(rtmp_msg.get_TimeStamp()+rtmp_msg.getExtendTimeStamp());
					}
					else {
						rtmp_msg.set_TimeStamp(rtmp_msg.get_TimeStamp()+rtmp_msg.getTimeStamp());
					}
					String chunkt=Integer.toString(chunk_stream_id_);
					int suma=0;
					chunk_stream_id_ = -1;
         		bodyfull=true;
					return chunkt;
				}
			}
		}
		return Integer.toString(chunk_stream_id_);

	}

	int ParseChunkHeader(InputStream buffer)
	{
		//System.out.println("De Parse Chunk parse header");
 
		try{
         if(buffer.available()>0){
			int bytes_used = 0;
         int csid;		
			int flags;
			byte fmt;
         int header_len;
      
         if(byteb>-50000)flags=byteb;else flags =buffer.read();         
                  
 
			  byteb=flags;
           fmt = (byte)(flags >> 6); // message header type
			  if (fmt >= 4) {return -1;}
			  header_len = kChunkMessageHeaderLen[fmt];
			  bytes_used += 1;
   		  csid = (flags & 0x3f)&0xff; // chunk stream id
			  if(csid==0)tr=header_len+1;if(csid==1)tr=header_len+2;if(csid>1)tr=header_len;
     		  if(buffer.available()<tr){return -1;}
			
			
			
         
			if (csid == 0) { // csid [64, 319]
				csid += ((int)buffer.read()&0xff)+64;
				bytes_used += 1;
			}
			else if (csid == 1) { // csid [64, 65599]
				//if (buf_size < (3 + bytes_used)) {//		return 0;	//	}

				csid +=(((int)buffer.read()&0xff)+64)+(((int)buffer.read()&0xff)*256);
				bytes_used += 2;
			}

			chunk_stream_id_=csid;
			byte arrHeader[] = new byte[header_len];
			for(int i=0;i<arrHeader.length;i++)arrHeader[i]=(byte)buffer.read();

			RtmpMessage rtmpMessage=null;
			if (getRtmpMessage(chunk_stream_id_)==null){
				rtmpMessage= new RtmpMessage();
				rtmp_messages_.put(csid,rtmpMessage);
			}
			getRtmpMessage(chunk_stream_id_).setCsid((byte)csid);

			if(fmt==0||fmt==1){
				int mlength=((int)arrHeader[3]&0xff)<<16|((int)arrHeader[4]&0xff)<<8|(int)arrHeader[5]&0xff;




				if(getRtmpMessage(chunk_stream_id_).getLength()!=mlength){
					getRtmpMessage(chunk_stream_id_).setLength(mlength);


					byte[] payloady=new byte[getRtmpMessage(chunk_stream_id_).getLength()];
					getRtmpMessage(chunk_stream_id_).setPayloady(payloady);


				}
				getRtmpMessage(chunk_stream_id_).setIndex(0);
				getRtmpMessage(chunk_stream_id_).setTypeId(arrHeader[6]);



			}
			if (fmt == 0) {
				int streamId=((int)arrHeader[10]&0xff)<<24|((int)arrHeader[9]&0xff)<<16|((int)arrHeader[8]&0xff)<<8|(int)arrHeader[7]&0xff;
				getRtmpMessage(chunk_stream_id_).setStreamId(streamId);
				
			}
			int timestampx =0;
			if(arrHeader.length>=3)
				timestampx=(((int)arrHeader[0])&0xff)<<16|(((int)arrHeader[1])&0xff)<<8|((int)arrHeader[2])&0xff;


			int extend_timestamp = 0;
			if (timestampx >= 0xffffff || getRtmpMessage(chunk_stream_id_).getTimeStamp() >= 0xffffff) {
				short arrGiroiIzquierda[]={24,16,8};
				for(int i=0;i<arrGiroiIzquierda.length;i++)
					extend_timestamp |=(short)buffer.read()<<arrGiroiIzquierda[i];
				extend_timestamp |=(short)buffer.read();
			}


			if (getRtmpMessage(chunk_stream_id_).getIndex() == 0) { // first chunk
				if (fmt == 0) {
					// absolute timestamp
					getRtmpMessage(chunk_stream_id_).set_TimeStamp(0);
					getRtmpMessage(chunk_stream_id_).setTimeStamp(timestampx);
					getRtmpMessage(chunk_stream_id_).setExtendTimeStamp(extend_timestamp);
				}
				else {
					if (getRtmpMessage(chunk_stream_id_).getTimeStamp() >= 0xffffff) {
						getRtmpMessage(chunk_stream_id_).setExtendTimeStamp(getRtmpMessage(chunk_stream_id_).getExtendTimeStamp()+ extend_timestamp);
					}
					else {
						getRtmpMessage(chunk_stream_id_).setTimeStamp(getRtmpMessage(chunk_stream_id_).getTimeStamp()+ timestampx);
					}
				}
			}
		//	getRtmpMessage(chunk_stream_id_).setTimeStamp(timestampx);

			state_ = State.PARSE_BODY;
         byteb=-50000;
         tr=0;
			return bytes_used;





       }
		}catch(IOException ex){}
     
		return 0;
	}

	int ParseChunkBody(InputStream buffer)
	{
		int bytes_used = 0;
		if (chunk_stream_id_ < 0) {
			return -1;
		}

		RtmpMessage rtmp_msg = rtmp_messages_.get(chunk_stream_id_);
		int chunk_size = getRtmpMessage(chunk_stream_id_).getLength() - getRtmpMessage(chunk_stream_id_).getIndex();
		if (chunk_size > in_chunk_size_) {chunk_size = in_chunk_size_;}
		try{if (buffer.available() < chunk_size) {return 0;}}catch(IOException ex){}
		if (getRtmpMessage(chunk_stream_id_).getIndex() + chunk_size > getRtmpMessage(chunk_stream_id_).getLength()) {
			//return -1;
		}
		int pos=getRtmpMessage(chunk_stream_id_).getIndex();
		try{
			for(int x=0;x<chunk_size;x++){/*payload[x]*/getRtmpMessage(chunk_stream_id_).getPayloady()[pos++]=(byte)buffer.read();
			}
		}catch(Exception ex){}

		bytes_used += chunk_size;
		getRtmpMessage(chunk_stream_id_).setIndex(getRtmpMessage(chunk_stream_id_).getIndex()+chunk_size);

		if (getRtmpMessage(chunk_stream_id_).getIndex() >= getRtmpMessage(chunk_stream_id_).getLength() ||	getRtmpMessage(chunk_stream_id_).getIndex()%in_chunk_size_ == 0) {
			state_ = State.PARSE_HEADER;

		}
		return bytes_used;
	}

	/*Fin de parse*/

	int CreateBasicHeader(short fmt, int csid, byte[] buf,int lent)
	{
		int len = lent;
		int retorno=0;
		if (csid >= 64 + 255) {
			buf[len++] =(byte)((fmt << 6)|0x01);
			buf[len++] =(byte)((csid - 64) & 0xFF);
			buf[len++] =(byte)(((csid - 64) >> 8) & 0xFF);
			retorno=3;
		}
		else if (csid >= 64) {
			buf[len++] =(byte)((fmt << 6) | 0);
			buf[len++] =(byte)((csid - 64) & 0xFF);
			retorno=2;
		}
		else {
			buf[len++] =(byte)((fmt << 6) | csid);
			retorno=1;
		}

		return retorno;
	}


	int CreateMessageHeader(int fmt, RtmpMessage rtmp_msg, byte[] buf,int offset)
	{
		int len = 0;
		int offsetl=offset;
		if (fmt <= 2) {
			if (rtmp_msg.get_TimeStamp() < 0xffffff) {

				buf[offsetl++]=(byte)((rtmp_msg.get_TimeStamp()>>16)&0xff);
				buf[offsetl++]=(byte)((rtmp_msg.get_TimeStamp()>>8)&0xff);
				buf[offsetl++]=(byte)((rtmp_msg.get_TimeStamp()>>0)&0xff);

			}
			else {
				int val=0xffffff;
				buf[offset]=(byte)((val>>16)&0xff);
				buf[offsetl++]=(byte)((val>>8)&0xff);
				buf[offsetl++]=(byte)((val>>0)&0xff);

			}
			len += 3;
		}

		if (fmt <= 1) {
			buf[offsetl++]=(byte)((rtmp_msg.getLength()>>16)&0xff);
			buf[offsetl++]=(byte)((rtmp_msg.getLength()>>8)&0xff);
			buf[offsetl++]=(byte)((rtmp_msg.getLength()>>0)&0xff);



			len += 3;
			buf[offsetl++] = rtmp_msg.getTypeId();
			len+=1;
		}

		if (fmt == 0) {

			buf[offsetl++]=(byte)((rtmp_msg.getStreamId()>>0)&0xff);
			buf[offsetl++]=(byte)((rtmp_msg.getStreamId()>>8)&0xff);
			buf[offsetl++]=(byte)((rtmp_msg.getStreamId()>>16)&0xff);
			buf[offsetl++]=(byte)((rtmp_msg.getStreamId()>>24)&0xff);

			len += 4;
		}

		return len;
	}



	
	int CreateChunkd(int csid, RtmpMessage rtmp_msg, /*int buf_size,*/BufferedOutputStream out)
	{

      int lengths=rtmp_msg.getLength();
      int contadorBytes=0;
		try{
			int buf_offset = 0, payload_offset = 0;
			int capacity=100;
			byte[] buf=new byte[capacity];
			buf_offset += CreateBasicHeader((short)0, csid, buf,buf_offset); //first chunk
			buf_offset += CreateMessageHeader(0, rtmp_msg, buf,buf_offset);
			if (rtmp_msg.get_TimeStamp()>= 0xffffff) {
				buf[buf_offset]=(byte)((rtmp_msg.get_TimeStamp()>>24)&0xff);
				buf[buf_offset+1]=(byte)((rtmp_msg.get_TimeStamp()>>16)&0xff);
				buf[buf_offset+2]=(byte)((rtmp_msg.get_TimeStamp()>>8)&0xff);
				buf[buf_offset+3]=(byte)((rtmp_msg.get_TimeStamp()>>0)&0xff);
				buf_offset += 4;
			}

			for(int i=0;i<buf_offset;i++){out.write(buf[i]);}
			while (rtmp_msg.getLength() > 0)
			{
				if (rtmp_msg.getLength() > out_chunk_size_) {

					out.write(rtmp_msg.getPayloady(),payload_offset,out_chunk_size_);
					contadorBytes+=out_chunk_size_;
					payload_offset += out_chunk_size_;
					buf_offset += out_chunk_size_;
					rtmp_msg.setLength(rtmp_msg.getLength()-out_chunk_size_);
					out.write((byte)(((short)3 << 6) | csid));
					if (rtmp_msg.get_TimeStamp() >= 0xffffff) {
						out.write((byte)((rtmp_msg.get_TimeStamp()>>24)&0xff));
						out.write(buf[buf_offset++]=(byte)((rtmp_msg.get_TimeStamp()>>16)&0xff));
						out.write(buf[buf_offset++]=(byte)((rtmp_msg.get_TimeStamp()>>8)&0xff));
						out.write(buf[buf_offset++]=(byte)((rtmp_msg.get_TimeStamp()>>0)&0xff));
					}
				}
				else {
					out.write(rtmp_msg.getPayloady(),payload_offset,rtmp_msg.getLength());


               contadorBytes+=rtmp_msg.getLength();

					buf_offset += rtmp_msg.getLength();
					rtmp_msg.setLength(0);
					break;
				}
			}

			return buf_offset;}
		catch(IOException ex){}
		return 0;
	}













	void setInChunkSize(int in_chunk_size)
	{
		in_chunk_size_ = in_chunk_size; }

	void setOutChunkSize(int out_chunk_size)
	{ out_chunk_size_ = out_chunk_size; }

	int GetStreamId()
	{ return stream_id_; }


}
