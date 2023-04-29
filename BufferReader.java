//package com.video.servflv;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedOutputStream;

public class BufferReader{

   
    
    private amfh amf=null;
    int acknowledgement_size_=0;    
    int max_chunk_size_=0;
    int peer_bandwidth_=0;
    int stream_id_ = 0;
    String app_ ="";
    public String stream_path_ ="";
    String stream_name_ ="";
    public Map<String, amfh.AmfObject> meta_data_=new HashMap<>();
    boolean has_key_frame_=false;
    public boolean is_playing_=false;
    public boolean is_player_=false;
    public List avc_sequence_header_= new ArrayList();
    List aac_sequence_header_ =new ArrayList();
    int avc_sequence_header_size_ = 0;
    int aac_sequence_header_size_ = 0;
    private ChatVideo server;
    public String mf="";
    public OutputStream output;
    public BufferedOutputStream outputb; 
    public boolean brwh=false;
    private Socket socket;
    public boolean metaDataY=false;
    public boolean avcSequenceY=false;
    Rtmp rtmp=null;
    RtmpChunkxyz rtmp_chunk_;
        BufferReader(ChatVideo server){
        rtmp_chunk_=new RtmpChunkxyz();
        amf=new amfh();
        Rtmp rtmp=new Rtmp();
        acknowledgement_size_ = rtmp.getAcknowledgementSize();
        max_chunk_size_ = rtmp.getChunkSize();
        max_chunk_size_=60000;
        peer_bandwidth_ = rtmp.getPeerBandWidth();
        max_chunk_size_ = rtmp.getChunkSize();
        app_= rtmp.getApp();
        stream_path_ = rtmp.getStreamPath();
        stream_name_ = rtmp.getStreamName();
        this.server = server;
    }

        int Read(InputStream sockfd,OutputStream out,Socket socket){
        this.output=out;
        this.socket=socket;
        String chunk="-1";
        RtmpMessage rtmp_msg=null;
        this.outputb=new BufferedOutputStream(this.output);
        while(!brwh){
     
            try{
                if(sockfd.available()>0){
                    chunk=rtmp_chunk_.Parse(sockfd);
                    rtmp_msg=rtmp_chunk_.getRtmpMessage(Integer.parseInt(chunk));
                    if(rtmp_msg!=null){
                    if(rtmp_msg.IsCompleted()){                      
                        rtmp_chunk_.getRtmpMessage(Integer.parseInt(chunk)).Clear();
                        HandleMessage(rtmp_msg,out,sockfd);
                        }
                }}
            }
            catch(IOException ex){System.out.println(ex.toString());}
        
        }
        return 0;
    }


    void HandleMessage(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd){
        switch((int)rtmp_msg.getTypeId())
        {
            case(20):
                HandleInvoke(rtmp_msg,out,sockfd);
                break;
            case(1):
                int valorch=0;
                int v1=(((int)((byte)rtmp_msg.getPayloady()[0]))&0xff)<<24;
                int v2=(((int)((byte)rtmp_msg.getPayloady()[1]))&0xff)<<16;
                int v3=(((int)((byte)rtmp_msg.getPayloady()[2]))&0xff)<<8;
                int v4=(((int)((byte)rtmp_msg.getPayloady()[3]))&0xff);
                valorch=v1|v2|v3|v4;
                rtmp_chunk_.setInChunkSize(valorch);
                break;
            case(9):
                HandleVideo(rtmp_msg);
                break;
            case(8):
         
             HandleAudio(rtmp_msg);
                break;
            case (18):
                HandleNotify(rtmp_msg,out);
                break;
            default:
                break;
        }
    }


