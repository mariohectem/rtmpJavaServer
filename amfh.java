//package com.video.servflv;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;
public class amfh{
public AmfDecoder amfDecoder=new AmfDecoder();
public AmfEncoder amfEncoder=new AmfEncoder();
public AmfObject amfObject=new AmfObject();
public enum AMF0DataType
{ 
	AMF0_NUMBER((byte)0x00), 
	AMF0_BOOLEAN((byte)0x01), 
	AMF0_STRING((byte)0x02), 
	AMF0_OBJECT((byte)0x03),
	AMF0_MOVIECLIP((byte)0x04),		/* reserved, not used */
	AMF0_NULL((byte)0x05), 
	AMF0_UNDEFINED((byte)0x06), 
	AMF0_REFERENCE((byte)0x07), 
	AMF0_ECMA_ARRAY((byte)0x08), 
	AMF0_OBJECT_END((byte)0x09),
	AMF0_STRICT_ARRAY((byte)0x0A), 
	AMF0_DATE((byte)0x0B), 
	AMF0_LONG_STRING((byte)0x0C), 
	AMF0_UNSUPPORTED((byte)0x0D),
	AMF0_RECORDSET((byte)0x0E),		/* reserved, not used */
	AMF0_XML_DOC((byte)0x0F), 
	AMF0_TYPED_OBJECT((byte)0x10),
	AMF0_AVMPLUS((byte)0x11),		/* switch to AMF3 */
	AMF0_INVALID((byte)0x12);
       
        private byte valor;
        private static final Map<Byte,AMF0DataType>mapa=new HashMap<>();
        static{
        for(AMF0DataType elemento:AMF0DataType.values()){
           mapa.put(elemento.obtenValor(),elemento);}} 
         byte obtenValor(){return valor;}
         AMF0DataType(byte valor){this.valor=valor;} 
         public static AMF0DataType valorDe(byte valorDe){
          if(mapa.containsKey(valorDe))return mapa.get(valorDe);
          else return null;
          }          

};

public enum AMF3DataType
{ 

        AMF3_UNDEFINED((byte)0x00),
	AMF3_NULL((byte)0x01), 
	AMF3_FALSE((byte)0x02), 
	AMF3_TRUE((byte)0x03),
	AMF3_INTEGER((byte)0x04), 
	AMF3_DOUBLE((byte)0x05), 
	AMF3_STRING((byte)0x06), 
	AMF3_XML_DOC((byte)0x07), 
	AMF3_DATE((byte)0x08),
	AMF3_ARRAY((byte)0x09), 
	AMF3_OBJECT((byte)0x0A), 
	AMF3_XML((byte)0x0B), 
	AMF3_BYTE_ARRAY((byte)0x0C);

  private byte valor;
        private static final Map<Byte,AMF3DataType>mapa=new HashMap<>();
        static{
        for(AMF3DataType elemento:AMF3DataType.values()){
           mapa.put(elemento.obtenValor(),elemento);}} 
         byte obtenValor(){return valor;}
         AMF3DataType(byte valor){this.valor=valor;} 
         public static AMF3DataType valorDe(byte valorDe){
          if(mapa.containsKey(valorDe))return mapa.get(valorDe);
          else return null;
          }          

};
   
enum AmfObjectType
{
	AMF_NUMBER((byte)0x00),
	AMF_BOOLEAN((byte)0x01),
	AMF_STRING((byte)0x02);
  private byte valor;
        private static final Map<Byte,AmfObjectType>mapa=new HashMap<>();
        static{
        for(AmfObjectType elemento:AmfObjectType.values()){
           mapa.put(elemento.obtenValor(),elemento);}} 
         byte obtenValor(){return valor;}
         AmfObjectType(byte valor){this.valor=valor;} 
         public static AmfObjectType valorDe(byte valorDe){
          if(mapa.containsKey(valorDe))return mapa.get(valorDe);
          else return null;
          }          
};

class AmfObject
{  
	AmfObjectType type;

