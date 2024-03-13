package ttrubinov.test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Mock {

    private static final Map<Integer, Map<String, Object>> mockMap = new HashMap<>();
    private static int counter = 0;
    private static AtomicReference<String> lastCalledMethod = new AtomicReference<>();
    private static AtomicReference<Integer> lastCalledObject = new AtomicReference<>();

    public static <T> T mock(Class<T> classToMock) {
        final int currentId = counter++;
        mockMap.put(currentId, new HashMap<>());
        var builder = new ByteBuddy().subclass(classToMock);
        var methodNames = Arrays.stream(classToMock.getMethods())
                .filter(method -> !method.getReturnType().isPrimitive())
                .map(Method::getName)
                .distinct()
                .toList();
        for (String methodName : methodNames) {
            builder = builder.method(ElementMatchers.named(methodName))
                    .intercept(MethodCall.call(() -> {
                        lastCalledMethod.set(methodName);
                        lastCalledObject.set(currentId);
                        var val = mockMap.get(currentId).get(methodName);
                        if (val instanceof Exception exception) {
                            throw exception;
                        }
                        return val;
                    }));
        }

        try (var maked = builder.make()) {
            var dynamicType = maked.load(classToMock.getClassLoader()).getLoaded();
            return dynamicType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Stub<T> when(T methodCall) {
        return new ObjectStub<>();
    }

    public static class ObjectStub<T> implements Stub<T> {
        @Override
        public Stub<T> thenReturn(T var) {
            mockMap.get(lastCalledObject.get()).put(lastCalledMethod.get(), var);
            return this;
        }

        @Override
        public Stub<T> thenThrow(Throwable throwable) {
            mockMap.get(lastCalledObject.get()).put(lastCalledMethod.get(), throwable);
            return this;
        }
    }

    public interface Stub<T> {
        Stub<T> thenReturn(T var);

        Stub<T> thenThrow(Throwable throwable);
    }
}

