package cn.com.karl.music;

import java.util.List;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import cn.com.karl.domain.Music;
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
	private AudioManager audioManager;// ����������
	private int maxVolume;// �������
	private int currentVolume;// ��ǰ����
	private SeekBar seekBarVolume;
	private List<Music> lists;
	public static Boolean isPlaying = false;
	private static int id = 1;
	private static int currentId = MusicList.ErrorID;
	private static Boolean replaying=false;
	private MyProgressBroadCastReceiver receiver;
	private MyCompletionListner completionListner;
	public static Boolean isLoop=false;//�Ƿ�Ϊ����ѭ��
	private SensorManager sensorManager;
	private boolean mRegisteredSensor;
    public static Bitmap bm;//��ǰ�������ֵķ���ͼ
    public static Music music;//��ǰ���ŵ�����
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.music);

		textName = (TextView) this.findViewById(R.id.music_name);
		textSinger = (TextView) this.findViewById(R.id.music_singer);
		textStartTime = (TextView) this.findViewById(R.id.music_start_time);
		textEndTime = (TextView) this.findViewById(R.id.music_end_time);
		
		seekBar1 = (SeekBar) this.findViewById(R.id.music_seekBar);//������
		//icon = (ImageView) this.findViewById(R.id.image_icon);
		imageBtnLast = (ImageButton) this.findViewById(R.id.music_lasted);
		imageBtnRewind = (ImageButton) this.findViewById(R.id.music_rewind);
		imageBtnPlay = (ImageButton) this.findViewById(R.id.music_play);
		imageBtnForward = (ImageButton) this.findViewById(R.id.music_foward);
		imageBtnNext = (ImageButton) this.findViewById(R.id.music_next);
		imageBtnLoop = (ImageButton) this.findViewById(R.id.music_loop);//ѭ������
		seekBarVolume = (SeekBar) this.findViewById(R.id.music_volume);
		imageBtnRandom = (ImageButton) this.findViewById(R.id.music_random);//�������
		lrc_view = (LrcView) findViewById(R.id.LyricShow);
		
		imageBtnLast.setOnClickListener(new MyListener());
		imageBtnRewind.setOnClickListener(new MyListener());
		imageBtnPlay.setOnClickListener(new MyListener());
		imageBtnForward.setOnClickListener(new MyListener());
		imageBtnNext.setOnClickListener(new MyListener());
		imageBtnLoop.setOnClickListener(new MyListener());
		imageBtnRandom.setOnClickListener(new MyListener());
		//sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

		lists = MusicList.getMusicData(this);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// ����������
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// ��õ�ǰ����
		seekBarVolume.setMax(maxVolume);
		seekBarVolume.setProgress(currentVolume);
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
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, AudioManager.FLAG_ALLOW_RINGER_MODES);
			}
		});
		//*
		//�绰״̬����
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
		public void onCallStateChanged(int state, String incomingNumber) {//state�绰״̬ incomingNumber�绰����
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: //* ���κ�״̬ʱ 
				intent = new Intent(MusicActivity.this,
						MusicService.class);
				intent.putExtra("play", "playing");
				intent.putExtra("id", id);
				startService(intent);
				isPlaying = true;
				imageBtnPlay.setImageResource(R.drawable.pause1);
				replaying=true;
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: //* ����绰ʱ 
				
			case TelephonyManager.CALL_STATE_RINGING: //* �绰����ʱ 
				intent = new Intent(MusicActivity.this,
						MusicService.class);
				intent.putExtra("play", "pause");
				startService(intent);
				isPlaying = false;
				imageBtnPlay.setImageResource(R.drawable.play1);
				replaying=false;
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
		receiver=new MyProgressBroadCastReceiver();
		IntentFilter filter=new IntentFilter("cn.com.karl.progress");
		this.registerReceiver(receiver, filter);		
		id = getIntent().getIntExtra("id", 1);
		doPlayById(id);
		
	}

	protected void doPlayById(int id){
		if (id > lists.size() - 1) {
			id = lists.size() - 1;
		} else if (id < 0) {
			id = 0;
		}
		Log.e("doPlayById", "play id is "+id);
        music = lists.get(id);
		if (id == currentId) {
			textName.setText(music.getTitle());
			textSinger.setText(music.getSinger());
			textEndTime.setText(toTime((int) music.getTime()));
			Intent intent = new Intent(MusicActivity.this, MusicService.class);
			intent.putExtra("play", "replaying");
			intent.putExtra("id", id);
			intent.putExtra("total", (int) music.getTime());
			Log.e("doPlayById", id+"=="+currentId+",now startService doing");
			startService(intent);
			if (replaying) {
				imageBtnPlay.setImageResource(R.drawable.pause1);
				replaying=false;
				isPlaying = true;
			} else {
				imageBtnPlay.setImageResource(R.drawable.play1);
				replaying=true;
				isPlaying=false;
			}
			
			
		}else {

			textName.setText(music.getTitle());
			textSinger.setText(music.getSinger());
			textEndTime.setText(toTime((int) music.getTime()));		
			imageBtnPlay.setImageResource(R.drawable.pause1);
			Intent intent = new Intent(MusicActivity.this, MusicService.class);
			intent.putExtra("play", "play");
			intent.putExtra("id", id);
			intent.putExtra("total", (int) music.getTime());

			startService(intent);
			isPlaying = true;
			replaying=true;
			currentId = id;//���µ�ǰ���ŵ�����ID
		}
        bm = MusicUtil.getArtwork(this, music.getId(), music.getAlbumId(), false);
        if(bm != null){
            Log.e("MusicActivity", "I Have Get The Bitmap ,The SongId Is " + music.getId());
            lrc_view.setBackgroundDrawable(new BitmapDrawable(bm));
        }else{
            lrc_view.setBackgroundResource(R.drawable.bg);
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
			// TODO Auto-generated method stub
			if (v == imageBtnLast) {
				// ��һ��
				doPlayById(0);
			} else if (v == imageBtnRewind) {
				// ǰһ��
				id=id-1;
				if(id>lists.size()-1){
					id=lists.size()-1;
				}else if(id<0){
					id=0;
				}
				doPlayById(id);
			} else if (v == imageBtnPlay) {
				// ���ڲ���
				if (isPlaying == true) {
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "pause");
					intent.putExtra("id", id);
					startService(intent);
					isPlaying = false;
					imageBtnPlay.setImageResource(R.drawable.play1);
					replaying=false;
				} else {
					Intent intent = new Intent(MusicActivity.this,
							MusicService.class);
					intent.putExtra("play", "playing");
					intent.putExtra("id", id);
					startService(intent);
					isPlaying = true;
					imageBtnPlay.setImageResource(R.drawable.pause1);
					replaying=true;
				}
			} else if (v == imageBtnForward) {
				// ��һ��
				id=id+1;
				if(id>lists.size()-1){
					id=lists.size()-1;
				}else if(id<0){
					id=0;
				}
				doPlayById(id);

			} else if (v == imageBtnNext) {
				// ���һ��
				doPlayById(lists.size()-1);
			} else if (v == imageBtnLoop) {
				if (isLoop == true) {
					// ˳�򲥷�
					imageBtnLoop
							.setBackgroundResource(R.drawable.play_loop_spec);
					isLoop = false;
				} else {
					// ��������
					imageBtnLoop
							.setBackgroundResource(R.drawable.play_loop_sel);
					isLoop = true;
				}
			} else if (v == imageBtnRandom) {
				imageBtnRandom.setImageResource(R.drawable.play_random_sel);
			}

		}
	}
   private class MyCompletionListner extends BroadcastReceiver{//�������ʱservice�ᷢ�͹㲥��activity���յ���������һ��������view��Ϣ

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		currentId = MusicList.ErrorID;//��ֹ��ɵľ��ǵ�ǰ���ŵ�
		int id = intent.getIntExtra("id", 0);
		Log.e("completionListner onReceive","id is "+id);
		doPlayById(id);
	}
	   
   }
	/**
	 * ʱ���ʽת��
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
	
	//������Ӧ ˦�����
	private static final int SHAKE_THRESHOLD = 3000;
	private long lastUpdate=0;
	private double last_x=0;
	private double last_y= 4.50;
	private double last_z=9.50;
	//������ƾ��ȣ�ԽС��ʾ��ӦԽ����
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		//������׼�ȸı�
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			long curTime = System.currentTimeMillis();
			
			// ÿ200������һ��   
			if ((curTime - lastUpdate) > 100) { 
			long diffTime = (curTime - lastUpdate);  
			lastUpdate = curTime;   
			double x=event.values[SensorManager.DATA_X];
			double y=event.values[SensorManager.DATA_Y];
			double z=event.values[SensorManager.DATA_Z];

			float speed = (float) (Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000);   			  
			if (speed > SHAKE_THRESHOLD) {   
                        //��⵽ҡ�κ�ִ�еĴ���
				  if(MusicService.playing==true){
					  Intent intent = new Intent(MusicActivity.this,
								MusicService.class);
						intent.putExtra("play", "pause");
						startService(intent);
						isPlaying = false;
						imageBtnPlay.setImageResource(R.drawable.play1);
						replaying=false;
				  }else{
					  Intent intent = new Intent(MusicActivity.this,
								MusicService.class);
						intent.putExtra("play", "playing");
						intent.putExtra("id", id);
						startService(intent);
						isPlaying = true;
						imageBtnPlay.setImageResource(R.drawable.pause1);
						replaying=true;
				  }
			}  
			last_x = x;   
			last_y = y;   
			last_z = z;   
			}
		}
	}

}