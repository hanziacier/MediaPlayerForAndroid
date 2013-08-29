package cn.com.karl.domain;

public class Music implements Comparable<Music> {
	private String title="";
	private String singer;
	private String album;
	private String url;
	private long size;
	private long time;
	private String name;
	
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
        if (this.title != null)
            return this.title.compareTo(other.getTitle());
        else
            throw new IllegalArgumentException();
    }
}
