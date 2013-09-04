package cn.com.karl.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;
import cn.com.karl.music.R;

/**
 * Created by basicer on 13-8-29.
 */
public class MusicUtil {

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static Bitmap mCachedBit = null;

    public static Bitmap getArtwork(Context context, long song_id,//������ý����е�id
                                    long album_id,//������ý�����ר��id
                                    boolean allowdefault//�Ƿ�ʹ��Ĭ��ר��ͼ
    )
    {
        Bitmap bitmap;
        Log.e("MusicUtil","song_id album_id "+song_id+" ,"+album_id);
        if (album_id < 0) {//������ר��idʱ ʹ��ʹ�ò����������ļ��е�ͼ����Ĭ��ר��ͼ
            // This is something that is not in the database, so get the album art directly
            // from the file.
            if (song_id >= 0) {
                bitmap = getArtworkFromFile(context, song_id, -1);
                if (bitmap != null) {
                    return bitmap;
                }
            }
            if (allowdefault) {
                return getDefaultArtwork(context);
            }
            return null;
        }
        /**���´���ʱ����ר��idʱ�ķ�ʽ**/
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(in, null, sBitmapOptions);
                if(bitmap !=null) return bitmap;
                throw new FileNotFoundException();//�׳����� ����catch
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
                // maybe it never existed to begin with.
                bitmap = getArtworkFromFile(context, song_id, album_id);
                if (bitmap != null) {
                    if (bitmap.getConfig() == null) {
                        bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
                        if (bitmap == null && allowdefault) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else if (allowdefault) {
                    bitmap = getDefaultArtwork(context);
                }
                return bitmap;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }

    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
        byte [] art = null;
        String path = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {

        }
        if (bm != null) {
            mCachedBit = bm;
        }
        return bm;
    }

    private static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(
                context.getResources().openRawResource(R.drawable.bg), null, opts);
    }

}
