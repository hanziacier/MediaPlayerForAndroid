package cn.com.karl.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by leju on 13-8-29.
 */
public class MusicFileFilter implements FilenameFilter{
    private String[] extension = {".mp3",".wma",".wav",".aac"};
    @Override
    public boolean accept(File dir, String name)
    {
        String fileExt = name.toLowerCase();
        for(String ext : this.extension){
            if(fileExt.endsWith(ext)) return true;
        }
        return false;
    }

    public boolean accept(String fileName)
    {
        if(fileName==null || fileName=="") return false;
        String fileExt = fileName.toLowerCase();
        for(String ext : this.extension){
            if(fileExt.endsWith(ext)) return true;
        }
        return false;
    }
    public void setExtensin(String[] extensin){
        this.extension = extensin;
    }



}
