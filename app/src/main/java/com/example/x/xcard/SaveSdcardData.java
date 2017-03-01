package com.example.x.xcard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 保存对象的到sdcard的缓存工具类,功能说明 1.所有对象都保存在mnt/sdcard/cacheData目录下面
 * 2.如果想建立子目录，必须初始化,那么久会在cache目录下建立一个子目录，方便管理 3.如果为了方便不想建立子目录，可以直接用静态方法
 * 4.所有保存的对象必须实现Serializable接口
 * 
 * encoding:utf-8
 * 
 * @author pursuege 2014-4-3 Email:pursuege@gmail.com QQ:751190264
 */
public class SaveSdcardData implements Properties {
	/**
	 * 缓存对象的跟目录
	 */
	private String parentDir = "cacheData";

	/**
	 * 在根目录下建立子目录
	 * 
	 * @param context
	 * @param cacheDir
	 */
	public SaveSdcardData(Context context, String cacheDir) {
		parentDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + app_dir + "/" + cacheDir;
		File dir = new File(parentDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

	}

	/**
	 * 根据一个key保存一个对象，该对象必须全部实现Serializable接口
	 * 
	 * @param key
	 * @param obj
	 */
	public void saveObj(Object obj, String key) {
		File dir = new File(this.parentDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(this.parentDir + "/" + key);
		if (null == file) {
			return;
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
			objOut.flush();
			out.flush();
			objOut.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据一个key得到一个对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getObj(String key) {
		File file = new File(this.parentDir + "/" + key);
		Object obj = null;
		if (!file.exists()) {
			return obj;
		}
		try {
			FileInputStream in = new FileInputStream(file);
			ObjectInputStream inObj = new ObjectInputStream(in);
			obj = inObj.readObject();
			inObj.close();
			in.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 根据key,删除某个对象
	 * 
	 * @param key
	 * @return
	 */
	public boolean deleteObj(String key) {
		File file = new File(this.parentDir + "/" + key);
		if (!file.exists()) {
			return false;
		}
		file.delete();
		return true;
	}

	public static final String replaceImgRules = "[\\.]";

	/**
	 * 把一个图片根据key保存到本地
	 * 
	 * @param bmp
	 * @param key
	 */
	public void saveBitmap(Bitmap bmp, String key) {
		if (bmp == null) {
			return;
		}
		// 为了不在别人手机直接看到下载的图片，就改下名字
		key = key.replaceAll(imgNameRule, "");
		File file = new File(this.parentDir + "/" + key);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 78, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把一个图片根据key保存到本地
	 * 
	 * @param bmp
	 * @param key
	 */
	public void saveBitmapPNG(Bitmap bmp, String key) {
		if (bmp == null) {
			return;
		}

		// 为了不在别人手机直接看到下载的图片，就改下名字
		key = key.replaceAll(imgNameRule, "");
		File file = new File(this.parentDir + "/" + key);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(file);
			bmp.compress(CompressFormat.PNG, 78, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final String imgNameRule = "[:?\\/\\*\\|\\\\\\.]";

	/**
	 * 根据key得到一个图片
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmap(String key) {
		key = key.replaceAll(imgNameRule, "");
		File file = new File(parentDir + "/" + key);
		if (!file.exists()) {
			return null;
		}
		FileInputStream in = null;
		Bitmap bm = null;
		try {
			in = new FileInputStream(file);
			bm = BitmapFactory.decodeStream(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bm;
	}

	public String getFilePath(String thumUri) {
		if (thumUri == null) {
			return "";
		}
		thumUri = thumUri.replaceAll(imgNameRule, "");
		File file = new File(parentDir + "/" + thumUri);
		return file.getPath();
	}

	public String getParentPath() {
		return parentDir;
	}

}
