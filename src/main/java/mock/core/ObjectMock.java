package mock.core;

import mock.exception.MockException;
import mock.exception.NotInterceptException;
import mock.matchers.Matchers;
import mock.matchers.Matchers.MatcherGroup;
import mock.matchers.MatchersUtils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
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

public class ObjectMock {
    private static final Map<Long, ObjectMockEntity> mockMap = new HashMap<>();
    private final static Long STATIC_OBJECT_ID = 0L;
    private static long counter = STATIC_OBJECT_ID + 1;
    private static final AtomicReference<Long> lastCalledObject = new AtomicReference<>(null);
    public static final List<StaticStub<?>> staticStabs = new ArrayList<>();

    public static <T> void setStaticIntercept(boolean bool, Class<T> tClass) {
        var methods = getStaticMethodsOfClass(tClass);
        var staticMap = mockMap.get(STATIC_OBJECT_ID).methodMap;
        for (Method method : methods) {
            staticMap.get(method).toIntercept = bool;
        }
    }

    private static class ObjectMockEntity {
        private final Map<Method, MethodMatchersAndCalls> methodMap = new HashMap<>();

        public void addMethod(Method method) {
            methodMap.put(method, new MethodMatchersAndCalls(method));
        }

        public void addMatchersAndCall(Method method, MatcherGroup matcherGroup, Callable<?> callable) {
            methodMap.get(method).addMatcherAndCall(matcherGroup, callable);
        }


        private static class MethodMatchersAndCalls {
            final List<MatchAndCall> matchAndCallList = new ArrayList<>();
            Callable<?> defaultCall;
            /**
             * false - default class method  (Mockito.spy)
             * true - intercept logic (Mockito.mock)
             */
            boolean toIntercept = true;

            MethodMatchersAndCalls(Method method) {
                MatcherGroup matcherGroup = new MatcherGroup();
                for (Class<?> parameterType : method.getParameterTypes()) {
                    matcherGroup.add(new MatchersUtils.AnyMatcher<>(parameterType));
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

    private static final Set<Method> objectMethods = Arrays.stream(Object.class.getMethods()).collect(Collectors.toSet());

    private static <T> List<Method> getMethodsOfClass(Class<T> classToMock) {
        return Arrays.stream(classToMock.getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers())
                        && !objectMethods.contains(method))
                .toList();
    }

    private static <T> List<Method> getStaticMethodsOfClass(Class<T> classToMock) {
        return Arrays.stream(classToMock.getMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers())
                        && !objectMethods.contains(method))
                .toList();
    }

    public static <T> T mock(Class<T> classToMock) {
        final long currentId = counter++;
        mockMap.put(currentId, new ObjectMockEntity());

        var builder = new ByteBuddy().subclass(classToMock);
        var methodList = getMethodsOfClass(classToMock);
        for (Method method : methodList) {
            mockMap.get(currentId).addMethod(method);
            DynamicType.Builder.MethodDefinition.ImplementationDefinition<T> methodBuilder;
            if (Modifier.isAbstract(method.getModifiers())) {
                methodBuilder = builder.define(method);
            } else {
                methodBuilder = builder
                        .method(ElementMatchers.is(method));
            }
            builder = methodBuilder
                    .intercept(
                            MethodDelegation.to(DelegationClass.class)
                                    .andThen(MethodCall.call(mockCall(currentId))));
        }
        try (DynamicType.Unloaded<T> maked = builder.make()) {
            var dynamicType = maked.load(ClassLoader.getSystemClassLoader()).getLoaded();
            var instance = dynamicType.getDeclaredConstructor().newInstance();
            for (Method instanceMethod : getMethodsOfClass(instance.getClass())) {
                mockMap.get(currentId).addMethod(instanceMethod);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> StaticStub<T> mockStatic(Class<T> classToMock) {
        mockMap.put(STATIC_OBJECT_ID, new ObjectMockEntity());

        StaticStub<T> staticStub = new StaticStub<>(classToMock);
        staticStabs.add(staticStub);

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        var staticMethods = getStaticMethodsOfClass(classToMock);

        ByteBuddyAgent.install();

        var builder = new ByteBuddy().redefine(classToMock);
        for (Method method : staticMethods) {
            mockMap.get(STATIC_OBJECT_ID).addMethod(method);
            builder = builder
                    .visit(Advice.to(DelegationClass.class).on(ElementMatchers.is(method)));
        }

        try (var made = builder.make()) {
            made.load(classLoader, ClassReloadingStrategy.fromInstalledAgent());
        }

        return staticStub;
    }

    private static Callable<?> mockCall(long objectId) {
        return () -> mockCall(DelegationClass.lastCalledMethod, DelegationClass.lastArguments, objectId);
    }


    static Long lastCalledObject() {
        return lastCalledObject.get();
    }

    public static Object mockCall(Method method, List<Object> arguments, long objectId) throws Exception {
        var mockEntity = mockMap.get(objectId);
        lastCalledObject.set(objectId);
        ObjectMockEntity.MethodMatchersAndCalls matchersAndCalls = mockEntity.methodMap.get(method);
        if (!matchersAndCalls.toIntercept) {
            throw new NotInterceptException();
        }

        if (!Matchers.last.isEmpty()) {
            return matchersAndCalls.defaultCall.call();
        }

        return matchersAndCalls.callWithMatch(arguments);
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
                matcherGroup.add(new MatchersUtils.EqualsMatcher<>(argument));
            }
            matchers = matcherGroup;
        }
        if (!matchers.correctForMethod(method)) {
            throw new MockException("Matchers doesn't corresponded for this method");
        }

        mockEntity.addMatchersAndCall(method, matchers, callable);
    }
}
