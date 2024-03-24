package mock.core;

import mock.exception.MockException;
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
import java.util.stream.Collectors;

//TODO: add check, Если был вызван метод и были указаны матчеры, то метод не должен выкинуть exception,
// чтобы можно было сделать when + return без exception'а
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

            MethodMatchersAndCalls(Method method) {
                MatcherGroup matcherGroup = new MatcherGroup();
                for (Class<?> parameterType : method.getParameterTypes()) {
                    matcherGroup.add(new Matchers.AnyMatcher<>(parameterType));
//                    matcherGroup.add(new Matchers.AnyMatcher<>());
                }
                matchAndCallList.add(new MatchAndCall(matcherGroup, () -> null));
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
            builder = builder
                    .method(ElementMatchers.is(method))
                    .intercept(
                            MethodDelegation.to(DelegationClass.class)
                                    .andThen(MethodCall.call(mockCall(counter))));
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
        return () -> mockCall(DelegationClass.lastArguments, objectId);
    }



    static Long lastCalledObject() {
        return lastCalledObject.get();
    }

    private static Object mockCall(List<Object> arguments, long objectId) {
        //todo: in progress


        return null;
    }

    public static void addLastCall(long lastCalledObject,
                                   Method method,
                                   List<Object> arguments,
                                   MatcherGroup matchers,
                                   Callable<?> callable)
    {
        ObjectMockEntity mockEntity = mockMap.get(lastCalledObject);
        if (!Arrays.asList(mockEntity.getClass().getMethods()).contains(method)) {
            throw new MockException("There is no such method for object. Maybe not this last called object");
        }
        if (!matchers.correctForMethod(method)) {
            throw new MockException("Matchers doesn't corresponded for this method");
        }

        //todo: in progress

    }

    public static <T> Stub<T> when(T methodCall) {

        return new ObjectStub<>();
    }
}
