package cn.com.karl.domain;

import java.util.List;

/**
 * Created by leju on 13-9-10.
 */
public class Playbox {
    public static Playbox playbox=null;//����ģʽ�Ķ���

    protected List<Music> playList=null;//��ǰ�����б�
    protected int playListCount=0;
    protected Music currentMusic=null;//��ǰ���ŵ�����
    protected int currentPlayListId =-1;//��ǰ�������ֵ�id��,LIST�е����
    protected boolean isPlaying = false;

    /**
     * ��private���ι��췽����ͬ��ʹ��getPlaybox��õ���ģʽ��Playbox
     * */
    private  Playbox(){  }
    public Music getMusic(int id){
        return playList.get(id);
    }
    public Music getCurrentMusic(){//��õ�ǰ���ŵ�����
        return this.currentMusic;
    }
    public int getCurrentPlayListId(){//��õ�ǰ���ŵ����ֵ�list���
        return this.currentPlayListId;
    }
    /**
     * �����һ��������
     * */
    public Music getNextMusic(){
        if(this.playListCount<= 0 ) return null;
        if(this.currentPlayListId < 0) {
           return this.getMusic(0);
        }
        if(this.currentPlayListId +1 > this.playListCount-1){//�������ŷ�Χ
            return this.getMusic(this.playListCount-1);
        }
        return this.getMusic(this.currentPlayListId +1);
    }

    public void setPlayingMusic(int currentPlayListId){//���õ�ǰ���ŵ�����
        this.currentPlayListId = currentPlayListId;
        this.currentMusic = this.playList.get(this.currentPlayListId);
    }
    public int setPlayList(List<Music> playList){
        this.playList = playList;
        this.playListCount = this.playList.size();
        return this.playListCount;
    }
    public List<Music> getPlayList(){
        return this.playList;
    }
    public void setPlaying(boolean isPlaying){
        this.isPlaying = (boolean) isPlaying;
    }
    public  boolean isPlaying(){
        return this.isPlaying;
    }
    public static Playbox getPlaybox(){
        if(playbox==null){
            playbox = new Playbox();
        }
        return  playbox;
    }
}
