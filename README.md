# PullRecyclerView
支持下拉刷新，上拉加载更多、点击footer加载更多、添加section头部View、添加尾部View.
![image](https://github.com/hhllnw/PullRecyclerView/tree/master/app/src/main/res/drawable/img_section.gif)


```JAVA
Add it in your root build.gradle at the end of repositories:
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency
    	dependencies {
    	        compile 'com.github.hhllnw:PullRecyclerView:v1.0'
    	}
```

```JAVA
一.为了方便使用，在项目中写了一个BaseListActivity的基础列表Activity,这个BaseListActivity可以继承自自己项目中BaseActivity
的基础Activity,然后implements PullRecycler.OnRecyclerRefreshListener这个接口，重写onPullRefresh(int action)这个方法，
上拉加载、下拉刷新都在这个方法中操作。若action==PullRecycler.ACTION_PULL_REFRESH表示下拉刷新，
若action == PullRecycler.ACTION_MORE_ACTION则表示加载更多。Adapter要继承BaselistAdapter实现其中的方法。
如果在Fragment中也要使用的话最好也写一个BaseListFragment，实现同BaseListActivity.
二.使用方法：
  //A LayoutManager must be provided for RecyclerView to function.
  //example:ILinearLayoutManager,IGridLayoutManager,IStaggeredGridLayoutManager
  1.mPullRecycler.setLayoutManager(getLayoutManager());

  //分割线
  2.mPullRecycler.addItemDecoration()

  //初始化页面后首次调用
  3.mPullRecycler.setFirstRefresh();

  //是否可以加载更多
  4.mPullRecycler.setIsCanLoadMore(true);

  //是否可以下拉刷新，默认是可以的
  5.mPullRecycler.setIsCanRefresh(true);

  //加载更多时，是选择滑动到底部加载还是手动点击加载
  6.mPullRecycler.setLoadMoreModle(PullRecycler.LoadMoreModle.click);

  //定位到某个位置
  7.mPullRecycler.scrollToPosition(int position)

  //滑动到某个位置
  8.mPullRecycler.smoothToPosition(int position)

  //添加footerView
  9.mPullRecycler.addFooterView(true, footerView);

  //footerView监听事件
  10.mPullRecycler.setOnClickFooterViewListener()

  需要注意的是setLayoutManager()方法必须在setAdapter()之前调用。


```