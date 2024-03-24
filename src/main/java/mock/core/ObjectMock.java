package mock.core;

import mock.exception.MockException;
import mock.matchers.ArgumentsMatcher;
import mock.matchers.ArgumentsMatcher.MatcherGroup;
import mock.matchers.Matchers;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ObjectMock {
    private static final Map<Long, ObjectMockEntity> mockMap = new HashMap<>();
    private static long counter = 0;
    private static final AtomicReference<Long> lastCalledObject = new AtomicReference<>(null);

    private static class ObjectMockEntity {
        //        public Object object;
        private Map<Method, MethodMatchersAndCalls> methodMap = new HashMap<>();

        public void addMethod(Method method) {
            methodMap.put(method, new MethodMatchersAndCalls(method));
        }

        public void addMatchersAndCall(Method method, MatcherGroup matcherGroup, Callable<?> callable) {
            methodMap.get(method).addMatcherAndCall(matcherGroup, callable);
        }


        private static class MethodMatchersAndCalls {
            final List<MatchAndCall> matchAndCallList = new ArrayList<>();
            Callable<?> defaultCall;

            MethodMatchersAndCalls(Method method) {
                MatcherGroup matcherGroup = new MatcherGroup();
                for (Class<?> parameterType : method.getParameterTypes()) {
                    matcherGroup.add(new Matchers.AnyMatcher<>(parameterType));
//                    matcherGroup.add(new Matchers.AnyMatcher<>());
                }
                defaultCall = defaultCallForMethod(method);
                matchAndCallList.add(new MatchAndCall(matcherGroup, defaultCall));
            }


            Object callWithMatch(List<Object> objects) throws Exception {
                for (int i = matchAndCallList.size() - 1; i >= 0; i--) {
                    MatchAndCall matchAndCall = matchAndCallList.get(i);
                    if (matchAndCall.matcher.match(objects)) {
                        return matchAndCall.callable.call();
                    }
                }
                //todo: think about return
                return null;
            }

            void addMatcherAndCall(MatcherGroup matcherGroup, Object value) {
                addMatcherAndCall(matcherGroup, () -> value);
            }

            void addMatcherAndCall(MatcherGroup matcherGroup, Exception throwable) {
                addMatcherAndCall(matcherGroup, () -> {
                    throw throwable;
                });
            }

            void addMatcherAndCall(MatcherGroup matcherGroup, Callable<?> callable) {
                matchAndCallList.addLast(new MatchAndCall(matcherGroup, callable));
            }


            private static Callable<?> defaultCallForMethod(Method method) {
                Class<?> returnType = method.getReturnType();
                if (!returnType.isPrimitive() || Void.TYPE.isAssignableFrom(returnType)) {
                    return () -> null;
                }
                if (Integer.TYPE.isAssignableFrom(returnType)
                        || Integer.TYPE.isAssignableFrom(returnType)
                        || Short.TYPE.isAssignableFrom(returnType)
                        || Long.TYPE.isAssignableFrom(returnType)
                        || Byte.TYPE.isAssignableFrom(returnType)
                        || Character.TYPE.isAssignableFrom(returnType)
                        || Double.TYPE.isAssignableFrom(returnType)
                        || Float.TYPE.isAssignableFrom(returnType)
                ) {
                    return () -> 0;
                }
                if (Boolean.TYPE.isAssignableFrom(returnType)) {
                    return () -> false;
                }
                throw new MockException("There is undefined class" + returnType);
            }

            record MatchAndCall(MatcherGroup matcher, Callable<?> callable) {
            }
        }
    }

    private static Set<Method> objectMethods = Arrays.stream(Object.class.getMethods()).collect(Collectors.toSet());


    public static <T> T mock(Class<T> classToMock) {
        final long currentId = counter++;
        mockMap.put(currentId, new ObjectMockEntity());

        var builder = new ByteBuddy().subclass(classToMock);
        var methodList = Arrays.stream(classToMock.getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers())
                        && !objectMethods.contains(method))
                .toList();
        for (Method method : methodList) {
            mockMap.get(currentId).addMethod(method);
            builder = builder
                    .method(ElementMatchers.is(method))
                    .intercept(
                            MethodDelegation.to(DelegationClass.class)
                                    .andThen(MethodCall.call(mockCall(currentId))));
        }
        try (DynamicType.Unloaded<T> maked = builder.make()) {
            var dynamicType = maked.load(classToMock.getClassLoader()).getLoaded();
            return dynamicType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Callable<?> mockCall(long objectId) {
        return () -> mockCall(DelegationClass.lastCalledMethod, DelegationClass.lastArguments, objectId);
    }


    static Long lastCalledObject() {
        return lastCalledObject.get();
    }

    private static Object mockCall(Method method, List<Object> arguments, long objectId) throws Exception {
        var mockEntity = mockMap.get(objectId);
        lastCalledObject.set(objectId);
        ObjectMockEntity.MethodMatchersAndCalls matchersAndCalls = mockEntity.methodMap.get(method);
        if (!ArgumentsMatcher.last.isEmpty()) {
            return matchersAndCalls.defaultCall.call();
        }
        var res = matchersAndCalls.callWithMatch(arguments);

        return res;
    }

    public static void addLastCall(long lastCalledObject,
                                   Method method,
                                   List<Object> arguments,
                                   MatcherGroup matchers,
                                   Callable<?> callable) {
        ObjectMockEntity mockEntity = mockMap.get(lastCalledObject);
//        if (!Arrays.asList(mockEntity.getClass().getMethods()).contains(method)) {
//            throw new MockException("There is no such method for object. Maybe not this last called object");
//        }
        if (matchers.isEmpty()) {
            MatcherGroup matcherGroup = new MatcherGroup();
            for (Object argument : arguments) {
                matcherGroup.add(new Matchers.EqualsMatcher<>(argument));
            }
            matchers = matcherGroup;
        }
        if (!matchers.correctForMethod(method)) {
            throw new MockException("Matchers doesn't corresponded for this method");
        }

        mockEntity.addMatchersAndCall(method, matchers, callable);
    }

    public static <T> Stub<T> when(T methodCall) {
        return new MethodCallStub<>();
    }

    public static <T> Stub<T> when(Supplier<T> methodCall) {
        try {
            methodCall.get();
        } catch (Exception ignore) {

        }
        return new MethodCallStub<>();
    }

    public static Stub<Void> when(Runnable methodCall) {

        return when(() -> {
            methodCall.run();
            return null;
        });
    }
}
