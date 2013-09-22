package cn.com.karl.music;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import cn.com.karl.domain.Music;
import cn.com.karl.domain.Playbox;
import cn.com.karl.util.MusicList;

import java.util.List;

/**
 * Created by leju on 13-9-6.
 */
public class TTMdeiaPlayer extends Application {
    public List<Music> musicList=null;//当前界面下的音乐列表
    public Playbox playbox=null;
    public long tempPlayListId = -1;//临时播放列表的id
    public long favoritePlayListId = -1;//我的最爱播放列表的id
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
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.PlaylistsColumns.NAME));
                    if(MusicService.TEMP_PLAY_LIST_NAME.equals(name)) {
                        needCreateTempPlayList=false;
                        tempPlayListId = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Audio.Playlists._ID));
                    }
                    if(MusicService.FAVORITE_PLAY_LIST_NAME.equals(name)) {
                        needCreateFavoritePlayList=false;
                        favoritePlayListId = cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Audio.Playlists._ID));
                    }


                }while (cursor.moveToNext());
            }
            if(needCreateTempPlayList){//创建临时播放列表
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Audio.PlaylistsColumns.NAME,MusicService.TEMP_PLAY_LIST_NAME);
                Uri uri = cr.insert( MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,contentValues );
                tempPlayListId = ContentUris.parseId(uri);
            }
            if(needCreateFavoritePlayList){//创建最爱播放列表
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Audio.PlaylistsColumns.NAME,MusicService.FAVORITE_PLAY_LIST_NAME);
                Uri uri = cr.insert( MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,contentValues );
                favoritePlayListId = ContentUris.parseId(uri);
            }
        }
        MusicList.setExternalPath();
        musicList = MusicList.getMusicData(this);
        playbox = Playbox.getPlaybox();
        playbox.setPlayList(playbox.getAudioPlayLists(getContentResolver(),tempPlayListId));
    }

}
