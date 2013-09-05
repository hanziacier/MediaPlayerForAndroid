package cn.com.karl.music;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import cn.com.karl.domain.Music;
import cn.com.karl.domain.ProgressSeekBar;
import cn.com.karl.util.MusicUtil;

public class MainActivity extends TabActivity {
    /** Called when the activity is first created. */
    public static ProgressSeekBar progressSeekBar;
    private PlayProgressBarReciver playProgressBarReciver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        Resources res = getResources(); 
        TabHost tabHost = getTabHost(); 
        TabHost.TabSpec spec; 
        Intent intent;  
        intent = new Intent().setClass(this, ListActivity.class);
        spec = tabHost.newTabSpec("音乐").setIndicator("音乐",
                          res.getDrawable(R.drawable.item))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ArtistsActivity.class);
        spec = tabHost.newTabSpec("艺术家").setIndicator("艺术家",
                          res.getDrawable(R.drawable.artist))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AlbumsActivity.class);
        spec = tabHost.newTabSpec("专辑").setIndicator("专辑",
                          res.getDrawable(R.drawable.album))
                      .setContent(intent);
        tabHost.addTab(spec);
/*        intent = new Intent().setClass(this, SeekBarActivity.class);
        spec = tabHost.newTabSpec("最近播放").setIndicator("最近播放",
                          res.getDrawable(R.drawable.album))
                      .setContent(intent);
        tabHost.addTab(spec);*/
        intent = new Intent().setClass(this, FolderActivity.class);
        spec = tabHost.newTabSpec("目录").setIndicator("目录",
                          res.getDrawable(R.drawable.folder))
                      .setContent(intent);
        tabHost.addTab(spec);      
        tabHost.setCurrentTab(0);

    }
    protected void onResume() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)this.findViewById(R.id.mainViewBottom).getLayoutParams();
        if(MusicActivity.music !=null && MusicActivity.music.getTitle()!=""){//媒体播放器中有音乐时
            layoutParams.height = TabHost.LayoutParams.WRAP_CONTENT;
            ((TextView)this.findViewById(R.id.playProgessName)).setText(MusicActivity.music.getTitle());
        }else{
            layoutParams.height = 0;
        }
        this.findViewById(R.id.mainViewBottom).setLayoutParams( layoutParams);
        progressSeekBar.mArtists = (TextView) this.findViewById(R.id.playProgessArtists);//艺术家
        progressSeekBar.mTitleTextView = (TextView) this.findViewById(R.id.playProgessName);//音乐标题
        progressSeekBar.mPlayImageButton = (ImageButton) this.findViewById(R.id.playProgessPlay);
        progressSeekBar.mImageView = (ImageView)this.findViewById(R.id.playProgessImage);
        progressSeekBar.mArtists.setOnClickListener(new MyListener());
        progressSeekBar.mTitleTextView.setOnClickListener(new MyListener());
        progressSeekBar.mPlayImageButton.setOnClickListener(new MyListener());
        playProgressBarReciver =new PlayProgressBarReciver();
        IntentFilter filter=new IntentFilter("cn.com.karl.progress");
        this.registerReceiver(playProgressBarReciver, filter);
        super.onResume();
        Log.e("MainActivity", "onResume");

    }
    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == progressSeekBar.mPlayImageButton) {
                Log.e("MainActivity","MusicService.playStatus "+MusicService.playing.toString()+" MusicService._id "+MusicService._id);
                // 正在播放
                if (MusicService.playing == true) {
                    Intent intent = new Intent(MainActivity.this,
                            MusicService.class);
                    intent.putExtra("play", "pause");
                    intent.putExtra("id", MusicService._id);
                    startService(intent);
                    progressSeekBar.mPlayImageButton.setImageResource(R.drawable.play1);

                } else {
                    Intent intent = new Intent(MainActivity.this,
                            MusicService.class);
                    intent.putExtra("play", "playing");
                    intent.putExtra("id", MusicService._id);
                    startService(intent);
                    progressSeekBar.mPlayImageButton.setImageResource(R.drawable.pause1);

                }
            }else if(v == progressSeekBar.mArtists ||
                    v == progressSeekBar.mTitleTextView){

                Intent intent = new Intent(MainActivity.this,
                        MusicActivity.class);
                intent.putExtra("id", MusicService._id);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(playProgressBarReciver);
        super.onPause();
        Log.e("MainActivity","onPause");
    }

    protected class PlayProgressBarReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int position=intent.getIntExtra("position", 0);
            int total=intent.getIntExtra("total", 100);

            Music music = (Music) intent.getParcelableExtra("music");
            int progress = position * 100 / total;
            progressSeekBar.mTitleTextView.setText(music.getTitle());
            progressSeekBar.mArtists.setText(MusicUtil.toTime((int)music.getTime()));
            if(MusicService.playing) {
                progressSeekBar.mPlayImageButton.setImageResource(R.drawable.pause1);
            }else {
                progressSeekBar.mPlayImageButton.setImageResource(R.drawable.play1);
            }

            if(false){

            }else {
                Bitmap bitmap = MusicUtil.getArtwork(context, music.getId(), music.getAlbumId(), false);
                if(bitmap != null){
                    Log.e("MusicActivity", "I Have Get The Bitmap ,The SongId Is " + music.getId());
                    progressSeekBar.mImageView.setImageBitmap(bitmap);
                }else{
                    progressSeekBar.mImageView.setImageResource(R.drawable.music);
                }
            }

        }
    }
}