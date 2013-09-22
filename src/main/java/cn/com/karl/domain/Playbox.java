package cn.com.karl.domain;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import cn.com.karl.filter.MusicFileFilter;
import cn.com.karl.music.MusicService;
import cn.com.karl.music.TTMdeiaPlayer;
import cn.com.karl.util.MusicUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leju on 13-9-10.
 */
public class Playbox {
    public static Playbox playbox=null;//单例模式的播放盒子引用

    protected List<Music> playList=null;//当前播放列表
    protected int playListCount=0;
    protected Music currentMusic=null;//当前播放的音乐
    protected int currentPlayListId =-1;//当前播放音乐的id号,LIST中的序号
    protected boolean isPlaying = false;//是否正在播放
    protected boolean isLoop=false;//是否为单曲循环
    public Bitmap currentMusicBitmap;//当前播放音乐的封面图
    public AudioManager audioManager;
    /**
     * 用private屏蔽构造方法，同意使用getPlaybox获得单例模式的Playbox
     * */
    private  Playbox(){  }
    public static Playbox getPlaybox(){
        if(playbox==null){
            playbox = new Playbox();
            playbox.audioManager = (AudioManager) TTMdeiaPlayer.getInstance().getSystemService(Context.AUDIO_SERVICE);

        }
        return  playbox;
    }
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

    public void setPlayingMusic(int currentPlayListId){//设置当前播放的音乐 设置bitmap
        this.currentPlayListId = currentPlayListId;
        this.currentMusic = this.playList.get(this.currentPlayListId);
        this.currentMusicBitmap = MusicUtil.getArtwork(TTMdeiaPlayer.getInstance(),currentMusic.getId(), currentMusic.getAlbumId(), false);
    }
    /*
    * 设置前端的播放列表
    * */
    public int setPlayList(List<Music> playList){
        this.playList = playList;
        this.playListCount = this.playList.size();
        return this.playListCount;
    }
    public int rsyncPlayList(List<Music> playList){
        setPlayList(playList);
        resetAudioPlayLists(this.playList);
        return this.playListCount;
    }
    public void resetAudioPlayLists(List<Music> musicList){
        ContentResolver cr = TTMdeiaPlayer.getInstance().getContentResolver();
        cleanAudioPlayLists(cr, TTMdeiaPlayer.getInstance().tempPlayListId);
        pushAudioPlayLists(cr,musicList,TTMdeiaPlayer.getInstance().tempPlayListId);
    }
    private int pushAudioPlayLists(ContentResolver resolver, List<Music> musicList, long playListId) {
        int count = musicList.size();
        int insertResult=0;
        if(count > 0){
            ContentValues[]  contentValuesList = new ContentValues[count];
            int i=0;
            for(Music music : musicList){
                contentValuesList[i] = new ContentValues();
                contentValuesList[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i);
                contentValuesList[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, music.getId());
                i++;
            }
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListId);
            insertResult = resolver.bulkInsert( uri,contentValuesList );
        }
        return insertResult;
    }
    private int cleanAudioPlayLists(ContentResolver resolver,long playListId){
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListId);
        return resolver.delete(uri,null,null);

    }
    /*
    * 获得指定播放列表下的音乐列表
    * */
    public List<Music> getAudioPlayLists(ContentResolver resolver,long playListId){
        List<Music> musicList = new ArrayList<Music>();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListId);
        Cursor cursor = resolver.query(uri,null,null,null, null);
        if (null == cursor) {
            return musicList;
        }
        if (cursor.moveToFirst()) {
            do {
                Music m = new Music();
                String title = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE));
                String singer = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long size = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media.SIZE));
                long time = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA));
                String name = cursor
                        .getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                Log.e("Playbox.getAudioPlayLists",title+"--"+singer);
                if (new MusicFileFilter().accept(name) ) {
                    m.setTitle(title);
                    m.setSinger(singer);
                    m.setAlbum(album);
                    m.setSize(size);
                    m.setTime(time);
                    m.setUrl(url);
                    m.setName(name);
                    m.setAlbumId(cursor
                            .getLong(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    m.setId(cursor
                            .getLong(cursor
                                    .getColumnIndex(MediaStore.Audio.Media._ID)));
                    musicList.add(m);
                }
            }while (cursor.moveToNext());
        }
        return  musicList;
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

    public  void  setLoop(boolean isLoop){
        this.isLoop=isLoop;
    }
    public boolean isLoop(){
        return this.isLoop;
    }

}
