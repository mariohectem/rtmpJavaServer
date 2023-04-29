//package com.video.micamarita.faucamp;
//package com.video.servflv;
public class Rtmp{
static final int RTMP_VERSION =0x03;
static final int RTMP_SET_CHUNK_SIZE =0x01;
static final int RTMP_AOBRT_MESSAGE =0x02;
static final int RTMP_ACK =0x03;
static final int RTMP_USER_EVENT =0x04;
static final int RTMP_ACK_SIZE =0x05;
static final int RTMP_BANDWIDTH_SIZE =0x06;
static final int RTMP_AUDIO =0x08;
static final int RTMP_VIDEO =0x09;
static final int RTMP_FLEX_MESSAGE =0x11;
static final int RTMP_NOTIFY =0x12;
static final int RTMP_INVOKE =0x14;
static final int RTMP_FLASH_VIDEO =0x16;

static final int RTMP_CHUNK_TYPE_0=0;
static final int RTMP_CHUNK_TYPE_1=1;
static final int RTMP_CHUNK_TYPE_2=2;
static final int RTMP_CHUNK_TYPE_3=3;

static final int RTMP_CHUNK_CONTROL_ID=2;
static final int RTMP_CHUNK_INVOKE_ID=3;
static final int RTMP_CHUNK_AUDIO_ID=4;
static final int RTMP_CHUNK_VIDEO_ID=5;
static final int RTMP_CHUNK_DATA_ID=6;

static final int RTMP_CODEC_ID_H264=7;
static final int RTMP_CODEC_ID_AAC=10;
static final int RTMP_CODEC_ID_G711A=7;
static final int RTMP_CODEC_IDG711U=8;

static final int RTMP_AVC_SEQUENCE_HEADER=0x18;
static final int RTMP_AAC_SEQUENCE_HEADER=0x19;


public int max_gop_cache_len_=5000;
public int max_chunk_size_=128;
public int peer_band_width_=5000000;
public int acknowledgementSize_=5000000;

public int port_=1935;
public String url_=null;
public String tc_url_=null,swf_url_=null;
public String ip_=null;
public String app_=null;
public String stream_name_=null;
public String stream_path_=null;


void setChunkSize(int size){ if(size > 0 && size <=60000 ) max_chunk_size_=size;}
void setGopCache(int len){max_gop_cache_len_=len;}
void setPeerBandWidth(int size){peer_band_width_=size;}

int getChunkSize(){return max_chunk_size_;}
int getGopCacheLen(){return max_gop_cache_len_;}
int getPeerBandWidth(){return peer_band_width_;}
int getAcknowledgementSize(){return acknowledgementSize_;}


String getUrl(){return url_;}
String getStreamPath(){return stream_path_;}
String getApp(){return app_;}
String getStreamName(){return stream_name_;}
String getSwfurl(){return swf_url_;}
String getTcurl(){return tc_url_;}
}

