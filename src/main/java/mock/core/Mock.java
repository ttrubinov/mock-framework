package mock.core;

import java.util.function.Supplier;

public class Mock {

    public static <T> T mock(Class<T> classToMock) {
        return ObjectMock.mock(classToMock);
    }

    public static <T> StaticStub<T> mockStatic(Class<T> classToMock) {
        return ObjectMock.mockStatic(classToMock);
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

