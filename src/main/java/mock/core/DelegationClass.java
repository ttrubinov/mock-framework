package mock.core;

import mock.exception.NotInterceptException;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Callable;

public class DelegationClass {
    public DelegationClass() {
    }

    public static final List<Object> lastArguments = new ArrayList<>();
    public static final List<Method> calledMethod = new ArrayList<>();
    public static Method lastCalledMethod = null;
    public static final List<Method> calledStaticMethods = new ArrayList<>();
    public static final Set<Method> originalMethods = new HashSet<>();
    public static Callable<?> lastPossibleCall;
    public static final String message = "default";

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
    @Advice.OnMethodExit()
    public static @RuntimeType Object bg(
            @Advice.AllArguments Object[] objects,
            @Advice.Origin Method method,
            @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object value
    ) {
        try {
            lastArguments.clear();
            calledMethod.add(method);
            lastCalledMethod = method;
            lastArguments.addAll(Arrays.stream(objects).toList());
            value = ObjectMock.mockCall(method, Arrays.stream(objects).toList(), 0);
            return value;
        } catch (NotInterceptException e) {
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}