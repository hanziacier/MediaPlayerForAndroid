package cn.com.karl.music;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.karl.adapter.IconifiedTextListAdapter;
import cn.com.karl.domain.IconifiedText;
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
		browseToRoot();
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
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(aFile.getAbsolutePath());
		// ȡ���ļ���
		String fileName = file.getName();
		// ���ݲ�ͬ���ļ����������ļ�
		if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingImage)))
		{
			intent.setDataAndType(Uri.fromFile(file), "image/*");
		}
		else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingAudio)))
		{
			intent.setDataAndType(Uri.fromFile(file), "audio/*");
			MusicList.setSearchFilePath(this.currentDirectory); 
			MusicList.getMusicData(this);

			intent.setClass(this, MusicActivity.class);
		}
		else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingVideo)))
		{
			intent.setDataAndType(Uri.fromFile(file), "video/*");
		}
		MusicList.setSearchFilePath(this.currentDirectory); 
		MusicList.getMusicData(this);
		int id = MusicList.getIndex(file);
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
					//�����ļ������ж��ļ����ͣ����ò�ͬ��ͼ��
					if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingImage)))
					{
						currentIcon = getResources().getDrawable(R.drawable.image);
					}
					else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingWebText)))
					{
						currentIcon = getResources().getDrawable(R.drawable.webtext);
					}
					else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingPackage)))
					{
						currentIcon = getResources().getDrawable(R.drawable.packed);
					}
					else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingAudio)))
					{
						currentIcon = getResources().getDrawable(R.drawable.audio);
					}
					else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingVideo)))
					{
						currentIcon = getResources().getDrawable(R.drawable.video);
					}
					else
					{
						currentIcon = getResources().getDrawable(R.drawable.text);
					}
				}
				//ȷ��ֻ��ʾ�ļ���������ʾ·���磺/sdcard/111.txt��ֻ����ʾ111.txt
				int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
				this.directoryEntries.add(new IconifiedText(currentFile.getAbsolutePath().substring(currentPathStringLenght), currentIcon));
			}
		}
		Collections.sort(this.directoryEntries);//directoryentries֧��sort ӦΪiconifiedtext ʵ����compareTo����
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		//�������õ�ListAdapter��
		itla.setListItems(this.directoryEntries);
		//ΪListActivity���һ��ListAdapter
		this.setListAdapter(itla);

	}
	
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
