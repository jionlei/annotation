package com.clazz.store;

import java.util.HashMap;
import java.util.Map;

public class ClassStoreSingleton {
    private Map<String, Class<? extends ILass>> mClassMap = null;

    private static volatile ClassStoreSingleton mClassStoreSingleton = null;
    private ClassStoreSingleton(){
        mClassMap = new HashMap<>();
    }
    private static ClassStoreSingleton getClassStore() {
        if (mClassStoreSingleton == null) {
            synchronized (ClassStoreSingleton.class) {
                if (mClassStoreSingleton == null) {
                    mClassStoreSingleton = new ClassStoreSingleton();
                }
            }
        }
        return mClassStoreSingleton;
    }

    public static boolean putClassStore(String key, Class<?> clazz) {
        // 只有继承自ILass的类才能被添加
        if (ILass.class.isAssignableFrom(clazz) && !getClassStore().mClassMap.containsKey(key)) {
            getClassStore().mClassMap.put(key,(Class<? extends ILass>) clazz);
            return true;
        }
       return false;
    }

    public static boolean putObjectClass(String key, Object activity){
        if (activity instanceof ILass && !getClassStore().mClassMap.containsKey(key)) {
            getClassStore().mClassMap.put(key, (Class<? extends ILass>) activity.getClass());
            return true;
        }
        return false;
    }

    public static void printAllClass() {
        for (String key : getClassStore().mClassMap.keySet()) {
            System.out.println("key = " + key + "calss = " + getClassStore().mClassMap.get(key));
        }
    }
}
