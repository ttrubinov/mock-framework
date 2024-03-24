package mock.core;

import java.util.concurrent.Callable;

public interface Stub<T> {
    Stub<T> thenReturn(T var);

    Stub<T> thenThrow(Exception throwable);

    Stub<T> thenCall(Callable<?> callable);
}