//package com.video.servflv;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedOutputStream;
//
public class BufferReader{
    Vector<Byte> buffer_=new Vector<Byte>(2048);
    int reader_index_ = 0;
    int writer_index_ = 0;
    private amfh amf=null;
    int acknowledgement_size_=0;
    int tpv=0;
    int max_chunk_size_=0;
    int peer_bandwidth_=0;
    int stream_id_ = 0;
    String app_ ="";
    String stream_path_ ="";
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
    int contV=-1;
    public BufferedOutputStream outputb;
    // public List lstPayload=new ArrayList();
    public List lstPayload=new ArrayList();
    public byte typex[]=new byte[100];
    public int timestampx[]=new int[100];
    public int sizex[]=new int[100];
    int nuevov=0;
    public boolean metaDataY=false;
    public boolean avcSequenceY=false;




    //public RtmpMessage arrMV[]= new RtmpMessage[1000];
    int contA=-1;

    boolean primerMensaje=false;
    Rtmp rtmp=null;
    RtmpChunk rtmp_chunk_;
    BufferReader(ChatVideo server){
        rtmp_chunk_=new RtmpChunk();
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



    int numMensajes=0;


    int Read(InputStream sockfd,OutputStream out){
        this.output=out;
        int ret=-1;
        String chunk="-1";
        RtmpMessage rtmp_msg=null;
        this.outputb=new BufferedOutputStream(this.output);
        boolean cond=true;
        while(cond){
            //	System.out.print("de regreso");
// if(numMensajes<5000){
            try{
                if(sockfd.available()>0){
                    //  System.out.println("bytes disponibles "+sockfd.available());

                    chunk=rtmp_chunk_.Parse(sockfd);
                    rtmp_msg=rtmp_chunk_.getRtmpMessage(Integer.parseInt(chunk));
                    if(rtmp_msg.IsCompleted()){
                        numMensajes++;
                        rtmp_chunk_.getRtmpMessage(Integer.parseInt(chunk)).Clear();
                        HandleMessage(rtmp_msg,out,sockfd);
                        }
                }
            }
            catch(IOException ex){System.out.println(ex.toString());}
            //}
        }
        return 0;
    }


    void HandleMessage(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd){


//System.out.println("Llega un mensaje con un length de "+ rtmp_msg.getLength() +" y un largo de arreglo de "+rtmp_msg.getPayloady().length);



        switch((int)rtmp_msg.getTypeId())
        {
            case(20):
                // System.out.println("||||||||||||||||||||El type id del mensajes es Invoke||||||||||||||||||||");
                HandleInvoke(rtmp_msg,out,sockfd);

                break;
            case(1):
//System.out.println("||||||||||||||||||||El type id del mensajes es Chunk||||||||||||||||||||");

                int valorch=0;
  /*int v1=(((int)((byte)rtmp_msg.getPayloadx().elementAt(0)))&0xff)<<24;
  int v2=(((int)((byte)rtmp_msg.getPayloadx().elementAt(1)))&0xff)<<16;
  int v3=(((int)((byte)rtmp_msg.getPayloadx().elementAt(2)))&0xff)<<8;
  int v4=(((int)((byte)rtmp_msg.getPayloadx().elementAt(3)))&0xff);//&0xff))<<24);
*/

                int v1=(((int)((byte)rtmp_msg.getPayloady()[0]))&0xff)<<24;
                int v2=(((int)((byte)rtmp_msg.getPayloady()[1]))&0xff)<<16;
                int v3=(((int)((byte)rtmp_msg.getPayloady()[2]))&0xff)<<8;
                int v4=(((int)((byte)rtmp_msg.getPayloady()[3]))&0xff);//&0xff))<<24);



                valorch=v1|v2|v3|v4;
                rtmp_chunk_.setInChunkSize(valorch);
                break;


            case(9):
//System.out.println("VIDEOOOOOOOOOOOOOOOOOOOOOOOOOOO");
//System.out.println("Llega un video ");
                HandleVideo(rtmp_msg);
                break;
            case(8):
                //      System.out.println("AUDIOOOOOOOOOOOOOOOOO");
                //  contV++;
                //  arrMV[contV]=rtmp_msg;

             HandleAudio(rtmp_msg);
                break;
            case (18):
                //    System.out.println("NOTIFYYYYYYYYYYYYY");
                HandleNotify(rtmp_msg,out);
                break;








            default:
                break;
        }



    }


    void HandleInvoke(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd){

        byte arrTemp[]=new byte[rtmp_msg.getLength()];

        //for(int x=0;x<arrTemp.length-1;x++){arrTemp[x]=(byte)((rtmp_msg.getPayloadx()).elementAt(x));}


        for(int x=0;x<arrTemp.length-1;x++){arrTemp[x]=(byte)((rtmp_msg.getPayloady())[x]);}

        int valor= amf.amfDecoder.decode(arrTemp,arrTemp.length,1);
        String metodo=amf.amfDecoder.getString();
        // System.out.println("el metodo que llega es -"+metodo+" ----y el csid ----"+rtmp_msg.getCsid());


        if(rtmp_msg.getStreamId() == 0) {

            byte arrTemp2[]=new byte[rtmp_msg.getLength()-valor];
            //for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=(byte)((rtmp_msg.getPayloadx()).elementAt(x+valor));}
            for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=rtmp_msg.getPayloady()[x+valor];}


            valor= amf.amfDecoder.decode(arrTemp2,arrTemp2.length,-1);
            if(metodo.equals("connect"))
                HandleConnect(rtmp_msg,out,sockfd,amf.amfDecoder.getNumber());
            if(metodo.equals("createStream")){


                //	System.out.println("el metodo es create stream y el length es de ##########################"+rtmp_msg.getLength());
                //  System.out.println("y el sizel del payload es ##############"+rtmp_msg.getPayloadx().size());
                HandleCreateStream(rtmp_msg,out,sockfd,amf.amfDecoder.getNumber());}
        }
        else if(rtmp_msg.getStreamId() == stream_id_) {
//       System.out.println("El stream id ya no es cero");

            byte arrTemp2[]=new byte[rtmp_msg.getLength()-valor];
//   for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=(byte)((rtmp_msg.getPayloadx()).elementAt(x+valor));}
            for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=(byte)((rtmp_msg.getPayloady())[x+valor]);}

            valor+= amf.amfDecoder.decode(arrTemp2,arrTemp2.length,3);
            stream_name_ =amf.amfDecoder.getString();
            stream_path_ = "/" + app_ + "/" + stream_name_;
            //     System.out.println("El stream path " + stream_path_);


            if(rtmp_msg.getLength() > valor) {
                byte arrTemp3[]=new byte[rtmp_msg.getLength()-valor];
                //for(int x=0;x<arrTemp3.length-1;x++){arrTemp3[x]=(byte)((rtmp_msg.getPayloadx()).elementAt(x+valor));}

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



        }

    }



