package main.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Store(value = "a_class")
class A {

}

@Store("a_class")
class B {

}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)  //我们的目的是在源码编译时，将所有使用该注解的类存储到map中，所以选择在编译时生效这个策略
//通过修改Class数据以实现修改代码逻辑目的。对于是否需要修改的区分或者修改为不同逻辑的判断可以使用注解。
public @interface Store{
    //
   // String[] key() default "";  // 用来接受注解参数key 默认值""
    String[] value() default ""; //用来接受注解参数key 默认值""

}
