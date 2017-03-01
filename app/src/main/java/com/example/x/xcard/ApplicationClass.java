package com.example.x.xcard;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.robin.lazy.cache.CacheLoaderConfiguration;
import com.robin.lazy.cache.CacheLoaderManager;
import com.robin.lazy.cache.disk.naming.HashCodeFileNameGenerator;
import com.robin.lazy.cache.memory.MemoryCache;
import com.x.custom.FileSizeUtil;
import com.x.custom.ServicesAPI;
import com.x.custom.XNetUtil;
import com.x.custom.XNotificationCenter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.qos.logback.core.util.FileUtil;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationClass extends Application {

	public static int stateBarHeight = 0;
	public static int navBarHeight = 0;

	public static int SW = 0;
	public static int SH = 0;

	public static Context context;

	public static Retrofit retrofit;

	public static ServicesAPI APPService;

	static public DataCache APPDataCache;

	private List<WeakReference<Activity>> vcArrs = new ArrayList<>();

	/**
	 * 创建全局变量 全局变量一般都比较倾向于创建一个单独的数据类文件，并使用static静态变量
	 * 
	 * 这里使用了在Application中添加数据的方法实现全局变量
	 * 注意在AndroidManifest.xml中的Application节点添加android:name=".MyApplication"属性
	 * 
	 */
	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}





	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();

		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				XNetUtil.APPPrintln("onActivityCreated: "+activity);
				context = activity;
			}

			@Override
			public void onActivityStarted(Activity activity) {

			}

			@Override
			public void onActivityResumed(Activity activity) {
				context = activity;
				WeakReference<Activity> item = new WeakReference<Activity>(activity);
				vcArrs.add(item);
			}

			@Override
			public void onActivityPaused(Activity activity) {

			}

			@Override
			public void onActivityStopped(Activity activity) {

			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

			}

			@Override
			public void onActivityDestroyed(Activity activity) {

			}

		});

		CacheLoaderManager.getInstance().init(this, new HashCodeFileNameGenerator(), 1024 * 1024 * 64, 200, 50);
		APPDataCache = new DataCache();
		initCloudChannel(this);
		initImageLoader();
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		SW = displayMetrics.widthPixels;
		SH = displayMetrics.heightPixels;

		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {

				Request request = chain.request().newBuilder()
						.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36")
						.addHeader("Content-Type","text/plain; charset=utf-8")
						.addHeader("Accept","*/*")
						.addHeader("Accept-Encoding","gzip, deflate, sdch")
						.build();

				XNetUtil.APPPrintln("URL: "+request.url().toString());
				if(request.body() != null)
				{
					XNetUtil.APPPrintln("Body: "+request.body().toString());
				}

				Response response = chain.proceed(request);

				return response;
			}
		}).build();


		retrofit = new Retrofit.Builder()
				.baseUrl(ServicesAPI.APPUrl)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.callFactory(client)
				.build();

		APPService = retrofit.create(ServicesAPI.class);

		XNotificationCenter.getInstance().addObserver("AccountLogout", new XNotificationCenter.OnNoticeListener() {

			@Override
			public void OnNotice(Object obj) {

				for(WeakReference<Activity> item : vcArrs)
				{
					if(item.get() != null)
					{
						if(item.get() instanceof MainActivity)
						{
							continue;
						}
						item.get().finish();
					}
				}

				XNotificationCenter.getInstance().postNotice("ShowAccountLogout",null);

			}
		});

		System.out.println("================init============");
	}

	//初始化网络图片缓存库
	private void initImageLoader() {
		//网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
				showImageForEmptyUri(R.drawable.app_default)
				.cacheInMemory(true).cacheOnDisk(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);

	}


	/**
	 * 初始化云推送通道
	 * @param applicationContext
	 */
	private void initCloudChannel(Context applicationContext) {
		PushServiceFactory.init(applicationContext);

		CloudPushService pushService = PushServiceFactory.getCloudPushService();

		pushService.register(applicationContext, new CommonCallback() {
			@Override
			public void onSuccess(String response) {
				XNetUtil.APPPrintln("init cloudchannel success");
				//APPDataCache.User.registNotice();
			}
			@Override
			public void onFailed(String errorCode, String errorMessage) {
				XNetUtil.APPPrintln("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
			}
		});
	}

}