    boolean HandleNotify(RtmpMessage rtmp_msg,OutputStream out)
    {
        amf.amfDecoder.reset();

        byte arrTemp[]=new byte[rtmp_msg.getLength()];
//   for(int x=0;x<arrTemp.length-1;x++){arrTemp[x]=(byte)((rtmp_msg.getPayloadx()).elementAt(x));}

        for(int x=0;x<arrTemp.length-1;x++){arrTemp[x]=(byte)((rtmp_msg.getPayloady())[x]);}

        int valor= amf.amfDecoder.decode(arrTemp,arrTemp.length,1);
         System.out.println("en el Handle Notify");

        if(amf.amfDecoder.getString().equals("@setDataFrame"))
        {


            amf.amfDecoder.reset();

            byte arrTemp2[]=new byte[rtmp_msg.getLength()-valor];
//   for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=(byte)((rtmp_msg.getPayloadx()).elementAt(x+valor));}
            for(int x=0;x<arrTemp2.length-1;x++){arrTemp2[x]=(byte)((rtmp_msg.getPayloady())[x+valor]);}


            valor= amf.amfDecoder.decode(arrTemp2,arrTemp2.length,1);


            if(amf.amfDecoder.getString().equals("onMetaData")) {
                //System.out.println(amf.amfDecoder.getString());
                byte arrTemp3[]=new byte[rtmp_msg.getLength()-valor];
                //for(int x=0;x<arrTemp3.length-1;x++){arrTemp3[x]=(byte)((rtmp_msg.getPayloadx())[x+valor]);}
                for(int x=0;x<arrTemp3.length-1;x++){arrTemp3[x]=rtmp_msg.getPayloady()[x+valor];}


                amf.amfDecoder.limpiarObjeto=false;
                amf.amfDecoder.limpiarObjetox();
                amf.amfDecoder.imprimirEstado=true;

                for(int k=0;k<=arrTemp3.length-1;k++)
                {

//System.out.print(arrTemp3[k]+"-");

                }

                valor= amf.amfDecoder.decode(arrTemp3,arrTemp3.length,-1);
                amf.amfDecoder.limpiarObjeto=true;

                meta_data_ = amf.amfDecoder.getObjects();
                int y=0;
                //    System.out.println("el size del metadata es "+meta_data_.size()+"y el del arreglo vale "+arrTemp3.length);
                //  for(Map.Entry<String,amfh.AmfObject>entry:meta_data_.entrySet()){

//System.out.println("clave decodificada "+y+ "--"+entry.getKey());
                //     y++;

                // }



                //amf.amfEncoder.reset();
              //  server.SendMetaData(meta_data_);
                //amf.amfEncoder.encodeString("onMetaData", 7,true);
                //amf.amfEncoder.encodeECMA(meta_data_);
                //SendNotifyMessage(rtmp.RTMP_CHUNK_DATA_ID, amf.amfEncoder.data(), amf.amfEncoder.size(),out);


            }
        }

        return true;
    }




