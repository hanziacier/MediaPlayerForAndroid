package cn.com.karl.music;

import java.util.List;

import cn.com.karl.adapter.MusicAdapter;
import cn.com.karl.domain.Music;
import cn.com.karl.util.MusicList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListActivity extends Activity {

	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listmusic);
		
		this.listView= (ListView) this.findViewById(R.id.listAllMusic);
		List<Music> listMusic=MusicList.getMusicData(this);
		MusicAdapter adapter=new MusicAdapter(this, listMusic);
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
	}
}
