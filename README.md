# ObjectBoxExample

ObjectBox官网：http://objectbox.io/

优点：

速度快，号称比目前主流数据库架构快 5-15 倍
NoSql，没有 rows、columns、SQL，是完全面向对象的 API
数据库升级做到完全自动
缺点是目前仍没有发布正式版本，目前版本号为 V.0.9.13（beta），不过感觉也快了，最新版本甚至增加了 Kotlin 的支持。

简单使用
依赖
根目录下 build.gradle ：

'''java

buildscript {
          repositories {
                  jcenter()
              maven { url "http://objectbox.net/beta-repo/" }
      }

    dependencies {
             classpath 'com.android.tools.build:gradle:2.3.2’
             classpath 'io.objectbox:objectbox-gradle-plugin:0.9.12.1'
    }
}

'''


app 下 build.gradle：


'''java
apply plugin: 'com.android.application'

    apply plugin: 'io.objectbox'

    repositories {
          jcenter()
          maven { url "http://objectbox.net/beta-repo/" }

    }

    dependencies {
         compile 'io.objectbox:objectbox-android:0.9.12'
    }
    
'''


初始化
官方推荐在 Application 中初始化 ObjectBox 的实例：
'''java
private static BoxStore mBoxStore;
@Override
public void onCreate() {
    super.onCreate();
    mBoxStore = MyObjectBox.builder().androidContext(this).build();
}
public BoxStore getBoxStore(){
    return mBoxStore;
}

'''


不要忘了在 AndroidManifest 引用自定义的 Application，然后在代码中获取：

notesBox = ((App) getApplication()).getBoxStore().boxFor(TestObjectBoxBean.class);
有一点要提一下， 如果是第一次引入 ObjectBox，这里的 MyObjectBox 是找不到的，创建了对应的实体类后 Rebuild Project 才会出现。

数据模型
和现在流行的架构一样，ObjectBox 的数据模型使用注解的方式定义：
'''java
@Entity
public class TestObjectBoxBean {

    @Id(assignable = true)
    long id;

    @Index
    String name;

    @Transient
    String uom;

    @NameInDb("age")
    String test;
}
//java
注解	说明
@Entity	这个对象需要持久化。
@Id	这个对象的主键。
@Index	这个对象中的索引。对经常大量进行查询的字段创建索引，会提高你的查询性能。
@NameInDb	有的时候数据库中的字段跟你的对象字段不匹配的时候，可以使用此注解。
@Transient	如果你有某个字段不想被持久化，可以使用此注解。
@Relation	做一对多，多对一的注解。
需要注意的是，默认情况下 id 是被 ObjectBox 管理的一个自增 id，也就是说被 @Id 标注的字段不需要也不能手动设置，如果要手动管理应该用 @Id(assignable = true) 标注字段。
而且被标注为主键的字段应该为 long 型。

增删查改
'''java
TestObjectBoxBean bean = new TestObjectBoxBean();
...

//第一步获取 Box 实例
Box<TestObjectBoxBean> beanBox = ((BApplication) getApplication())
        .getBoxStore().boxFor(TestObjectBoxBean.class);

//新增和修改，put 的参数可以是 list
beanBox.put(bean);

//删除 id 为 2 的数据
beanBox.remove(2);

//查询，名字为 Test 或者 uom 为 kg 的数据
List<TestObjectBoxBean> item = beanBox.query()
        .startsWith(TestObjectBoxBean_.name,"Test")
        .or().equal(TestObjectBoxBean_.uom,"kg")
        .orderDesc(TestObjectBoxBean_.gid).build().find();
//java
查询时，用到了生成类 TestObjectBoxBean_ 通常是实体类加一个下划线。
使用 builder.equal() 进行设置匹配，调用 startWith() 设置查询条件，find() 可以用于分页。

事务
Box 实力下的 put 和 remove 的执行实际上已经是事务的。
除此之外显示的使用事务也是可以的，ObjectBox 提供了几个 api：

API	说明
runInTx	在给定的 runnable 中运行的事务。
runInReadTx	只读事务，不同于 runInTx，允许并发读取
runInTxAsync	运行在一个单独的线程中执行，执行完成后，返回 callback。
callInTx	与runInTx 相似，不同的是可以有返回值。
'''java
boxStore.runInTx(new Runnable() {
  @Override
  public void run() {
     for(User user: allUsers) {
         if(modify(user)) box.put(user);
         else box.remove(user);
       }
    }
});
//java
数据库升级
首先，在要修改的字段添加 @Uid 注解。



然后 Build -> Make Project，



此时就可以直接修改字段的名称。

Rx 监听
'''java
Query<TestObjectBoxBean> builder = beanBox.query().build();
builder.subscribe().on(AndroidScheduler.mainThread()).observer(new DataObserver<List<TestObjectBoxBean>>() {
    @Override
    public void onData(List<TestObjectBoxBean> testObjectBoxBeen) {

    }
});
//java
另外
官方有这样一个提示，假如需要插入或修改多条数据，可以这样做：
'''java
for(User user: allUsers) {
   modify(user); // modifies properties of given user
   box.put(user);
}
//java
但这种做法可能会需要较多的时间、花费更多的性能，正确做法：
'''java
for(User user: allUsers) {
   modify(user); // modifies properties of given user
}
box.put(allUsers);
//java
