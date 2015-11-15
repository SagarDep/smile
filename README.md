# smile

对之前的App进行了重构,不但可以欣赏妹纸，还可以看笑话两不误

###项目中用到的知识点：
   * Jsoup: 通过Jsoup 解析Html 拿到笑话数据
   * OkHttp:OkHttp和 Retrofit 配合使用获取干货网妹纸 url
   * Android Design Support Library库的简单使用
   * litepal 数据库存取操作
   * RecyclerView 的使用
   * Glide 库加载图片
   * 加入了优雅的 Activity 之间转场动画
   * Butterknife对View 注解绑定
   * 分享图片和文字
   * RxAndroid 的使用

###11.08更新
   * 加入收藏妹纸功能
   * 修改UI配色
   * 修改代码，去掉一些冗余的代码

###11.01更新
   * 在妹纸详情activity中加入了下拉退出activity的功能，参考了[pull-back-layout](https://github.com/oxoooo/pull-back-layout)库，之所以没用pull-back-layout原因是PhotoView和ViewDrapHelper一起用时，图片缩小会发生异常，程序会crash掉，所以自己重新写了个Layout处理了一下事件分发解决了异常。

###10.31更新
   * 修改妹纸图片UI，去掉Toolbar看起来更有趣了
   * 妹纸图片可以缩放了，放大看起来也不错

###10.29 更新
   * 加入 reveal 动画提示笑话
   * 修改UI 界面
   * 修改UI 配色

###10.4 更新
   * 修复了之前妹纸图片页面滑动的小bug
   * 加入了直接分享妹纸图片功能(哈,参考Drakeet大神的妹纸应用的)
   * 可以看干货网休息视频了(额,效果有点挫)
   * 后续可能会更改App 界面,因为层级深了点
   

<img src="/screenshots/Screenshot_2015-11-08-12-44-14-215.png" alt="screenshot" title="screenshot" width="270" height="490" /> <img src="/screenshots/Screenshot_2015-11-08-12-44-21-616.png" alt="screenshot" title="screenshot" width="270" height="490" />
<img src="/screenshots/Screenshot_2015-11-15-14-04-08.png" alt="screenshot" title="screenshot" width="270" height="490" />
<img src="/screenshots/Screenshot_2015-11-15-14-04-16.png" alt="screenshot" title="screenshot" width="270" height="490" />
<img src="/screenshots/Screenshot_2015-11-15-14-04-25.png" alt="screenshot" title="screenshot" width="270" height="490" />
<img src="/screenshots/Screenshot_2015-11-15-14-04-33.png" alt="screenshot" title="screenshot" width="270" height="490" />

###致谢
* [代码家](http://blog.daimajia.com/)和他的[干货集中营](http://gank.io/)
* [Drakeet](http://drakeet.me/)和他的妹纸应用[妹纸&gank.io](https://github.com/drakeet/Meizhi)
* [oxoooo/pull-back-layout](https://github.com/oxoooo/pull-back-layout)
* [oxoooo/mr-mantou-android](https://github.com/oxoooo/mr-mantou-android)


###LICENSE
[LICENSE](https://github.com/Assassinss/smile/blob/master/LICENSE)
