package mock.core;

import mock.exception.NotInterceptException;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DelegationClass {
    public DelegationClass() {
    }

    public static final List<Object> lastArguments = new ArrayList<>();
    public static final List<Method> calledMethod = new ArrayList<>();
    public static Method lastCalledMethod = null;

    @BindingPriority(2)
    public static @RuntimeType Object ag(@AllArguments Object[] objects,
                                         @Origin Method method) {
        lastArguments.clear();
        lastCalledMethod = method;
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