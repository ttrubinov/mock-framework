package mock.core;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
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

    @BindingPriority(1)
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

    @BindingPriority(0)
    @Advice.OnMethodEnter
    public static @RuntimeType Object bg(@Advice.AllArguments Object[] objects,
                                         @Advice.Origin Method method) {
        try {
            return method.invoke(objects).toString() + " HEHEHE";
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}