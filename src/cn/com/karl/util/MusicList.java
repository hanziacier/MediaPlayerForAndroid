package cn.com.karl.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
//import android.util.Log;

import cn.com.karl.domain.Music;

public class MusicList {

	public static List<Music> getMusicData(Context context) {
		List<Music> musicList = new ArrayList<Music>();
		ContentResolver cr = context.getContentResolver();
		System.out.println("cr");
		System.out.println(cr);
		if (cr != null) {
			// 获取所有歌曲
			
			
			Uri uri = MediaStore.Audio.Media.getContentUriForPath("/data/misc/background02.mp3");
			uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//INTERNAL_CONTENT_URI,EXTERNAL_CONTENT_URI 分别表示内存和外存的uri
			Cursor cursor = cr.query(
					uri, null, null,
					null, null);
			System.out.println("Uri");
			System.out.println(uri);
		
			if (null == cursor) {
				return null;
			}
			System.out.println("cr.query");
			System.out.println(cursor);
			if (cursor.moveToFirst()) {
				
				do {
					System.out.println("cursor");
					System.out.println(cursor);
					System.out.println("MediaStore.Audio.Media.TITLE");
					System.out.println(MediaStore.Audio.Media.TITLE);
					Music m = new Music();
					String title = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.TITLE));
					String singer = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					System.out.println("title:");
					System.out.println(title);
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
					String sbr = name.substring(name.length() - 3,
							name.length());
					System.out.println("cursorToString:");
					System.out.println("singer:"+singer+"album:"+album+"size:"+size+"time:"+time+"url:"+url+"name:"+name+"sbr:"+sbr);
					if (sbr.equals("mp3") || sbr.equals("ogg") ) {
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
