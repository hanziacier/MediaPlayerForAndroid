package cn.com.karl.music;

import java.util.List;

import android.content.IntentFilter;

import cn.com.karl.adapter.ListAdapter;
import cn.com.karl.domain.Music;
import cn.com.karl.domain.ProgressSeekBar;
import cn.com.karl.receiver.PlayProgressBarReciver;
import cn.com.karl.util.MusicList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ListActivity extends SeekBarActivity {

	private ListView listView;
    private PlayProgressBarReciver playProgressBarReciver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listmusic);
		
		this.listView= (ListView) this.findViewById(R.id.listAllMusic);
        MusicList.setExternalPath();
		List<Music> listMusic=MusicList.getMusicData(this);
		ListAdapter adapter=new ListAdapter(this, listMusic);
		this.listView.setAdapter(adapter);
		this.listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(id == -1) {  
			        // 点击的是headerView或者footerView  
			        return;  
			    }
				Intent intent = new Intent(ListActivity.this,
						MusicActivity.class);
				intent.putExtra("id", position);
				startActivity(intent);

				
			}
		});
        progressSeekBar.mProgressSeekBar = (SeekBar) this.findViewById(R.id.musicSeekBar);//进度条
        progressSeekBar.mTitleTextView = (TextView) this.findViewById(R.id.playMusicName);//音乐标题
        playProgressBarReciver =new PlayProgressBarReciver(this);
        IntentFilter filter=new IntentFilter("cn.com.karl.progress");
        this.registerReceiver(playProgressBarReciver, filter);
	}
/*    @Override
    protected void onStart() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)this.findViewById(R.id.listAllMusicBottom).getLayoutParams();
        if(MusicActivity.music !=null && MusicActivity.music.getTitle()!=""){//媒体播放器中有音乐时
            //layoutParams.width = RelativeLayout.LayoutParams.FILL_PARENT;
            layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            //this.findViewById(R.id.albumListViewBottom).setLayoutParams( new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT));
            //this.findViewById(R.id.albumListViewBottom).setLayoutParams( layoutParams);
            ((TextView)this.findViewById(R.id.playMusicName)).setText(MusicActivity.music.getTitle());
        }else{
            //layoutParams.width = RelativeLayout.LayoutParams.FILL_PARENT;
            layoutParams.height = 0;
            //this.findViewById(R.id.albumListViewBottom).setLayoutParams( new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,0));
        }
        this.findViewById(R.id.listAllMusicBottom).setLayoutParams( layoutParams);
        super.onStart();
    }*/
/*    @Override
    protected void onResume() {
        playProgressBarReciver =new PlayProgressBarReciver();
        IntentFilter filter=new IntentFilter("cn.com.karl.progress");
        this.registerReceiver(playProgressBarReciver, filter);
        super.onResume();

    }*/
    protected void onDestroy(){
        this.unregisterReceiver(playProgressBarReciver);
        super.onDestroy();
    }

}
