package mock.core;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Callable;

public class DelegationClass {
    public DelegationClass() {
    }

    static final List<Object> lastArguments = new ArrayList<>();
    static final List<Method> calledMethod = new ArrayList<>();
    static Method lastCalledMethod = null;
    static final List<Method> calledStaticMethods = new ArrayList<>();
    static final Set<Method> originalMethods = new HashSet<>();
    static Callable<?> lastPossibleCall;
    static final String message = "default";

    public static @RuntimeType Object ag(@AllArguments Object[] objects,
                                         @Origin Method method,
                                         @SuperCall Callable<?> callable) {
        lastArguments.clear();
        lastCalledMethod = method;
        calledMethod.add(method);
        lastPossibleCall = callable;
        if (Modifier.isStatic(method.getModifiers())) {
            if (originalMethods.contains(method)) { //TODO: mb use this for turn back redefining
                try {
                    return method.invoke(objects);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            calledStaticMethods.add(method);
        }
        lastArguments.addAll(Arrays.stream(objects).toList());
        return null;
    }
}