    void HandleConnect(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd,double numero)
    {
        mf="Publisher";
//	System.out.println("---------------en el handle connectateeeeeeeeeeeeeeeeeee---------y el numero es "+numero);
/*byte arrTemp2[]=new byte[rtmp_msg.getPayloadx().size()-valor];
    for(int x=0;x<arrTemp2.length-1;x++){
     arrTemp2[x]=(byte)((rtmp_msg.getPayloadx()).elementAt(x+valor));
   }


   int valor= amf.amfDecoder.decode(arrTemp2,arrTemp2.length,-1);
  */
        amfh.AmfObject amfObjeto=amf.new AmfObject();


        amfObjeto=amf.amfDecoder.getObject("app");


        app_ = amfObjeto.amf_string;
        SendAcknowledgement(out);
        SetPeerBandwidth(out);

        SetChunkSize(out);





        amf.amfEncoder.reset();

        amf.amfEncoder.encodeString("_result", 7,true);
        amf.amfEncoder.encodeNumber(amf.amfDecoder.getNumber());
        //amf.amfEncoder.encodeNumber(1.0);
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


//return true;

    }


    void HandleCreateStream(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd,double numero)
    {

//System.out.println("!!!!!!!!!!!!!!!!!!en el handle create stream!!!!!!!!!!!!!!!!!!!!!!!!!111");

        int stream_id = rtmp_chunk_.GetStreamId();
        amf.amfEncoder.reset();

        amf.amfEncoder.encodeString("_result", 7,true);
        amf.amfEncoder.encodeNumber(numero);

        //amf.amfEncoder.encodeNumber(amf.amfDecoder.getNumber());
        amf.amfEncoder.obtenMapaObjetos().clear();
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());

        //amf_encoder_.encodeObjects(objects);
        //amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());




        amf.amfEncoder.encodeNumber(stream_id);
        //  System.out.println("e l  n u m e r o "+amf.amfDecoder.getNumber()+" size "+amf.amfEncoder.obtenMapaObjetos().size());

        //SendInvokeMessage(rtmp.RTMP_CHUNK_INVOKE_ID, amf.amfEncoder.data(), amf.amfEncoder.size(),out);

