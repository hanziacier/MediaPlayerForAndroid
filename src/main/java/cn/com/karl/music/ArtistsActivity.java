package cn.com.karl.music;

import android.widget.*;
import cn.com.karl.adapter.ArtistsAdapter;
import cn.com.karl.util.MusicList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class ArtistsActivity extends Activity {
   
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

    @Override
    protected void onStart() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)this.findViewById(R.id.artistListViewBottom).getLayoutParams();
        if(MusicActivity.music !=null && !MusicActivity.music.getTitle().isEmpty()){//媒体播放器中有音乐时
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
        this.findViewById(R.id.artistListViewBottom).setLayoutParams( layoutParams);
        super.onStart();
    }
}
