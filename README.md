# 自定义的音乐播放器 #
* 下面是所有用到的知识点


## 列表主界面 ##

### 音乐标题 ###

### 视音频指示器 ###


## 视频界面 ##
	

### 视频模块（VideoView） ###
* 系统里唯一可以播放视频和音乐的类是MediaPlayer
* videoView只是一个界面， 他继承自 surfaceView 并对 MediaPlayer进行了封装
* MediaPlayer的生命周期

* MediaPlayer的状态转换图也表征了它的生命周期，搞清楚这个图可以帮助我们在使用MediaPlayer时考虑情况更周全，写出的代码也更具健壮性。


>  ![](http://i.imgur.com/Ta1NB9U.gif)

* 这张状态转换图清晰的描述了MediaPlayer的各个状态，也列举了主要的方法的调用时序，每种方法只能在一些特定的状态下使用，如果使用时MediaPlayer的状态不正确则会引发IllegalStateException异常。
 
* **Idle 状态：** 
  	* 当使用new()方法创建一个MediaPlayer对象或者调用了其reset()方法时，该MediaPlayer对象处于idle状态。这两种方法的一个重要差别就是：如果在这个状态下调用了getDuration()等方法（相当于调用时机不正确），通过reset()方法进入idle状态的话会触发OnErrorListener.onError()，并且MediaPlayer会进入Error状态；如果是新创建的MediaPlayer对象，则并不会触发onError(),也不会进入Error状态。
 
* **End 状态：**  
	* 通过release()方法可以进入End状态，只要MediaPlayer对象不再被使用，就应当尽快将其通过release()方法释放掉，以释放相关的软硬件组件资源，这其中有些资源是只有一份的（相当于临界资源）。如果MediaPlayer对象进入了End状态，则不会在进入任何其他状态了。
 
* **Initialized 状态：** 
	* 这个状态比较简单，MediaPlayer调用setDataSource()方法就进入Initialized状态，表示此时要播放的文件已经设置好了。
 
* **Prepared 状态：**  
	* 初始化完成之后还需要通过调用prepare()或prepareAsync()方法，这两个方法一个是同步的一个是异步的，只有进入Prepared状态，才表明MediaPlayer到目前为止都没有错误，可以进行文件播放。
 
* **Preparing 状态：** 
	*  这个状态比较好理解，主要是和prepareAsync()配合，如果异步准备完成，会触发OnPreparedListener.onPrepared()，进而进入Prepared状态。
 
* **Started 状态：**  
	* 显然，MediaPlayer一旦准备好，就可以调用start()方法，这样MediaPlayer就处于Started状态，这表明MediaPlayer正在播放文件过程中。可以使用isPlaying()测试MediaPlayer是否处于了Started状态。如果播放完毕，而又设置了循环播放，则MediaPlayer仍然会处于Started状态，类似的，如果在该状态下MediaPlayer调用了seekTo()或者start()方法均可以让MediaPlayer停留在Started状态。
 
* **Paused 状态：**  
	* Started状态下MediaPlayer调用pause()方法可以暂停MediaPlayer，从而进入Paused状态，MediaPlayer暂停后再次调用start()则可以继续MediaPlayer的播放，转到Started状态，暂停状态时可以调用seekTo()方法，这是不会改变状态的。
 
* **Stop 状态：**  
	* Started或者Paused状态下均可调用stop()停止MediaPlayer，而处于Stop状态的MediaPlayer要想重新播放，需要通过prepareAsync()和prepare()回到先前的Prepared状态重新开始才可以。
 
* **PlaybackCompleted状态：**  
	* 文件正常播放完毕，而又没有设置循环播放的话就进入该状态，并会触发OnCompletionListener的onCompletion()方法。此时可以调用start()方法重新从头播放文件，也可以stop()停止MediaPlayer，或者也可以seekTo()来重新定位播放位置。
 
* **Error状态：**  
	* 如果由于某种原因MediaPlayer出现了错误，会触发OnErrorListener.onError()事件，此时MediaPlayer即进入Error状态，及时捕捉并妥善处理这些错误是很重要的，可以帮助我们及时释放相关的软硬件资源，也可以改善用户体验。通过setOnErrorListener(android.media.MediaPlayer.OnErrorListener)可以设置该监听器。如果MediaPlayer进入了Error状态，可以通过调用reset()来恢复，使得MediaPlayer重新返回到Idle状态。

* <table><td bgcolor=#FFE4B5>
* <font color=##0000CD  size=6 face="黑体" > MediaPlayer的生命周期 </font>
* <font color=#0099ff  size=5 face="黑体" > 为了演示的方便，我们使用几副图片来展示这里面的逻辑关系 </font>

![](http://i.imgur.com/819OOXV.png)

* 1.MediaPlayer进入prepare()状态后，播放相关的资源就在内存里准备好了，这时视频可以start和stop
 
![](http://i.imgur.com/pM2FaA2.png)

* 2.当计入stop() 状态，资源已经被释放，此时必须重新 prepare() 才能恢复播放

![](http://i.imgur.com/JODZtCt.png)

* 3. prepare() 方法是在当前线程执行的，prepareAsync()方法是在子线程中执行的（资源的加载是特别的耗时的，应该放在子线程中进行调用，每次调用麻烦，这时系统为我们准备了prepareAsync()这个方法，系统的底层会默认的为我们开启一个子线程）

![](http://i.imgur.com/tiAPlKk.png)

* 4.MediaPlayer的Stop 、 reset 、 release方法的不同
	* Stop() 之后MediaPlayer的路径还在，目前处于stoped() 状态，可以再次直接调用start直接播放
	* reset() 之后MediaPlayer的路径信息不再存在，MediaPlayer目前处于Idle状态，这时可以重新设置新的路径，开始播放，相当于重新初始化了或者创建了一个MediaPlayer
	* release() 之后当前的MediaPlayer资源被释放，就不可以使用了

* 这几张图是对谷歌提供的mediaPlayer生命周期的简化
*  
* 
* 
</td></table>





### 系统电量 ###

### 自定义的进度条 ###

### 屏幕上下滑动修改音量和亮度 ###

### 全屏（单击、双击、长按） ###

### 相应文件管理器调用 ###

### 集成第三方的视频解码库（Vitamio） ###



## 音乐界面 ##


### 自定义界面通知 ###

### 示波器动画（帧动画） ###

### 自定义歌词控件 ###

### 后台服务处理界面 ###

### 自定义通知界面 ###




 


# 项目开始 #


* 在最初开始项目之前一定要注意，有机会把包创建了就行动，然自己主宰开发的规则
* 这样在多人开发的时候可以建立一套统一的规范，方便后期代码的维护

 
### 项目的框架 ###

* 一个代码开始的时候，不是首先从SplashActivity开始的，而是首先通过从定义BaseActivity 开始着手的，这就是框架

* 在面试的时候我们会经常的被问到，你们的项目中都用到那些的框架
* 其实我们的项目里边的框架除了反射，注解，配置文件（builderConfiger）还有BaseActivity，BaseFragment这些；
* 其实只要是我们对代码进行约定的都是一个项目的框架。
-'-'
### 操作解耦 ###
* 在使用的过程中,广播注册和设置adapter，以及setListener都是为了解耦





## 在项目中生成全局文件的方法 ##

* 在文件buildConfig中添加如下代码： 

		buildTypes {   
	        debug {
	            //调试模式下打开log日志控制台输出
	            buildConfigField "boolean", "LOG_DEBUG", "true"
	            debuggable true
	            minifyEnabled false
				//生成对应的文件
	            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-project.txt'
	            signingConfig signingConfigs.debug
	        }
	        release {
	            //正式上线时，关闭log打印
	            buildConfigField "boolean", "LOG_DEBUG", "false"
	            minifyEnabled false
	            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
	        }
	    }


* 相应的配置会出现在编译器给我们生成buildConfig.java文件中

* 这个是我们自定义全局文件变量的规则：   buildConfigField "boolean", "LOG_DEBUG", "true"，这样定义好后我们就可以很轻松的更改一处代码就可以将全局的代码全部替换了，方便代码维护


* 值得注意的是buildConfig.java文件是系统默认生成的



# 代码步骤 #

## 1. 基础框架搭建 ##

### 1.1 添加项目所需要的依赖 ###
### 1.2 增加utils工具包，从以前的工程中直接的拷贝 ###
### 1.3 创建自己的BaseActivity,增加自己的规则，比如InitView,initListener,initAdapter,initData等等  ###
### 1.4 将子类公共的部分直接的放到BaseActivity中进行处理，比如放回button的操作等，节约代码量 ###
* 这里涉及到一个手动创建资源id的方法
 
> * 创建

![](http://i.imgur.com/2vvawqp.png)

![](http://i.imgur.com/BX4kpZG.png)

> * 引用

![](http://i.imgur.com/gdXpz8Z.png)





## Splash界面延时跳转 ##
* 实现的方案有以下的几种
* 1.计时器Timer
* 2.子线程延时
* 3.使用handler.postDelay()方法

* 这里注意的是handler是android系统自带的使用时更加方便管理，尽量多的使用



## BaseActivity有两个作用 ##
* 约定代码结构，统一编程风格
* 提供子类通用的代码，节约代码量




## 快捷键实现构造方法 ##

Alt+insert  快速的实现构造方法



## 在使用activity的时候会出现一定的兼容性的问题 ##

baseActivity 是继承 V4 包的 FragmentActivity 和还是 V7 包的 AppCompatActivity

#### FragmentActivity和Activity的区别  ####

>  * Fragment是Android 3.0以后的东西，为了在低版本中使用Fragment就要用到android-support-v4.jar兼容包,而FragmentActivity就是这个兼容包里面的，它提供了操作Fragment的一些方法，其功能跟3.0及以后的版本的Activity的功能一样。
下面是API中的原话：
FragmentActivity is a special activity provided in the Support Library to handle fragments on system versions older than API level 11. If the lowest system version you support is API level 11 or higher, then you can use a regular Activity.

**主要区别如下：**

- 1、FragmentActivity 继承自Activity，用来解决Android 3.0之前无法使用Fragment的问题，所以在使用的时候需要导入android-support-v4.jar兼容包，同时继承 FragmentActivity，这样在Activity中就能嵌入Fragment来实现你想要的布局效果。 
- 2、当然Android 3.0之后你就可以直接继承自Activity，并且在其中嵌入使用Fragment。 
- 3、获得FragmentManager的方式也不同 
- Android 3.0以下：getSupportFragmentManager() 
- Android 3.0以上：getFragmentManager() 



## 标题选中，这里有了一种更好的解决的方案 （厉害了我的哥）##

* 当有多个标签选中和不选中状态改变的时候，我们以前的做法是直接的代码设置文本的颜色和文本的字体大小
* 这里的新的解决的方案就是通过选择器的的形式进行处理


* **方法抽取**
* 现将想要抽取的代码中的所有的写死的变量，替换成变量
* 然后直接抽取

* 1.替换之前代码： 

 			tvVideo.setSelected(position == 0);
            if (position == 0) {
                ViewCompat.animate(tvVideo).scaleX(1.1f).scaleY(1.1f).setDuration(300).setInterpolator(new AnticipateOvershootInterpolator()).start();
            }else {
                ViewCompat.animate(tvVideo).scaleX(0.9f).scaleY(0.9f).setDuration(300).setInterpolator(new AnticipateOvershootInterpolator()).start();

            }


* 2.替换其中的变量tvMusic 和 0

		 TextView tab= tvVideo;
         int tabPosition=0;

* 3.替换完成后，Alt+ shift+ M 方法抽取，获得方法upDataTab（）{}


		private void upDataTab(int position, TextView tab, int tabPosition) {
	        //变色
	        tab.setSelected(position == tabPosition);
	
	        //缩放
	        //写代码的时候注意，尽量的提取
	        // 动画设置，没有必要进行版本的判断，谷歌提供的兼容类
	        if (position == 0) {
	            ViewCompat.animate(tvVideo).scaleX(1.1f).scaleY(1.1f).setDuration(300).setInterpolator(new AnticipateOvershootInterpolator()).start();
	        }else {
	            ViewCompat.animate(tvVideo).scaleX(0.9f).scaleY(0.9f).setDuration(300).setInterpolator(new AnticipateOvershootInterpolator()).start();
	
	        }
        }





## 动画部分这个需要看，仔细的总结 ##
- 动画的分类
- 动画的差值器
- 动画的版本兼容
- 不同版本的动画
- 值动画的使用



## 指示器操作 ##

### requestLayout 和 invalidate的区别 ###
* 1. requestLayout要求重新计算大小并刷新界面
* 2. invalidate只要求刷新界面

### 计算知识器的位置 ###
* 指示器的位置算法
	* 最终要使用的位置 = 其实位置 + 偏移位置
	* 其实位置 = position * 指示器的宽
	* 偏移位置 = 页面展开百分比 * 指示器的宽


## 手机多媒体信息的获取 ##
* 手机中的多媒体信息都是存放在mediaStore中的，所有的播放器等的列表都是同过一个多媒体的 MediaProvider 来获取信息的
* 其实在系统的 MediaProvider 文件中会找到Android系统扫描到的多媒体文件，路径地址

找到文件管理器：
	
	data --> data --> com.android.provider.media --> databases
> 
	--> external.db 这个是内存和sd卡公共的 
    --> internal.db 这个是android 系统使用的 

* 其中系统存储的部分是我们没有权限操作的这里我们不关心，直接的跳过



## Android Studio的一个使用技巧 --- 快速查找方法 ##

	ctrl + F12可以快速的定位当前类的方法



### android studio 看不到系统类源码解决 ###

* android studio的源码是自动下载关联的，如果看不到，就是没有下载，或者是官方没有提供，只需要在buildGradle里面修改targeVersion就可以，或者也可以在sdk中系在对应的版本


## 熟悉CursorAdapter的使用 ##
* CursorAdapter是谷歌提供的一个在listviewAdapter基础上的封装，他会将查询出来的数据放在list中进行展示，我们只需要实现其中的几个方法就可以的


* **1.需要实现下面这几个方法** 

   * 1.两个构造
 
			public cursorListAdapter(Context context, Cursor c) {
		        super(context, c);
		    }
		
		    public cursorListAdapter(Context context, Cursor c, boolean autoRequery) {
		        super(context, c, autoRequery);
		    }


	* 2.方法 newView() 创建view 和  方法 bindView() 绑定数据


			/**
		     * 创建新的view 和 viewHolder
		     *
		     * @param context
		     * @param cursor
		     * @param parent
		     * @return
		     */
		    @Override
		    public View newView(Context context, Cursor cursor, ViewGroup parent) {
		        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
		        ViewHolder holder = new ViewHolder();
		        holder.tv_tittle = (TextView) view.findViewById(R.id.tv_video_list_tittle);
		        holder.tv_size = (TextView) view.findViewById(R.id.tv_video_list_videosize);
		        holder.tv_duration = (TextView) view.findViewById(R.id.tv_video_list_videoDuration);
		
		
		        view.setTag(holder);   //想要复用成功，必须设置tag
		        return view;
		    }
		
		    /**
		     * 填充条目内容    此时 view不可能为空  因为不为空则复用，为空就调用newView创建
		     *
		     * @param view
		     * @param context
		     * @param cursor  已经移动到指定位置的cursor 我们只需要解析对应的内容就可以了
		     */
		    @Override
		    public void bindView(View view, Context context, Cursor cursor) {
		        ViewHolder holder = (ViewHolder) view.getTag();    //这个不可能为空
		        holder.tv_tittle.setText(cursor.getString(cursor.getColumnIndex(Media.TITLE)));
		        holder.tv_size.setText(cursor.getString(cursor.getColumnIndex(Media.SIZE)));
		        holder.tv_duration.setText(cursor.getString(cursor.getColumnIndex(Media.DURATION)));
		
		    }
		

			//缓存服用holder类
		    private class ViewHolder {
		        TextView tv_tittle, tv_duration, tv_size;
		    }


* **2. adater调用**


		//获取手机里的数据
        //1. 获取内容观察者
        ContentResolver contentResolver = getActivity().getContentResolver();
        //2. 进行查询    Cursor query(Uri, String[], String, String[], String)
        //查询的位置， 查询的关键字， 查询条件占位符 ， 查询条件填充占位符的数组 ，排序
        Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.DATA, Media.SIZE, Media.DURATION, Media.TITLE}, null, null, null);
        CursorUtils.printCursor(VideoListFragment.class,cursor);
        cursorListAdapter.swapCursor(cursor);    //相当于  notifyDataSetInvalidated();  替换原有的cursor   其实底层调用的还是   notifyDataSetInvalidated();这个方法



* **3. 报错解决**
* 这时候会出现报错

		java.lang.IllegalArgumentException: column '_id' does not exist


* 原因
* 查看 CursorAdapter 的源码，会发现有这样的解释

			
		/**
		 * Adapter that exposes data from a {@link android.database.Cursor Cursor} to a
		 * {@link android.widget.ListView ListView} widget.
		 * <p>
		 * The Cursor must include a column named "_id" or this class will not work.
		 * Additionally, using {@link android.database.MergeCursor} with this class will
		 * not work if the merged Cursors have overlapping values in their "_id"
		 * columns.
		 */

* 大体是说在数据库查询的时候，必须包含The Cursor must include a column named "_id" or this class will not work.  "_id"的指定列,也就是我们常常会说的主列  

* 知道了这些剩下的就简单了

	* 1.在这个的基础上添加一个主列  "_id"
	 
			/2. 进行查询    Cursor query(Uri, String[], String, String[], String)
		        //查询的位置， 查询的关键字， 查询条件占位符 ， 查询条件填充占位符的数组 ，排序
		        Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.DATA, Media.SIZE, Media.DURATION, Media.TITLE}, null, null, null);


	* 2.成为这样的 


			//2. 进行查询    Cursor query(Uri, String[], String, String[], String)
	        //查询的位置， 查询的关键字， 查询条件占位符 ， 查询条件填充占位符的数组 ，排序
	        Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID,Media.DATA, Media.SIZE, Media.DURATION, Media.TITLE}, null, null, null);

		
* 其实巧了，在我们的这一张表里面正好有 "_id"	这个列
* 如果没有的解决就是使用别名 比如 "tvName as _id"  ,随便指定一个主列名
  
	* 3.没有这个列的情况是这样的
	 
			//2. 进行查询    Cursor query(Uri, String[], String, String[], String)
	        //查询的位置， 查询的关键字， 查询条件占位符 ， 查询条件填充占位符的数组 ，排序
	        Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{"tvName as _id" ,Media.DATA, Media.SIZE, Media.DURATION, Media.TITLE}, null, null, null);

* 好的运行一下，问题完美解决	 

展示一下现在的样子吧

![](http://i.imgur.com/mZvR631.png) 


## 数据查询代码优化，查询数据库是一个耗时操作，应该开启子线程完成，但是更爽的是系统为我们提供了一个类AsyncQueryHandler，完成这个操作 ##

* 优化前这部分代码在主线程中调用


  		//获取手机里的数据
        //1. 获取内容观察者
        ContentResolver contentResolver = getActivity().getContentResolver();
        //2. 进行查询    Cursor query(Uri, String[], String, String[], String)
        //查询的位置， 查询的关键字， 查询条件占位符 ， 查询条件填充占位符的数组 ，排序
        Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID,Media.DATA, Media.SIZE, Media.DURATION, Media.TITLE}, null, null, null);
        CursorUtils.printCursor(VideoListFragment.class,cursor);
        cursorListAdapter.swapCursor(cursor);    //相当于  notifyDataSetInvalidated();  替换原有的cursor   其实底层调用的还是   notifyDataSetInvalidated();这个方法
     


