package cn.com.karl.music;

import java.io.File;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
    /** Called when the activity is first created. */
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
        spec = tabHost.newTabSpec("����").setIndicator("����",
                          res.getDrawable(R.drawable.item))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, ArtistsActivity.class);
        spec = tabHost.newTabSpec("������").setIndicator("������",
                          res.getDrawable(R.drawable.artist))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AlbumsActivity.class);
        spec = tabHost.newTabSpec("ר��").setIndicator("ר��",
                          res.getDrawable(R.drawable.album))
                      .setContent(intent);
        tabHost.addTab(spec);
        intent = new Intent().setClass(this, SongsActivity.class);
        spec = tabHost.newTabSpec("�������").setIndicator("�������",
                          res.getDrawable(R.drawable.album))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);  
        scanIntent.setData(Uri.fromFile(new File("/mnt/sdcard/background02.mp3")));  
        sendBroadcast(scanIntent);
        
        tabHost.setCurrentTab(0);

    }
}