	public String amf_string;
	public double amf_number;
	public boolean amf_boolean;    

	AmfObject(){}
	AmfObject(String str)
	{
		this.type = type.valorDe((byte)0x02); 
		this.amf_string = str; 
	}

	AmfObject(double number)
	{
		
                this.type = type.valorDe((byte)0x00);  
		this.amf_number = number; 
	}
};



class AmfDecoder
{

    Map<String, AmfObject> amf_objs=new HashMap<>();
    AmfObject m_obj=new AmfObject();
    public boolean limpiarObjeto=true;
    Map<String, AmfObject> getObjects() 
    { return amf_objs; }    
    boolean imprimirEstado=false;
 //   AmfObjects m_objs;

  //  

public void limpiarObjetox(){
amf_objs.clear();

}

int decode(byte data[], int size, int n){
int bytes_used = 0;
	while (size > bytes_used)
	{
		int ret = 0;
		byte type = data[bytes_used];
		bytes_used += 1;
                if(type==AMF0DataType.AMF0_NUMBER.obtenValor())
                {
                   m_obj.type = AmfObjectType.AMF_NUMBER;
                   byte datatemp[]=new byte[size-bytes_used];
                   System.arraycopy(data,bytes_used,datatemp,0,datatemp.length);
                   ret = decodeNumber(datatemp,size-bytes_used);
	        }
               if(type==AMF0DataType.AMF0_STRING.obtenValor())
                {
                   m_obj.type = AmfObjectType.AMF_STRING;
                   byte datatemp[]=new byte[size-bytes_used];
                   System.arraycopy(data,bytes_used,datatemp,0,datatemp.length);
                   ret = decodeString(datatemp,size-bytes_used);
			
	        }
                 if(type==AMF0DataType.AMF0_BOOLEAN.obtenValor())
                 {
        		m_obj.type = AmfObjectType.AMF_BOOLEAN;
                        byte datatemp[]=new byte[size-bytes_used];
                        System.arraycopy(data,bytes_used,datatemp,0,datatemp.length);
		        ret = decodeBoolean(datatemp,size-bytes_used);	
                  }

                  if(type==AMF0DataType.AMF0_OBJECT.obtenValor())
                 {
	               byte datatemp[]=new byte[size-bytes_used];
                        System.arraycopy(data,bytes_used,datatemp,0,datatemp.length);
		        ret = decodeObject(datatemp,size-bytes_used);	
                  }
              if(type==AMF0DataType.AMF0_ECMA_ARRAY.obtenValor())
                 {
	               byte datatemp[]=new byte[size-bytes_used-4];
                        System.arraycopy(data,bytes_used+4,datatemp,0,datatemp.length);
		        ret = decodeObject(datatemp,size-bytes_used+4);	
                  }




		if (ret < 0) {
			break;
		}

		bytes_used += ret;
        	n--;
		if (n == 0) {
			break;
		}
	}

	return bytes_used;


}


int decodeNumber(byte[] data, int size)
{
	if (size < 8) {
		return 0;
	}
try{
        long bits =
                ((long) (data[0] & 0xff) << 56)
                        | ((long) (data[1] & 0xff) << 48)
                        | ((long) (data[2]
                        & 0xff) << 40)
                        | ((long) (data[3] & 0xff) << 32)
                        | ((data[4] & 0xff) << 24)
                        | ((data[5] & 0xff) << 16)
                        | ((data[6] & 0xff) << 8)
                        | (data[7] & 0xff);
        m_obj.amf_number=Double.longBitsToDouble(bits);
      	return 8;
}catch(Exception ex){System.out.println(ex.toString());}
return 8;
}



int decodeString(byte[] data, int size)
{     
	if (size < 2) {
		return 0;
	}
        int bytes_used = 0;
	int strSize =((data[0]<<8)&0xff)|((data[1])&0xff);
	bytes_used += 2;
	if (strSize > (size - bytes_used)) {
		return -1;
}
        byte arrTemp[]=new byte[strSize];
        for(int x=0;x<strSize;x++){
        arrTemp[x]=data[x+2]; 

   }

        try{ 
             m_obj.amf_string=new String(arrTemp,"UTF-8");
        }catch(IOException ex){System.out.println(ex.toString());}
        bytes_used += strSize;
	return bytes_used;
}

int  decodeBoolean(byte[] data, int size)
{
	if (size < 1) {
		return 0;
	}

	m_obj.amf_boolean = (data[0] != 0);
	return 1;
}


int decodeObject(byte[] data, int size)
{

  if(limpiarObjeto==true)
	amf_objs.clear();
	
	
	int bytes_used = 0;
	
	while (size > 0)
	{
        if(bytes_used>=data.length||bytes_used+1>=data.length)
            return bytes_used;
		 int strLen =((data[bytes_used]<<8)&0xff)|((data[bytes_used+1])&0xff); 

        	size -= 2;
      	if (size < strLen) {
			return bytes_used;
		}
                byte[] keyt=new byte[strLen];
                System.arraycopy(data,bytes_used+2,keyt,0,strLen);                

                String key=null;
                try{
                	
                	key=new String(keyt,"UTF-8");
                }catch(IOException ex){}
		size -= strLen;

		AmfDecoder dec=new AmfDecoder();
                byte[] arrParDecode=new byte[size];
                System.arraycopy(data,bytes_used + 2 + strLen,arrParDecode,0,data.length-bytes_used-2-strLen);
             
		int ret = dec.decode(arrParDecode, arrParDecode.length, 1);
		bytes_used += 2 + strLen + ret;
	if (ret <= 1) {
			break;
		}
      if(bytes_used >data.length){
         bytes_used-=ret;
         break;         
         }  
    		amf_objs.put(key, dec.getObject());
	}
	
	
  int y=0;
         for(Map.Entry<String,amfh.AmfObject>entry:amf_objs.entrySet()){
           y++;
        
     }
    	return bytes_used;
}