    void HandleInvoke(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd){
        byte arrTemp[]=new byte[rtmp_msg.getLength()];
        for(int x=0;x<arrTemp.length-1;x++){arrTemp[x]=(byte)((rtmp_msg.getPayloady())[x]);}
        int valor= amf.amfDecoder.decode(arrTemp,arrTemp.length,1);
        String metodo=amf.amfDecoder.getString();
        if(rtmp_msg.getStreamId() == 0) {
            byte arrTemp2[]=new byte[rtmp_msg.getLength()-valor];
            for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=rtmp_msg.getPayloady()[x+valor];}
            valor= amf.amfDecoder.decode(arrTemp2,arrTemp2.length,-1);
            if(metodo.equals("connect"))
                HandleConnect(rtmp_msg,out,sockfd,amf.amfDecoder.getNumber());
            if(metodo.equals("createStream")){
                HandleCreateStream(rtmp_msg,out,sockfd,amf.amfDecoder.getNumber());}
        }
        else if(rtmp_msg.getStreamId() == stream_id_) {
            byte arrTemp2[]=new byte[rtmp_msg.getLength()-valor];
            for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=(byte)((rtmp_msg.getPayloady())[x+valor]);}
            valor+= amf.amfDecoder.decode(arrTemp2,arrTemp2.length,3);
            stream_name_ =amf.amfDecoder.getString();
            stream_path_ = "/" + app_ + "/" + stream_name_;
              System.out.println("El stream path " + stream_path_);
            if(rtmp_msg.getLength() > valor) {
                byte arrTemp3[]=new byte[rtmp_msg.getLength()-valor];
                for(int x=0;x<arrTemp3.length-1;x++){arrTemp3[x]=(byte)((rtmp_msg.getPayloady())[x+valor]);}
                valor+= amf.amfDecoder.decode(arrTemp3,arrTemp3.length,-1);}
            if(metodo.equals("publish")) {
                HandlePublish(rtmp_msg,out,sockfd,amf.amfDecoder.getNumber());
            }

            if(metodo.equals("play")) {
                is_player_=true;
                HandlePlay(out);
            }
            if(metodo.equals("play2")) {
                is_player_=true;
                HandlePlay(out);
            }

 if(metodo.equals("DeleteStream")) {
                HandleDeleteStream();
            }


        }

    }

