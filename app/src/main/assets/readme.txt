1. 当一个应用安装在设备上，只有该设备支持的CPU架构对应的.so文件会被安装：
   1) 在x86设备上，libs/x86目录中如果存在.so文件的话，会被安装
   2) 如果不存在，则会选择armeabi-v7a中的.so文件
   3) 如果也不存在，则选择armeabi目录中的.so文件（因为x86设备也支 持armeabi-v7a和armeabi）

2. 启动页(AdActivity)：
   translate + alpha动画，动画结束时显示，1s钟之后，自动跳转。

3. 登陆页面(LoginActivity)：
   环信注册 与 环信登陆，集成的是环信3.0。

4. 设置引导页(GuideActivity)：
   9个Fragment(2层继承) + WheelView。

5. 主界面(HomeActivity)：
   1) 3个Fragment(SportFragment、MessageFragment、MyselfFragment)
   2) 侧滑(支持左边侧滑、右边侧滑，可随意切换)，2种侧滑效果(DrawerLayout + SlidingMenu)，SlidingMenu又支持三种模式(普通 + 抽屉 + 仿qq，可任意切换)。xml中利用fragment标签，将侧滑代码单独放在Fragment中，方便代码管理
   3) 设置聊天界面表情面板切换方式(滑动切换 + 点击tab切换)

6. 运动界面(SportFragment)：
   1) 注册step counter计步;
   2) 记录每小时步数(app运行时) 或 进行步数分配(app关闭中，刚打开时)
   3) PullToRefreshScrollView、ViewPager + 自定义SportView可显示30天数据

7. MessageFragment(聊天界面)：
   1) 支持发文字、表情、语音
   2) 表情面板 + 更多面板 与 输入法切换自然
   3) 添加好友
   4) 获取好友列表

8. MyselfFragment(个人界面)：
   1) 布局中利用fragment标签，分开到CoverFragment 和 MyselfFragment_1这两个Fragment中，避免一个类中处理大量代码
   2) MyselfFragment_1模块采用自定义preference，将layout中一部分xml文件移至res/xml(直接指定启动意图)下，省去item的点击事件代码
   3) MyselfFragment_1模块计步器设置、扫一扫(依托于google.zxing封装)、app介绍、关于等功能
   4) 计步器设置(SportSettingActivity)，继承PreferenceActivity，依然加载res/xml下的布局文件，重写onPreferenceTreeClick()方法处理点击事件，可对当前登录用户设置目标步数、体重、步幅、朝朝起止时间、朝朝目标部署、暮暮起止时间、暮暮目标步数进行设置
   5) CoverFragment模块可设置封面 和 头像
   6) 个人信息界面(PersonalInfoActivity)，继承PreferenceActivity，可设置性别、身高、体重、城市 以及 扫描二维码

9. app仅以环信为服务器，记录的运动数据手机本地保存，卸载app数据将丢失。