    void reset()
    {
        m_obj.amf_string = "";
        m_obj.amf_number = 0;
        amf_objs.clear();
    }

    String getString()
    { return m_obj.amf_string; }

    double getNumber()
    { return m_obj.amf_number; }

   boolean hasObject(String key) 
    { 
  return true;
   }

    AmfObject getObject(String key) 
    { return amf_objs.get(key); }

    AmfObject getObject() 
    { return m_obj; }

 
};

class AmfEncoder
{

	public Vector m_data=new Vector();	
	int m_size  = 0;
	int m_index = 0; 
   Map<String, AmfObject> AmfObjects=new LinkedHashMap<String, AmfObject>();
   AmfObject m_obj=new AmfObject();
   AmfEncoder(){m_data=new Vector();}

   Map<String, AmfObject> obtenMapaObjetos(){return AmfObjects;}
 

    void encodeNumber(double value){

    	m_data.insertElementAt((byte)AMF0DataType.AMF0_NUMBER.obtenValor(),m_index++);
           long l = Double.doubleToRawLongBits(value);
           short arrShift[]={56,48,40,32,24,16,8,0};
           for(int x=0;x<arrShift.length;x++){
              m_data.insertElementAt((byte)((l>>arrShift[x])&0xff),m_index++);
             }
     }



