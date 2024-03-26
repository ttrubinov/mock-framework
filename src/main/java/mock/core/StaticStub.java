package mock.core;

public class StaticStub<T> implements AutoCloseable {

    private Class<T> originalClass;

    public StaticStub(Class<T> classToMock) {
        saveOriginalClass(classToMock);
    }

    public <S> Stub<S> when(S methodCall) {
        return new MethodCallStub<>();
    }

    private void saveOriginalClass(Class<T> classToMock) {
        originalClass = classToMock;
    }

    private void restoreOriginalClass() {
        ObjectMock.setStaticIntercept(false, originalClass);
    }

    @Override
    public void close() {
        restoreOriginalClass();
    }
}
