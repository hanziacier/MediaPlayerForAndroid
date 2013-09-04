package cn.com.karl.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Music implements  Comparable<Music>, Parcelable {
	private String title="";
	private String singer;
	private String album="";
	private String url;
	private long size;
	private long time;
	private String name;
    private String dirPath="";
    private long albumId = -1;//媒体库中的专辑id
    private long id = -1;//媒体库中的id

    public Music(Parcel parcel) {
        title = parcel.readString();
        singer = parcel.readString();
        album = parcel.readString();
        url = parcel.readString();
        size = parcel.readLong();
        time = parcel.readLong();
        name = parcel.readString();
        dirPath = parcel.readString();
        albumId = parcel.readLong();
        id = parcel.readLong();
    }
    public Music(){}
    public long getId(){
        return id;
    }
    public long getAlbumId(){
        return albumId;
    }
    public void setAlbumId(long albumId){
        this.albumId = albumId;
    }
    public void setId(long id){
        this.id=id;
    }
	public String getDirPath(){
        return dirPath;
    }
    public void setDirPath(String dir){
        this.dirPath = dir;
    }
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
        if (album.length()==0) {
            return;
        }
        this.setDirPath(album);
    }
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
    //比较文件名是否相同
    public int compareTo(Music other)
    {
        if(!(other.getDirPath().length()==0 || this.dirPath.length()==0)){
            int dirCompare = this.dirPath.compareTo(other.getDirPath());
            if(dirCompare != 0) return dirCompare;
        }
        //目录相同时比较文件名
        if (this.title != null)
            return this.title.compareTo(other.getTitle());
        else
            throw new IllegalArgumentException();
    }
    public static final Parcelable.Creator<Music> CREATOR  = new Parcelable.Creator<Music>() {
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int i) {
            return new Music[0];
        }

    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(singer);
        parcel.writeString(album);
        parcel.writeString(url);
        parcel.writeLong(size);
        parcel.writeLong(time);
        parcel.writeString(name);
        parcel.writeString(dirPath);
        parcel.writeLong(albumId);
        parcel.writeLong(id);
    }
}
