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

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
		}
		else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingVideo)))
		{
			intent.setDataAndType(Uri.fromFile(file), "video/*");
		}
		startActivity(intent);
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
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "�½�Ŀ¼").setIcon(R.drawable.addfolderr);
		menu.add(0, 1, 0, "ɾ��Ŀ¼").setIcon(R.drawable.delete);
		menu.add(0, 2, 0, "ճ���ļ�").setIcon(R.drawable.paste);
		menu.add(0, 3, 0, "��Ŀ¼").setIcon(R.drawable.goroot);
		menu.add(0, 4, 0, "��һ��").setIcon(R.drawable.uponelevel);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
			case 0:
				Mynew();
				break;
			case 1:
				//ע�⣺ɾ��Ŀ¼�������������������ṩ��
				//deleteFile��ɾ���ļ�����deleteFolder��ɾ������Ŀ¼��
				MyDelete();
				break;
			case 2:
				MyPaste();
				break;
			case 3:
				this.browseToRoot();
				break;
			case 4:
				this.upOneLevel();
				break;
		}
		return false;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onPrepareOptionsMenu(menu);
	}
	//ճ������
	public void MyPaste()
	{}
	//ɾ�������ļ���
	public void MyDelete()
	{}
	//�½��ļ���
	public void Mynew()
	{}
	//�½��ļ���
	public boolean newFolder(String file)
	{
		return true;
	}
	//ɾ���ļ�
    public boolean deleteFile(File file)
	{
    	return true;
	} 
    //ɾ���ļ���
	public boolean deleteFolder(File folder)
	{
		return true;
	} 
	
	//�����ļ��������򿪣��������Ȳ���
	public void fileOptMenu(final File file)
	{}
	//�õ���ǰĿ¼�ľ���·��
	public String GetCurDirectory()
	{
		return this.currentDirectory.getAbsolutePath();
	}
	//�ƶ��ļ�
	public void moveFile(String source, String destination)
	{
		new File(source).renameTo(new File(destination));   
	}
	//�����ļ�
	public void copyFile(File src, File target)
	{
		InputStream in = null;
		OutputStream out = null;

		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		try
		{
			in = new FileInputStream(src);
			out = new FileOutputStream(target);
			bin = new BufferedInputStream(in);
			bout = new BufferedOutputStream(out);

			byte[] b = new byte[8192];
			int len = bin.read(b);
			while (len != -1)
			{
				bout.write(b, 0, len);
				len = bin.read(b);
			}

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (bin != null)
				{
					bin.close();
				}
				if (bout != null)
				{
					bout.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
