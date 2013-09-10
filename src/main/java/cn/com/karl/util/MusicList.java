package cn.com.karl.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.karl.domain.Music;
import cn.com.karl.filter.MusicFileFilter;



public class MusicList {
	protected static Uri uri;//音乐检索路径 默认为sd卡
	public static Uri setInternalPath(){
        MusicList.setSearchUri(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
		return MusicList.uri;
	}
	public static Uri setExternalPath(){
        MusicList.setSearchUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		return MusicList.uri;
	}
	protected static Uri setSearchUri(Uri uri){
		MusicList.uri =uri;
		return MusicList.uri;
	}
	public static List<Music> getMusicData(Context context) {
        return MusicList.getMusicDataFromContentResolver(context);
	}
	protected static List<Music> getMusicDataFromContentResolver(Context context){
		List<Music> musicList = new ArrayList<Music>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			Log.e("MusicList.getMusicData.uri", MusicList.uri.toString());
			// 获取所有歌曲
			//Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;//INTERNAL_CONTENT_URI,EXTERNAL_CONTENT_URI 分别表示内存和外存的uri
			Cursor cursor = cr.query(
					MusicList.uri, null, null,
					null, null);
		
			if (null == cursor) {
				return musicList;
			}
			if (cursor.moveToFirst()) {
				
				do {
					Music m = new Music();
					Log.e("MusicList.cursor", cursor.toString());
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
				} while (cursor.moveToNext());
			}
		}
        Collections.sort(musicList);
		return musicList;	
	}

}