* 我们自己开启一个子线程，有点没必要，可以使用已经封装好的

	 ContentResolver contentResolver = getActivity().getContentResolver();
 
        /**
         * 本来需要自己创建一个子线程完成这个操作，但是扩展性更好的是使用系统封装好的 AsyncQueryHandler 来完成这个操作
         * 这个方法在完成查询的时候在主线程中调用
         */
        AsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(contentResolver);

        
        asyncQueryHandler.startQuery( VIDEO_QUERY,cursorListAdapter ,Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.SIZE, Media.DURATION, Media.TITLE}, null, null, null);


* 参数解释：

		int token,   相当于handler的what  用于区分事件的类型
        Object cookie,  相当于handler的Object ，用于传递 需要刷新的控件的
        android.net.Uri uri,      下面的就是query的那一套了，直接拷贝
        String[] projection,
        String   selection,
        String[] selectionArgs,
        String   orderBy


* MyAsyncQueryHandler中的相关代码



		/**
		 *  * 作者：Zbc on 2016/12/7 00:11
		 *  * 邮箱：mappstore@163.com
		 * 功能描述：
		 *   这个类可以实现数据库的异步增 ， 删 ， 改 ， 查 ，使用的时候只需要重写相应的方法就好了，执行的过程都是在主线程中
		 *   使用的最后不要忘记调用startQuery()方法开启线程操作
		 *   使用的方法类比handler
		 */
		public class MyAsyncQueryHandler extends AsyncQueryHandler {
		
		
		    public MyAsyncQueryHandler(ContentResolver resolver) {
		        super(resolver);
		    }
		
		    /**
		     * 查询完成后这个方法会在主线程回调
		     * @param token    相当于handler中的what
		     * @param cookie   相当于handler中的Object，传递刷新的控件
		     * @param cursor    当前一条记录的cursor
		     */
		    @Override
		        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		            super.onQueryComplete(token, cookie, cursor);
		        cursorListAdapter mAdapter = (cursorListAdapter) cookie;  //控件还原
		        CursorUtils.printCursor(MyAsyncQueryHandler.class,cursor);
		        mAdapter.swapCursor(cursor);    //相当于  notifyDataSetInvalidated();  替换原有的cursor   其实底层调用的还是   notifyDataSetInvalidated();这个方法
		        }
		}


*  AsyncQueryHandler底层会开启一个子线程完成数据库的增删改查的操作，并同过handler的形式进行数据的传递和界面的刷新


## 将一个类快速的转换匿名内部类 ##

![](http://i.imgur.com/R9jLvPo.png)


![](http://i.imgur.com/jpMSjUr.png)

* 好了，到这里就打工告成了


## android Studio 快速生成get set 构造等方法的快捷键 ##


   Alt + insert


## 序列化接口用到了，而且使用的时候很方便，多看一下 ##

		public  class VideoListItem implements Serializable





## 使用videoView播放视频 ##




* 防止MediaPlayer异步资源加载报错，应该将播放视频放在监听里面进行

正确的做法是：

 		String videoPath = videoListItem.getPath();
	    vdVideoview.setVideoPath(videoPath);    //设置播放路径
	       
		/**
         * 设置MediaPlayer的异步加载监听
         * 这个方法在资源异步加载完成后调用
         * 在给videoView设置完路径后直接的调用vdVideoview.start(); 可能会在资源异步加载还没完成的时候就调用播放start了
         */
         vdVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vdVideoview.start();          //开启播放
            }
        });
		 



 
* 如果快速开发只是实现共能还可以使用系统自带的控制器


		//公司要求快速开发的时候，可以使用系统自带的播放控制进度条，虽然丑但是还不错
        //vdVideoview.setMediaController(new MediaController(this));



## VideoPlayer界面布局 ##

* 布局复杂的时候尽量的使用include

	
	    <!--顶部栏-->
	    <include layout="@layout/activity_video_player_top"/>
	
	    <!--底部栏-->
	    <include layout="@layout/activity_video_player_bottom"/>

 

## 自定义进度条seekBar ##

* 总体感觉这个还是蛮有新意的

* 首先随便找到系统的一个精度条样式 
 
		style="@android:style/Widget.SeekBar"
 查看他的源码


	    <style name="Widget.SeekBar">
	        <item name="indeterminateOnly">false</item>    
	        <item name="progressDrawable">@drawable/progress_horizontal</item>
	        <item name="indeterminateDrawable">@drawable/progress_horizontal</item>
	        <item name="minHeight">20dip</item>
	        <item name="maxHeight">20dip</item>
	        <item name="thumb">@drawable/seek_thumb</item>
	        <item name="thumbOffset">8dip</item>
	        <item name="focusable">true</item>
	        <item name="mirrorForRtl">true</item>
	    </style>


* indeterminateOnly: 表示进度值是否确定，true表示不确定，false表示确定   通俗的说就是进度条是不是可以拉
* progressDrawable: 进度条的背景，第一进度，第二进度样式
* indeterminateDrawable： 定义不确定模式是否可拉
* maxHeight： 背景色最大高度限制
* minHeight： 背景色最小高度限制
* thumb    :  按下是的图形，就是拖动的小点
* thumbOffset： 小点的偏移量
* focusable   ： 是否可以获取焦点
* mirrorForRtl： 定义了相关画板如果需要反映在RTL模式  



* 现在我们就参考系统的样式，设置我们的进度条

		<!--自定义seekBar的样式，最简单的方式就是查看系统的源码，然后修改对应的属性-->
	    <!--通过源码可以发现
	    android:thumb="@mipmap/video_progress_thumb"  //拇指按下的图形
	    android:thumbOffset="0dp"   图形的偏移量
	    参考系统样式
	    android:maxHeight="6dp" 可以限定进度条背景的高度
	     style="@android:style/Widget.SeekBar"-->
	    <SeekBar
	        style="@android:style/Widget.SeekBar"
	        android:layout_width="0dp"
	        android:layout_weight="1"
	        android:maxHeight="6dp"
        	android:minHeight="6dp"
	        android:thumb="@mipmap/video_progress_thumb"
	        android:thumbOffset="0dp"
	        android:progress="40"
	        android:progressDrawable="@drawable/video_progress_seekbar"
	        android:layout_height="wrap_content" />


* 进度条背景，进度样式 video_progress_seekbar.xml文件的代码

直接的拷贝系统的源代码修改就好了：

		<?xml version="1.0" encoding="utf-8"?>

	 
		
		<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
		
		    <item android:id="@android:id/background" android:drawable="@drawable/video_seekbar_bg">
		        <!--<shape>
		            <corners android:radius="5dip" />
		            <gradient
		                android:startColor="#ff9d9e9d"
		                android:centerColor="#ff5a5d5a"
		                android:centerY="0.75"
		                android:endColor="#ff747674"
		                android:angle="270"
		                />
		        </shape>-->
		    </item>
		
		    <item android:id="@android:id/secondaryProgress"   >
		        <!--<clip>
		            <shape>
		                <corners android:radius="5dip" />
		                <gradient
		                    android:startColor="#80ffd300"
		                    android:centerColor="#80ffb600"
		                    android:centerY="0.75"
		                    android:endColor="#a0ffcb00"
		                    android:angle="270"
		                    />
		            </shape>
		        </clip>-->
		        <shape >
		            <corners android:radius="5dp"/>
		            <solid android:color="#2595FD" >
		
		            </solid>
		        </shape>
		
		    </item>
		
		    <item android:id="@android:id/progress"  >
		       <!-- <clip>
		            <shape>
		                <corners android:radius="5dip" />
		                <gradient
		                    android:startColor="#ffffd300"
		                    android:centerColor="#ffffb600"
		                    android:centerY="0.75"
		                    android:endColor="#ffffcb00"
		                    android:angle="270"
		                    />
		            </shape>
		        </clip>-->
		        <shape >
		            <corners android:radius="5dp"/>
		            <solid android:color="#2595FD" >
		
		            </solid>
		        </shape>
		    </item>
		
		</layer-list>



* 值得注意的是：item中是直接可以设置 drawable图形的,以后这种技能要学会了-
 

		<item android:id="@android:id/secondaryProgress"  android:drawable="@drawable/video_seekbar_progress" />


* 但是在使用的时候发现只能够实用drawable的出图片和.9图片的情况不然会出现季度失效的情况，暂时不知道为什么
* 出现背景很粗的问题尝试添加


		android:maxHeight="6dp"
       	android:minHeight="6dp"

两个属性

*  如果出现seekBar在不同品牌手机适配问题的时候，可以尝试美工且多套图来完成适配






## 界面的暂停和播放 ##
* 注意的是在工作的时候ui界面的更新和功能逻辑的更新一定是分开的，使用不同的方法，方便维护


* 按钮点击的时候更新界面和播放的状态

		case R.id.iv_video_pause:  //播放暂停按钮
                upDataPause();
                upDataPauseBtn();
                break;


* 播放控制的逻辑代码 upDataPause()

	
	    /**
	     * 暂停更新的方法
	     * 作用： 切换暂停的状态和更新暂停按钮的图片
	     */
	    private void upDataPause() {
	        if (vdVideoview.isPlaying()) {
	            vdVideoview.pause();   //调用videoView的暂停方法
	        } else {
	            vdVideoview.start();   //调用videoView的播放方法
	        }

* 播放控制的界面刷新代码 upDataPauseBtn();

		 /**
	     * 暂停播放界面的更新
	     * 工作的时候界面的逻辑代码和功能代码是分开的，这样方便后期的维护
	     * 同时供代码和界面代码单独写还可以提高两者调用的灵活性
	     */
	    private void upDataPauseBtn() {
	        if (vdVideoview.isPlaying()) {
	            ivVideoPause.setImageResource(R.drawable.btn_video_pause);
	        } else {
	            ivVideoPause.setImageResource(R.drawable.btn_video_play);
	        }
	
	    }



* 在开始播放的时候初始化，按钮的状态


		/**
         * 设置MediaPlayer的异步加载监听
         * 这个方法在资源异步加载完成后调用
         * 在给videoView设置完路径后直接的调用vdVideoview.start(); 可能会在资源异步加载还没完成的时候就调用播放start了
         */
        vdVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //资源加载完成，开启播放
                vdVideoview.start();
                //开始播放的时候更新按钮的播放状态
                upDataPauseBtn();
            }
        });


* **这里也体现出一个设计的思想：解耦，将业务的逻辑和UI分离开，这样代码用起来十分的爽**



## 系统电量监听 ##

* 实现匿名对象快速转换成成员对象的快捷键  ctrl + alt + V


* 系统电量的变换是通过广播的形式发送的

* 1.注册广播


		// 注册广播获取系统电量信息
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        broadcastReceiver = new VideoBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
	

* 2.广播注销（有注册就会有注销）

		@Override
	    protected void onDestroy() {
	        super.onDestroy();
	        // 广播有注册就得有反注册
	        unregisterReceiver(broadcastReceiver);
	    }

* 3.监听回调

		 /**
	     * 接收电池变化信息的广播
	     */
	    private class VideoBroadcastReceiver extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	             //获取系统电量
	            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
	            updataBatteryPic(level);
	        }
	    }


* 4.UI界面更新

		 /**
	     * 时时更新电量显示图片
	     * @param level
	     */
	    private void updataBatteryPic(int level) {
	        LogUtil.e("当前系统电量"+level);
	        if(level<10){
	            ivVideoElectric.setImageResource(R.mipmap.ic_battery_0);
	        }else if(level<20){
	            ivVideoElectric.setImageResource(R.mipmap.ic_battery_10);
	        }else if(level<40){
	            ivVideoElectric.setImageResource(R.mipmap.ic_battery_20);
	        }else if(level<60){
	            ivVideoElectric.setImageResource(R.mipmap.ic_battery_40);
	        }else  if(level<80){
	            ivVideoElectric.setImageResource(R.mipmap.ic_battery_60);
	        }else if(level<100){
	            ivVideoElectric.setImageResource(R.mipmap.ic_battery_80);
	        }else {
	            ivVideoElectric.setImageResource(R.mipmap.ic_battery_100);
	        }
		}



## 音乐音量调节 ##

* 系统的媒体音量和系统音量是由AudioManger来控制的

> AudioManger获取 AudioManager myAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


* 界面音量初始化

		//初始化音量进度
        myAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //获取系统的最大音量
        int maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//AudioManager这里面封装了来电铃声，通话，蓝牙，系统，闹钟，音乐等各种情景的铃声
        //当前音量获取
        int currentVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //根据系统的音量界面初始化当前的音量值
        sbVideoCurrentVolume.setMax(maxVolume);
        sbVideoCurrentVolume.setProgress(currentVolume);


