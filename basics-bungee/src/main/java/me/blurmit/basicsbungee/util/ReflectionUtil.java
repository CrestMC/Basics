package me.blurmit.basicsbungee.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ReflectionUtil {


    private ReflectionUtil() {}

    private static boolean checkParams(Class<?>[] parameterTypes, Executable executable) {
        Class<?>[] executableParameterTypes = executable.getParameterTypes();

        if (executableParameterTypes.length != parameterTypes.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].isAssignableFrom(executableParameterTypes[i])) {
                return false;
            }
        }

        return true;
    }

    public static <T> T construct(Class<T> clazz, Object... params) {
        try {
            Class<?>[] parameterTypes = Arrays.stream(params)
                    .map(Object::getClass)
                    .toArray(Class<?>[]::new);

            Constructor<T> constructor = null;

            for (Constructor<?> declared : clazz.getDeclaredConstructors()) {
                if (!checkParams(parameterTypes, declared)) {
                    continue;
                }

                constructor = (Constructor<T>) declared;
            }

            if (constructor == null) {
                return null;
            }

            if (Modifier.isAbstract(clazz.getModifiers())) {
                return null;
            }

            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (NullPointerException | ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void consume(String packageName, ClassLoader classLoader, Class<T> type, Consumer<T> consumer, boolean recursive, Object... args) {
        try {
            ClassPath path = ClassPath.from(classLoader);
            ImmutableSet<ClassPath.ClassInfo> classes = recursive ? path.getTopLevelClassesRecursive(packageName) : path.getTopLevelClasses(packageName);

            classes.stream()
                    .map(ClassPath.ClassInfo::load)
                    .filter(clazz -> {
                        Class<?> testClass = clazz;
                        while (testClass != null) {
                            if (type.isAssignableFrom(testClass)) {
                                return true;
                            }

                            testClass = testClass.getSuperclass();
                        }

                        return false;
                    })
                    .map(clazz -> (Class<? extends T>) clazz)
                    .collect(Collectors.toSet())
                    .forEach(clazz -> {
                        T instance = construct(clazz, args);
                        if (instance == null) {
                            return;
                        }

                        consumer.accept(instance);
                    });
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }

}
