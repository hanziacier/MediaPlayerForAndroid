package cn.com.karl.music;

import android.content.IntentFilter;
import android.widget.*;
import cn.com.karl.adapter.ArtistsAdapter;
import cn.com.karl.domain.ProgressSeekBar;
import cn.com.karl.util.MusicList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class ArtistsActivity extends Activity{
   
	private ListView artistListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist);

        MusicList.setExternalPath();
		artistListView=(ListView) this.findViewById(R.id.artistListView);
		ArtistsAdapter adapter=new ArtistsAdapter(ArtistsActivity.this, MusicList.getMusicData(ArtistsActivity.this));
		artistListView.setAdapter(adapter);
		artistListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				if(id == -1) {  
			        // 点击的是headerView或者footerView  
			        return;  
			    }
				// TODO Auto-generated method stub
				Intent intent = new Intent(ArtistsActivity.this,
						MusicActivity.class);
				intent.putExtra("id", position);
				startActivity(intent);
			}
		});

	}
}
