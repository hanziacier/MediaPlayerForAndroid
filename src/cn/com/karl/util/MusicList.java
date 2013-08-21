package cn.com.karl.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
//import android.util.Log;

import cn.com.karl.domain.Music;

public class MusicList {
	public static Uri uri=MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
	public static List<Music> getMusicData(Context context) {
		List<Music> musicList = new ArrayList<Music>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			//Log.e("MusicList.getMusicData > uri", MusicList.uri.toString());
			// 获取所有歌曲
			//Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;//INTERNAL_CONTENT_URI,EXTERNAL_CONTENT_URI 分别表示内存和外存的uri
			Cursor cursor = cr.query(
					MusicList.uri, null, null,
					null, null);
		
			if (null == cursor) {
				return null;
			}
			if (cursor.moveToFirst()) {
				
				do {
					Music m = new Music();
					String title = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.TITLE));
					String singer = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ARTIST));

					if ("<unknown>".equals(singer)) {
						singer = "未知艺术家";
					}
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
					if((name == null || name.length() < 4) ) {
						if( cursor.moveToNext()) continue;
						else break;
					}
					String sbr = name.substring(name.length() - 3);
					
					//System.out.println("singer:"+singer+"album:"+album+"size:"+size+"time:"+time+"url:"+url+"name:"+name+"sbr:"+sbr);
					if (sbr.toLowerCase().equalsIgnoreCase("mp3") && time > 1000 ) {
						m.setTitle(title);
						m.setSinger(singer);
						m.setAlbum(album);
						m.setSize(size);
						m.setTime(time);
						m.setUrl(url);
						m.setName(name);
						musicList.add(m);
					}
				} while (cursor.moveToNext());
			}
		}
		return musicList;

	}
}
