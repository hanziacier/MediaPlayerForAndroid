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
	private File				currentDirectory = new File("/");//当前目录
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
	//浏览文件系统的根目录
	private void browseToRoot() 
	{
		browseTo(new File("/"));
    }
	//返回上一级目录
	private void upOneLevel()
	{
		if(this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}
	//浏览指定的目录,如果是文件则进行打开操作
	private void browseTo(final File file)
	{
		this.setTitle(file.getAbsolutePath());//设置title为当前目录文件的绝对路径
		if (file.isDirectory())
		{
			this.currentDirectory = file;//更新当前目录为指定目录
			File[] files = file.listFiles();			
			fill(files);//列表页渲染
		}
		else
		{
			fileOptMenu(file);
		}
	}
	//打开指定文件
	protected void openFile(File aFile)
	{
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(aFile.getAbsolutePath());
		// 取得文件名
		String fileName = file.getName();
		// 根据不同的文件类型来打开文件
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
	//这里可以理解为设置ListActivity的源
	private void fill(File[] files)
	{

		//清空列表（表示自定义文件项的列表，用做列表页渲染的数据源）
		this.directoryEntries.clear();

		//添加一个当前目录的选项
		this.directoryEntries.add(new IconifiedText(getString(R.string.current_dir), getResources().getDrawable(R.drawable.folder)));
		//如果不是根目录则添加上一级目录项
		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add(new IconifiedText(getString(R.string.up_one_level), getResources().getDrawable(R.drawable.uponelevel)));

		Drawable currentIcon = null;
		if(files != null){
			for (File currentFile : files)
			{
				//判断是一个文件夹还是一个文件
				if (currentFile.isDirectory())
				{
					currentIcon = getResources().getDrawable(R.drawable.folder);
				}
				else
				{
					//取得文件名
					String fileName = currentFile.getName();
					//根据文件名来判断文件类型，设置不同的图标
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
				//确保只显示文件名、不显示路径如：/sdcard/111.txt就只是显示111.txt
				int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
				this.directoryEntries.add(new IconifiedText(currentFile.getAbsolutePath().substring(currentPathStringLenght), currentIcon));
			}
		}
		Collections.sort(this.directoryEntries);//directoryentries支持sort 应为iconifiedtext 实现了compareTo方法
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		//将表设置到ListAdapter中
		itla.setListItems(this.directoryEntries);
		//为ListActivity添加一个ListAdapter
		this.setListAdapter(itla);

	}
	
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		// 取得选中的一项的文件名
		String selectedFileString = this.directoryEntries.get(position).getText();
		
		if (selectedFileString.equals(getString(R.string.current_dir)))
		{
			//如果选中的是刷新
			this.browseTo(this.currentDirectory);
		}
		else if (selectedFileString.equals(getString(R.string.up_one_level)))
		{
			//返回上一级目录
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
	//通过文件名判断是什么类型的文件
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
	
	//处理文件，包括打开，重命名等操作
	public void fileOptMenu(final File file)
	{
		openFile(file);
	}
	//得到当前目录的绝对路劲
	public String GetCurDirectory()
	{
		return this.currentDirectory.getAbsolutePath();
	}
}
