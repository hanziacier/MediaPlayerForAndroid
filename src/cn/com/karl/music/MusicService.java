package cn.com.karl.music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private MediaPlayer player;//ϵͳ��ý�岥��������
	private List<Music> lists;//�����б�
	public static int _id = 0; // ��ǰ����λ��
	public static Boolean isRun = true;
	public LrcProcess mLrcProcess;//��ʴ�����
	public LrcView mLrcView;//�����ͼ
	public static int playing_id = 0;
	public static Boolean playing = false;
	
	//---��ʴ���----//		
	private List<LrcContent> lrcList = new ArrayList<LrcContent>();// lrc����б����	
	private int lrcListIndex = 0;// ��ʼ����ʼ���ֵ	
	private int CurrentTime = 0;// ��ʼ����������ʱ��ı���	
	private int CountTime = 0;	// ��ʼ��������ʱ��ı���
	Handler mHandler = new Handler();
	// ��ʹ����߳�
	Runnable mRunnable = new Runnable() {//�����ڲ���
		@Override
		public void run() {
			// TODO Auto-generated method stub ��������
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
		lists = MusicList.getMusicData(getApplicationContext());
		SeekBarBroadcastReceiver receiver = new SeekBarBroadcastReceiver();
		IntentFilter filter = new IntentFilter("cn.com.karl.seekBar");
		this.registerReceiver(receiver, filter);
		new Thread(this).start();//�����µ��߳�ִ��run����
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub

		String play = intent.getStringExtra("play");
		_id = intent.getIntExtra("id", 1);
		if (_id > lists.size() - 1) {
			_id = lists.size() - 1;
		} else if (_id < 0) {
			_id = 0;
		}
		Music m = lists.get(_id);
		String url = m.getUrl();
		
		if (play.equals("play")) {
			if (null != player) {
				player.release();
				player = null;
			}
			playMusic(_id);

		} else if (play.equals("pause")) {
			if (null != player) {
				player.pause();
			}
		} else if (play.equals("playing")) {
			if (player != null) {
				player.start();
			} else {
				playMusic(_id);
			}
		} else if (play.equals("replaying")) {

		} else if (play.equals("first")) {
			playMusic(0);
		} else if (play.equals("rewind")) {
			playMusic(_id-1);
		} else if (play.equals("forward")) {
			playMusic(_id+1);
		} else if (play.equals("last")) {
			playMusic(lists.size()-1);
		}
		// /////////////////////// ��ʼ��������� /////////////////////// //

		mLrcProcess = new LrcProcess();		
		// ��ȡ����ļ�
		mLrcProcess.readLRC(url);
		// ���ش����ĸ���ļ�
		lrcList = mLrcProcess.getLrcContent();
		MusicActivity.lrc_view.setSentenceEntities(lrcList);
		// �л���������ʾ���
		MusicActivity.lrc_view.setAnimation(AnimationUtils.loadAnimation(	MusicService.this, R.anim.alpha_z));
		// �����߳�
		mHandler.post(mRunnable);
		// /////////////////////// ��ʼ��������� /////////////////////// //

	}

		
	private void playMusic(int id) {

		if (null != player) {
			player.release();
			player = null;
		}		

		Music m = lists.get(id);
		String url = m.getUrl();
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
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				// ��һ��
				if (MusicActivity.isLoop == false) {
					player.reset();
					Intent intent = new Intent("cn.com.karl.completion");					
					_id = _id + 1;
					intent.putExtra("id", _id);
					sendBroadcast(intent);
				} else { // ��������
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
				// TODO Auto-generated method stub
				if (null != player) {
					player.release();
					player = null;
				}
				Music m = lists.get(_id);
				String url = m.getUrl();
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

	}

	//�������㲥�����������ܽ������仯�Ĺ㲥�����ݣ�ע��������Ĺ�����ָ����
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
			if (null != player) {
				if (!player.isPlaying()) {//������ڲ���״̬����ֹͣ���¡���
                  //Log.d("player is stoped ","������ֹͣ����,������ȡλ��");
                  continue;
                }
				int position = player.getCurrentPosition();
				int total = player.getDuration();
				if(total>1){
					Intent intent = new Intent("cn.com.karl.progress");
					intent.putExtra("position", position);
					intent.putExtra("total", total);
					sendBroadcast(intent);//��ʱ���� ����					
				}

			}
			if (null != player) {
				if (player.isPlaying()) {
					playing = true;
				} else {
					playing = false;
				}
			}
		}
	}

	/**
	 * ���ͬ��������
	 */
	public int LrcIndex() {//���ص�ǰ���ŵ�������lrcList�е�λ��
		if (player.isPlaying()) {
			// ��ø����������ĵ�ʱ��
			CurrentTime = player.getCurrentPosition();
			// ��ø�����ʱ�䳤��
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
