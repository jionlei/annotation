package lei;

import java.lang.annotation.*;

/**
 * @Target
 * 声明一个注解，这个注解的作用是将目标类在编译时自动加到ClassStoreSingleton的map中
 *  TYPE,  作用在类上，表示注解对类生效
 *  METHOD, 作用在方法上，表示对方法生效 类似 @Override @Deprecated
 *  FIELD, 作用在成员变量上
 *  PARAMETER, 作用在方法的参数上
 */
@Target(ElementType.TYPE)
/**
 * @Retention
 * 表示注解生效的策略是什么
 * RetentionPolicy.SOURCE 注解只在源码阶段保留，在编译器进行编译时它将被丢弃忽视。(编译成class文件不存在)

 * RetentionPolicy.CLASS 注解只被保留到编译进行的时候，它并不会被加载到 JVM 中。 (编译成class文件存在，但JVM会忽略)

 * RetentionPolicy.RUNTIME  注解可以保留到程序运行的时候，它会被加载进入到 JVM 中，所以在程序运行时可以获取到它们

 */
@Retention(RetentionPolicy.CLASS)  //我们的目的是在源码编译时，将所有使用该注解的类存储到map中，所以选择在编译时生效这个策略
//通过修改Class数据以实现修改代码逻辑目的。对于是否需要修改的区分或者修改为不同逻辑的判断可以使用注解。
public @interface StoreAnnotation {
    String[] value() ; //用来接受注解参数key 默认值""

}