* 获取当前进度条的变化，并设置给系统

		
	    /**
	     *
	     * 当进度条改变的时候调用
	     */
	    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            if(!fromUser){return;}  //只处理来自用户的操作
	            /**
	             * 根据进度条修改系统的音量
	             *  参数1： 参数的类型（媒体音量还是通话等）
	             *  参数2：目标值
	             *  参数3： 是不是显示系统自带的Dialog 0：不显示，1：显示
	             */
	            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
	        }
		}



* 将音量设置给系统的代码要注意

		 myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);

*  public void setStreamVolume(int streamType, int index, int flags) {}
* setStreamVolume()作用根据进度条修改系统的音量
* 参数解释



 streamType  | 参数的类型（媒体音量还是通话等）
:------------:|---------|
index		 |目标音量值，不同的手机的音量等级不一样，应该通过设置seekbar表示的最大值限定
flags   	 | 是不是显示系统自带的Dialog 0：不显示，1：显示
	    


* 像这样繁琐的方法完全可以进行二次封装
 
		 /**
	     * 当前媒体音量是设置
	     * @param flag_showSystemDialog  是不是显示系统自带的音量进度条
	     * @param volume  目标设置音量值
	     */
	    private void setCurrentVolute( int volume ,int flag_showSystemDialog) {
	        myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,flag_showSystemDialog);
	        //同步更新进度条
	        sbVideoCurrentVolume.setProgress(volume);
	    }
	
	    /**
	     * 获取当前的媒体音量
	     * @return
	     */
	    private int getCurrentVolume() {
	        return myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    }  


## 手指上下滑动修改音量和亮度 ##

* **音量修改**

* 获取和修改音量需使用AudioManger，操作声音的时候需要说明具体的声音类型（电话、音乐、闹铃等）
* 手指滑动时，修改系统音量的算法
	* 最终使用的音量 = 起始音量 + 偏移音量
	* 偏移音量 = 最大音量 * 划过屏幕的百分比
	* 划过屏幕的百分比 = 手指划过屏幕的距离 / 屏幕的高度
	* 手指划过屏幕的距离 = 手指当前的位置 - 是指落下的位置

* 在滑动监听中对手势的移动进行判断

		/**
	     * 屏幕触摸事件的监听
	     * 最终使用的音量 = 起始音量 + 偏移音量
	     * 偏移音量 = 最大音量 * 划过屏幕的百分比
	     * 划过屏幕的百分比 = 手指划过屏幕的距离 / 屏幕的高度
	     * 手指划过屏幕的距离 = 手指当前的位置 - 是指落下的位置
	     *
	     * @param event
	     * @return true 事件拦截自己处理
	     */
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                mStartY = event.getY();
	                mStartX = event.getX();
	                //系统当前音量获取
	                moveDownVolume = getCurrentVolume();
	                //获取起始透明度
	                startAlpha = ViewCompat.getAlpha(viewVideoAlphaCover);
	                break;
	            case MotionEvent.ACTION_MOVE:
	                float mCurrentY = event.getY();
	                //滑动的距离
	                float offsetY = mStartY - mCurrentY;
	
	                //手势滑动的百分比
	                float movePresent = offsetY / mHalfScreenH;
	                if (mStartX < mHalfScreenW) {   //左侧屏幕滑动
	                    //根据手指移动百分比，改变屏幕亮度
	                    upDataMoveAlpha(movePresent);
	                } else {
	                    //根据手指移动百分比，改变媒体音量
	                    upDataMoveVolume(movePresent);
	                }
	                break;
	
	        }
	
	        return true;
	    }

* 获取屏幕的宽高发现每次调用浪费资源，提取了全局变量

		 //获取屏幕的高度的一半
	    private int mHalfScreenH;
	    //获取屏幕的宽度的一半
	    private int mHalfScreenW;

* 初始化放在了initData()方法中

	 	//获取屏幕的高度
        mHalfScreenH = getWindowManager().getDefaultDisplay().getHeight() / 2;
        //获取屏幕的宽度
        mHalfScreenW = getWindowManager().getDefaultDisplay().getWidth() / 2;

* 根据手指滑动改变媒体音量，upDataMoveVolume() 方法

		/**
	     * 根据手指滑动改变媒体音量
	     *
	     * @param movePresent
	     */
	    private void upDataMoveVolume(float movePresent) {
	        //获取系统的最大音量
	        int maxVolume = sbVideoCurrentVolume.getMax();
	        //音量的偏移值
	        int offsetVolume = (int) (maxVolume * movePresent);
	        //最终的音量
	        int finalVolume = moveDownVolume + offsetVolume;
	        //将音量设置给系统
	        setCurrentVolume(finalVolume, 0);
	    }
	
* upDataMoveAlpha() 根据手指滑动改变屏幕亮度，发现系统没有为我们处理下限和上限我们自己增加了判断if (finalApha >= 0 && finalApha <= 0.8)   0.8是为了防止黑屏

	    /**
	     * 根据手指滑动改变屏幕亮度
	     * 这里折中，使用蒙版的形式改变亮度
	     * 其实工作中也是，一种方式实现有难度，可以采用另一种方式
	     *
	     * @param movePresent
	     */
	    private void upDataMoveAlpha(float movePresent) {
	        float finalApha = startAlpha - movePresent * 1;
	        //对亮度值进行最后的限定
	        if (finalApha >= 0 && finalApha <= 0.8) {   //这里系统没有为我们的透明度做上下限的约束，需要我们自己约束
	            //将透明度设置给控件
	            ViewCompat.setAlpha(viewVideoAlphaCover, finalApha);
	        }
	    }



* 初始化的时候发现黑屏增加，透明度初始化的操作

		//将透明度设置给控件
        ViewCompat.setAlpha(viewVideoAlphaCover, 0);




## 总时长和进度格式化处理 ##

* 时间长度的获取和当前播放时长的获取，必须在异步资源加载完成的调用中，不然视频加载失败等显示是没有意义的

	 	/**
         * 设置MediaPlayer的异步加载监听
         * 这个方法在资源异步加载完成后调用
         * 在给videoView设置完路径后直接的调用vdVideoview.start(); 可能会在资源异步加载还没完成的时候就调用播放start了
         */
        vdVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            	.........
				 
                //初始化当前时间
                int currentPosition = vdVideoview.getCurrentPosition();
                tvVideoCurrentTime.setText(DateUtils.videoTimeFormat(currentPosition));
                //进度条进度值设置
                sbVideoCurrent.setProgress(currentPosition);
                upDataPlayPosition();

            }


* 更新当前播放时间和指示条进度UI的方法 upDataPlayPosition(),通过handler实现自动刷新
* handler使用不同的what值

			/**
		     * 视频播放进度更新
		     */
		    private void upDataPlayPosition() {
		        //获取实时播放进度并更新
		        int currentPosition = vdVideoview.getCurrentPosition();
		        tvVideoCurrentTime.setText(DateUtils.videoTimeFormat(currentPosition));
		        //进度条进度值设置
		        sbVideoCurrent.setProgress(currentPosition);
		        //通过延时消息循环更新
		        handler.sendEmptyMessageDelayed(WHAT_UPDATA_VIEDEOPOSITION,500);
		    }



* 拖动指示条改变进度，同一个seekBar监听器是可以设置给多个seekBar的，通过ID()区别

		 	//音量进度条拖动监听,seekBar是可以公用一个监听器的
	        MyOnSeekBarChangeListener seekBarChangeListener = new MyOnSeekBarChangeListener();
	        sbVideoCurrentVolume.setOnSeekBarChangeListener(seekBarChangeListener);
	        sbVideoCurrent.setOnSeekBarChangeListener(seekBarChangeListener);


* 监听其中的代码


		/**
	     * 当进度条改变的时候调用
	     * 媒体音量进度和视频播放进度公用一套
	     * seekBar的监听器是可以公用一套的，通过seekBar的id区别
	     */
	    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            if (!fromUser) {
	                return;
	            }  //只处理来自用户的操作
	            switch (seekBar.getId()){
	                case R.id.sb_video_current_volume:
	                    /**
	                     * 根据进度条修改系统的音量
	                     *  参数1： 参数的类型（媒体音量还是通话等）
	                     *  参数2：目标值
	                     *  参数3： 是不是显示系统自带的Dialog 0：不显示，1：显示
	                     */
	                    setCurrentVolume(progress, 0);
	                    break;
	                case R.id.sb_video_current:
	                    //进度条进度值设置
	                    vdVideoview.seekTo(progress);
	                    break;
	            }
	
	        }


* 通过  
	                 
		   vdVideoview.seekTo(progress);

进度跳转




## 播放进度小细节调整 ##

* 播放完成handler消息移除
* 播放完成暂停按钮改变成播放按钮
* 暂停中handler消息移除
* 开始播放handler重新开始消息



* 添加OnCompletionListener()播放完成的回调方法

 		//视频播放结束监听
        vdVideoview.setOnCompletionListener(new MyOnCompletionListener());

* 在回调方法中完成按钮同步和消息移除

		/**
	     * 更能视频播放结束的时候此方法被调用
	     */
	    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
	        @Override
	        public void onCompletion(MediaPlayer mp) {
	            //当视频播放结束的时候次方法被调用
	            //解决部分视频播放完成视频时间没有同步完成的bug
	            upDataVideoTimeUI(vdVideoview.getDuration());
	            //视频播放完成，停止handler消息
	            handler.removeMessages(WHAT_UPDATA_VIEDEO_POSITION);
	
	            //同步视频播放按钮
	            upDataPauseStatus();
	             
	        }
	    }



* 暂停和播放切换中的代码


		/**
	     * 暂停更新的方法
	     * 作用： 切换暂停的状态和更新暂停按钮的图片
	     */
	    private void upDataPause() {
	        if (vdVideoview.isPlaying()) {
	            vdVideoview.pause();   //调用videoView的暂停方法
	        } else {
	            vdVideoview.start();   //调用videoView的播放方法
	        }
	    }



* upDataVideoTimeUI()中的代码

	
	 private void upDataVideoTimeUI(int currentPosition) {
	        tvVideoCurrentTime.setText(DateUtils.videoTimeFormat(currentPosition));
	        //进度条进度值设置
	        sbVideoCurrent.setProgress(currentPosition);
	    }
 

### 将播放菜单传递给播放界面 ###

* 在 VideoItem 中增加以下方法

		/**
	     * 根据传入的cursor 解析出对应的实体对象，以后的解析一般这么操作
	     *
	     * @param cursor
	     * @return
	     */
	    public static VideoItem purseVideoItem(Cursor cursor) {
	        if (cursor == null || cursor.getCount() == 0) {   //做一波健壮性检查 ， 以后的检查都这么做 ，不要先处理，最后检查，那样显得逻辑乱
	            return null;
	        }
	        VideoItem listItem = new VideoItem();
	        String tittle = cursor.getString(cursor.getColumnIndex(Media.TITLE));
	        String path = cursor.getString(cursor.getColumnIndex(Media.DATA));
	        long size = cursor.getLong(cursor.getColumnIndex(Media.SIZE));
	        int duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
	        listItem.tittle = tittle;
	        listItem.path = path;
	        listItem.size = size;
	        listItem.duration = duration;
	        return listItem;
	    }
	
	    /**
	     * 获取视频列表
	     *
	     * @param cursor
	     * @return
	     */
	    public static ArrayList<VideoItem> purseVideoListItem(Cursor cursor) {
	        ArrayList<VideoItem> list = new ArrayList<>();
	        //做一下健壮性检查
	        if (cursor == null || cursor.getCount() == 0) {
	            return list;   //防止空指针
	        }
	        cursor.moveToPosition(-1);
	        int i= 0;
	        while (cursor.moveToNext()) {  //移动获取数据就可以了
	            VideoItem item =  VideoItem.purseVideoItem(cursor);
	            VideoItem videoItem = new VideoItem();
	            videoItem.setDuration(item.getDuration());
	            videoItem.setTittle(item.getTittle());
	            videoItem.setPath(item.getPath());
	            videoItem.setSize(item.getSize());
	
	            list.add(item);
	        }
	        return list;
	    }

* 记得  VideoItem 这个实体类的成员变量一定不可以是静态的，静态方法使用的变量不一定都是静态的，否则会出现list集合中的数据只有一个的情况

	 	private  String tittle;
	    private  String path;
	    private  long size;
	    private  int duration;


 
* **在网上搜索到的解释：**
		
		1、static变量
		
		　　按照是否静态的对类成员变量进行分类可分两种：一种是被static修饰的变量，叫静态变量或类变量；另一种是没有被static修饰的变量，叫实例变量。
		
		　　两者的区别是：
		
		　　对于静态变量在内存中只有一个拷贝（节省内存），JVM只为静态分配一次内存，在加载类的过程中完成静态变量的内存分配，可用类名直接访问（方便），当然也可以通过对象来访问（但是这是不推荐的）。
		
		　　对于实例变量，没创建一个实例，就会为实例变量分配一次内存，实例变量可以在内存中有多个拷贝，互不影响（灵活）。
		
		　　所以一般在需要实现以下两个功能时使用静态变量：
		
		在对象之间共享值时
		方便访问变量时
		
		
		
		* 静态的变量仅仅在当前的类引用的时候初始化一次（内存地址只进行一次分配），再次引用的时候将这个地址多次拷贝（节约内存）但是很容易出现值被覆盖的问题，以最后一次赋值为准



* 在 VideoListFragment 中的引用

	 		Cursor cursor = (Cursor) cursorListAdapter.getItem(position);

            //代码多一般的解析都不会放在这里，而是直接的放在been类中
            ArrayList<VideoItem> videoListItems = VideoItem.purseVideoListItem(cursor);


            //将数据传递到播放界面
            Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
            intent.putExtra("videoListItems", videoListItems);
            intent.putExtra("position", position);
            startActivity(intent);



* 抽取播放界面播放代代码的公共部分

	 	/**
	     * 视屏播放切换，播放指定位置的视频
	     */
	    private void videoPlayPosition() {
	        videoItem = videoListItems.get(currentVideosPosition);
	        String videoPath = videoItem.getPath();
	        vdVideoview.setVideoPath(videoPath);    //设置播放路径
	        //公司要求快速开发的时候，可以使用系统自带的播放控制进度条，虽然丑但是还不错
	        //vdVideoview.setMediaController(new MediaController(this));
	        LogUtil.e(videoItem.toString());
	        //初始化标题
	        txVideoTittle.setText(videoItem.getTittle());
	    }

* 上一曲，下一曲播放监听并解决索引越界和按钮置灰


 		case R.id.iv_video_pre:    //上一视频按钮
                currentVideosPosition--;
                ivVideoPre.setEnabled(currentVideosPosition >= 0); //现在的条目小于0将状态设置伟不可点击
                ivVideoNext.setEnabled(currentVideosPosition < videoListItems.size());  //现在的条目大于制定的条目将状态设置伟不可点击
                if (currentVideosPosition < 0) {//现在的条目大于指定的条目不在进行播放操作
                    return;
                }
                videoPlayPosition();
                break;
        case R.id.iv_video_next:    //下一视频按钮
                currentVideosPosition++;
                ivVideoPre.setEnabled(currentVideosPosition >= 0); //现在的条目小于0将状态设置伟不可点击
                ivVideoNext.setEnabled(currentVideosPosition < videoListItems.size());  //现在的条目大于制定的条目将状态设置伟不可点击
                if (currentVideosPosition >= videoListItems.size()) {  //现在的条目大于指定的条目不在进行播放操作
                    return;
                }
                videoPlayPosition();
                break;



## 获取控制面板的高度，隐藏控制面板 ##


* 控件的大小是在View的measure的过程计算出来的。所有我们如果通过直接在onCreate()、onResume()等等方法中直接去取控件的大小是取不到的。 原因是View的measure过程和Activity的生命周期是不同步的，也就是说无法保证Activity中的onCreate()等等执行的时候View的measure()已经执行了，如果此时还没有执行肯定是得不到值的。

* **获取控件高度方式**
* 1.通过 `控件.getMeasuredHeight()` 方法获取控件的宽高

* 2.通过 `getHeight()` 方法

两种方式的比较

