package cn.com.karl.domain;

import java.util.List;

/**
 * Created by leju on 13-9-10.
 */
public class Playbox {
    public static Playbox playbox=null;//单例模式的对象

    protected List<Music> playList=null;//当前播放列表
    protected int playListCount=0;
    protected Music currentMusic=null;//当前播放的音乐
    protected int currentPlayListId =-1;//当前播放音乐的id号,LIST中的序号
    protected boolean isPlaying = false;

    /**
     * 用private屏蔽构造方法，同意使用getPlaybox获得单例模式的Playbox
     * */
    private  Playbox(){  }
    public Music getMusic(int id){
        return playList.get(id);
    }
    public Music getCurrentMusic(){//获得当前播放的音乐
        return this.currentMusic;
    }
    public int getCurrentPlayListId(){//获得当前播放的音乐的list序号
        return this.currentPlayListId;
    }
    /**
     * 获得下一播放音乐
     * */
    public Music getNextMusic(){
        if(this.playListCount<= 0 ) return null;
        if(this.currentPlayListId < 0) {
           return this.getMusic(0);
        }
        if(this.currentPlayListId +1 > this.playListCount-1){//超出播放范围
            return this.getMusic(this.playListCount-1);
        }
        return this.getMusic(this.currentPlayListId +1);
    }

    public void setPlayingMusic(int currentPlayListId){//设置当前播放的音乐
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