        SendInvokeMessage(rtmp.RTMP_CHUNK_INVOKE_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),out);
        stream_id_ = stream_id;





    }







    void HandlePublish(RtmpMessage rtmp_msg,OutputStream out,InputStream sockfd,double numero)
    {
//	System.out.println("entrando al handle publish");
        amf.amfEncoder.reset();

        amf.amfEncoder.encodeString("onStatus", 8,true);
        amf.amfEncoder.encodeNumber(0);

        //amf.amfEncoder.encodeNumber(amf.amfDecoder.getNumber());
        amf.amfEncoder.obtenMapaObjetos().clear();
        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());

        amf.amfEncoder.obtenMapaObjetos().put("description",amf.new AmfObject("Start publising."));
        amf.amfEncoder.obtenMapaObjetos().put("code",amf.new AmfObject("NetStream.Publish.Start"));
        amf.amfEncoder.obtenMapaObjetos().put("level",amf.new AmfObject("status"));





        amf.amfEncoder.encodeObjects(amf.amfEncoder.obtenMapaObjetos());
        SendInvokeMessage(rtmp.RTMP_CHUNK_INVOKE_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),out);
        //System.out.println("se fue el publish");


    }






    void HandleVideo(RtmpMessage rtmp_msg)
    {
        byte type = (byte)rtmp.RTMP_VIDEO;
        int length = rtmp_msg.getLength();
        byte frame_type =(byte)((rtmp_msg.getPayloady()[0] >> 4) & 0x0f);
        byte codec_id =(byte) (rtmp_msg.getPayloady()[0]  & 0x0f);
        if (frame_type== 1 && codec_id == rtmp.RTMP_CODEC_ID_H264) {
            if (rtmp_msg.getPayloady()[1] == 0) {

                System.out.println("si es codec h264 frame type es 1 yel paylod data 1 es igual a 0");
                if(avc_sequence_header_.size()>0)                
                avc_sequence_header_.remove(0);
                avc_sequence_header_.add(rtmp_msg.getPayloady());
                server.SendMediaData((byte)rtmp.RTMP_AVC_SEQUENCE_HEADER, 0,rtmp_msg.getPayloady(),rtmp_msg.getPayloady().length,"avc",(byte[])avc_sequence_header_.get(0),meta_data_);
                	type = (byte)rtmp.RTMP_AVC_SEQUENCE_HEADER;		       
            }
        }
       
   //     	    byte arr[]=new byte[0];
             server.SendMediaData(type,(int) rtmp_msg.get_TimeStamp(),rtmp_msg.getPayloady(),rtmp_msg.getPayloady().length,"",(byte[])avc_sequence_header_.get(0),meta_data_);
          
    }



    void HandleAudio(RtmpMessage rtmp_msg)
    {
        byte type = (byte)rtmp.RTMP_AUDIO;
//	byte payload[] =new byte[rtmp_msg.getLength()];
//   for(int x=0;x<payload.length;x++)
//   {payload[x]=(byte)rtmp_msg.getPayloadx().elementAt(x);}
        //System.out.println("llega un audio con un length de "+rtmp_msg.length);
        int length = rtmp_msg.getLength();
         byte arr[]=new byte[0];
        if(length>0){
            byte sound_format =(byte)( (rtmp_msg.getPayloady()[0] >> 4) & 0x0f);
            //uint8_t sound_size = (payload[0] >> 1) & 0x01;
            //uint8_t sound_rate = (payload[0] >> 2) & 0x03;
            byte codec_id = (byte)(rtmp_msg.getPayloady()[0] & 0x0f);
//   System.out.println("Enviando audio data con un length de  "+payload.length);
            if (sound_format ==(byte)(rtmp.RTMP_CODEC_ID_AAC) && rtmp_msg.getPayloady()[1] == 0) {
                aac_sequence_header_size_ = length;
                //byte arr[]=new byte[length];
                //for(int i=0;i<length;i++){arr[i]=(byte)rtmp_msg.getPayloadx().elementAt(i);}
 //               server.SendMetaData(meta_data_);
                //    System.out.println("Enviando un aac con un length de "+arr.length);
                //server.SendMediaDataa((byte)rtmp.RTMP_AAC_SEQUENCE_HEADER, 0,arr,arr.length,"aac");
                server.SendMediaDataa((byte)rtmp.RTMP_AAC_SEQUENCE_HEADER, 0,rtmp_msg.getPayloady(),rtmp_msg.getPayloady().length,"aac"/*arr,meta_data_*/);

                type = (byte)rtmp.RTMP_AAC_SEQUENCE_HEADER;
            }

            server.SendMediaDataa(type,(int) rtmp_msg.get_TimeStamp(),rtmp_msg.getPayloady(),rtmp_msg.getPayloady().length,""/*arr,meta_data_*/);

        }

    }








    void SendMetaData( Map<String, amfh.AmfObject> meta_data)
    {


        int y=0;
        for(Map.Entry<String,amfh.AmfObject>entry:meta_data.entrySet()){

//System.out.println("clave decodificada ya para enviar"+y+ "--"+entry.getKey());
            y++;

        }


        amf.amfEncoder.reset();
        amf.amfEncoder.encodeString("onMetaData", 10,true);
        amf.amfEncoder.encodeECMA(meta_data);
        SendNotifyMessage(rtmp.RTMP_CHUNK_DATA_ID,amf.amfEncoder.data(), amf.amfEncoder.size(),this.output);
        boolean metaDataY=false;
        System.out.println("#######METADATA##%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%########");





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
        if (type == rtmp.RTMP_VIDEO)
            rtmp_msg.set_TimeStamp(timer);
        if (type == rtmp.RTMP_AUDIO){
            rtmp_msg.set_TimeStamp(timerAudio);
        }
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








       if (!has_key_frame_&&avc_sequence_header_size_ > 0&&
                 (type != (byte)rtmp.RTMP_AVC_SEQUENCE_HEADER)
                && (type != (byte)rtmp.RTMP_AAC_SEQUENCE_HEADER)) {
            if (IsKeyFrame(payload, payload_size)) {


                has_key_frame_ = true;
            }
            else {
           
                return ;
            }
        }




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
        nuevov=0;


        //return true;
    }


    void HandlePlay(OutputStream out)
    {

//System.out.println("##########!!!!!!!!!!!!!!!!!!!!!!!!!1En el handle play!!!!!!!!!!!!!!!!!!!!!!!!#########$$$$$$$$$$");
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


/*if(mf.equals("player")){
     while(true){
System.out.println(contV);

     	if(contV>0){

            SendMediaData(typex[contV],timestampx[contV],(byte[])lstPayload.get(contV),sizex[contV]);




   }}}


	*/

    }



    boolean SendInvokeMessage(int csid,byte[] payload,int payload_size,OutputStream out)
    {
        // if(this->IsClosed()) {
        //   return false;
        //  }

        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_INVOKE);
        //  System.out.println("el type id enviado es "+rtmp_msg.getTypeId());
        rtmp_msg.setTimeStamp(0);
        // System.out.println("######      el valor del stream id e--------##### "+stream_id_);
        rtmp_msg.setStreamId(stream_id_);//.stream_id = stream_id_;
        //rtmp_msg.setPayloadx(payload,0);
        rtmp_msg.setPayloady(payload);

        rtmp_msg.setLength(payload.length);
