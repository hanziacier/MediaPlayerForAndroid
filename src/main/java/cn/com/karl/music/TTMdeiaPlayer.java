package cn.com.karl.music;

import android.app.Application;
import cn.com.karl.domain.Music;
import cn.com.karl.domain.Playbox;
import cn.com.karl.util.MusicList;

import java.util.List;

/**
 * Created by leju on 13-9-6.
 */
public class TTMdeiaPlayer extends Application {
    public List<Music> musicList;
    public Playbox playbox=Playbox.getPlaybox();//为了全局使用Playbox实例化只能放在application中
    public void onCreate() {
        super.onCreate();
        MusicList.setExternalPath();
        musicList = MusicList.getMusicData(this);
        playbox.setPlayList(musicList);//设置播放盒子的播放列表
    }

}