void HandleDeleteStream()
{
  brwh=true;
  try{
  socket.close();}
   catch(IOException ex){System.out.println(ex.toString());}
  server.cerrarSocket(stream_path_);
}


    boolean HandleNotify(RtmpMessage rtmp_msg,OutputStream out)
    {
        amf.amfDecoder.reset();

        byte arrTemp[]=new byte[rtmp_msg.getLength()];
        for(int x=0;x<arrTemp.length-1;x++){arrTemp[x]=(byte)((rtmp_msg.getPayloady())[x]);}
        int valor= amf.amfDecoder.decode(arrTemp,arrTemp.length,1);
        if(amf.amfDecoder.getString().equals("@setDataFrame"))
        {
            amf.amfDecoder.reset();
            byte arrTemp2[]=new byte[rtmp_msg.getLength()-valor];
            for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=(byte)((rtmp_msg.getPayloady())[x+valor]);}
            valor= amf.amfDecoder.decode(arrTemp2,arrTemp2.length,1);
            if(amf.amfDecoder.getString().equals("onMetaData")) {
                byte arrTemp3[]=new byte[rtmp_msg.getLength()-valor];
                for(int x=0;x<arrTemp3.length-1;x++){arrTemp3[x]=rtmp_msg.getPayloady()[x+valor];}
                amf.amfDecoder.limpiarObjeto=false;
                amf.amfDecoder.limpiarObjetox();
                amf.amfDecoder.imprimirEstado=true;

                valor= amf.amfDecoder.decode(arrTemp3,arrTemp3.length,-1);
                amf.amfDecoder.limpiarObjeto=true;
                meta_data_ = amf.amfDecoder.getObjects();
            
            }
        }

        return true;
    }




    void HandleConnect(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd,double numero)
    {
        mf="Publisher";
        amfh.AmfObject amfObjeto=amf.new AmfObject();
        amfObjeto=amf.amfDecoder.getObject("app");
        app_ = amfObjeto.amf_string;
        SendAcknowledgement(out);
        SetPeerBandwidth(out);
        SetChunkSize(out);
        amf.amfEncoder.reset();
        amf.amfEncoder.encodeString("_result", 7,true);
        amf.amfEncoder.encodeNumber(amf.amfDecoder.getNumber());
           amf.amfEncoder.obtenMapaObjetos().put("mode",amf.new AmfObject(1.0));
        amf.amfEncoder.obtenMapaObjetos().put("capabilities",amf.new AmfObject(255.0));
        amf.amfEncoder.obtenMapaObjetos().put("fmsVer",amf.new AmfObject("FMS/4,5,0,297"));
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        amf.amfEncoder.obtenMapaObjetos().clear();
        amf.amfEncoder.obtenMapaObjetos().put("objectEncoding",amf.new AmfObject(0.0));
        amf.amfEncoder.obtenMapaObjetos().put("description",amf.new AmfObject("Connection succeeded."));
        amf.amfEncoder.obtenMapaObjetos().put("code",amf.new AmfObject("NetConnection.Connect.Success"));
        amf.amfEncoder.obtenMapaObjetos().put("level",amf.new AmfObject("status"));
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        SendInvokeMessage(rtmp.RTMP_CHUNK_INVOKE_ID, amf.amfEncoder.data(), amf.amfEncoder.size(),out);
    }


    void HandleCreateStream(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd,double numero)
    {

       int stream_id = rtmp_chunk_.GetStreamId();
        amf.amfEncoder.reset();
        amf.amfEncoder.encodeString("_result", 7,true);
        amf.amfEncoder.encodeNumber(numero);
        amf.amfEncoder.obtenMapaObjetos().clear();
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        amf.amfEncoder.encodeNumber(stream_id);
        SendInvokeMessage(rtmp.RTMP_CHUNK_INVOKE_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),out);
        stream_id_ = stream_id;

    }







    void HandlePublish(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd,double numero)
    {
        amf.amfEncoder.reset();
        amf.amfEncoder.encodeString("onStatus", 8,true);
        amf.amfEncoder.encodeNumber(0);
        amf.amfEncoder.obtenMapaObjetos().clear();
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        amf.amfEncoder.obtenMapaObjetos().put("description",amf.new AmfObject("Start publising."));
        amf.amfEncoder.obtenMapaObjetos().put("code",amf.new AmfObject("NetStream.Publish.Start"));
        amf.amfEncoder.obtenMapaObjetos().put("level",amf.new AmfObject("status"));
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        SendInvokeMessage(rtmp.RTMP_CHUNK_INVOKE_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),out);
    }






    void HandleVideo(RtmpMessage rtmp_msg)
    {
        byte type = (byte)rtmp.RTMP_VIDEO;
        int length = rtmp_msg.getLength();
        byte frame_type =(byte)((rtmp_msg.getPayloady()[0] >> 4) & 0x0f);
        byte codec_id =(byte) (rtmp_msg.getPayloady()[0]  & 0x0f);
        if (frame_type== 1 && codec_id == rtmp.RTMP_CODEC_ID_H264) {
            if (rtmp_msg.getPayloady()[1] == 0) {
                if(avc_sequence_header_.size()>0)                
                avc_sequence_header_.remove(0);
                avc_sequence_header_.add(rtmp_msg.getPayloady());
                server.SendMediaData((byte)rtmp.RTMP_AVC_SEQUENCE_HEADER, 0,rtmp_msg.getPayloady(),rtmp_msg.getPayloady().length,"avc",(byte[])avc_sequence_header_.get(0),meta_data_,stream_path_);
                	type = (byte)rtmp.RTMP_AVC_SEQUENCE_HEADER;		       
            }
        }
             server.SendMediaData(type,(int) rtmp_msg.get_TimeStamp(),rtmp_msg.getPayloady(),rtmp_msg.getPayloady().length,"",(byte[])avc_sequence_header_.get(0),meta_data_,stream_path_);
          
    }



    void HandleAudio(RtmpMessage rtmp_msg)
    {
        byte type = (byte)rtmp.RTMP_AUDIO;
        int length = rtmp_msg.getLength();
         byte arr[]=new byte[0];
        if(length>0){
            byte sound_format =(byte)( (rtmp_msg.getPayloady()[0] >> 4) & 0x0f);
            byte codec_id = (byte)(rtmp_msg.getPayloady()[0] & 0x0f);
            if (sound_format ==(byte)(rtmp.RTMP_CODEC_ID_AAC) && rtmp_msg.getPayloady()[1] == 0) {
                aac_sequence_header_size_ = length;
                server.SendMediaDataa((byte)rtmp.RTMP_AAC_SEQUENCE_HEADER, 0,rtmp_msg.getPayloady(),rtmp_msg.getPayloady().length,"aac",stream_path_);

                type = (byte)rtmp.RTMP_AAC_SEQUENCE_HEADER;
            }

            server.SendMediaDataa(type,(int) rtmp_msg.get_TimeStamp(),rtmp_msg.getPayloady(),rtmp_msg.getPayloady().length,"",stream_path_);

        }

    }








    void SendMetaData( Map<String, amfh.AmfObject> meta_data)
    {
        amf.amfEncoder.reset();
        amf.amfEncoder.encodeString("onMetaData", 10,true);
        amf.amfEncoder.encodeECMA(meta_data);
        SendNotifyMessage(rtmp.RTMP_CHUNK_DATA_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),this.output);
    }


    boolean IsKeyFrame(byte[] payload, int payload_size)
    {
        byte frame_type =(byte) ((payload[0] >> 4) & 0x0f);
        byte codec_id = (byte)(payload[0] & 0x0f);
        return (frame_type == 1 && codec_id == rtmp.RTMP_CODEC_ID_H264);
    }

    int timer=3000;
    int timerAudio=3000;
    int tiempoDelta=0;
    void SendMediaData(byte type, int timestamp, byte[]payload,int payload_size)
    {
    	
    	  RtmpMessage rtmp_msg=new RtmpMessage();
    	  rtmp_msg.set_TimeStamp(timestamp);
        timer+=10;
        timerAudio+=10;
        is_playing_ = true;
        if (type == rtmp.RTMP_AVC_SEQUENCE_HEADER) {
            if(avc_sequence_header_.size()==0)
                avc_sequence_header_size_ = payload_size;
                rtmp_msg.set_TimeStamp(0);

        }
        else if (type == rtmp.RTMP_AAC_SEQUENCE_HEADER) {
            return;
   	}
       if (!has_key_frame_&&avc_sequence_header_size_ > 0&&(type != (byte)rtmp.RTMP_AVC_SEQUENCE_HEADER)&& (type != (byte)rtmp.RTMP_AAC_SEQUENCE_HEADER)) {
            if (IsKeyFrame(payload, payload_size)) {has_key_frame_ = true;}else {return;}}

        rtmp_msg.setStreamId(stream_id_);
        rtmp_msg.setPayloady(payload);
        rtmp_msg.setLength(payload.length);

        if (type == rtmp.RTMP_VIDEO || type == rtmp.RTMP_AVC_SEQUENCE_HEADER) {
            rtmp_msg.setTypeId((byte)rtmp.RTMP_VIDEO);
            SendRtmpChunks(rtmp.RTMP_CHUNK_VIDEO_ID, rtmp_msg,this.output);
        }
        else if (type == rtmp.RTMP_AUDIO || type == rtmp.RTMP_AAC_SEQUENCE_HEADER) {
            rtmp_msg.setTypeId((byte)rtmp.RTMP_AUDIO);
            SendRtmpChunks(rtmp.RTMP_CHUNK_AUDIO_ID, rtmp_msg,output);
        }
       


    }


    void HandlePlay(OutputStream out)
    {

        mf="player";
        amf.amfEncoder.reset();
        amf.amfEncoder.encodeString("onStatus", 8,true);
        amf.amfEncoder.encodeNumber(0);
        amf.amfEncoder.obtenMapaObjetos().clear();
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        amf.amfEncoder.obtenMapaObjetos().put("description",amf.new AmfObject("Resetting and playing stream."));
        amf.amfEncoder.obtenMapaObjetos().put("code",amf.new AmfObject("NetStream.Play.Reset"));
        amf.amfEncoder.obtenMapaObjetos().put("level",amf.new AmfObject("status"));
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        SendInvokeMessage(rtmp.RTMP_CHUNK_INVOKE_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),out);


        amf.amfEncoder.reset();
        amf.amfEncoder.encodeString("onStatus", 8,true);
        amf.amfEncoder.encodeNumber(0);
        amf.amfEncoder.obtenMapaObjetos().clear();
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        amf.amfEncoder.obtenMapaObjetos().put("description",amf.new AmfObject("Started playing."));
        amf.amfEncoder.obtenMapaObjetos().put("code",amf.new AmfObject("NetStream.Play.Start"));
        amf.amfEncoder.obtenMapaObjetos().put("level",amf.new AmfObject("status"));
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        SendInvokeMessage(rtmp.RTMP_CHUNK_INVOKE_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),out);


        amf.amfEncoder.reset();
        amf.amfEncoder.encodeString("|RtmpSampleAccess", 17,true);
        amf.amfEncoder.encodeBoolean(1);
        amf.amfEncoder.encodeBoolean(1);
        SendInvokeMessage2(rtmp.RTMP_CHUNK_DATA_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),out);




    }



    boolean SendInvokeMessage(int csid,byte[] payload,int payload_size,OutputStream out)
    {
        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_INVOKE);
        rtmp_msg.setTimeStamp(0);
        rtmp_msg.setStreamId(stream_id_);//.stream_id = stream_id_;
        rtmp_msg.setPayloady(payload);
        rtmp_msg.setLength(payload.length);
        SendRtmpChunks(csid, rtmp_msg,out);
        return true;
    }

    boolean SendInvokeMessage2(int csid,byte[] payload,int payload_size,OutputStream out)
    {
        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)20);
        rtmp_msg.setTimeStamp(0);
        rtmp_msg.setStreamId(stream_id_);//.stream_id = stream_id_;
        rtmp_msg.setPayloady(payload);
        rtmp_msg.setLength(payload.length);
        SendRtmpChunks(csid, rtmp_msg,out);
        return true;
    }


    boolean SendNotifyMessage(int csid,byte[] payload,int payload_size,OutputStream out)
    {
        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_NOTIFY);
        rtmp_msg.setTimeStamp(0);
        rtmp_msg.setStreamId(stream_id_);//.stream_id = stream_id_;
        rtmp_msg.setPayloady(payload);
        rtmp_msg.setLength(payload.length);
        SendRtmpChunks(csid, rtmp_msg,out);
        return true;
    }





    void SendAcknowledgement(OutputStream out)
    {
        byte[] data= new byte[4];
        short arrShift[]={24,16,8,0};
        for(int i=0;i<4;i++)data[i]= (byte)((acknowledgement_size_>>arrShift[i])&0xff);
        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_ACK_SIZE);
        rtmp_msg.setPayloady(data);
        rtmp_msg.setLength(4);
        SendRtmpChunks(rtmp.RTMP_CHUNK_CONTROL_ID, rtmp_msg,out);
    }


    void SetPeerBandwidth(OutputStream out)
    {
        byte data[]=new byte[5];
        short arrShift[]={24,16,8,0};
        for(int i=0;i<4;i++)data[i]= (byte)((peer_bandwidth_>>arrShift[i])&0xff);
        data[4] = (byte)2;
        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_BANDWIDTH_SIZE);
        rtmp_msg.setPayloady(data);
        rtmp_msg.setLength(5);
        SendRtmpChunks(rtmp.RTMP_CHUNK_CONTROL_ID, rtmp_msg,out);
    }

    boolean IsPlaying(){ return is_playing_; }

    void SetChunkSize(OutputStream out)
    {
        rtmp_chunk_.setOutChunkSize(max_chunk_size_);
        rtmp_chunk_.setOutChunkSize(60000);
        byte data[]=new byte[4];
        short arrShift[]={24,16,8,0};
        max_chunk_size_=60000;
        for(int i=0;i<4;i++)data[i]= (byte)((max_chunk_size_>>arrShift[i])&0xff);
        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_SET_CHUNK_SIZE);
        rtmp_msg.setPayloady(data);
        rtmp_msg.setLength(4);
        SendRtmpChunks(rtmp.RTMP_CHUNK_CONTROL_ID, rtmp_msg,out);
    }
    void SendRtmpChunks(int csid, RtmpMessage rtmp_msg,OutputStream out)
    {
            int size = rtmp_chunk_.CreateChunkd(csid, rtmp_msg,outputb);
            try{outputb.flush();}catch(IOException ex){}
    }





}