比较项目   	|控件.getMeasuredHeight() 或者 控件.getMeasuredHeight() 	 |getHeight()
------------|:------------------------------------------------------:|:-------:
调用的前提	|必须在前面添加代码 需要测量的控件.measure(0,0);表示使用估计值 |Activity中的View.Callback中的onWindowFocusChanged()监听中回调
获取时机		| 			在onCreate()中								 | 只有在OnGlobalLayoutListener()监听的回调方法中才会获取到宽高
准确性		|  在组合使用的时候不精确									 | 都是准确的
使用限制 	| 当两个控件同时占用一个屏幕，并且布局最外层中都是用match_parent时候不可使用 |无限制


* 两种代码如下：
* 使用回调方法的形式


		//这种方式更加的精确，但是执行效率厚底一些
        llVideoTop.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                topH = llVideoTop.getHeight();
                LogUtil.e("上栏的高度："+ topH);
                llVideoTop.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewCompat.animate(llVideoTop).translationY(-topH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
            }
        });

* 直接使用 measure(0,0) 的形式
		
		  //这种方法的调用效率更高，优先执行
	        llVideoBottom.measure(0,0);
	        bottomH = llVideoBottom.getMeasuredHeight();
	        LogUtil.e("底部面板的高度： "+ bottomH);
	        ViewCompat.animate(llVideoBottom).translationY(bottomH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();



* 总结： 
	* 1.回调监听会在 measure(0,0) 执行完了以后才会执行：
	* 2.大多数情况我们的布局中不会出现组合布局的形式，所以使用	measure(0,0) 的情况更多一些	



## 快捷键获取子类构造 ##

![](http://i.imgur.com/tdYzJd2.png)

## 快捷键实现快速查找重构的方法 ##



## 控制面板的隐藏操作   ##

* 初始化测量控件的高度和隐藏控制面板

		/**
	     * 获取顶部和底部控制栏的测量宽高
	     * 获取旷告是不可以在onCreate方法中直接的获取的到的
	     * 有两种方式获取的到，分别是：
	     * 1. 使用全局的监听回调，调用完成必须马上注销监听，不然一直回调；测量的结果就是实际的显示的效果，精确；慢效率低调用在measure()方法的后面
	     * 2. 使用布局加载时的估计值；；在层叠布局中不能使用；调用快，在控件渲染之前就已经侧来过完成，但是在层叠布局中不精确
	     */
	    private void hideContrlorOnInit() {
	        //创建布局监听器的形式获取控件的宽高
	        //这种方式更加的精确，但是执行效率厚底一些
	        llVideoTop.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	
	            @Override
	            public void onGlobalLayout() {
	                topH = llVideoTop.getHeight();
	                LogUtil.e("上栏的高度：" + topH);
	                llVideoTop.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	                ViewCompat.animate(llVideoTop).translationY(-topH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	                //更改面板状态的标志位
	                MenuShowing=false;
	            }
	        });
	        //这种方法的调用效率更高，优先执行
	        llVideoBottom.measure(0, 0);
	        bottomH = llVideoBottom.getMeasuredHeight();
	        LogUtil.e("底部面板的高度： " + bottomH);
	        ViewCompat.animate(llVideoBottom).translationY(bottomH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	    }

* 两种放肆都有使用到，方便后期看的时候想起来

* 因为 `ViewTreeObserver.OnGlobalLayoutListener()` 的方式测量的时候是界面绘制出来，因此调用的时候` llVideoBottom.measure(0, 0);` 的宽高一定都是测量好的了

* 界面的手势操作需要借助手势分析器

* 在 initListener() 中创建监听，创建的时候需要传入一个 `OnGestureListener` 看构造 `public GestureDetector(Context context, OnGestureListener listener)` 他实现的方法太多了，考虑使用子类，快捷键获取子类构造 得到 `SimpleOnGestureListener()`
 	 

		//注册手势监听器,使用的时候需要将当前的系统的触摸事件交给他，才能分析
        //原来的类实现的抽象方法太多的时候，可以考虑使用他的子类
        gestureDetector = new GestureDetector(this, new mySimpleOnGestureListener());	



* 要想分析触摸事件，需要在 `onTouchEvent() ;` 中将触摸事件传递过去
 

	 	 public boolean onTouchEvent(MotionEvent event) {
        //屏幕的手势分析器如果分析手势的操作，必须先获取到当前的触摸事件
        gestureDetector.onTouchEvent(event);

		......
		}


* mySimpleOnGestureListener()中的回调代码，Ctrl + o 重写对应的方法


		 /**
	     * 手势分析器
	     */
	    private class mySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
	        /**
	         * 这个是单击
	         *
	         * @param e
	         * @return
	         */
	        @Override
	        public boolean onSingleTapConfirmed(MotionEvent e) {
	            switchControlor();
	            return super.onSingleTapConfirmed(e);
	        }
	
	        /**
	         * 这个是只要点击就调用
	         *
	         * @param e
	         * @return
	         */
	        @Override
	        public boolean onSingleTapUp(MotionEvent e) {
	
	            return super.onSingleTapUp(e);
	        }
	    }

		
* 创建 `switchControlor()` 方法，改变控制栏的状态


		 /**
	     * 切换控制面板的显示状态
	     */
	    private void switchControlor() {
	        //如果是开就变成关，如果是关就变开
	
	        if (MenuShowing) {
	            //这两种方式的区别，很显然 translationYBy(topH)实在移动后的基础上移动，translationY(bottomH)在布局文件基础上每次都是
	            //ViewCompat.animate(llVideoTop).translationYBy(topH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	            ViewCompat.animate(llVideoTop).translationY(-topH).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	            ViewCompat.animate(llVideoBottom).translationY(bottomH).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	        } else {
	            ViewCompat.animate(llVideoTop).translationY(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	            ViewCompat.animate(llVideoBottom).translationY(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	        }
	        MenuShowing=!MenuShowing;
	
	    }

* 不要忘记创建变量记录状态栏的信息

	private boolean MenuShowing；


* 在改变状态栏的地方改变他的状态

## 延时隐藏控制面板 ##
* 在打开面板的时候发送演示消息，并且移除上一次的消息防止干扰

	 	 /**
	     * 切换控制面板的显示状态
	     */
	    private void switchControlor() {
	        //如果是开就变成关，如果是关就变开
	
	        if (MenuShowing) {
	            hideControlor();
	        } else {
	            showControlor();
	            //清除上一次的操作   ，代码是成对出现的最好放在一起
	            handler.removeMessages(WHAT_HIDE_CONTROLOR);
	            //打开面板的时候，延时5s自动关闭
	            handler.sendEmptyMessageDelayed(WHAT_HIDE_CONTROLOR, 5000);
	        }

* 代码抽取
 
	  	/**
	     * 控制面板显示
	     */
	    private void showControlor() {
	        ViewCompat.animate(llVideoTop).translationY(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	        ViewCompat.animate(llVideoBottom).translationY(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	        MenuShowing = true;
	    }
	
	    /**
	     * 控制面板隐藏
	     */
	    private void hideControlor() {
	        //这两种方式的区别，很显然 translationYBy(topH)实在移动后的基础上移动，translationY(bottomH)在布局文件基础上每次都是
	        //ViewCompat.animate(llVideoTop).translationYBy(topH).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	        ViewCompat.animate(llVideoTop).translationY(-topH).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	        ViewCompat.animate(llVideoBottom).translationY(bottomH).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
	        MenuShowing = false;
	    } 



* 进度条操作的时候移除小消息

	 


		/**
	     * 当进度条改变的时候调用
	     * 媒体音量进度和视频播放进度公用一套
	     * seekBar的监听器是可以公用一套的，通过seekBar的id区别
	     */
	    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	           。。。。

	        }
	
	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	            //清除上一次的操作   ，代码是成对出现的最好放在一起
	            handler.removeMessages(WHAT_HIDE_CONTROLOR);
	
	        }
	
	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	            //打开面板的时候，延时5s自动关闭
	            handler.sendEmptyMessageDelayed(WHAT_HIDE_CONTROLOR, 5000);
	        }
	    }


* handler接收消息并处理

		Handler handler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	            switch (msg.what) {
	                case WHAT_UPDATASYSTEM_TIME:
	                    upDataSystemTime();
	                    break;
	                case WHAT_UPDATA_VIEDEO_POSITION:
	                    upDataPlayPosition();    //实时播放时间更新
	                    break;
	                case WHAT_HIDE_CONTROLOR:
	                    hideControlor();    //5s延时到，隐藏控制面板
	                    break;
	            }
	        }
	    };


## 全屏切换操作 ##

* VideoView的默认的全屏是跟随视屏的宽高比例的，因此宽和高总有一个是不能填充窗体的
* VideoView在初始化的时候会被重新的计算宽高

* 而VideoView 的父类 SurfaceView 是跟随xml文件布局的不会自动得计算


* 1.实现屏幕切换生效，必须先将 VideoView设置一个属性  `android:layout_centerInParent="true"` 至于为什么需要查看源码
 	 	
		<com.itcast.zbc.mediaplayer.view.VideoView
	        android:layout_centerInParent="true"
	        android:id="@+id/vd_videoview"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent" />

* 2.拷贝sdk中的VideoView源码(Android是开源的，只一点比较好)，解决报错问题
	* 1.使用的 VideoView.java 一定是来自sdk 18 的，小于这个版本的兼容性不可以，大于的报错的方法太多，不好修改
	 
	* 2.在 `onMeasure()` 方法的最后记录处理好的宽高

 			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

				......

				// 记录原始的控件大小(这个大小实在屏幕的基础上调整得到的)
	            mDefaultH = height;
	            mDefaultW = width;
			}

	* 3.在  `private void initVideoView(Context context) ` 初始化方法中初始化记录全屏的宽高和mContext
				
	
			// 初始化自定义的变量
	        mContext =context;
	        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	             mScreenW = windowManager.getDefaultDisplay().getWidth();
	             mScreenH = windowManager.getDefaultDisplay().getHeight();
			



* 3.在VideoView中添加一下方法：
	* 1.全屏切换

		    // 如果当前是全屏状态则切换到默认的大小，否则切换到全屏大小
		    public void switchFullScreen(){
		        if (isFullSreen){
		            // 全屏状态，切换到默认大小
		            getLayoutParams().width = mDefaultW;
		            getLayoutParams().height = mDefaultH;
		            LogUtil.e("VideoView.switchFullScreen,mDefaultW="+mDefaultW+";mDefaultH="+mDefaultH+";isFullSreen="+isFullSreen);
		            LogUtil.e("VideoView.switchFullScreen,mScreenW="+mScreenW+";mScreenH="+mScreenH+";isFullSreen="+isFullSreen);
		        }else {
		
		            // 非全屏状态，切换到全屏大小
		            getLayoutParams().width = mScreenW;
		            getLayoutParams().height = mScreenH;
		            LogUtil.e("VideoView.switchFullScreen,mDefaultW="+mDefaultW+";mDefaultH="+mDefaultH+";isFullSreen="+isFullSreen);
		            LogUtil.e("VideoView.switchFullScreen,mScreenW="+mScreenW+";mScreenH="+mScreenH+";isFullSreen="+isFullSreen);
		        }
		        // 刷新控件大小
		        requestLayout();
		        isFullSreen = !isFullSreen;
		    }

	* 2.获取当前屏幕状态

		    /** 如果返回 true 说明当前是全屏状态 */
		    public boolean isFullSreen() {
		        return isFullSreen;
		    }

* 4.使用全局变量记录当前的全屏状态

		// 自定义的变量
	    private  Context mContext;
	    //默认尺寸
	    private int mDefaultH;
	    private int mDefaultW;
	    //屏幕尺寸
	    private int mScreenH;
	    private int mScreenW;
	    /** 如果为true，则说明当前是全屏状态 */
	    private boolean isFullSreen = false;



## 双击操作和单击处理 ##
 
* 在手势分析器 `SimpleOnGestureListener` 中添加双击操作 `onDoubleTap()` 和长按操作  `onLongPress()`  

		/**
	     * 手势分析器
	     */
	    private class mySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
	        /**
	         * 这个是单击
	         *
	         * @param e
	         * @return
	         */
	        @Override
	        public boolean onSingleTapConfirmed(MotionEvent e) {
	            switchControlor();
	            return super.onSingleTapConfirmed(e);
	        }
	
	        /**
	         * 双击时调用
	         * 双击全屏切换
	         * @param e
	         * @return
	         */
	        @Override
	        public boolean onDoubleTap(MotionEvent e) {
	            // LogUtil.e("onDoubleTap");
	            vdVideoview.switchFullScreen();
	            upDatabtnFullScreen();
	            return super.onDoubleTap(e);
	        }
	
	
	        /**
	         * 长按时调用
	         * 长按暂停
	         * @param e
	         */
	        @Override
	        public void onLongPress(MotionEvent e) {
	            //LogUtil.e("onLongPress");
	            upDataPause();
	            upDataPauseStatus();
	            super.onLongPress(e);
	        }
	    }



## 添加外部文件播放的功能 ##

* 这个功能可以让我们轻松的在资源管理器中找到的视频可以在我们的app中播放


