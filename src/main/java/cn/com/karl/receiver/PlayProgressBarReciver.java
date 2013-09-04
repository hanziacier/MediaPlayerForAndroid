package cn.com.karl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.com.karl.domain.Music;
import cn.com.karl.domain.ProgressSeekBar;
import cn.com.karl.music.SeekBarActivity;

/**
 * Created by leju on 13-9-4.
 */
public class PlayProgressBarReciver extends BroadcastReceiver {

    public PlayProgressBarReciver(Context context){
        super();
    }
    public PlayProgressBarReciver(){
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        SeekBarActivity activity = (SeekBarActivity)context;
        int position=intent.getIntExtra("position", 0);
        int total=intent.getIntExtra("total", 100);
        Music music = (Music) intent.getParcelableExtra("music");
        int progress = position * 100 / total;
        //Log.e("PlayProgressBarReciver","total = "+total+" position = "+position+" progress = "+progress);
        activity.progressSeekBar.mTitleTextView.setText(music.getTitle());
        activity.progressSeekBar.mProgressSeekBar.setProgress(progress);
        activity.progressSeekBar.mProgressSeekBar.invalidate();
    }

}
