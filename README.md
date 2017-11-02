# ObjectBoxExample
ObjectBox是移动端数据库框架，灵感来自于NoSql，速度非常快，号称是市面上最快的移动端数据库框架。目前非关系行数据库也就只有Realm 能与之相比。本文将会探讨两个框架的取舍。

一、为什么要使用ObjectBox？

官方给出来的有五大特性：

1、快: 比测试过的其它数据库快 5~15 倍

2、面向对象的 API: 没有 rows、columns 和 SQL，完全面向对象的 API

3、即时的单元测试: 因为它是跨平台的，所以可以在桌面运行单元测试

4、简单的线程: 它返回的对象可以在任何线程运转

5、不需要手动升级和迁移: 升级是完全自动的，不需要关心属性的变化以及命名的变化

二、如何安装？

1、首先在根目录的gradle 文件中添加：

buildscript {

          repositories {

          jcenter()

          maven { url "http://objectbox.net/beta-repo/" }

  }

dependencies {

         classpath 'com.android.tools.build:gradle:2.3.2'

         classpath 'io.objectbox:objectbox-gradle-plugin:0.9.12.1'

   }

}

2.在你的项目gradle 文件中添加：

apply plugin: 'com.android.application'

apply plugin: 'io.objectbox'

repositories {

        jcenter()

        maven { url "http://objectbox.net/beta-repo/" }

}

dependencies {

       compile 'io.objectbox:objectbox-android:0.9.12'

}

ps.此时gradle 的时候有可能图1出现的问题：


图1


这问题是因为，AS2.3.3版本会默认把Expresso框架自动引入，谷歌默认的现在是3.0.5版本，而Objectbox中使用的是3.0.2版本，有个版本冲突，所以统一一下版本即可，在gradle dependency中添加即可：

androidTestCompile'com.google.code.findbugs:jsr305:3.0.2'

三、如何使用？

1.初始化

在 Application 中初始化boxStore = MyObjectBox.builder().androidContext(App.this).build();

2.对象注解

ObjectBox跟其他的ORM框架一样，通过对象属性注解来决定是否要持久化某个对象，或者某个属性。接下来看看常见的注解。




图2
如图2所示，这是一个Categoty 对象，

@Entity：这个对象需要持久化。

@Id：这个对象的主键。

@Index：这个对象中的索引。对经常大量进行查询的字段创建索引，会提高你的查询性能。

@NameInDb：有的时候数据库中的字段跟你的对象字段不匹配的时候，可以使用此注解。

@Transient:如果你有某个字段不想被持久化，可以使用此注解。

@Relation:做一对多，多对一的注解。

需要注意的是：默认情况下，id是会被objectbox管理的，也就是自增id，如果你想手动管理id需要在注解的时候加上@Id(assignable = true)即可。当你在自己管理id的时候如果超过long的最大值，objectbox 会报错。id=0的表示此对象未被持久化，id的值不能为负数。

3.增删查改

（1）新增

调用box put 方法即可完成新增。


图3
(2) 删除   

调用box remove 方法即可完成删除。

roleBox.remove(2);//删除id=2的对象

roleBox.removeAll();//清空所有表对象

（3）查询

首先要获取 Box 对象，然后通过 QueryBuilder 查询，以下是一个找出角色名称以采字开头的角色以及或者角色等于“运营”人员的例子：

List item =roleBox.query().startsWith(Role_.role_name,"采")

.or().equal(Role_.role_name,"运营")

.orderDesc(Role_.created_at).build().find();

QueryBuilder 还提供了形如greater、less、contain等 API，使用非常方便。

（4）修改

调用put 方法，即可完成更新动作。


4、事务

框架提供了四个事务机制：

runInTx:在给定的runnable 中运行的事务。

runInReadTx:只读事务，不同于runintx，允许并发读取。

runInTxAsync:运行在一个单独的线程中执行，执行完成后，返回callback。

callInTx:与runInTx 相似，不同的是可以有返回值。

看这个例子：

新增30000个role 对象：


图4
此例子会有什么问题？

5、数据库升级

在新增和删除字段的时候，基于NoSql的特性ObjectBox会自动的升级你的数据库。不像Sqlite数据库需要准备版本号，准备数据库的sql升级脚本，这点来看是十分方便的。如果你有在原来的基础上修改字段的需求，可以这样做：

1、在你要修改的表添加@Uid注解。


图5
2、编译项目，make project


图6
3、直接修改你要的字段


图7
6、与RxJava实现的数据监听

ObjectBox提供了RxJava的扩展，实现了数据库操作的全程异步，以及数据异步监听机制。对UI性能毫无影响。接下来看个简单的例子：

监听Role表的数据，如果有新增，则更新UI列表数据。


图8
值得注意的一点：在页面关闭的时候记得：subscription.cancel() 记得取消监听动作。

四、与Realm 性能比较

1、新增性能

同时插入10000、50000、100000条数据的时间对比：


可以看出，ObjectBox速度是Realm的3-4倍左右。



2.删除性能

同时删除10000、50000、100000条数据的时间对比：

可以看出ObjectBox速度是Realm的2倍左右。


3.更新新能

同时更新10000、50000、100000条数据时间对比：


可以看出ObjectBox速度是Realm的2倍左右。

五、总结

ObjectBox相对而言操作十分简单、易用。重点是还超快。我们没有理由不用他，相对Realm而言，目前ObjectBox只支持Java和android，官方已经宣布会支持ios 和mac系统。

作者：Hamsing
链接：http://www.jianshu.com/p/38c5d6f239d2
來源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
