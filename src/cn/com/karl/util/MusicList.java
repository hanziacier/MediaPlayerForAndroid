package cn.com.karl.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.util.Log;

import cn.com.karl.domain.IconifiedText;
import cn.com.karl.domain.Music;
import cn.com.karl.music.R;

public class MusicList {
	protected static Uri uri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//音乐检索路径 默认为sd卡
	protected static File uriFile ;//检索路径，在不使用（无法使用）媒体库查询时使用目录遍历的方式
	protected static List<Music> musicList;//检索出的音乐列表
	public static Uri setInternalPath(){
		if(MusicList.uri != MediaStore.Audio.Media.INTERNAL_CONTENT_URI){
			MusicList.setSearchPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
			MusicList.clearMusicList();
		}
		return MusicList.uri;		
	}
	public static Uri setExternalPath(){
		if(MusicList.uri != MediaStore.Audio.Media.EXTERNAL_CONTENT_URI){
			MusicList.setSearchPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
			MusicList.clearMusicList();
		}
		return MusicList.uri;
		
	}
	public static Uri setSearchFilePath(File currentDirectory){
		Uri currentUri = Uri.fromFile(currentDirectory);
		if(MusicList.uri != currentUri){
			MusicList.uri =currentUri;			
			MusicList.uriFile = currentDirectory;
			MusicList.clearMusicList();
		}
		return MusicList.uri;
	}
	protected static Uri setSearchPath(Uri uri){
		MusicList.uri =uri;
		MusicList.uriFile = null;
		//Log.e("MusicList.uri.toString", MusicList.uri.toString());
		return MusicList.uri;
	}
	public static void clearMusicList(){
		MusicList.musicList=null;
	}
	public static void setMusicList(List<Music> list){
		MusicList.musicList = list;
	}
	public static List<Music> getMusicData(Context context) {
		if(MusicList.musicList !=null) return MusicList.musicList;
		else{
			if(MusicList.uri.equals(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) || 
			MusicList.uri.equals(MediaStore.Audio.Media.INTERNAL_CONTENT_URI)){
				MusicList.musicList = MusicList.getMusicDataFromContentResolver(context);
			}else{
				MusicList.musicList = MusicList.getMusicDataFromDir(MusicList.uriFile);
				//MusicList.musicList = MusicList.getMusicDataFromContentResolver(context);
			}
		}

		return MusicList.musicList;

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
				return null;
			}
			if (cursor.moveToFirst()) {
				
				do {
					Music m = new Music();
					Log.e("MusicList.cursor", cursor.toString());
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
					
					Log.e("MusicList","url:"+url+" title:"+title);
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
	protected static List<Music> getMusicDataFromDir(File dir){
		List<Music> musicList = new ArrayList<Music>();
		File[] files = dir.listFiles();
		Log.e("MusicList.getMusicDataFromDirUri.dir", dir.toString());
		Log.e("MusicList.getMusicDataFromDirUri.files", files.toString());
		if(files != null){
			for (File currentFile : files)
			{
				//判断是一个文件夹还是一个文件
				if (currentFile.isDirectory())
				{
					//currentIcon = getResources().getDrawable(R.drawable.folder);
					continue;
				}
				else
				{
					//取得文件名
					String fileName = currentFile.getName();
					//根据文件名来判断文件类型，设置不同的图标
					if(fileName.substring(fileName.length() - 3).toLowerCase().equalsIgnoreCase("mp3"))
					{
						Music m = new Music();
						//确保只显示文件名、不显示路径如：/sdcard/111.txt就只是显示111.txt
						m.setTitle(fileName);
						m.setSinger("未知艺术家");
						//m.setAlbum(album);
						//m.setSize(size);
						//m.setTime(time);
						m.setUrl(currentFile.getAbsolutePath());
						//m.setName(name);
						musicList.add(m);
						Log.e("MusicList.getMusicDataFromDirUri.music",m.getUrl().toString());
					}
					
				}
				
			}
		}		
		return musicList;
	}
	public static int getIndex(File file){
		int index = -100;
		if(MusicList.musicList.isEmpty()) return index;
		int i=0;
		for(Music m:MusicList.musicList){
			if(m.getUrl().equals(file.getAbsolutePath())){
				return i;
			}
			i++;
		}
		return index;
	}
}
