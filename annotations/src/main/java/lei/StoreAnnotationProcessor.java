package lei;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
public class StoreAnnotationProcessor extends AbstractProcessor {
    private Filer mFiler = null; // 这个就是编译时用于生成代码的对象，所有代码资源文件都是通过它实现， 这个是编译器时生成代码的关键

    //打印日志， 不能用print() 因为这个时在编译器执行的
    private Messager messager = null;

    private Types types = null;

    private Elements elements = null;
    //注解处理器可用此创建新文件（源文件、类文件、辅助资源文件）
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler(); // 在初始化是获取注解处理器的mFiler
        messager = processingEnv.getMessager();
        types = processingEnv.getTypeUtils();
        elements =processingEnv.getElementUtils();
        messager.printMessage(Diagnostic.Kind.NOTE, "init StoreAnnotationProcessor");
    }


    /**
     * 这个要把我们自定义的注解加入到set中，可以传入不止一个注解 传入的注解就是我们要识别的注解
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationSet = new LinkedHashSet<>();
        messager.printMessage(Diagnostic.Kind.NOTE, "init StoreAnnotationProcessor");
        annotationSet.add(StoreAnnotation.class.getCanonicalName());  // 带有包名的完整类名 com.xxx.xxx.StoreAnnotation.class
        return annotationSet;
    }


    /**
     * 这个注解时表示注解支持的Java版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


    /**
     * 这个时注解真正的处理方法，这里面时生成代码的方法
     * 这个就是编译时代码生成的精髓
     *
     * @param annotations 这个就是通过getSupportedAnnotationTypes方法传入的注解处理器所支持的注解 @StoreAnnotation
     * @param roundEnv    所有添加该注解的类，方法, 参数....返回的集合 对应的时参入的参数 @Target(ElementType.TYPE)
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 临时存储 key 和 类 value 这里的value是 string 是因为，我们要把它们写入到文件里。
        // putMap("xxx", xxx.class) 这个在代码层面其实都是 string 字符
        if (mFiler == null || annotations.size() == 0) return false;
        Map<String, String> tmpMap = new HashMap<>(); // value 为string 因为我们要把
        //1.遍历所有带有注解StoreAnnotation的元素
        for (Element annotationElement : roundEnv.getElementsAnnotatedWith(StoreAnnotation.class)) {
            // 2. 判断所注解的元素是否是一个类，防御性编程，虽然我们之前时声明了@Target(ElementType.TYPE),但实际上有可能传入的不是一个类
            if (annotationElement.getKind() == ElementKind.CLASS) {
                // 这里模拟在每个模块下创建一个类，将该模块下所有带StoreAnnotation添加到ClassStoreSingleton的map中
                //String claseName = annotationElement.getClass().getCanonicalName(); //这样也能拿到全路径类名
                String className = ((TypeElement) annotationElement).getQualifiedName().toString() + ".class";
                String[] keys = annotationElement.getAnnotation(StoreAnnotation.class).value(); // 拿到我们的key ，可能有多个
                for (String key : keys) {
                    tmpMap.put(key, className);
                }
            } else {
                throw new IllegalArgumentException("StoreAnnotation can only used in a class but now it used in a "
                        + annotationElement.getKind().toString()
                        + "type");
            }
        }
        if (tmpMap.size() == 0) return true; // 没有使用该注解的类直接返回
        // 3. 生成一个java class 文件

        //           JavaFileObject classFile;
//            try {
//               classFile = mFiler.createClassFile("com.lei.annotation."+"ClassStore"+ System.currentTimeMillis());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

        // 4.写入类文件 File中，这个是编译时生成的类 我这里用JavaPoet工具，直接用FileStream也可以
        StringBuilder sb = new StringBuilder();

        // 生成方法体代码
        for (String key : tmpMap.keySet()) {
            String clazz = tmpMap.get(key);
            sb.append("putClassStore(\"").append(key).append("\", ").append(clazz).append("); \n");
        }


        // 3.1定义一个方法 putClass()
        MethodSpec putClass = MethodSpec
                .methodBuilder("putClass")
                .addModifiers(Modifier.PUBLIC)
                .addCode("\n" + sb)  //函数体
                .build();

        //3.2 定义一个类
        TypeSpec typeSpec = TypeSpec.classBuilder("ClassStore")
                .addModifiers(Modifier.PUBLIC)   //声明public
                .addMethod(putClass)
                .build();


        //  import static com.clazz.store.ClassStoreSingleton.xxx;
        ClassName className = ClassName.get("com.clazz.store","ClassStoreSingleton");

        // 定义一个java文件，指定package和类定义
        JavaFile javaFile = JavaFile.builder("com.annotation.utils", typeSpec)
                .addStaticImport(className, "putClassStore")
                .build();


        // 将java文件内容写入文件中
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
/**
 * public ClassStore {
 * <p>
 * public void pusStore(String key, Class<?> clazz) {
 * ClassStoreSingleton.putClass(key,clazz);
 * }
 * <p>
 * }
 */
