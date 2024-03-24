package mock;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Callable;

public class Main {
    public static void main(String[] args) throws Exception {
//        Class<Main> classToMock = Main.class;
//        ByteBuddyAgent.install();
//
//        Main obj = new ByteBuddy().subclass(classToMock)
//                .method(ElementMatchers.named("sum"))
//                .intercept(MethodDelegation.to(MyClass.class).andThen(MethodCall.call(() -> {
//
//                    return res;
//                })))
//                .make()
//                .load(classToMock.getClassLoader()
//                        , ClassReloadingStrategy.fromInstalledAgent()
//                ).getLoaded().getDeclaredConstructor().newInstance();

    }
}