package cn.com.karl.music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import cn.com.karl.domain.Music;
import cn.com.karl.util.LrcProcess;
import cn.com.karl.util.LrcProcess.LrcContent;
import cn.com.karl.util.LrcView;
import cn.com.karl.util.MusicList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
//import android.media.MediaPlayer.OnPreparedListener;
//import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
//import android.os.PowerManager;
//import android.util.Log;
import android.view.animation.AnimationUtils;

public class MusicService extends Service implements Runnable {
	private MediaPlayer player;//系统多媒体播放器对象

    public static int _id = 0; // 当前播放在lists中位置
	public static Boolean isRun = true;
	public LrcProcess mLrcProcess;//歌词处理类
	public LrcView mLrcView;//歌词视图
	public static Boolean playing = false;

    public static Music music=null;
	//---歌词处理----//		
	private List<LrcContent> lrcList = new ArrayList<LrcContent>();// lrc歌词列表对象	
	private int lrcListIndex = 0;// 初始化歌词检索值	
	private int CurrentTime = 0;// 初始化歌曲播放时间的变量	
	private int CountTime = 0;	// 初始化歌曲总时间的变量
	Handler mHandler = new Handler();
	// 歌词滚动线程
	Runnable mRunnable = new Runnable() {//匿名内部类
		@Override
		public void run() {
			// TODO Auto-generated method stub 测试屏蔽
			MusicActivity.lrc_view.SetIndex(LrcIndex());
			MusicActivity.lrc_view.invalidate();
			mHandler.postDelayed(mRunnable, 200);
		}
	};
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		SeekBarBroadcastReceiver receiver = new SeekBarBroadcastReceiver();
		IntentFilter filter = new IntentFilter("cn.com.karl.seekBar");
		this.registerReceiver(receiver, filter);
		new Thread(this).start();//开启新的线程执行run方法
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		
		// TODO Auto-generated method stub

		if(intent==null){
            Log.e("MusicService onStart", "intent is null");
            return super.onStartCommand(intent, flags, startId)	;

		}

		String play = intent.getStringExtra("play");
		_id = intent.getIntExtra("id", 1);
		if (_id > MusicList.getMusicList().size() - 1) {
			_id = MusicList.getMusicList().size() - 1;
		} else if (_id < 0) {
			_id = 0;
		}
		Log.e("MusicService onStartCommand", "_id is"+_id+"");
		music = MusicList.getMusicList().get(_id);
		String url = music.getUrl();
		
		if (play.equals("play")) {
			if (null != player) {
				player.release();
				player = null;
			}
			playMusic(music);

		} else if (play.equals("pause")) {
			if (null != player) {
				player.pause();
			}
		} else if (play.equals("playing")) {
			if (player != null) {
				player.start();
			} else {
				playMusic(music);
			}
		} else if (play.equals("replaying")) {

		} else if (play.equals("first")) {
			playMusic(MusicList.getMusicList().get(0));
		} else if (play.equals("rewind")) {
			playMusic(MusicList.getMusicList().get(_id-1<0?0:_id-1));
		} else if (play.equals("forward")) {
			playMusic(MusicList.getMusicList().get(_id+1>MusicList.getMusicList().size()-1?MusicList.getMusicList().size()-1:_id+1));
		} else if (play.equals("last")) {
			playMusic(MusicList.getMusicList().get(MusicList.getMusicList().size()-1));
		}
		// /////////////////////// 初始化歌词配置 /////////////////////// //

		mLrcProcess = new LrcProcess();		
		// 读取歌词文件
		mLrcProcess.readLRC(url);
		// 传回处理后的歌词文件
		lrcList = mLrcProcess.getLrcContent();
		MusicActivity.lrc_view.setSentenceEntities(lrcList);
		// 切换带动画显示歌词
		MusicActivity.lrc_view.setAnimation(AnimationUtils.loadAnimation(	MusicService.this, R.anim.alpha_z));
		// 启动线程
		mHandler.post(mRunnable);

		// /////////////////////// 初始化歌词配置 /////////////////////// //
		int returnInt = super.onStartCommand(intent, flags, startId)	;
		return returnInt;

	}

		
	private void playMusic(final Music music) {

		if (null != player) {
			player.release();
			player = null;
		}		

		String url = music.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.reset();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		Log.e("MusicService playMusic", "before try");
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("MusicService playMusic", "after try");
			
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				// 下一首
				if (MusicActivity.isLoop == false) {
					player.reset();
					Intent intent = new Intent("cn.com.karl.completion");					
					_id = _id + 1;
					intent.putExtra("id", _id);
					sendBroadcast(intent);
				} else { // 单曲播放
					player.reset();
					Intent intent = new Intent("cn.com.karl.completion");
					intent.putExtra("id", _id);
					sendBroadcast(intent);
				}
			}
		});
		player.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e("MusicService playMusic onErrorListener", mp.toString());
				// TODO Auto-generated method stub
				if (null != player) {
					player.release();
					player = null;
				}
				String url = music.getUrl();
				Uri myUri = Uri.parse(url);
				player = new MediaPlayer();
				player.reset();
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				try {
					player.setDataSource(getApplicationContext(), myUri);
					player.prepare();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				player.start();
				return false;
			}
		});
		player.start();	
		Log.e("MusicService playMusic", "after player.start");

	}

	//进度条广播接受器（接受进度条变化的广播，依据：注册接收器的过滤器指定）
	private class SeekBarBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int seekBarPosition = intent.getIntExtra("seekBarPosition", 0);
			player.seekTo(seekBarPosition * player.getDuration() / 100);
			player.start();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRun) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
			try {
				if (null != player) {

                    if (player.isPlaying()) {
                        playing = true;
                    } else {
                        playing = false;
                    }
					int position = player.getCurrentPosition();
					int total = player.getDuration();
					if (total > 1) {
						Intent intent = new Intent("cn.com.karl.progress");
						intent.putExtra("position", position);
						intent.putExtra("total", total);
                        intent.putExtra("music", (Parcelable) music);
						sendBroadcast(intent);// 临时屏蔽 调试
                        Log.e("MusicService","playStatus "+playing.toString());
					}

				}

			} catch (Exception e) {
                //Log.e("MusicService",e.toString());
				// TODO: handle exception
			}
		}
	}

	/**
	 * 歌词同步处理类
	 */
	public int LrcIndex() {//返回当前播放的音乐在lrcList中的位置
		if (player.isPlaying()) {
			// 获得歌曲播放在哪的时间
			CurrentTime = player.getCurrentPosition();
			// 获得歌曲总时间长度
			CountTime = player.getDuration();
		}else{
			return lrcListIndex;
		}
		//Log.e("CurrentTime--CountTime--lrcListSize", CurrentTime+"--"+CountTime+"--"+lrcList.size());
		if (CurrentTime < CountTime) {
			int countLrcList = lrcList.size();
			for (int i = 0; i < countLrcList; i++) {
				if (i < countLrcList - 1) {
					if (CurrentTime < lrcList.get(i).getLrc_time() && i == 0) {
						lrcListIndex = i;
					}
					if (CurrentTime > lrcList.get(i).getLrc_time()
							&& CurrentTime < lrcList.get(i + 1).getLrc_time()) {
						lrcListIndex = i;
					}
				}
				if (i == countLrcList - 1
						&& CurrentTime > lrcList.get(i).getLrc_time()) {
					lrcListIndex = i;
				}
			}
		}
		return lrcListIndex;
	}

}
