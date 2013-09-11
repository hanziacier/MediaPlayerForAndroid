package cn.com.karl.music;

import java.util.List;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import cn.com.karl.domain.Music;
import cn.com.karl.domain.Playbox;
import cn.com.karl.util.LrcView;
import cn.com.karl.util.MusicList;
import android.annotation.SuppressLint;
//import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
//import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import cn.com.karl.util.MusicUtil;

public class MusicActivity extends Activity implements SensorEventListener{
    private TTMdeiaPlayer app;
	private TextView textName;
	private TextView textSinger;
	private TextView textStartTime;
	private TextView textEndTime;
	private ImageButton imageBtnLast;
	private ImageButton imageBtnRewind;
	public static ImageButton imageBtnPlay;
	private ImageButton imageBtnForward;
	private ImageButton imageBtnNext;
	private ImageButton imageBtnLoop;
	private ImageButton imageBtnRandom;
	public static LrcView lrc_view;
	//private ImageView icon;
	private SeekBar seekBar1;
	private SeekBar seekBarVolume;

	private MyProgressBroadCastReceiver receiver;
	private MyCompletionListner completionListner;
	private SensorManager sensorManager;
	private boolean mRegisteredSensor;

    private List<Music> lists;
    private Playbox playbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.music);
        app = (TTMdeiaPlayer)getApplication();//获得applicaiton的全局引用
        playbox = app.playbox;
        lists = playbox.getPlayList();
		textName = (TextView) this.findViewById(R.id.music_name);
		textSinger = (TextView) this.findViewById(R.id.music_singer);
		textStartTime = (TextView) this.findViewById(R.id.music_start_time);
		textEndTime = (TextView) this.findViewById(R.id.music_end_time);
		
		seekBar1 = (SeekBar) this.findViewById(R.id.music_seekBar);//进度条
		//icon = (ImageView) this.findViewById(R.id.image_icon);
		imageBtnLast = (ImageButton) this.findViewById(R.id.music_lasted);
		imageBtnRewind = (ImageButton) this.findViewById(R.id.music_rewind);
		imageBtnPlay = (ImageButton) this.findViewById(R.id.music_play);
		imageBtnForward = (ImageButton) this.findViewById(R.id.music_foward);
		imageBtnNext = (ImageButton) this.findViewById(R.id.music_next);
		imageBtnLoop = (ImageButton) this.findViewById(R.id.music_loop);//循环播放
		seekBarVolume = (SeekBar) this.findViewById(R.id.music_volume);
		imageBtnRandom = (ImageButton) this.findViewById(R.id.music_random);//随机播放
		lrc_view = (LrcView) findViewById(R.id.LyricShow);
		
		imageBtnLast.setOnClickListener(new MyListener());
		imageBtnRewind.setOnClickListener(new MyListener());
		imageBtnPlay.setOnClickListener(new MyListener());
		imageBtnForward.setOnClickListener(new MyListener());
		imageBtnNext.setOnClickListener(new MyListener());
		imageBtnLoop.setOnClickListener(new MyListener());
		imageBtnRandom.setOnClickListener(new MyListener());
		//sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);



		seekBarVolume.setMax(playbox.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		seekBarVolume.setProgress(playbox.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
		seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				playbox.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, AudioManager.FLAG_ALLOW_RINGER_MODES);
			}
		});
		//*
		//电话状态监听
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(new MobliePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
		//*/
		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			
				seekBar1.setProgress(seekBar.getProgress());
				Intent intent=new Intent("cn.com.karl.seekBar");
				intent.putExtra("seekBarPosition", seekBar.getProgress());
				sendBroadcast(intent);
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
		
		completionListner=new MyCompletionListner();
		IntentFilter filter=new IntentFilter("cn.com.karl.completion");
		this.registerReceiver(completionListner, filter);
		
	}
	
	private class MobliePhoneStateListener extends PhoneStateListener {
		Intent intent;
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {//state电话状态 incomingNumber电话号码
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: //* 无任何状态时 
				intent = new Intent(MusicActivity.this,
						MusicService.class);
				intent.putExtra("play", "playing");
				intent.putExtra("id", playbox.getCurrentPlayListId());
				startService(intent);
				playbox.setPlaying(true);
				imageBtnPlay.setImageResource(R.drawable.pause1);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: //* 接起电话时 
				
			case TelephonyManager.CALL_STATE_RINGING: //* 电话进来时 
				intent = new Intent(MusicActivity.this,
						MusicService.class);
				intent.putExtra("play", "pause");
				startService(intent);
				playbox.setPlaying(false);
				imageBtnPlay.setImageResource(R.drawable.play1);
				break;
			default:
				break;

			}
			 super.onCallStateChanged(state, incomingNumber);

		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
        lists = app.playbox.getPlayList();
		receiver=new MyProgressBroadCastReceiver();
		IntentFilter filter=new IntentFilter("cn.com.karl.progress");
		this.registerReceiver(receiver, filter);		
		int id = getIntent().getIntExtra("id", 0);
		doPlayById(id);
		
	}

	protected void doPlayById(int id){
		if (id > lists.size() - 1) {
			id = lists.size() - 1;
		} else if (id < 0) {
			id = 0;
		}
		Log.e("doPlayById", "play id is "+id);
        int currentId = playbox.getCurrentPlayListId();//去除原盒子中正在播放的music 序号
        playbox.setPlayingMusic(id);

		if (id == currentId) {
			textName.setText(playbox.getCurrentMusic().getTitle());
			textSinger.setText(playbox.getCurrentMusic().getSinger());
			textEndTime.setText(toTime((int) playbox.getCurrentMusic().getTime()));
			Intent intent = new Intent(MusicActivity.this, MusicService.class);
			intent.putExtra("play", "replaying");
			intent.putExtra("id", id);
			intent.putExtra("total", (int) playbox.getCurrentMusic().getTime());
			Log.e("doPlayById", id+"=="+currentId+",now startService doing");
			startService(intent);
			if (playbox.isPlaying()) {
				imageBtnPlay.setImageResource(R.drawable.pause1);
			} else {
				imageBtnPlay.setImageResource(R.drawable.play1);
                playbox.setPlaying(true);
			}
			
			
		}else {
			textName.setText(playbox.getCurrentMusic().getTitle());
			textSinger.setText(playbox.getCurrentMusic().getSinger());
			textEndTime.setText(toTime((int) playbox.getCurrentMusic().getTime()));
			imageBtnPlay.setImageResource(R.drawable.pause1);
			Intent intent = new Intent(MusicActivity.this, MusicService.class);
			intent.putExtra("play", "play");
			intent.putExtra("id", id);
			intent.putExtra("total", (int) playbox.getCurrentMusic().getTime());

			startService(intent);
            playbox.setPlaying(true);
		}
        playbox.currentMusicBitmap  = MusicUtil.getArtwork(this, playbox.getCurrentMusic().getId(), playbox.getCurrentMusic().getAlbumId(), false);
        if(playbox.currentMusicBitmap != null){
            Log.e("MusicActivity", "I Have Get The Bitmap ,The SongId Is " + playbox.getCurrentMusic().getId());
            lrc_view.setBackgroundDrawable(new BitmapDrawable(playbox.currentMusicBitmap));
        }else{
            lrc_view.setBackgroundResource(R.drawable.listbg);
        }

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/*
		List<Sensor> sensors=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors.size()>0){
			Sensor sensor=sensors.get(0);
			mRegisteredSensor=sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		*/
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		/*
		if(mRegisteredSensor){
			sensorManager.unregisterListener(this);
			mRegisteredSensor=false;
		}
		*/
		super.onPause();
	}
	protected void onStop(){
		this.unregisterReceiver(receiver);
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		this.unregisterReceiver(completionListner);
		super.onDestroy();
	}
    public class MyProgressBroadCastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int position=intent.getIntExtra("position", 0);
			int total=intent.getIntExtra("total", 100);
			int progress = position * 100 / total;
            textEndTime.setText(toTime(total));
			textStartTime.setText(toTime(position));
			seekBar1.setProgress(progress);
			seekBar1.invalidate();
		}
    	
    }
	private class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {
            int id = playbox.getCurrentPlayListId();
			// TODO Auto-generated method stub
			if (v == imageBtnLast) {
				// 第一首
				doPlayById(0);
			} else if (v == imageBtnRewind) {
				// 前一首
				id=id-1;
				if(id > lists.size()-1){
					id = lists.size()-1;
				}else if(id < 0){
					id = 0;
				}
				doPlayById(id);
			} else if (v == imageBtnPlay) {
				// 正在播放
				if (playbox.isPlaying() == true) {
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "pause");
					intent.putExtra("id", playbox.getCurrentPlayListId());
					startService(intent);
					playbox.setPlaying(false);
					imageBtnPlay.setImageResource(R.drawable.play1);
				} else {
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "playing");
					intent.putExtra("id", playbox.getCurrentPlayListId());
					startService(intent);
					playbox.setPlaying(true);
					imageBtnPlay.setImageResource(R.drawable.pause1);

				}
			} else if (v == imageBtnForward) {
				// 下一首
				id=id+1;
				if(id > lists.size()-1){
					id = lists.size()-1;
				}else if(id < 0){
					id=0;
				}
				doPlayById(id);

			} else if (v == imageBtnNext) {
				// 最后一首
				doPlayById(lists.size()-1);
			} else if (v == imageBtnLoop) {
				if (playbox.isLoop()) {
					// 顺序播放
					imageBtnLoop
							.setBackgroundResource(R.drawable.play_loop_spec);
					playbox.setLoop(false) ;
				} else {
					// 单曲播放
					imageBtnLoop
							.setBackgroundResource(R.drawable.play_loop_sel);
                    playbox.setLoop(true) ;
				}
			} else if (v == imageBtnRandom) {
				imageBtnRandom.setImageResource(R.drawable.play_random_sel);
			}

		}
	}
   private class MyCompletionListner extends BroadcastReceiver{//播放完成时service会发送广播给activity，收到后设置下一个歌曲的view信息

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int id = intent.getIntExtra("id", 0);
		Log.e("completionListner onReceive","id is "+id);
		doPlayById(id);
	}
	   
   }
	/**
	 * 时间格式转换
	 * 
	 * @param time
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		//int hour = minute / 60;
		int second = time % 60;
		//minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
	
	//重力感应 甩歌代码
	private static final int SHAKE_THRESHOLD = 3000;
	private long lastUpdate=0;
	private double last_x=0;
	private double last_y= 4.50;
	private double last_z=9.50;
	//这个控制精度，越小表示反应越灵敏
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		//处理精准度改变
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			long curTime = System.currentTimeMillis();
			
			// 每200毫秒检测一次   
			if ((curTime - lastUpdate) > 100) { 
			long diffTime = (curTime - lastUpdate);  
			lastUpdate = curTime;   
			double x=event.values[SensorManager.DATA_X];
			double y=event.values[SensorManager.DATA_Y];
			double z=event.values[SensorManager.DATA_Z];

			float speed = (float) (Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000);   			  
			if (speed > SHAKE_THRESHOLD) {   
                        //检测到摇晃后执行的代码
				  if(MusicService.playing==true){
					  Intent intent = new Intent(MusicActivity.this,
								MusicService.class);
						intent.putExtra("play", "pause");
						startService(intent);
                        playbox.setPlaying(false);
						imageBtnPlay.setImageResource(R.drawable.play1);

				  }else{
					  Intent intent = new Intent(MusicActivity.this,
								MusicService.class);
						intent.putExtra("play", "playing");
						intent.putExtra("id", playbox.getCurrentPlayListId());
						startService(intent);
                      playbox.setPlaying(true);
						imageBtnPlay.setImageResource(R.drawable.pause1);

				  }
			}  
			last_x = x;   
			last_y = y;   
			last_z = z;   
			}
		}
	}

}
