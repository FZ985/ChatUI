package io.im.lib.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * author : JFZ
 * date : 2024/1/5 16:43
 * description :
 */
public class FileUtils {

    public static boolean isFileExistsWithUri(Context pContext, Uri pUri) {
        if (pUri == null) {
            return false;
        } else if (uriStartWithFile(pUri)) {
            String subPath = pUri.toString().substring(7);
            return (new File(subPath)).exists();
        } else if (uriStartWithContent(pUri)) {
            InputStream is = null;

            boolean var4;
            try {
                is = pContext.getContentResolver().openInputStream(pUri);
                boolean var3 = is != null;
                return var3;
            } catch (Exception var14) {
                var4 = false;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException var13) {
                        JLog.e("TAG", "isFileExistsWithUri IOException");
                    }
                }

            }

            return var4;
        } else {
            return pUri.toString().startsWith("/") ? (new File(pUri.toString())).exists() : false;
        }
    }


    public static boolean uriStartWithFile(Uri pUri) {
        return pUri != null && "file".equals(pUri.getScheme()) && pUri.toString().length() > 7;
    }

    public static boolean uriStartWithContent(Uri srcUri) {
        return srcUri != null && "content".equals(srcUri.getScheme());
    }

}