//System.out.println("y el length es de "+rtmp_msg.getLength());

        SendRtmpChunks(csid, rtmp_msg,out);
        return true;
    }

    boolean SendInvokeMessage2(int csid,byte[] payload,int payload_size,OutputStream out)
    {
        // if(this->IsClosed()) {
        //   return false;
        //  }

        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)20);
        //  System.out.println("el type id enviado es "+rtmp_msg.getTypeId());
        rtmp_msg.setTimeStamp(0);
        // System.out.println("######      el valor del stream id e--------##### "+stream_id_);
        rtmp_msg.setStreamId(stream_id_);//.stream_id = stream_id_;
        //rtmp_msg.setPayloadx(payload,0);
        rtmp_msg.setPayloady(payload);

        rtmp_msg.setLength(payload.length);
//System.out.println("y el length es de "+rtmp_msg.getLength());

        SendRtmpChunks(csid, rtmp_msg,out);
        return true;
    }


    boolean SendNotifyMessage(int csid,byte[] payload,int payload_size,OutputStream out)
    {
        // if(this->IsClosed()) {
        //   return false;
        //  }

        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_NOTIFY);
        //System.out.println("el type id enviado es "+rtmp_msg.getTypeId());
        rtmp_msg.setTimeStamp(0);
        //System.out.println("######      el valor del stream id e--------##### "+stream_id_);
        rtmp_msg.setStreamId(stream_id_);//.stream_id = stream_id_;
        //rtmp_msg.setPayloadx(payload,0);
        rtmp_msg.setPayloady(payload);
        rtmp_msg.setLength(payload.length);
