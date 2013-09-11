package cn.com.karl.music;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AdapterView;
import cn.com.karl.adapter.IconifiedTextListAdapter;
import cn.com.karl.domain.IconifiedText;
import cn.com.karl.domain.IconifiedTextView;
import cn.com.karl.filter.MusicFileFilter;
import cn.com.karl.util.MusicList;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class FolderActivity extends ListActivity
{
	private List<IconifiedText>	directoryEntries = new ArrayList<IconifiedText>();
	private File				currentDirectory = new File("/");//��ǰĿ¼
	private File 				myTmpFile 		 = null;
	private int 				myTmpOpt		 = -1;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
        setContentView(R.layout.folder);
		browseToRoot();
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                String selectedFileString = directoryEntries.get(position).getText();
                final File clickedFile = new File(currentDirectory.getAbsolutePath()+ selectedFileString);
                if(clickedFile != null && clickedFile.isDirectory()){
                    File[] mp3Files = clickedFile.listFiles(new MusicFileFilter());
                    if(mp3Files!=null && mp3Files.length>0) {//��ѡĿ¼����MP3�ļ�ʱ
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                //MusicList.setSearchFilePath(clickedFile);
                                MusicList.getMusicData(FolderActivity.this);
                                intent.setClass(FolderActivity.this, MusicActivity.class);
                                intent.putExtra("id", 0);
                                startActivity(intent);
                                Log.e("FolderActivity", "onItemLongClick.AlertDialog.OnClickListener " + id);

                            }

                        };
                        //��ʾ�����˵�
                        String[] menu = {"����Ŀ¼"};
                        new AlertDialog.Builder(FolderActivity.this)
                                .setTitle("��ѡ����Ҫ���еĲ���")
                                .setItems(menu, listener)
                                .show();
                    }else{//��ѡĿ¼��û��mp3�ļ�
                        Log.e("FolderActivity", "onItemLongClick not find mp3 files ");
                        return false;
                    }
                }
                return false;


            }
        });
        this.setSelection(0);
	}
	//����ļ�ϵͳ�ĸ�Ŀ¼
	private void browseToRoot() 
	{
		browseTo(new File("/"));
    }
	//������һ��Ŀ¼
	private void upOneLevel()
	{
		if(this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}
	//���ָ����Ŀ¼,������ļ�����д򿪲���
	private void browseTo(final File file)
	{
		this.setTitle(file.getAbsolutePath());//����titleΪ��ǰĿ¼�ļ��ľ���·��
		if (file.isDirectory())
		{
			this.currentDirectory = file;//���µ�ǰĿ¼Ϊָ��Ŀ¼
			File[] files = file.listFiles();			
			fill(files);//�б�ҳ��Ⱦ
		}
		else
		{
			fileOptMenu(file);
		}
	}
	//��ָ���ļ�
	protected void openFile(File aFile)
	{

		File file = new File(aFile.getAbsolutePath());
		// ȡ���ļ���
		String fileName = file.getName();
		// ���ݲ�ͬ���ļ����������ļ�
        //MusicList.setSearchFilePath(this.currentDirectory);
        MusicList.getMusicData(this);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClass(this, MusicActivity.class);
		//int id = MusicList.getIndex(file);
        int id=0;
		if(id>=0){
			intent.putExtra("id", id);
			startActivity(intent);
		}else{
			Log.e("FolderActivity", "MusicList.getIndex ERROR");
		}
		
	}
	//����������Ϊ����ListActivity��Դ
	private void fill(File[] files)
	{

		//����б���ʾ�Զ����ļ�����б������б�ҳ��Ⱦ������Դ��
		this.directoryEntries.clear();

		//���һ����ǰĿ¼��ѡ��
		this.directoryEntries.add(new IconifiedText(getString(R.string.current_dir), getResources().getDrawable(R.drawable.folder)));
		//������Ǹ�Ŀ¼�������һ��Ŀ¼��
		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add(new IconifiedText(getString(R.string.up_one_level), getResources().getDrawable(R.drawable.uponelevel)));

		Drawable currentIcon = null;
		if(files != null){
			for (File currentFile : files)
			{
				//�ж���һ���ļ��л���һ���ļ�
				if (currentFile.isDirectory())
				{
					currentIcon = getResources().getDrawable(R.drawable.folder);
				}
				else
				{

					//ȡ���ļ���
					String fileName = currentFile.getName();
                    if(new MusicFileFilter().accept(fileName)){
						currentIcon = getResources().getDrawable(R.drawable.audio);
					}else{
                        continue;
                    }

				}
				//ȷ��ֻ��ʾ�ļ���������ʾ·���磺/sdcard/111.txt��ֻ����ʾ111.txt
				int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
                Log.e("FolderActivity","IconifiedText "+currentFile.getAbsolutePath().substring(currentPathStringLenght));
				this.directoryEntries.add(
                        new IconifiedText(currentFile.getAbsolutePath().substring(currentPathStringLenght),
                                        currentIcon)
                );
			}
		}
		Collections.sort(this.directoryEntries);//directoryentries֧��sort ӦΪiconifiedtext ʵ����compareTo����
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		//�������õ�ListAdapter��
		itla.setListItems(this.directoryEntries);
		//ΪListActivity���һ��ListAdapter
		this.setListAdapter(itla);

	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		// ȡ��ѡ�е�һ����ļ���
		String selectedFileString = this.directoryEntries.get(position).getText();
		
		if (selectedFileString.equals(getString(R.string.current_dir)))
		{
			//���ѡ�е���ˢ��
			this.browseTo(this.currentDirectory);
		}
		else if (selectedFileString.equals(getString(R.string.up_one_level)))
		{
			//������һ��Ŀ¼
			this.upOneLevel();
		}
		else
		{	
			File clickedFile = null;
			clickedFile = new File(this.currentDirectory.getAbsolutePath()+ this.directoryEntries.get(position).getText());
			if(clickedFile != null)
				this.browseTo(clickedFile);
		}
	}
	//ͨ���ļ����ж���ʲô���͵��ļ�
	private boolean checkEndsWithInStringArray(String checkItsEnd, 
					String[] fileEndings)
	{
		for(String aEnd : fileEndings)
		{
			if(checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
	


	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onPrepareOptionsMenu(menu);
	}
	
	//�����ļ��������򿪣��������Ȳ���
	public void fileOptMenu(final File file)
	{
		openFile(file);
	}
	//�õ���ǰĿ¼�ľ���·��
	public String GetCurDirectory()
	{
		return this.currentDirectory.getAbsolutePath();
	}
}
