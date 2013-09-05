package cn.com.karl.music;

import android.content.IntentFilter;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.com.karl.adapter.AlbumsAdapter;
import cn.com.karl.domain.ProgressSeekBar;
import cn.com.karl.util.MusicList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AlbumsActivity extends Activity {

	private ListView albumListView;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.albums);
        MusicList.setExternalPath();
		albumListView=(ListView) this.findViewById(R.id.albumListView);
		AlbumsAdapter adapter=new AlbumsAdapter(AlbumsActivity.this, MusicList.getMusicData(AlbumsActivity.this));
		albumListView.setAdapter(adapter);
		albumListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AlbumsActivity.this,
						MusicActivity.class);
				intent.putExtra("id", arg2);
				startActivity(intent);
			}
		});

	}
}
