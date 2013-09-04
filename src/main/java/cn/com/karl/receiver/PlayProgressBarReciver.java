package cn.com.karl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TableLayout;
import cn.com.karl.domain.Music;
import cn.com.karl.domain.ProgressSeekBar;
import cn.com.karl.music.ProgressSeekBarInterface;

/**
 * Created by leju on 13-9-4.
 */
public class PlayProgressBarReciver extends BroadcastReceiver {
    protected static TableLayout tableLayout;
    protected static ProgressSeekBar progressSeekBar;


    public  PlayProgressBarReciver(ProgressSeekBarInterface context){
        super();
        if(progressSeekBar == null ){
             progressSeekBar = context.getSeekBar();
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int position=intent.getIntExtra("position", 0);
        int total=intent.getIntExtra("total", 100);
        Music music = (Music) intent.getParcelableExtra("music");
        int progress = position * 100 / total;
        //Log.e("PlayProgressBarReciver","total = "+total+" position = "+position+" progress = "+progress);
        progressSeekBar.mTitleTextView.setText(music.getTitle());
        progressSeekBar.mProgressSeekBar.setProgress(progress);
        progressSeekBar.mProgressSeekBar.invalidate();
    }

}
