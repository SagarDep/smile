# smile
一个纯粹看笑话的app ，material design 风格，支持下拉刷新，上拉加载更多，支持数据库缓存，无网络状态下也能浏览笑话

下面是app 中用到了一些库，其中 jsoup和litepal的jar 包在libs文件目录下有<br>
<html>
 <head></head>
 <body>
  dependencies {
  <br /> compile fileTree(dir: 'libs', include: ['*.jar'])
  <br /> compile 'com.android.support:appcompat-v7:22.2.0'
  <br /> compile 'com.android.support:recyclerview-v7:22.2.0'
  <br /> compile 'com.android.support:cardview-v7:22.2.0'
  <br /> compile 'com.android.support:design:22.2.0'
  <br /> compile files('libs/jsoup-1.7.2.jar')
  <br /> compile files('libs/litepal-1.2.0-src.jar')
  <br /> }
 </body>
</html>

app截图：
![image](https://github.com/Assassinss/smile/blob/master/screenshots/Screenshot_2015-07-19-16-14-24-723.png)  ![image](https://github.com/Assassinss/smile/blob/master/screenshots/Screenshot_2015-07-21-12-18-27-486.png)
