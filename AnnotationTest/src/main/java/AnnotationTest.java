import com.annotation.utils.ClassStore;

import static com.clazz.store.ClassStoreSingleton.printAllClass;

public class AnnotationTest {
    public static void main(String[] args) {
        ClassStore classStore = new ClassStore();
        classStore.putClass();
        printAllClass();
    }
}