//System.out.println("y el length es de "+rtmp_msg.getLength());

        SendRtmpChunks(csid, rtmp_msg,out);
        return true;
    }





    void SendAcknowledgement(OutputStream out)
    {
        //std::shared_ptr<char> data(new char[4], std::default_delete<char[]>());
        //WriteUint32BE(data.get(), acknowledgement_size_);
        byte[] data= new byte[4];

        short arrShift[]={24,16,8,0};
        for(int i=0;i<4;i++)data[i]= (byte)((acknowledgement_size_>>arrShift[i])&0xff);
        //System.out.println("el acknowldegment size vale "+acknowledgement_size_);


        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_ACK_SIZE);
        // System.out.println("el type id es "+rtmp_msg.getTypeId());
        // rtmp_msg.setPayloadx(data,0);
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
        //rtmp_msg.setPayloadx(data,0);
        rtmp_msg.setPayloady(data);

        rtmp_msg.setLength(5);
        SendRtmpChunks(rtmp.RTMP_CHUNK_CONTROL_ID, rtmp_msg,out);
    }

    boolean IsPlaying(){ return is_playing_; }

    void SetChunkSize(OutputStream out)
    {
        rtmp_chunk_.setOutChunkSize(max_chunk_size_);

//System.out.println("a enviar el chunk sizeeeeeeeeeeeeeeeeeeeeeeeeeeee");

    //    rtmp_chunk_.setOutChunkSize(60000);
    rtmp_chunk_.setOutChunkSize(20000);

        byte data[]=new byte[4];
        short arrShift[]={24,16,8,0};
        max_chunk_size_=60000;
        for(int i=0;i<4;i++)data[i]= (byte)((max_chunk_size_>>arrShift[i])&0xff);




        RtmpMessage rtmp_msg=new RtmpMessage();
        rtmp_msg.setTypeId((byte)rtmp.RTMP_SET_CHUNK_SIZE);
        // rtmp_msg.setPayloadx(data,0);
        rtmp_msg.setPayloady(data);

        rtmp_msg.setLength(4);
        SendRtmpChunks(rtmp.RTMP_CHUNK_CONTROL_ID, rtmp_msg,out);
        //  System.out.println("ya envie el chunk size");
    }



    int ReadAll(String data){return 0;};
    int ReadUntilCrlf(String data){return 0;};

    int Size()
    { return (int)buffer_.size();}

    private int Begin(){
        return 0;
    }



    private int beginWrite()
    { return Begin() + writer_index_; }



    void SendRtmpChunks(int csid, RtmpMessage rtmp_msg,OutputStream out)
    {
        int capacity = rtmp_msg.getLength() + rtmp_msg.getLength()/ max_chunk_size_ *5 + 1024;
        byte buffer[]=new byte[capacity];//(new char[capacity], std::default_delete<char[]>());
        if((byte)rtmp_msg.getTypeId()==(byte)rtmp.RTMP_VIDEO||(byte)rtmp_msg.getTypeId()==(byte)rtmp.RTMP_AUDIO){
            int size = rtmp_chunk_.CreateChunkd(csid, rtmp_msg,outputb);
            try{outputb.flush();}catch(IOException ex){}
            return;

        }


        int size = rtmp_chunk_.CreateChunk(csid, rtmp_msg, buffer, capacity);





        //System.out.println("el size fue de "+size);

/*	for(int i=0;i<buffer.length;i++){
      System.out.print(Integer.toString(buffer[i]&0xff)+"-");
}*/

        //System.out.println("type enviado es "+rtm)


        byte[] slicedArray = Arrays.copyOfRange(buffer,0,size);
/*	for(int i=0;i<slicedArray.length;i++){

slicedArray[i]=(byte)(slicedArray[i]&0xff);
     //   if(slicedArray[i]<0)
       // System.out.print("un numero negativo "+Integer.toString(slicedArray[i])+"-");
}
*/

        if (size > 0)  {
            try{out.write(slicedArray);out.flush();/*System.out.println("paquete enviado "+slicedArray.length);*/}catch(IOException ex){}
        }
    }





}