![](http://i.imgur.com/1LCHVuX.png)


* 1.这个是通过意图广播来完成的，主要在我们的app清单文件的activity下添加以下代码


		<!--视频播放界面-->
        <!--android:screenOrientation="landscape"设置播放界面为横向
        android:theme="@android:style/Theme.Holo.Light.NoActionBar.Fullscreen"  设置当前activity全屏-->
        <activity
            android:name=".ui.activity.VideoPlayerActivity"
            android:screenOrientation="landscape">
            <!--添加文件管理器启动视频播放的拦截-->
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
 
				<!--匹配可以接收流媒体rtsp格式的文件-->
                <data android:scheme="rtsp" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />

				<!--匹配可以接收流媒体sdp格式的视频文件-->
                <data android:mimeType="video/*" />
                <data android:mimeType="application/sdp" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

				<!--匹配可以接收来自网页http协议的MP4，3dp等格式的格式的视频，需要的话还可以添加-->
                <data android:scheme="http" />
                <data android:mimeType="video/mp4" />
                <data android:mimeType="video/3gp" />
                <data android:mimeType="video/3gpp" />
                <data android:mimeType="video/3gpp2" />
            </intent-filter>
        </activity>

* 2.完成上面的代码，在文件管理器中代开视频会触发下面的方法

		/**
         * 外部打开媒体处理
         */
        //content://media/external/video/media/56937
        Uri uri = getIntent().getData();
        if (uri != null) {   //内容不为空，说明是一个通过文件管理器打开的视频
            if (uri.toString().contains("content://media/")) {   //如果是媒体库地址，获取到的是一个内容提供者提供的地址
                uri = conrentTransformUri(uri);
            }//如果不是媒体库形式的uri那就是一个真实路径的了后面的就统一处理
            LogUtil.e("uri=" + uri);
            vdVideoview.setVideoURI(uri);  //播放外部打开的指定视频
            //初始化标题
            txVideoTittle.setText(uri.getPath());
            //将上一曲和下一曲禁用
            ivVideoPre.setEnabled(false);
            ivVideoNext.setEnabled(false);
        } else {   //uri为空，说明是本地打开的视频
            videoListItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra("videoListItems");
            //做一波健壮性检查
            if (videoListItems.size() == 0 || videoListItems == null) {
                return;
            }
            currentVideosPosition = getIntent().getIntExtra("position", 0);
            LogUtil.e("currentPosition" + currentPosition);
            //初始化播放选中位置的视频
            videoPlayPosition();
        }


* 会在 `Uri uri = getIntent().getData();` 中获取到一个需要播放的媒体的uri ，但是不同的文件管理器的传送的方式不一样，我的华为手机传送的是一个这样格式的路径  uri=    `content://media/external/video/media/56937` 这个是我的uri ，是一个媒体库的存储位置，没法直接的播放需要转换成 uri=  `file:///storage/sdcard1/test/video/oppo%20-%202.mp4` 的形式

* 3.这个方法已经被我封装好了，使用的时候可以改成一个工具类，同时查询的媒体库还可以改成  `String[] proj = {MediaStore.Audio.Media.DATA};` 图片等

		/**
	     * 将媒体库的路径转换成真实物理存储地址
	     *
	     * @param contentUri content://media/external/video/media/56937
	     * @return 真实的物理地址uri
	     */
	    private Uri conrentTransformUri(Uri contentUri) {
	        String[] proj = {MediaStore.Video.Media.DATA};
	        Cursor actualimagecursor = this.managedQuery(contentUri, proj, null, null, null);
	        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
	        actualimagecursor.moveToFirst();
	
	        String img_path = actualimagecursor.getString(actual_image_column_index);
	        File file = new File(img_path);
	        return Uri.fromFile(file);
	    }



* 最后介绍一下URI的组成

		就Android平台而言，URI主要分三个部分：scheme, authority and path。其中authority又分为host和port。格式如下：
		scheme://host:port/path
		举个实际的例子：
		content://com.example.project:200/folder/subfolder/etc
		\---------/  \---------------------------/ \---/ \--------------------------/
		scheme                 host               port        path
		                \----------------------------/
		                          authority   

scheme  | 协议名 ; 一般的有 `http://   content://  file:// svn：//` 等这都是常用的
--------|-----
host    |主机名或服务器名
port	|端口号
path	|相对路径




## 播放网络视频 ##

* 创建一个新的应应用发送对应我们app匹配器的意图即可
* 新应用的代码如下：

		 public void start(View v) {
	        Intent intent = new Intent();
	        intent.setDataAndType(Uri.parse("http://192.168.199.247/qwer1.avi"), "video/avi");
	        startActivity(intent);
	    }

* `video/avi` 对应我们应用中的 `<data android:mimeType="video/mp4" />`



## 网络加载视频缓冲显示第二进度 ##

* 首先在seekBar的xml文件中定义第二进度

		<?xml version="1.0" encoding="utf-8"?>
 

		<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
		
		    <item
		        android:id="@android:id/background"
		        android:drawable="@drawable/video_seekbar_bg">
		        <!--<shape>
		            <corners android:radius="5dip" />
		            <gradient
		                android:startColor="#ff9d9e9d"
		                android:centerColor="#ff5a5d5a"
		                android:centerY="0.75"
		                android:endColor="#ff747674"
		                android:angle="270"
		                />
		        </shape>-->
		    </item>
		
		    <item android:id="@android:id/secondaryProgress">
		        <clip>
		            <shape>
		                <corners android:radius="5dip" />
		                <solid android:color="@color/half_white" />
		
		                <!--这是一个渐变色-->
		                <!-- <gradient
		                     android:startColor="#80ffd300"
		                     android:centerColor="#80ffb600"
		                     android:centerY="0.75"
		                     android:endColor="#a0ffcb00"
		                     android:angle="270"
		                     />-->
		            </shape>
		        </clip>
		

		    </item>
		
		    <item
		        android:id="@android:id/progress"
		        android:drawable="@drawable/video_seekbar_progress">
		        <!-- <clip>
		             <shape>
		                 <corners android:radius="5dip" />
		                 <gradient
		                     android:startColor="#ffffd300"
		                     android:centerColor="#ffffb600"
		                     android:centerY="0.75"
		                     android:endColor="#ffffcb00"
		                     android:angle="270"
		                     />
		             </shape>
		         </clip>-->
		
		    </item>
		
		</layer-list>
		
* <clip>标签		
* **重点来了：** 使用剪切图像资源可以只显示一部分图像，这种资源经常被用在进度条的制作上。剪切图像资源是一个XML格式文件，资源只包含一个<clip>标签。
* 这是以前定义的xml文件在使用在progressBar的时候会变粗的原因
 

* 初始化第二进度为 0
   	 	
		<SeekBar
            android:id="@+id/sb_video_current"
            style="@android:style/Widget.SeekBar"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginRight="10dp"
            android:layout_weight="1"
            android:maxHeight="6dp"
            android:minHeight="6dp"
            android:progress="40"
            android:progressDrawable="@drawable/video_progress_seekbar"
            android:thumb="@mipmap/video_progress_thumb"
            android:secondaryProgress="0"     //第二进度初始化为0
            android:thumbOffset="0dp" />	  


* 有一个重点： 

* 阅读 VideoView 的时候发现，VideoView 是通过 `mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);` 将进度监听器 `mBufferingUpdateListener` 最终传递给 MediaPlayer 的

 		//最终将传递的这个缓冲监听器传递给了 MediaPlayer
        mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);



* 并且将当前的缓冲进度存储在了  `mCurrentBufferPercentage` 当中
 
	 	/**
	     * 系统原有的监听器是不对外公开的，想要监听只有通过获取进度，自己刷新的形式，这种情况耗费更多的资源
	     */
	   private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                    LogUtil.e(VideoView.class,"percent::::"+percent);
                }
        };

* 并通过 `getBufferPercentage()` 方法最终获取到这个缓冲的百分比

		@Override
	    public int getBufferPercentage() {
	        if (mMediaPlayer != null) {
	            return mCurrentBufferPercentage;
	        }
	        return 0;
	    }


* 于是第二进度条的刷新出现了两种方式


* 1.第一种在无法修改 VideoView 源码的情况下，通过handler延时消息实现界面的刷新

		  case WHAT_UPDATA_PROGRESS_BUFFERING:
                    //加载缓冲百分比
                    int duration = vdVideoview.getDuration();
                    int bufferPercentage = vdVideoview.getBufferPercentage();
                    LogUtil.e("duration====" + duration + "_____bufferPercentage====" + bufferPercentage);
                    handler.sendEmptyMessageDelayed(WHAT_UPDATA_PROGRESS_BUFFERING, 400);
                    break;

* 但是这种方式是有弊端的，就是我们不知道什么时候开始刷新，至于频率： 通过log发现在100ms---200ms 左右最有最好

* 第二种方式学习系统，传递一个  `MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener` 带回我们所需要的信息

* 具体实现：
* 在	VideoView中添加以下的代码	

		// 允许从外部设置缓冲监听
	    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener ;
	
	
	    /**
	     * 获外部触底过来的进度监听器
	     * @param mBufferingUpdateListener
	     */
	    public void setBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener) {
	        this.mBufferingUpdateListener = mBufferingUpdateListener;
	    }


* 在播放界面添加监听器

	 vdVideoview.setBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                LogUtil.e(VideoView.class, "percent::::" + percent);
                if (vdVideoview.getCurrentPosition() == vdVideoview.getDuration() && percent == 100) {   //刷新进度再次达到100%刷新完成不在刷新
                    return;
                }
                float loadingProgress = vdVideoview.getDuration() * percent / 100f;    //将缓存进度进行换算
                sbVideoCurrent.setSecondaryProgress((int) loadingProgress);
            }
        });

* 这样最终就可以将MediaPlayer的回调消息带回来了


## 总结回调这一部分我们还们有学习到他的精髓，一定要再次的深入学习 ##

* 回调可以向子类传递，亦可以向父类传递

	具体的总结为，返回值传递给父类  比如BaseActivity `public View getView()；`
				参数传递给子类    比如  `public void onClickListener(View.OnClickListener listener)`

## 代码书写规范的一些问题 ##

* 先看以下人家的代码：

 
![](http://i.imgur.com/Zdmi7Ql.png)

* 将相同的逻辑放在一起，这样看起来很清爽


## 网络缓存加载进度条处理 ##

* 添加监听

		vdVideoview.setOnInfoListener(new MyOnInfoListener());

* `MyOnInfoListener()` 中的代码

		/**
	     * 当视频不放过程中，缓存属性发生变化时被回调
	     * 在这里处理了缓冲进度条
	     */
	    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {
	        @Override
	        public boolean onInfo(MediaPlayer mp, int what, int extra) {
	            switch (what) {
	                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
	                    ToastUtil.showToastShort("当前网络差");
	                    break;
	                case MediaPlayer.MEDIA_INFO_BUFFERING_START:   //视频播放过程中，缓冲开始
	                    llVideoLoadingInplaying.setVisibility(View.VISIBLE);
	                    break;
	                case MediaPlayer.MEDIA_INFO_BUFFERING_END:     //视频播放过程中，缓冲结束
	                    llVideoLoadingInplaying.setVisibility(View.GONE);
	                    break;
	            }
	            return false;
	        }
	    }


## 视屏播放错误的处理，通过Dialog弹层提示 ##

* 添加监听

        vdVideoview.setOnErrorListener(new MyOnErrorListener());

* `MyOnErrorListener` 中的代码

		 /**
	     * 视频播放错误时回调
	     */
	    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
	        @Override
	        public boolean onError(MediaPlayer mp, final int what, int extra) {
	            String errMsg="";
	            switch (what){
	                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
	                    errMsg="网络连接超时，请检查网络或服务器无资源";
	                    break;
	                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
	                    errMsg="未知的服务器或视频";
	                    break;
	                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
	                    errMsg="暂不支持的视频格式";
	                    break;
	            }
	            //通过dialog提示错误信息
	            AlertDialog.Builder dialog =new AlertDialog.Builder(VideoPlayerActivity.this);
	            dialog.setIcon(R.mipmap.notification_music_playing);
	            dialog.setTitle("警告");
	            dialog.setMessage(TextUtils.isEmpty(errMsg)?"该视屏无法播放":errMsg);
	            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                   finish();
	                }
	            });
	            dialog.create().show();
	            return false;
	        }
	    }



## 视频解码器Vitamio的集成 ##