  void encodeString(String data, int len, boolean isObject){

//int val=m_index;


	if (len < 65536)
	 {if (isObject){	
        m_data.insertElementAt((byte)AMF0DataType.AMF0_STRING.obtenValor(),m_index++);}
        
     	  m_data.insertElementAt((byte)((len>>8)&0xff),m_index++);
     	  m_data.insertElementAt((byte)(len&0xff),m_index++);}
	else {
		
		if (isObject){	
        m_data.insertElementAt((byte)AMF0DataType.AMF0_LONG_STRING.obtenValor(),m_index++);}
		  m_data.insertElementAt((byte)((len>>24)&0xff),m_index++);
		  m_data.insertElementAt((byte)((len>>26)&0xff),m_index++);
		  m_data.insertElementAt((byte)((len>>8)&0xff),m_index++);
	     m_data.insertElementAt((byte)((len)&0xff),m_index++);
		}
   
    byte[] bdata=new byte[len];
    if(len>0){
    try{
    bdata=data.getBytes("ASCII");
     for(int x=0;x<bdata.length;x++){
      m_data.insertElementAt(bdata[x],m_index++);
      
    }}catch(IOException ex){System.out.println(ex.toString());}}
    








}

void encodeObjects(Map<String, AmfObject> objs)
{
	if (objs.size() == 0) {
		 m_data.insertElementAt((byte)((AMF0DataType.AMF0_NULL.obtenValor())&0xff),m_index++);
		return;
	}
	 m_data.insertElementAt((byte)((AMF0DataType.AMF0_OBJECT.obtenValor())&0xff),m_index++);
    for(Map.Entry<String,AmfObject>entry:objs.entrySet()){
           
         encodeString(entry.getKey(),entry.getKey().length(),false);
     
     
     AmfObject amfOb= (AmfObject)entry.getValue();

     if (AmfObjectType.AMF_NUMBER.obtenValor()==amfOb.type.obtenValor()){encodeNumber(amfOb.amf_number);
    }	
     if (AmfObjectType.AMF_STRING.obtenValor()==amfOb.type.obtenValor()){
     	encodeString(amfOb.amf_string,amfOb.amf_string.length(),true);}	
	  if (AmfObjectType.AMF_BOOLEAN.obtenValor()==amfOb.type.obtenValor()){
	  	int val=0;
	  	if((byte)amfOb.type.AMF_BOOLEAN.obtenValor()>0)val=1;
	  	encodeBoolean(val);
	  	
	  	}
}

    encodeString("", 0, false);

	 m_data.insertElementAt((byte)((AMF0DataType.AMF0_OBJECT_END.obtenValor())&0xff),m_index++);















}
void encodeECMA(Map<String, AmfObject> objs)
{
   byte c0[]={0x00,0x00,0x00,0x00};	
	m_data.insertElementAt((byte)((AMF0DataType.AMF0_ECMA_ARRAY.obtenValor())&0xff),m_index++);
	//m_data.put(c0);
for(int i=0;i<c0.length;i++){
   m_data.insertElementAt(c0[i],m_index+i);

}	
	
   m_index+=4;
 
    for(Map.Entry<String,AmfObject>entry:objs.entrySet()){
      
         encodeString(entry.getKey(),entry.getKey().length(),false);
     
     
     AmfObject amfOb= (AmfObject)entry.getValue();

     if (AmfObjectType.AMF_NUMBER.obtenValor()==amfOb.type.obtenValor()){encodeNumber(amfOb.amf_number);
     }	
     if (AmfObjectType.AMF_STRING.obtenValor()==amfOb.type.obtenValor()){
       	encodeString(amfOb.amf_string,amfOb.amf_string.length(),true);}	
	  if (AmfObjectType.AMF_BOOLEAN.obtenValor()==amfOb.type.obtenValor()){
	  	int val=0;
	  	if((byte)amfOb.type.AMF_BOOLEAN.obtenValor()>0)val=1;
	  	encodeBoolean(val);
	  	
	  	}
}
      encodeString("", 0, false);
	 m_data.insertElementAt((byte)((AMF0DataType.AMF0_OBJECT_END.obtenValor())&0xff),m_index++);
	
	}



void encodeBoolean(int value)
{
	
	byte res=0x00;
	if(value>0)res=(byte)0x01;
	 
	 
   m_data.insertElementAt((byte)AMF0DataType.AMF0_BOOLEAN.obtenValor(),m_index++);
   m_data.insertElementAt(res,m_index++);
	
}








void reset(){m_index = 0;}	

byte[] data(){
   byte data[]=new byte[m_index];
   for(int i=0;i<data.length;i++){
     data[i]=(byte)m_data.elementAt(i);   
   }

	return data;
	
	}
int size(){return m_index;}

}

}

