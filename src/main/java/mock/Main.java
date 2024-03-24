package mock;

import mock.core.ObjectMock;
import mock.matchers.ArgumentsMatcher;

public class Main {
    public static void main(String[] args) throws Exception {
        var mock = ObjectMock.mock(MyClass.class);
        mock.aboba();

        ObjectMock.when(mock.inc(ArgumentsMatcher.anyInt())).thenReturn(-1);
        ObjectMock.when(mock.inc(0)).thenReturn(2);
        System.out.println(mock.inc(0));
        System.out.println(mock.inc(1));
        System.out.println(mock.inc(-1));
//        ObjectMock.when(MyClass.incc(0)).thenReturn(2);


//        ObjectMock.when(() -> mock.some(anyInt(), anyInt())).thenThrow(new Exception("no zero"));
//        ObjectMock.when(() -> mock.some(eq(0), anyInt())).thenThrow(new Exception("first 0"));
//        ObjectMock.when(() -> mock.some(anyInt(), eq(0))).thenThrow(new Exception("second 0"));
//        ObjectMock.when(() -> mock.some(eq(0), eq(0))).thenThrow(new Exception("double zero"));


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