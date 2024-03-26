package mock.core;

import mock.exception.MockException;
import mock.matchers.Matchers;
import mock.matchers.Matchers.MatcherGroup;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MethodCallStub<T> implements Stub<T> {
    Method method;
    List<Object> arguments;
    MatcherGroup matchers;
    Long lastCalledObject;

    public MethodCallStub() {
        method = DelegationClass.lastCalledMethod;
        arguments = new ArrayList<>(DelegationClass.lastArguments);
        matchers = Matchers.last;
        lastCalledObject = ObjectMock.lastCalledObject();
        if (lastCalledObject == null) {
            throw new MockException("There is no last mock object's call");
        }
        Matchers.clearMatchers();
    }

    @Override
    public Stub<T> thenReturn(T var) {
        return thenCall(() -> var);
    }

    @Override
    public Stub<T> thenThrow(Exception throwable) {
        return thenCall(() -> {
            throw throwable;
        });
    }

    @Override
    public Stub<T> thenCall(Callable<?> callable) {
        ObjectMock.addLastCall(lastCalledObject, method, arguments, matchers, callable);
        return this;
    }
}


