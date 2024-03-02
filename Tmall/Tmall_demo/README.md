# 迷你天猫商城
+ **作者联系方式（备注：天猫商城）可接受二次开发改造需求（如支付宝沙箱支付），项目功能、源码、数据库远程一对一讲解：**
+ <img src="mmqrcode1672065137273.png" alt="问题交流微信群" width="30%" height="30%"/>
+ **前台演示地址（服务器到期暂时停用）：<https://xianqu.fun/tmall/>**
+ **2023/4/9：我们为本项目开发了一些新的风格主题，有需要可以微信联系作者** 
+ **2022/4/21：我们基于本项目二次开发了一个二次元动漫主题的商城项目，增加了支付宝沙箱支付的功能，商城整体主题改为二次元动漫风格，替换了所有原有的天猫素材和元素，整体色调从天猫红改成卡通黄，增加大量动漫主题素材，方便同学们用于答辩和毕设场景扩展开发，有需要可以微信联系作者，部分示例图如下：**
+ ![示例1](%E6%BC%AB%E7%94%BB%E4%B8%BB%E9%A2%98%E5%95%86%E5%9F%8E%E7%A4%BA%E4%BE%8Ba.png)
+ ![示例2](%E6%BC%AB%E7%94%BB%E4%B8%BB%E9%A2%98%E5%95%86%E5%9F%8E%E7%A4%BA%E4%BE%8Bb.png)
+ ![示例3](%E6%BC%AB%E7%94%BB%E4%B8%BB%E9%A2%98%E5%95%86%E5%9F%8E%E7%A4%BA%E4%BE%8Bc.png)
### 介绍
迷你天猫商城是一个基于Spring Boot的综合性B2C电商平台，需求设计主要参考天猫商城的购物流程：用户从注册开始，到完成登录，浏览商品，加入购物车，进行下单，确认收货，评价等一系列操作。
作为迷你天猫商城的核心组成部分之一，天猫数据管理后台包含商品管理，订单管理，类别管理，用户管理和交易额统计等模块，实现了对整个商城的一站式管理和维护。

所有页面均兼容IE10及以上现代浏览器。

### 部署方式
1. 项目使用IntelliJ IDEA开发，请使用IntelliJ IDEA的版本控制检出功能，输入“<https://gitee.com/project_team/Tmall_demo.git>”拉取项目即可。
2. 项目数据库为MySQL 5.7版本，在**sqls文件夹**中找到SQL文件并导入到数据库中。
3. 使用IDEA打开项目后，在maven面板刷新项目，下载依赖包。
4. 配置数据库连接并启动SpringBootApplication即可。

### 项目默认运行地址
+ 前台地址：<http://localhost:8080/tmall>
+ 后台地址：<http://localhost:8080/tmall/admin>

### 注意事项：
1. 后台管理界面的订单图表没有数据为正常现象，该图表显示的为近7天的交易额。
2. 该项目同时兼容eclipse，但如有自行扩展代码的意愿，建议使用IDEA。
3. 该项目是我们几个学生在校合作完成的一个练习项目，目的是让编程初学者和应届毕业生可以参考一下用较少的代码实现一个完整MVC模式，Spring Boot体系的电商项目，相关领域大神们可以给我们建议，让我们做得更好。