* 详细的资料参考：
[https://www.vitamio.org/](https://www.vitamio.org/ "vitamio官网，这里可以下载对应的库文件")

* gitHub   Demo下载地址：
 [https://github.com/yixia/VitamioBundle/tree/v3.0](https://github.com/yixia/VitamioBundle/tree/v3.0 "Demo下载地址")


* 解压文档后，将类库

![](http://i.imgur.com/wfKp0MO.png)

* 1.添加到自己的项目中并添加为项目的依赖

![](http://i.imgur.com/TIbeo3q.png)


* 2.剩下的就是讲我们项目的VideoView的包换成Vitamio里面提供的就可以了


		import io.vov.vitamio.MediaPlayer;
		import io.vov.vitamio.Vitamio;
		import io.vov.vitamio.widget.VideoView;

* 3.Vitamio 里面的方法和系统的 VideoView 里面的方法都是一致的，直接的使用都是没有问题的，源码也在实在不行可以自己定义了


* 4.联网权限添加，Activity 清单文件配置

* 5.还是报错，还有最后一个步骤：在使用Vitamio的时候，最终要的是添加以下代码初始化 Vitamio的 C 库

	   	//使用vitamio最为解码器必须调用这个方法初始化 c 类库
        Vitamio.isInitialized(this);


* 好了可以播放视频了，但是最后总结 Vitamio 类库虽然可以做到完美的硬解码软解码的切换，但是如果是自己继承开发，这个库存在以下的缺点：

> * 1.依赖库70M 应用的体积明显变大
> * 2.对地段配置的手机经常崩溃，需要客服解决(说白了，就是留的营销模式，估计是故意的)
> * 3.官方维护费用高
> * 4.很多的性能根本用不到


## 推荐另一个比较不错的库 ijkplayer##

* ijkplayer 是一个基于 ffplay 的轻量级 Android/iOS 视频播放器。实现了跨平台功能，API易于集成；编译配置可裁剪，方便控制安装包大小；支持硬件加速解码，更加省电；提供Android平台下应用弹幕集成的解决方案，此方案目前已用于美拍和斗鱼 APP。 		

* 资源托管： [https://github.com/benchegnzhou/ijkplayer](https://github.com/benchegnzhou/ijkplayer "ijkplayer gitHub托管地址")

## 需要注意的问题 ##

* 在项目中我们直接的调用了  `Vitamio.isInitialized(this);`  这句初始化代码的底层实际做了，一件事，如果我们引用的 c 类库如果没有初始化的话，就会调用 `InitActivity()` 方法进行初始化，
* 这时候使用的工具如果是Android Studio那么系统本身会将引用项目的清单文件和自己项目的清单文件在编译的时候合并在一起
* 如果使用的工具是Eclipes的话，需要手动的将引用项目的用到的Activity配置的清单文件合并到自己的项目中 



-------------------------
## 我是分割线 ##
-------------------------

# 音乐播放 #
* 先看一眼界面

![](http://i.imgur.com/JgXEKj8.png)

* 界面布局就不多说了

* 1.初始化一个空的 listView的CursorAdapter

 		cursorListAudioAdapter = new AudioCursorListAudioAdapter(getActivity(), null);
        lvVideolist.setAdapter(cursorListAudioAdapter);

* 2.在 `initData()` 方法中对数据初始化

	//查询音乐数据
        ContentResolver resolver = getActivity().getContentResolver();
        //调试完成，进一步优化，查询使用异步线程
        //注意使用cursorAdapter必须添加首列为 "_id" 做为cursorAdapter排序使用  ，没有的时候可以将随意的一列as 为"_id"就可以了
		// Cursor curcor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.TITLE, MediaStore.Video.Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);
        MyAsyncQueryHandler asyncQueryHandler = new MyAsyncQueryHandler(resolver);
        asyncQueryHandler.startQuery(CommonValue.AUDIO_CURSOR_ASYNCQUERY,cursorListAudioAdapter,Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.TITLE, MediaStore.Video.Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);

* 3.使用了事先封装好的异步子线程查询 `MyAsyncQueryHandler`

	 	/**
		 *  * 作者：Zbc on 2016/12/7 00:11
		 *  * 邮箱：mappstore@163.com
		 * 功能描述：
		 *   这个类可以实现数据库的异步增 ， 删 ， 改 ， 查 ，使用的时候只需要重写相应的方法就好了，执行的过程都是在主线程中
		 *   使用的最后不要忘记调用startQuery()方法开启线程操作
		 *   使用的方法类比handler
		 */
		public class MyAsyncQueryHandler extends AsyncQueryHandler {
		
		
		    public MyAsyncQueryHandler(ContentResolver resolver) {
		        super(resolver);
		    }
		
		    /**
		     * 查询完成后这个方法会在主线程回调
		     * @param token    相当于handler中的what
		     * @param cookie   相当于handler中的Object，传递刷新的控件
		     * @param cursor    当前一条记录的cursor，简单说就是查询出来的单条结果
		     */
		    @Override
		        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		            super.onQueryComplete(token, cookie, cursor);
		
		        CursorAdapter mAdapter = (CursorAdapter) cookie;  //控件还原
		        CursorUtils.printCursor(MyAsyncQueryHandler.class,cursor);
		        mAdapter.swapCursor(cursor);    //相当于  notifyDataSetInvalidated();  替换原有的cursor   其实底层调用的还是   notifyDataSetInvalidated();这个方法
		        }
		}

* 4.接着就是     `AudioCursorListAudioAdapter(); ` 方法




* 到这里界面就已经可以显示出来了

		/**
		 *  * 作者：Zbc on 2016/12/15 18:08
		 *  * 邮箱：mappstore@163.com
		 * 功能描述：
		 *  
		 */
		public class AudioCursorListAudioAdapter extends CursorAdapter {
		
		
		    public AudioCursorListAudioAdapter(Context context, Cursor c) {
		        super(context, c);
		    }
		
		    public AudioCursorListAudioAdapter(Context context, Cursor c, boolean autoRequery) {
		        super(context, c, autoRequery);
		    }
		
		    public AudioCursorListAudioAdapter(Context context, Cursor c, int flags) {
		        super(context, c, flags);
		    }
		
		    @Override
		    public View newView(Context context, Cursor cursor, ViewGroup parent) {
		        //获取行的view 没有的话创建
		        View view = LayoutInflater.from(context).inflate(R.layout.audio_list_item, null);
		        ViewHolder holder = new ViewHolder();
		        holder.tv_tittle = (TextView) view.findViewById(R.id.tv_audio_list_tittle);
		        holder.tv_arties = (TextView) view.findViewById(R.id.tv_audio_list_arties);
		        //要想在bindView中获取的到view必须设置Tag
		        view.setTag(holder);
		        return view;
		    }
		
		    @Override
		    public void bindView(View view, Context context, Cursor cursor) {
		        ViewHolder holder = (ViewHolder) view.getTag();
		        holder.tv_tittle.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
		        holder.tv_arties.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
		    }
		
		    private class ViewHolder {
		        public TextView tv_tittle, tv_arties;
		    }
		}



## 音乐播放界面 ##

* 传递列表

* 出入列表，在 AudioListFragment中添加条目的点击监听

	 	//获取一个条目位置的item 看源码，返回的是一个移动到指定位置的cursor
            Cursor cursor = (Cursor) cursorListAudioAdapter.getItem(position);
            //解析列表数据成一个been类
            ArrayList<AudioItem> audioItems = AudioItem.purseVideoListItem(cursor);

            //开启音乐播放窗口
            Intent intent=new Intent(getActivity(),AudioPlayerActivity.class);
            intent.putExtra("audioItems",audioItems);
            intent.putExtra("position",position);
            startActivity(intent);

* 列表接收，AudioPlayerActivity 中接收列表，这里有完整的健壮性判断
* 考虑到列表有可能为空，这样强转就会崩溃，因此这里就可以实现做一下对应的判断

 		Serializable serializable = getIntent().getSerializableExtra("audioItems");
        currentPosition = getIntent().getIntExtra("position", -1);
        if (serializable == null) {   //做空指针检测，增加健壮性    null数据直接进行强转直接就崩了
            return;
        }
        audioItems = (ArrayList<AudioItem>) serializable;
        if(audioItems ==null|| audioItems.size()==0|| currentPosition ==-1){  //列表数据异常检测
            return;
        }

	
* 界面布局没什么好说的

* 这里只有一点，就是在seekBar的自定义上




	<!--进度条-->
    <!--播放进度条-->
    <!--自定义seekBar的样式，最简单的方式就是查看系统的源码，然后修改对应的属性-->
    <!--通过源码可以发现
        android:thumb="@mipmap/video_progress_thumb"  //拇指按下的图形
        android:thumbOffset="0dp"   图形的偏移量
    参考系统样式
        android:maxHeight="6dp" 可以限定进度条背景的高度
        style="@android:style/Widget.SeekBar"
        android:secondaryProgress="20"第二进度的值

    这里有一个很魔性的东西，就是如果我们实用的是drawable目录下的需要设置宽高
    如果是mipmap 目录下的资源就要限制宽高
        android:maxHeight="6dp"
        android:minHeight="6dp"-->
    <SeekBar
        android:id="@+id/sb_audio_current"
        style="@android:style/Widget.SeekBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:progress="40"
        android:progressDrawable="@drawable/video_progress_seekbar"
        android:thumb="@mipmap/audio_seek_thumb"
        android:thumbOffset="0dp" />



* 至于这两句代码到底是又还是没有，很关键，不然会显示不出东西 

		android:maxHeight="6dp"
        android:minHeight="6dp"

* 至于有没有是取决于 ` @drawable/video_progress_seekbar` 里面的代码的


		<?xml version="1.0" encoding="utf-8"?>
		<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
		
		    <item
		        android:id="@android:id/background"
		        android:drawable="@mipmap/audio_seekbar_bg">
		        <!--<shape>
		            <corners android:radius="5dip" />
		            <gradient
		                android:startColor="#ff9d9e9d"
		                android:centerColor="#ff5a5d5a"
		                android:centerY="0.75"
		                android:endColor="#ff747674"
		                android:angle="270"
		                />
		        </shape>-->
		    </item>
		
		    <item android:id="@android:id/secondaryProgress">
		        <clip>
		            <shape>
		                <corners android:radius="5dip" />
		                <solid android:color="@color/half_white" />
		
		                <!--这是一个渐变色-->
		                <!-- <gradient
		                     android:startColor="#80ffd300"
		                     android:centerColor="#80ffb600"
		                     android:centerY="0.75"
		                     android:endColor="#a0ffcb00"
		                     android:angle="270"
		                     />-->
		            </shape>
		        </clip>
		
		
		    </item>
		
		    <item
		        android:id="@android:id/progress"
		        android:drawable="@mipmap/audio_seekbar_progress">     /////就是这句话起了决定性的作用
		        <!-- <clip>
		             <shape>
		                 <corners android:radius="5dip" />
		                 <gradient
		                     android:startColor="#ffffd300"
		                     android:centerColor="#ffffb600"
		                     android:centerY="0.75"
		                     android:endColor="#ffffcb00"
		                     android:angle="270"
		                     />
		             </shape>
		         </clip>-->
		
		    </item>
		
		</layer-list>



* 假如说进度条 `android:id="@android:id/progress"` 使用的资源是来自mipmap的他是不可以我们认为的限制大小的
* 加入是来自drawable 的他就会自动的放大失真，所以必须限制大小


* **在公中也遇到宇哥这样的问题就是本来美工切好的图放到drawable里面就会出问题 ———— 被拉伸了**

## 开启后台服务播放音乐 ##

* 启动service 

		Intent intent= new Intent(getIntent());   //在原有的 Intent 的基础上创建一个复制有原有数据的intent
        intent.setClass(this,AudioService.class);
        startService(intent);

* 上面的代码很经典，在原有 intent 的基础上追加 数据，并改变要启动class




## 后台服务于前台Activity通讯 ##


* `activity—向——>service` 单向传递数据：   service初始化之前，将数据传递给service 这个在创建 service的时候可以通过 intent 携带数据
* 初始化之后数据传递： 通过 binder 可以获取到service的方法和变量

* 但是想要service 获取activity的信息，或者主动的想一个activity推送信息或者行为只有通过两种方式
	* 1.广播
	* 2.EventBus

------------
* 1.binder： 实现activity主动获取service信息或方法 ，也可以实现主动控制service

* 具体实现
* 在service中创建 `AudioBinder` 继承 `Binder` 并在这个类中完成的方法或者变量，在activity中都可以使用

		/**
	     * 作为与外界 activity 通讯的中间人
	     */
	    public class AudioBinder extends Binder {
	        /**
	         * 播放指定位置的歌曲
	         */
	        public void playCurrentMusic() {
	            if (audioItems == null || audioItems.size() == 0 || currentPosition == -1) {  //列表数据异常检测
	                return;  //遇到空指针，机会直接跳过
	            }
	
	            //播放音乐
	            String path = audioItems.get(currentPosition).getPath();
	            mediaPlayer = new MediaPlayer();
	            try {
	                mediaPlayer.setDataSource(path);
	                mediaPlayer.prepareAsync();
	                mediaPlayer.setOnPreparedListener(new OnAudioPreparedListener());
	
	
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	
	        /**
	         * 功能： 切换播放的状态
	         * 如果正在播放，切换暂停状态
	         * 否则，切换播放状态
	         */
	        public void changPauseStatus() {
	            if (mediaPlayer.isPlaying()) {
	                mediaPlayer.pause();
	            } else {
	                mediaPlayer.start();
	            }
	        }
	
	        /**
	         * 获取当前音乐播放状态
	         *
	         * @return 返回true： 正在播放
	         */
	        public boolean getPlayStatus() {
	            return mediaPlayer.isPlaying();
	        }
	
	        /**
	         * MedioPlayer资源加载完成回调
	         */
	        private class OnAudioPreparedListener implements MediaPlayer.OnPreparedListener {
	            @Override
	            public void onPrepared(MediaPlayer mp) {
	                //音乐准备完成，开始播放
	                mediaPlayer.start();
	            }
	        }
	    }


* 2.activity获取这个binder

	* 1.开启服务的同事绑定服务是通讯的前提

			Intent intent = new Intent(getIntent());   //在原有的 Intent 的基础上创建一个复制有原有数据的intent
	        intent.setClass(this, AudioService.class);
	        startService(intent);
	        connection = new AudioServiceConnection();
	        //绑定服务
	        bindService(intent, connection, BIND_AUTO_CREATE);

	* 2.有绑定就必须解绑

		 	@Override
		    protected void onDestroy() {
		        //服务在activity销毁的时候一定要解绑
		        unbindService(connection);
		        super.onDestroy();
		    }


	* 3.如果绑定成功，这个 `connection` 将 `binder` 对象携带回来

			/**
		     * activity是通过ServiceConnection进行通信的
		     */
		    private  class AudioServiceConnection implements ServiceConnection {
		        /**
		         * @param name    区别多个服务进程
		         * @param service 是从服务端出传递过来的一个可控的对象
		         */
		        @Override
		        public void onServiceConnected(ComponentName name, IBinder service) {
		            audioBinder = (AudioService.AudioBinder) service;
		        }
		
		        @Override
		        public void onServiceDisconnected(ComponentName name) {
		
		        }
		* 4.这里的 `service` 就是 `service` 里面创建的 `binder`
		* 5.当然真正的返回方法在 `service` 的 `onBind()` 方法中返回

				public IBinder onBind(Intent intent) {
			        //提供一个builder作为外界activity控制的中间人
			        return audioBinder;
			    }




## 广播实现 `service` 调用 activity 的方法 ##


* service 发送广播


	  	// 发送广播，通知界面歌曲已经播放请求刷新界面  这里后期使用 EventBus替换更高效
                Intent intent=new Intent(CommonValue.BOADCAST_MUSICPREPERA);
                sendBroadcast(intent);

* activity中代码注册接收，并实现反注册

		   //广播注册
	        IntentFilter filter = new IntentFilter(CommonValue.BOADCAST_MUSICPREPERA);
	        audioBroadcastReceiver = new AudioBroadcastReceiver();
	        registerReceiver(audioBroadcastReceiver, filter);


	
* `AudioBroadcastReceiver` 中的代码

  		/**
	     * 音乐资异步加载完成后调用
	     */
	    private class AudioBroadcastReceiver extends BroadcastReceiver {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            // 接收在歌曲资源异步加载完成
	            upDataPlayStatusBtn();
	        }
	    }
	
* 进行反注册
   
	  	unregisterReceiver(audioBroadcastReceiver);	

* 这里的广播我们仅使用一次，使用来刷新界面的， 在我们的界面被销毁的时候，activity被吊起没有意义，因此代码注册
* xml注册是一种静态的方式，只要有广播，就会被调起来


## 解决多个歌曲同时播放的问题 ##	

* 每次播放歌曲的时候就创建一个新的 `MediaPlayer` 造成的，判断以前的 `MediaPlayer` 存在就 `reset()` 一下资源就可以了
	 	
			// 播放新的歌曲之前，必须停止以前的歌曲
            if(mediaPlayer!=null){    //解决每次开始播放，是上一首歌不能停止的问题
                mediaPlayer.reset();
            }else {
                mediaPlayer = new MediaPlayer();
            }



## 音乐播放模式的处理 ##


* 音乐播放和界面的依赖性，应该是界面依赖于 `service` 的，界面再次打开的时候，应该回显 `service` 的状态

* 因此播放模式的定义应该放在服务的里面

* 1.变量状态的初始化


		//定义播放模式
	    public static final int PLAYMODE_ALL_REPEAT = 0;
	    public static final int PLAYMODE_SINGLE_REPEAT = 1;
	    public static final int PLAYMODE_RANDOM = 2;
	    private int mPlayMode = PLAYMODE_ALL_REPEAT;   //初始化为列表循环

* 2.通过方法，对外提供操作

	 	 /**
         * 切换音乐播放模式
         */
        public void switchPlayMode() {
            switch (mPlayMode) {
                case PLAYMODE_ALL_REPEAT:
                    mPlayMode = PLAYMODE_SINGLE_REPEAT;
                    break;
                case PLAYMODE_SINGLE_REPEAT:
                    mPlayMode = PLAYMODE_RANDOM;
                    break;
                case PLAYMODE_RANDOM:
                    mPlayMode = PLAYMODE_ALL_REPEAT;
                    break;
            }
        }

        /**
         * 获取当前播放模式
         * {@link #PLAYMODE_ALL_REPEAT } : 列表播放,
         * {@link #PLAYMODE_SINGLE_REPEAT } : 单曲循环 ,
         * {@link #PLAYMODE_RANDOM } ： 随机播放
         *
         * @return Int
         */
        public int getPlayMode() {
            return mPlayMode;
        }



* 在 `activity` 中直接调用

	    /**
	     * 切换播放模式
	     */
	    private void SwitchPlayMode() {
	        audioBinder.switchPlayMode();
	        upDataPlayModeBtn();      //功能和界面严格分离，方便调用
	    }
	
	    /**
	     * 更新播放模式按钮图片
	     */
	    private void upDataPlayModeBtn() {
	        switch (audioBinder.getPlayMode()) {
	            case AudioService.PLAYMODE_ALL_REPEAT:
	                ivAudioPlaymodle.setImageResource(R.drawable.btn_playmodle_singlerepeat);
	                break;
	            case AudioService.PLAYMODE_SINGLE_REPEAT:
	                ivAudioPlaymodle.setImageResource(R.drawable.btn_playmodle_random);
	                break;
	            case AudioService.PLAYMODE_RANDOM:
	                ivAudioPlaymodle.setImageResource(R.drawable.btn_playmodle_allrepeat);
	                break;
	        }
	    }


* 播放初始化同步一下按按钮

		 private class AudioBroadcastReceiver extends BroadcastReceiver {
	
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            // 接收在歌曲资源异步加载完成
	            upDataPlayStatusBtn();
	            audioItem = (AudioItem) intent.getSerializableExtra("audioItem");
	
	            //音乐信息更新
	            upDataMusicData();
	
	            //开启播放进度更新
	            upDataPosition();
	
	            // 播放模式按钮更新
	            upDataPlayModeBtn();
	        }
	    }


## 方法连接类型注释的书写 ##

*  `{@link #PLAYMODE_ALL_REPEAT }`  
*  这样的语句形式，就可以在调用的方法上面 `Ctrl+ P` 查看了




## 播放完成自动播放下一曲 ##


* 1.获取播放状态完成监听

	 public void playCurrentMusic() {
            
			.......
         
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new OnAudioPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

* 2.`MyOnCompletionListener` 中的代码

	 	/**
         * 歌曲播放完成的监听回调
         */
        private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
            @Override
            public void onCompletion(MediaPlayer mp) {
                autoPlayNext(false);
            }
        }


* 3.`autoPlayNext()` 中的代码,其中的参数是为了解决，按模式下一曲代码服用解决的 传 `true` 表示手动下一曲



		
        /**
         *  根据播放模式自动播放下一首歌曲
         * @param isNext 是不是播放下一曲调用的这个方法，这时候解决一个单曲循环也能解决跳转的效果
         */

        private void autoPlayNext(boolean isNext) {
            switch (mPlayMode) {
                case PLAYMODE_ALL_REPEAT:
                    currentPosition++;
                    if (currentPosition >= audioItems.size()) {
                        currentPosition = 0;
                    }
                    break;
                case PLAYMODE_SINGLE_REPEAT:
                    break;
                case PLAYMODE_RANDOM:
                    currentPosition = new Random().nextInt(audioItems.size());
                    break;
            }
            // 解决单曲循环的下一曲效果
            if(isNext&&mPlayMode==PLAYMODE_SINGLE_REPEAT){
                currentPosition++;
                if (currentPosition >= audioItems.size()) {
                    currentPosition = 0;
                }
            }
            playCurrentMusic();
        }


## 使用系统的通知栏，提示通知消息 ##


* 使用说明： 方便说明我们这里只说明兼容 Android 3.0-7.0 的方法
* 再次一下的版本兼容我们在后面加以讨论



* 1.**发送通知 `NotificationManager` 对象发送通知**

	    Notification  notification = getNotificationByNewAPI();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		//开启通知栏通知  参数1： 请求码，在取消通知的时候一定要对应 参数2： notification对象
        notificationManager.notify(100, notification);


* 2.**通知取消或移除，通知请求码要相同**
	
		 notificationManager.cancel(100);  

* 如果通知有很多可以使用  		 `notificationManager.cancelAll(); ` 方法一次性发送多条的通知
		 

* 3.**获取通知显示的格式：**    在版本6.0中系统在API中将 `notification.setLatestEventInfo()` 方法移除，并说明使用 `Notification.Builder(this)` 方法代替，并且  `Notification.Builder(this) `  的 ` .build();`  方法也仅仅是兼容到 `API 16`  这个方法最低兼容到 `API  16  Android 4.1 `有点太新了，兼容性差  ，不过还好为了兼容到  API 11 Android 3.0 还可以使用 `.getNotification();`  方法代替原来的 ` .build();`  方法。

	
	 	/**
	     * 使用新的API可以获得较好的适配体验，但是在低版本的兼容性上面存在问题
	     * 兼容范围  3.0 < API
	     * Notification.Builder(this).build()               方法最低兼容到API 16   4.1.x
	     * Notification.Builder(this).getNotification()     方法最低兼容到API 11   3.0
	     *
	     * @return
	     */
	    public Notification getNotificationByNewAPI() {
	
	        Notification noti = new Notification.Builder(this)
	                .setTicker("转为后台播放")
	                .setContentTitle("音乐正在后台播放 ")
	                .setContentText("music: Dream")
					.setContentInfo("我是补充内容")
	                .setSmallIcon(R.mipmap.notification_music_playing)
	                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_audio_play))
	                .setWhen(System.currentTimeMillis())
	                .setContentIntent(getContentIntent())    //获取提示正文的点击响应
	                .setOngoing(true)  //设置通知消息不可移除
	                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
	                .getNotification();
	
	        //.build();  这个方法最低兼容到API  16  Android 4.1 有点太新了，兼容性差
	        return noti;
	    }
	
	

* 4.**通知点击事件响应的定义：**  需要得到一个相应的意图，这个意图最终需要  `PendingIntent.意图的类型` 封装最终返回，意图的类型无非就是告知系统你最中是想要做什么，是打开Activity还是打开一个Service等等


		   /**
		     * 生成正文的点击响应
		     * 当正文的通知被点击的时候返回这个意图，完成对应的动作
		     *
		     * @return
		     */
		    public PendingIntent getContentIntent() {
		        // 这个的意图必须是系统提前创建好的，不可以手动创建
		        // PendingIntent.getActivities()  启动过个activity
		        //PendingIntent.getActivity();  启动单个activity
		        //PendingIntent.getBroadcast(); 发送一个广播使用这个
		        //PendingIntent.getService()  启动服务对应这个
		        Intent intent = new Intent();
		        intent.setDataAndType(Uri.parse("http://192.168.199.247/qwer2.mp4"), "video/mp4");
		        //PendingIntent.FLAG_UPDATE_CURRENT 当前如果有消息就把他更新起来
		        PendingIntent broadcast = PendingIntent.getActivity(this, 200, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		        return broadcast;
		    }





![](http://i.imgur.com/mFr0FnT.png)


* 这里有几点地方需要注意：

	* 1.如果该通知只是起到 “通知”的作用，不希望用户点击后有相应的跳转，那么，intent,pendingIntent这几行代码可以不写，可以创建延时操作，当通知被成功 notify 后，一段时间后调用manager.cancel(notificationID),将通知清除，此时builder.setAutoCancel()方法不写也可以。
	   
	* 2.如果通知栏下拉后，希望用户点击并有相应的跳转事件。那么，要注意跳转后，通知是否有必要继续存在。如果点击后通知消失，两种方法A 设置 `setAutoCancel` 参数设置为 `true`，默认是 `false`，B 在 `intent` 事件中的如本例中的 `MainActivity` 的  `onCreate()` 方法调用  `manager.cancel(notificationID)` 取消该通知，此ID要和创建通知的ID一致，否则通知不消失。如果点击不消失，将 `setAutoCancel` 参数设置为 `false` 即可。 其实对于不可取消，我更喜欢使用  `.setOngoing(true)  //设置通知消息不可移除` 这种方式去实现


## 通知消息老版本的兼容3.0以前的Android 设备 ##

*  当我们工程的编译版本大于等于23的时候是无法做到兼容3.0以前版本的
  
* 为了兼容性我们这样做，在第三步 **获取通知显示的格式：**的时候使用 `notification.setLatestEventInfo(this, tittile, content, getContentIntent());` 方法，这个方法在 Android 6.0 以前都可以使用，最低可以兼容到 Android 的 2.3 系统


		 /**
	     * 使用旧的API获取通知栏消息，notification.setLatestEventInfo() 这个方法，在版本6.0已经这个方法已经被移除
	     * 兼容范围是：API <  6.0 都可以
	     * 过时的方法，兼容低版本的3.0以前的设备使用
	     * @return
	     */
	    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	    private Notification getNotificationByOldAPI() {
	        /**
	         * 参数1： 状态栏图标  参数2： 状态栏文字提示  参数3 ：消息时间戳  一般是系统当前的毫秒值
	         */
	        Notification notification = new Notification(R.mipmap.icon, "音乐正在后台播放", System.currentTimeMillis());
	        //这个方法只有在，编译器的版本不大于22时候才能生效，此方法在Android 6.0以后被删除了
	        String tittile = "通知";
	        String content = "视屏播放";
	        notification.setLatestEventInfo(this, tittile, content, getContentIntent());
	
	        //设置下拉消息图标
	        notification.largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_audio_play);
	        //设置下拉列表消息不可移除
	        //消息进程正在后台执行 和 FLAG_NO_CLAER 是一样的,里面有好多的flag自己研究
	        notification.flags = Notification.FLAG_ONGOING_EVENT;
	        return notification;
	    }


其他的都是一样的，仅仅是 获取 `Notification` 对象的方法上面有一点点的小区别


## 使用系统自定义消息栏通知消息更详细的介绍，文件中搜索 `Android 通知栏通知实现详解.pdf` ##
* 原文链接： [http://blog.csdn.net/vipzjyno1/article/details/25248021/](http://blog.csdn.net/vipzjyno1/article/details/25248021/ "通知栏消息详细讲解")


* 这个文件中讲的很详细了，这里我就不在过多的赘述

## 自定义的通知栏消息 ##
* 使用自定义的方式发送通知消息
* 这里默认我们已经明白了通知栏消息发送的基础知识，如果没有看过请自行阅读  `Android 通知栏通知实现详解.pdf` (磁盘中搜索) 或
* 查看链接： [http://blog.csdn.net/vipzjyno1/article/details/25248021/](http://blog.csdn.net/vipzjyno1/article/details/25248021/ "通知栏消息详细讲解")


* 1.**第一步获取一个 `Notification` 对象** 这里面原有的对下拉通知的定义和点击事件的处理 就省略吧，稍后我们自己来实现

	
	 	/**
	     * 自定已通知栏布局，实现通知栏消息通知
	     * @return
	     */
	    public Notification getCustomNotificationByNewAPI() {
	        Notification noti = new Notification.Builder(this)
	                .setTicker("转为后台播放")    		//提示的顶栏通知
	                .setSmallIcon(R.mipmap.notification_music_playing)   		//顶栏通知的图标
	                .setContent(getVontentView())       //获取一个远程布局，供系统反射调用
	                .setOngoing(true)  //设置通知消息不可移除
	                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
	                .setVibrate(new long[] {0,300,500,700})  //自定义震动  实现效果：延迟0ms，然后振动300ms，在延迟500ms，接着在振动700ms。
	                .getNotification();
	        return noti;
	    }


* 2.获取下拉消息的通知的布局
 	
		/**
	     *  获取通知消息正文布局
	     * @return
	     */
	    public RemoteViews getVontentView() {
	        //  RemoteViews(String packageName, int layoutId) 这里的 packageName 是龚茜彤使用的，系统通过packageName进行反射获取对用的View
	        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.custom_remoteview);

			//数据的填充
	        remoteViews.setTextViewText(R.id.tv_custom_tittile,"演员");
	        remoteViews.setTextViewText(R.id.tv_custom_artist,"薛之谦");
	        remoteViews.setImageViewResource(R.id.btn_videoPlay,R.mipmap.btn_audio_pause);
	        //设置点击事件
	        remoteViews.setOnClickPendingIntent(R.id.rl_custom,getRlPendingIntent());
	        remoteViews.setOnClickPendingIntent(R.id.iv_custom_pre,getPrePendingIntent());
	        remoteViews.setOnClickPendingIntent(R.id.iv_custom_playstatust,getPlayStatusPendingIntent());
	        remoteViews.setOnClickPendingIntent(R.id.iv_custom_next,getPlayNextPendingIntent());
	        return remoteViews;
	    }


* 3.处理布局信息的填充和点击事件的处理
	* 1.最外层的点击事件
	
			 public PendingIntent getRlPendingIntent() {
		        Intent intent=new Intent(this,MainActivity.class);
		        intent.putExtra("ss","最外层点击");
		        //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
		        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		        return activity;
		    }
	* 2.上一曲的点击事件
				
			  public PendingIntent getPrePendingIntent() {
			        Intent intent=new Intent(this,MainActivity.class);
			        intent.putExtra("ss","上一曲");
			        //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
			        PendingIntent activity = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			        return  activity;
			    }
	* 3.播放状态的点击事件

	
		    public PendingIntent getPlayStatusPendingIntent() {
		        Intent intent=new Intent(this,MainActivity.class);
		        intent.putExtra("ss","暂停播放");
		        //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
		        PendingIntent activity = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		        return activity;
		    }

	 * 对这几个方法的处理中  `PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT) ` 方法 都会有一个 `requestCode` 请求码，如果相同的话，更具最后的一个标志位，系有可能的会将其覆盖成最后依次设置的请求码里面的回调
	 * 换句话说，这个请求码，就是用来确定是不是使用不同点击事件回调的，一定不要相同，否则写在前面的监听就会失效

 
* 3.通过 `notificationManager`的 `notify()` 方法发送通知消息 100是一个用于取消的请求码标识
 		
		Notification  notification = getCustomNotificationByNewAPI();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(100, notification);


## 对于服务内容的补充，一个服务的启动的方式有两种 ##
服务的启动和 `activity` 是相同的，他们都是通过 `Intent` 来启动的
* 他们分别是：
	* 1.仅使用 `onStart()` 启动一个服务
	* 2.通过bindService来启动
	* 3.实现通过  `StartService()` 启动然后通过 `bindService()` 绑定服务

上面的几种方式的区别在于



* 后台的service 优先级还是不算很高的，在系统的资源不足的情况下，还是很容易的被回收资源而被杀死的，这时候又有了两个概念：
* 前台进程和后台进程
* 前台进程:
* 就是类似于我们的音乐播放器的这种，优先级比后台进程高，这时候不是特别容易的被后台杀死




-------------------
* 提升服务优先级的几种方法： 
	* **1.向系统的状态栏发送通知，将服务写成前台服务foreground service(已实践，很大程度上能解决问题，但不能保证一定不会被杀)：**

  ![](http://i.imgur.com/tuffMVP.png)
  这样的服务进程将会被提升为前台进程，优先级将会被提高

*  重写 `onStartCommand()` 方法，使用 `StartForeground(int,Notification)` 方法来启动 service。
	  注：前台服务会在状态栏显示一个通知，最典型的应用就是音乐播放器，只要在播放状态下，就算休眠也不会被杀，如果不想显示通知，只要把参数里的int设为0即可。

Java代码
 
			Notification notification = new Notification(R.drawable.logo,"wf update service is running",System.currentTimeMillis());
	        pintent=PendingIntent.getService(this, 0, intent, 0);
	        notification.setLatestEventInfo(this, "WF Update Service","wf update service is running！", pintent);
	            
	        //让该service前台运行，避免手机休眠时系统自动杀掉该服务
	        //如果 id 为 0 ，那么状态栏的 notification 将不会显示。
	        startForeground(startId, notification);

* 
	* 同时，对于通过startForeground启动的service，onDestory方法中需要通过stopForeground(true)来取消前台运行状态。
ps：如果service被杀后下次重启出错，可能是此时重发的Intent为null的缘故，可以通过修改onStartCommand方法的返回值来解决：

START_STICKY			|	如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。
	:------------------:|-------------
START_NOT_STICKY		|	“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
START_REDELIVER_INTENT	|	重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。
START_STICKY_COMPATIBILITY|	START_STICKY的兼容版本，但不保证服务被kill后一定能重启。


> Java代码
 
			Notification notification = new Notification(R.drawable.logo,"wf update service is running", System.currentTimeMillis());
	        pintent=PendingIntent.getService(this, 0, intent, 0);
	        notification.setLatestEventInfo(this, "WF Update Service","wf update service is running！", pintent);
	            
	        //让该service前台运行，避免手机休眠时系统自动杀掉该服务
	        //如果 id 为 0 ，那么状态栏的 notification 将不会显示。
	        startForeground(startId, notification);

*  
	* **2.提高service的优先级(未实践)：**
	`设置android:priority="1000"`
		*  默认启动的 Service 是被标记为 background，当前运行的 Activity 一般被标记为 foreground，也就是说你给 Service 设置了 foreground 那么他就和正在运行的 Activity 类似优先级得到了一定的提高。当让这并不能保证你得 Service 永远不被杀掉，只是提高...
		
	Xml代码
 
 
			<!-- 为了消去加上android:priority="1000"后出现的警告信息，可以设置android:exported属性，指示该服务是否能够被其他应用程序组件调用或跟它交互 -->
			<!-- 为防止Service被系统回收，可以通过提高优先级解决，1000是最高优先级，数字越小，优先级越低 -->
		<intent-filter android:priority="1000"></intent-filter>
					     </service>


* 
	* **3.把service写成系统服务，将不会被回收(未实践)：**
	  * 在 Manifest.xml 文件中设置 `persistent` 属性为 `true`，则可使该服务免受 `out-of-memory killer` 的影响。但是这种做法一定要谨慎，系统服务太多将严重影响系统的整体运行效率。



	* **4.利用ANDROID的系统广播检查Service的运行状态，如果被杀掉，就再起来(未实践)：**
		* 利用的系统广播是 `Intent.ACTION_TIME_TICK`，这个广播每分钟发送一次，我们可以每分钟检查一次Service的运行状态，如果已经被结束了，就重新启动Service。




## 小bug： 通知栏按钮点击回调，service的方法 ##
*  可以通重启  service的方式来讲信息以intent的形式传递
*  1.定义几种协议类型

	  	//定义通过通知栏 ，启动sevice的意图
	    //上一曲
	    public static final int NOTIFICATION_PRE = 0;
	    //下一曲
	    public static final int NOTIFICATION_NEXT = 1;
	    //activity 重新绑定 service 暂停回复继续播放
	    public static final int NOTIFICATION_CONTINUE = 2;
	    //状态栏暂停播放状态切换
	    public static final int NOTIFICATION_CHANGE_PLAYSTATUS = 3;


* 2.通过重启  `service` 通过 `intent` 的形式，将协议数据传递給 `service`，注意再重新启动同一个 service的时候，修通会自动的判断如果这个 service 如果存在了系统就不会从新创建，而是直接的走 `onStartCommand()` 方法，
 	
	
	
        public PendingIntent getRlPendingIntent() {    // 最外层点击的意图
            Intent intent = new Intent(getApplicationContext(), AudioPlayerActivity.class);
            intent.putExtra("notifity_Type", NOTIFICATION_CONTINUE);
            //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
            PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return activity;
        }

        public PendingIntent getPrePendingIntent() {  //上一曲的意图
            Intent intent = new Intent(getApplicationContext(), AudioService.class);
            intent.putExtra("notifity_Type", NOTIFICATION_PRE);
            //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
            PendingIntent service = PendingIntent.getService(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return service;
        }

        public PendingIntent getPlayStatusPendingIntent() {    //暂停播放的的意图
            Intent intent = new Intent(getApplicationContext(), AudioService.class);
            intent.putExtra("notifity_Type", NOTIFICATION_CHANGE_PLAYSTATUS);
            //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
            PendingIntent service = PendingIntent.getService(getApplicationContext(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return service;
        }

        public PendingIntent getPlayNextPendingIntent() {
            Intent intent = new Intent(getApplicationContext(), AudioService.class);
            intent.putExtra("notifity_Type", NOTIFICATION_NEXT);
            //导致点击事件冲突的原因就在于第二个参数requestCode，当requestCode值一样时，后面的就会对之前的消息起作用，所以为了避免影响之前的消息，requestCode每次要设置不同的内容。
            PendingIntent service = PendingIntent.getService(getApplicationContext(), 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return service;
        }


* 在 `onStartCommand(Intent intent, int flags, int startId)` 方法里，对不同的协议状态进行处理
	

		  	@Override
		    public int onStartCommand(Intent intent, int flags, int startId) {
		        if (intent == null) {   //解决手动结束进程服务崩溃
		            return super.onStartCommand(intent, flags, startId);
		        }
		        //正常的activity启动会获取不到这个值，赋值为默认值-1  ，正常处理   ，其他的 情况是通过通知栏进行切换的
		        int notifity_Type = intent.getIntExtra("notifity_Type", -1);
		
		        switch (notifity_Type) {
		            case NOTIFICATION_PRE:      //通知栏的上一曲启动的service
		                audioBinder.playPre();
		                break;
		            case NOTIFICATION_NEXT:     //通知栏的下一曲启动的activity
		                audioBinder.playNext();
		                break;
		            case NOTIFICATION_CONTINUE:  //通知栏启动界面activity的时候我们不希望界面改变我们的service播放状态，所以不进行操作
		                break;
		            case NOTIFICATION_CHANGE_PLAYSTATUS:  //通知栏的播放状态按钮点击
		                audioBinder.changPauseStatus(true);
		                break;
		            default:            //首次绑定服务通过activity启动这个service的情况
		                serializable = intent.getSerializableExtra("audioItems");
		                //当前传入的 position
		                int position = intent.getIntExtra("position", -1);
		                if (serializable == null) {   //做空指针检测，增加健壮性    null数据直接进行强转直接就崩了
		                    return super.onStartCommand(intent, flags, startId);  //遇到空指针就会直接跳过
		                }
		                audioItems = (ArrayList<AudioItem>) serializable;
		                //处理列表页选中同一首正在播放的歌曲的问题
		                if(position==currentPosition){
		                    //选中的歌曲正在播放，不做任何的处理直接的刷新界面
		                    audioBinder.notifyAudioPlayerListener();
		                }else {
		                    // 选中的歌曲和当前播放的歌曲不是同一首歌，刷新位置直接播放
		                    currentPosition=position;
		                    audioBinder.playCurrentMusic();
		                }
		
		
		                break;
		        }
		
		        return super.onStartCommand(intent, flags, startId);
		    }



## bug： 点击状态栏通知启动一个activity的时候， 会发现每次会启动一个新的 activity  ##

* 更改启动的模式为单例： 

* 在 XML文件里添加一下代码：
 
		<activity
	            android:name=".ui.activity.AudioPlayerActivity"
	            android:launchMode="singleTop"
	            android:screenOrientation="portrait"
	        />


## 列表页进入，点击同一首歌重复播放的bug在上面多额代码中已经解决了 ##


## 总结：查看我们整个的acivity 和 service 发现我们的activity和 service 完全解耦的，纵使activity创建还是销毁我们的数据是不受应影响的，并且比如service 通知界面ui更新的部分，在activity消失的时候调用也不会出现空指针，代码非常的完美 ##





## 自定义 view ， 处理歌词显示 ##
* 创建自定义类 `LyricsView` 继承自 `TextView` 不要直接的继承view 这样的话需要自己处理 `onMessure()` 方法，集成view的子类，可以不用自己去处理控件测量的问题
* 这部分的重点知识就是，自定义控件绘制文本



## 绘制单行文本显示 ##
* 文本绘制基本思想：
* 通过画笔和画布来绘制，绘制大基本的方法是 `canvas.drawText(text, drawX, drawY, mPaint);` 这里就需要绘制文本，绘制起始的横坐标，纵坐标，和绘制的画笔，以及对画笔设置对应的属性
* 首先解决文字


		   // 绘制一行文本
	        String text = "正在加载歌词....."; 
* 然后解决绘制的文字起始的横坐标，纵坐标的计算
	* 应该了解的是，如果没有对用的设置，文字初始绘制的坐标原点是：左下角的（0，0）点，这样一来文字绘制的坐标就有了
	* 摆放起始横坐标 = 控件宽度的一半 - 文字宽度的一半
	* 摆放起始纵坐标 = 控件高度的一般 + 文字高度的一半
	* 那么问题来了： 需要计算自定义View的宽高这个怎么获取
		* 一般的自定义控件宽高的获取是在方法 `onSizeChanged()` 中得到的（**这个是以后自定义控件的基础**）

				/**
			     * 常见的自定义组合控件 大多有两种
			     * 1、在onSizeChanged里面写
			     * 2、在onFinishInflate里面写
			     * 这个是系统回调方法，是系统调用的，这个方法会在这个view的大小发生改变时被系统调用，
			     * 只要view大小变化，这个方法就被执行就可以了
			     *
			     * @param w    变化后的控件的宽度
			     * @param h    变化后的控件的高度
			     * @param oldw 变化前。。
			     * @param oldh
			     */
			    @Override
			    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			        //记录变化后的控件的宽高
			        mViewWith = w;
			        mViewHeight = h;
			    }

* 		
 	*  
 	  * 那么文字的宽和高怎样去获取呢
 	  * 很简单： 通过画笔绘制的文字，他的属性一定会保存在画笔里面，可以同过画笔来解决
 	  *  `mPaint.getTextBounds(text, 0, text.length(), rect);` 可以获取单个文字的宽高，也可以获取到多个文字的宽高
 	

				Rect rect = new Rect();
		        //文字的宽度属性可以通过画笔获取,可以测量一个文字，多个文字的上下左右和宽高的属性
		        mPaint.getTextBounds(text, 0, text.length(), rect);
		        //居中显示一行文字
		        // 计算摆放控件的位置   摆放起始横坐标=控件宽度的一半 - 文字宽度的一半
		        int drawX = mViewWith / 2 - rect.width()/2;
		        int drawY = mViewHeight / 2 + rect.height()/2;
		        canvas.drawText(text, drawX, drawY, mPaint);
	
	

![](http://i.imgur.com/XSQGzkf.png)

## 完成多行文本的绘制，并通过偏移量实现滚送加载 ##

![](http://i.imgur.com/FuhiUHd.png)

* 通过对居中行（参考行，进行Y轴的偏移实现滚动效果）
	 * Y 位置的偏移量 = 行高 * 时间进度百分比
     * 行高 = 字体的高度 + 行间距
     * 时间百分比 = 行已用时间 / 两句歌词的时间差
     * 行已用时间 = 播放时间 - 上一句歌词的起始时间
     * 两句歌词的时间差 = 下一句的时间 - 上一句的时间


		 	/**
		     * 多行文本绘制，遵循公式
		     * drawY= centerY + (postion - centerPostion) * （行间距 + 文字的高度）
		     *
		     * @param canvas
		     * @param rect
		     */
		    private void DrawMultleLines(Canvas canvas, Rect rect) {
		        for (int i = 0; i < lyricses.size(); i++) {
		            String content = lyricses.get(i).getContent();
		
		            if (i == mCenterLine) {    //当前绘制的行是中间行
		                //画笔颜色，字体大小
		                mPaint.setColor(mHeightLightTextColor);
		                mPaint.setTextSize(mHeightLightTextsize);
		            } else {
		                mPaint.setColor(mOrdinaryTextColor);
		                mPaint.setTextSize(mOrdinaryTextsize);
		            }
		            //文字的宽度属性可以通过画笔获取,可以测量一个文字，多个文字的上下左右和宽高的属性
		            mPaint.getTextBounds(content, 0, content.length(), rect);
		            //居中显示一行文字
		            // 计算摆放控件的位置   摆放起始横坐标=控件宽度的一半 - 文字宽度的一半
		
		
		            //重新计算每一行的高度
		            float lineHeight = (rect.height() + mContext.getResources().getDimension(R.dimen.lyrics_tv_Padding));
		            int centerY = mViewHeight / 2 + rect.height() / 2;
		            //自动滚动需要计算中心点的偏移
		            float offSetY = getCenterLineOffset(lineHeight);
		
		            //每一行的位置都会在原有位置的基础上进行偏移
		            float drawY = centerY + (i - mCenterLine) * lineHeight - offSetY;
		
		            //绘制单行文本
		            DrawSimpleLine(canvas, content, drawY);
		        }
		
		    }
		
		    /**
		     * 给定纵坐标，绘制水平居中的文本
		     *
		     * @param canvas
		     * @param text   绘制的文本
		     * @param drawY  给定文本绘制的高度坐标
		     */
		    private void DrawSimpleLine(Canvas canvas, String text, float drawY) {
		        float width = mPaint.measureText(text);   //文本的宽度
		        float drawX = mViewWith / 2 - width / 2;   //居中绘制
		        canvas.drawText(text, drawX, drawY, mPaint);
		    }

* 这里对最后的一行文本的处理是一个很有参考性的地方
    * 每一行都有一个起始的时间，同样的每一行在向上滚动，知道切换下一行的时间使用 `i` 行和 `i+1` 行计算得到的
    * 但是我们最后一行的时候的下一行是没有的，获取时间点事越界的，这时候的处理可以使用，一首歌的全部时长作为下一句的时长
    * 这样就可以按照时间比例滚动歌词了




## 实现文本滚动绘制效果 ##

* 1.加载文本  ： 在 `AudioPlayerActivity` 中调用 `upDataLyrics();` 刷新歌词，并通过 handler不停的发送消息，实现文本的滚动绘制，其实原理是由一幅幅的静态的view组成的

		 /**
	     * 歌词实时滚动刷新
	     */
	    private void upDataLyrics() {
	        //调用控件的方法刷新歌词
	        lrcvAudio.calculateCenterLine(audioBinder.getCurrentPosition(), audioBinder.getDuration());
	        //100ms刷新一次歌词
	        handler.sendEmptyMessageDelayed(MSG_UPDATALYRIC, 2);
	    }

	
## 实现歌词的解析 ##

* 创建类 `LyricParse` 
* 歌词这种格式的 `[01:22.04][02:35.04]寂寞的夜和谁说话`
* 首先经过健壮性判断，防止崩溃
* 歌词都是通过文件读取的，在读取的时候编码问题需要注意
	* 手机设备默认使用的编码的方式是 `utf-8` 当时，通过电脑直接编辑的歌词一般是 `GBK` 编码的 
	* 因此使用 	`BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));` 指定对应的编码格式，来加载歌词

* 通过正则匹配来实现歌词的截取，和格式的判断
*  `if (content.matches("]")) {   //没有时间戳的内容暂时不解析`
	`return null;`
	`}`

* 在后来的维护测试中发现最英明的一点就是，对有可能崩溃的代码部分做了  `try(){ }catch{ }` 操作 ，后面的好多有问题的歌词竟然正确 的解析了
* 在这里总结try 。。。。catch。。。异常捕获以后应该尽量多的使用

		/**
	     * 歌词文件解析成 list 集合
	     */
	    public static ArrayList<LyricBeen> parseFile(File file) {
	        ArrayList<LyricBeen> lyricses = new ArrayList<>();
	
	        if (file == null || !file.exists()) {   //做一定的健壮性检查
	            lyricses.add(new LyricBeen(0, "正在加载歌词..."));
	            return lyricses;    //剩下的方法不在执行
	        }
	        try {
	            // 在不同的播放设备上，歌词的编码格式是不相同的，必须制定对应的编码   默认 "utf-8"
	            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
	            String content = null;
	            while ((content = reader.readLine()) != null) {
	                //对当行歌词进行解析
	                ArrayList<LyricBeen> lyricBeens = parseSimpleLine(content);
	                //将解析后的歌词集合添加到集合容器中
	                if (lyricBeens != null && lyricBeens.size() != 0) {
	                    lyricses.addAll(lyricBeens);
	                }
	
	            }
	
	
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        //要想实现排序，必须填家这一行代码。这个才是排序的真正的代码
	        Collections.sort(lyricses);
	        return lyricses;
	    }
	
	    /**
	     * 解析当行歌词
	     * //[01:22.04][02:35.04]寂寞的夜和谁说话
	     *
	     * @param content 需要解析的文本的内容
	     * @return 解析后的歌词内容
	     */
	    private static ArrayList<LyricBeen> parseSimpleLine(String content) {
	        ArrayList<LyricBeen> lyricList = new ArrayList<>();
	        if (TextUtils.isEmpty(content)) {
	            return null;  //空内容直接返回
	        }
	
	        //[01:22.04][02:35.04]寂寞的夜和谁说话
	        if (content.matches("]")) {   //没有时间戳的内容暂时不解析
	            return null;
	        }
	        //切割后的文本  [01:22.04    [02:35.04    寂寞的夜和谁说话
	        String[] split = content.split("]");
	        String text = split[split.length - 1];   //最后的一个是歌词的内容
	        for (int i = 0; i < split.length - 1; i++) {
	            //将时间解析出来
	            int startPoint = parseTime(split[i]);
	            //歌词文件格式不正确的处理
	            if (startPoint == -1) {  //歌词文件格式错误
	                lyricList.clear();
	                lyricList.add(new LyricBeen(0, "歌词文件格式错误"));
	                return lyricList;
	            }
	            lyricList.add(new LyricBeen(startPoint, text));
	        }
	
	        return lyricList;
	    }
	
	    /**
	     * 将这样的文本字符串解析成毫秒值  [01:22.04
	     *
	     * @param s
	     * @return
	     */
	    private static int parseTime(String s) {
	        int startPoint=-1;
	        try {
	            if (TextUtils.isEmpty(s)) {
	                return 0;   //文本错误返回当前的时间为0
	            }
	            //[01    22.04
	            String[] splitH = s.split(":");
	            //22  04
	            String[] split1L = splitH[splitH.length - 1].split("\\.");
	
	            int minute = Integer.parseInt(splitH[0].substring(1, splitH[0].length()));
	            int second = Integer.parseInt(split1L[0]);
	            int millsecond = Integer.parseInt(split1L[1]);
	
	            startPoint = minute * 60 * 1000 + second * 1000 + millsecond * 10;
	            //将时间转换成毫秒值
	
	        } catch (Exception e) {
	            startPoint=-1;
	        }
	        return startPoint;
	
	    }

* 这里中点的解释一下一个难点：
* 就是解析完成的歌词比一定就是安装时间的顺序排序好的，这时候，就必须的对集合的顺序进行重新的排序
* **排序的过程：**
* 1. 对应的集合泛型的实体been实现接口 `Comparable` 


		 	public class LyricBeen implements Comparable<LyricBeen>{

				.....

			}


* 2.重写 `compareTo()` 方法
		

			public class LyricBeen implements Comparable<LyricBeen>{

				.....

			  /**
			     * 对歌词按照时间进行排序
			     * @param another
			     * @return
			     */
			    @Override
			    public int compareTo(LyricBeen another) {
			
			        return startPoint - another.getStartPoint();
			    }

				.....

			}

* 3.你一为这样就完了么，错了，最重要的异步就是调用排序的方法，对集合进行排序
* 在集合填充好的地方调用一下的代码。实现集合的排序


	 		//要想实现排序，必须填家这一行代码。这个才是排序的真正的代码
	        Collections.sort(lyricses);
	
* 当然也有一部分集合是不支持排列顺序的，set集合，本身就是无序的，排序是没有价值的


## 加载歌词，不应该仅仅的局限在一中格式的歌词上面，应该做一个歌词解析器 ##

* `LyricsLoader` 可以加载本地的不同的目录下面的歌词文件，同时也可以加载lrc，txt，格式的文件，其实最终要的是还可以去服务器下载对应的歌词

	
		  public static File LoadLrcFile(String tittleName) {
		        //与我们存放歌曲和歌词相关的跟目录
		        File rootFile = new File(Environment.getExternalStorageDirectory(), "/music/");
		        //在本地根目录查找 .lrc 和 .txt 文件
		        File file = new File(rootFile, tittleName + ".lrc");
		        if (file.exists()) {
		            return file;
		        }
		        file = new File(rootFile, tittleName + ".txt");
		        if (file.exists()) {
		            return file;
		        }
		        //在本地lrc文件目录查找 .lrc 和 .txt 文件
		        file = new File(rootFile, "lrc/" + tittleName + ".lrc");
		        if (file.exists()) {
		            return file;
		        }
		        file = new File(rootFile, "lrc/" + tittleName + ".txt");
		        if (file.exists()) {
		            return file;
		        }
		
		        //去服务器查找下载
		        //......
		
		        //如果还是没有找到
		        return null;
		    }

