package cn.com.karl.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.karl.domain.Music;
import cn.com.karl.filter.MusicFileFilter;



public class MusicList {
	protected static Uri uri;//���ּ���·�� Ĭ��Ϊsd��
	protected static File uriFile ;//����·�����ڲ�ʹ�ã��޷�ʹ�ã�ý����ѯʱʹ��Ŀ¼�����ķ�ʽ
	protected static List<Music> musicList;//�������������б�
    public final static int ErrorID = -100;
    public static final String defaultSinger = "δ֪������";
	public static Uri setInternalPath(){
        MusicList.setSearchUri(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
        MusicList.clearMusicList();
		return MusicList.uri;
	}
	public static Uri setExternalPath(){
        MusicList.setSearchUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        MusicList.clearMusicList();
		return MusicList.uri;
	}
	public static Uri setSearchFilePath(File currentDirectory){
		Uri currentUri = Uri.fromFile(currentDirectory);
        MusicList.uri =currentUri;
        MusicList.uriFile = currentDirectory;
        MusicList.clearMusicList();
		return MusicList.uri;
	}
	protected static Uri setSearchUri(Uri uri){
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
        Collections.sort(MusicList.musicList);
	}
	public static List<Music> getMusicData(Context context) {
		if(MusicList.musicList != null && !MusicList.musicList.isEmpty()) return MusicList.musicList;
		else{
			if(MusicList.uri.equals(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) || 
			MusicList.uri.equals(MediaStore.Audio.Media.INTERNAL_CONTENT_URI)){
				MusicList.musicList = MusicList.getMusicDataFromContentResolver(context);
                if(!MusicList.musicList.isEmpty()) Collections.sort(MusicList.musicList);
			}else{
				MusicList.musicList = MusicList.getMusicDataFromDir(MusicList.uriFile);
                if(!MusicList.musicList.isEmpty()) Collections.sort(MusicList.musicList);
			}
		}

		return MusicList.musicList;

	}
	protected static List<Music> getMusicDataFromContentResolver(Context context){
		List<Music> musicList = new ArrayList<Music>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			Log.e("MusicList.getMusicData.uri", MusicList.uri.toString());
			// ��ȡ���и���
			//Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;//INTERNAL_CONTENT_URI,EXTERNAL_CONTENT_URI �ֱ��ʾ�ڴ������uri
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

					if ("<unknown>".equals(singer)) {
						singer = MusicList.defaultSinger;
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
					if (new MusicFileFilter().accept(name) ) {
						m.setTitle(title);
						m.setSinger(singer);
						m.setAlbum(album);
						m.setSize(size);
						m.setTime(time);
						m.setUrl(url);
						m.setName(name);
                        m.setAlbumid(cursor
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
		return musicList;	
	}
	protected static List<Music> getMusicDataFromDir(File dir){
		List<Music> musicList = new ArrayList<Music>();
		File[] files = dir.listFiles(new MusicFileFilter());
		Log.e("MusicList.getMusicDataFromDirUri.dir", dir.toString());
		Log.e("MusicList.getMusicDataFromDirUri.files", files.toString());
		if(files != null){
			for (File currentFile : files)
			{
				//�ж���һ���ļ��л���һ���ļ�
				if (currentFile.isDirectory())
				{
					//currentIcon = getResources().getDrawable(R.drawable.folder);
					continue;
				}
				else
				{
					//ȡ���ļ���
					String fileName = currentFile.getName();

                    Music m = new Music();
                    m.setTitle(fileName);
                    m.setSinger(MusicList.defaultSinger);
                    m.setDirPath(dir.toString());
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
		return musicList;
	}
	public static int getIndex(File file){

		if(MusicList.musicList.isEmpty()) return MusicList.ErrorID;
		int i=0;
		for(Music m:MusicList.musicList){
			if(m.getUrl().equals(file.getAbsolutePath())){
				return i;
			}
			i++;
		}
		return MusicList.ErrorID;
	}
    public static List<Music> getMusicList(){
        return MusicList.musicList;

    }
}