### 项目界面
+ ##### 后台界面(部分)---
![主页](https://images.gitee.com/uploads/images/2019/0720/132736_629d409d_1616166.png "主页.png")
![所有产品](https://images.gitee.com/uploads/images/2019/0720/132752_a9065bdc_1616166.png "所有产品.png")
![产品详情](https://images.gitee.com/uploads/images/2019/0720/132804_07364d8e_1616166.png "产品详情.png")
![产品分类](https://images.gitee.com/uploads/images/2019/0720/132815_4fa23e1c_1616166.png "产品分类.png")
![分类详情](https://images.gitee.com/uploads/images/2019/0720/132824_0392314c_1616166.png "分类详情.png")
![用户管理](https://images.gitee.com/uploads/images/2019/0720/132840_582530ca_1616166.png "用户管理.png")
![用户详情](https://images.gitee.com/uploads/images/2019/0720/132849_481238d6_1616166.png "用户详情.png")
![订单列表](https://images.gitee.com/uploads/images/2019/0720/132912_190142c1_1616166.png "订单详情.png")
![订单详情](https://images.gitee.com/uploads/images/2019/0720/132926_0393d549_1616166.png "订单详情2.png")
![我的账户](https://images.gitee.com/uploads/images/2019/0720/132934_e0132cc9_1616166.png "我的账户.png")
+ ##### 前台界面(部分)---
![登陆界面](https://gitee.com/uploads/images/2018/0526/223030_17b28619_1616166.png "2018-05-26_221715.png")
![首页](https://gitee.com/uploads/images/2018/0526/223018_14e999f1_1616166.png "2018-05-26_221703.png")
![产品详情](https://gitee.com/uploads/images/2018/0526/223044_e481ec5f_1616166.png "2018-05-26_221725.png")
![下单界面](https://gitee.com/uploads/images/2018/0526/223100_ef6e9612_1616166.png "2018-05-26_221837.png")
![订单列表](https://gitee.com/uploads/images/2018/0526/223117_dfd64b43_1616166.png "2018-05-26_221901.png")
![确认收货](https://gitee.com/uploads/images/2018/0526/223220_71e2ee3d_1616166.png "2018-05-26_221911.png")
![产品列表](https://gitee.com/uploads/images/2018/0526/223233_18e131a5_1616166.png "2018-05-26_222006.png")
![购物车](https://gitee.com/uploads/images/2018/0526/223245_3f80d8f4_1616166.png "2018-05-26_223157.png")

### 作者的话

首先感谢您看到这里

本项目是我和其他两个朋友空闲时间在校合作完成的一个商城demo

前后台业务代码，都是自主完成，后台都是专门设计的样式，前台是参考天猫自行开发界面

技术日新月异，目前我们在使用更流行的前后台技术栈，从而开源更多优质项目

但我们仍记得，我们对这个项目付出的热情和精力

在校开发不易，如对您有帮助，您可以给予我们一点支持!

+ ##### 支付宝
<img src="https://images.gitee.com/uploads/images/2021/0908/120223_b882a30f_1616166.png" alt="支付宝打赏二维码" width="128px" height="128px"/>

+ ##### 微信
<img src="https://images.gitee.com/uploads/images/2021/0908/120333_894f5c7e_1616166.png" alt="微信打赏二维码" width="128px" height="128px"/>

### 关于拓展
近期本项目通过改造，将原来war部署方式改成了jar部署，进入到target目录，仅需要java -jar tmall.jar即可完成部署启动。

另本项目已使用当前较为流利的CICD方式，jenkins+k8s进行持续构建、部署，涉及文件:Dockerfile,K8sJenkinsFile及deploy.yaml

详情可参考：或者点击[链接](https://mp.weixin.qq.com/s?__biz=MzI0NzI3ODcxOA==&mid=2650174707&idx=1&sn=4faf5d058891993dc838f3df0ec678d3&chksm=f1b0a9e2c6c720f41762c5371dbb26987e49aac2acc180e9ff773f1b29af9579f213a38c5bee#rd)

```
https://mp.weixin.qq.com/s?__biz=MzI0NzI3ODcxOA==&mid=2650174707&idx=1&sn=4faf5d058891993dc838f3df0ec678d3&chksm=f1b0a9e2c6c720f41762c5371dbb26987e49aac2acc180e9ff773f1b29af9579f213a38c5bee#rd
```

Dockerfile:大概作用是依据基础的jdk镜像，添加上自己本身的jar包，打成镜像包

K8sJenkinsFile:大概作用是把生成jar包、部署jar包流程自动化

deploy.yaml:此文件为k8s deployment svc资源模板化文件

k8s部署的项目访问网址:

+ 前台地址：http://tmall.howlaisi.com:31253/tmall
+ 后台地址：http://tmall.howlaisi.com:31253/tmall/admin

具体情况可联系以下同学(加他时请备注"贤趣CICD"):

```
ggttxlss
```

