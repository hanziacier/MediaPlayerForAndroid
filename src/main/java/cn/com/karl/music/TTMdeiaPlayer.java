package cn.com.karl.music;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.MediaStore;
import cn.com.karl.domain.Music;
import cn.com.karl.domain.Playbox;
import cn.com.karl.util.MusicList;

import java.util.List;

/**
 * Created by leju on 13-9-6.
 */
public class TTMdeiaPlayer extends Application {
    public List<Music> musicList;
    public Playbox playbox;
    private static TTMdeiaPlayer instance;

    public static TTMdeiaPlayer getInstance() {
        return instance;
    }
    public void onCreate() {
        super.onCreate();
        instance = this;
        ContentResolver cr = getContentResolver();
        boolean needCreateTempPlayList=true;
        boolean needCreateFavoritePlayList=true;
        if (cr != null) {
            Cursor cursor = cr.query(
                    MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.PlaylistsColumns.NAME));
                    if(MusicService.TEMP_PLAY_LIST_NAME.equals(name)) needCreateTempPlayList=false;
                    if(MusicService.FAVORITE_PLAY_LIST_NAME.equals(name)) needCreateTempPlayList=false;


                }while (cursor.moveToNext());
            }
            if(needCreateTempPlayList){//创建临时播放列表
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Audio.PlaylistsColumns.NAME,MusicService.TEMP_PLAY_LIST_NAME);
                cr.insert( MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI,contentValues );
            }
            if(needCreateFavoritePlayList){//创建最爱播放列表
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Audio.PlaylistsColumns.NAME,MusicService.FAVORITE_PLAY_LIST_NAME);
                cr.insert( MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI,contentValues );
            }
        }
        MusicList.setExternalPath();
        musicList = MusicList.getMusicData(this);
        playbox = Playbox.getPlaybox();
        playbox.setPlayList(musicList);//设置播放盒子的播放列表
    }

}
