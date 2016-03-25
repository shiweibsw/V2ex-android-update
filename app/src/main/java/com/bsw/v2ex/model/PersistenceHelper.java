package com.bsw.v2ex.model;

import android.content.Context;

import com.bsw.v2ex.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by baishiwei on 2016/3/25.
 */
public class PersistenceHelper {
    public static final String TAG = "PersistenceHelper";

    public static Serializable loadObject(Context context, String file) {
        if (!FileUtils.isExistDataCache(context, file))
            return null;
        //以下为对象的反序列化读取操作
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            return (Serializable) obj;

        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            //反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean saveObject(Context context, Serializable obj, String file) {
        //以下为对象的序列化存储操作
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T extends BaseModel> boolean saveModel(Context context, T obj, String file) {
        return saveObject(context, obj, file);
    }

    public static <T extends BaseModel> boolean saveModelList(Context context, ArrayList<T> objs, String file) {
        return saveObject(context, objs, file);
    }

    public static <T extends BaseModel> T loadModel(Context context, String file) {
        return (T) loadObject(context, file);
    }

    public static <T extends BaseModel> ArrayList<T> loadModelList(Context context, String file) {
        return (ArrayList<T>) loadObject(context, file);
    }
}
