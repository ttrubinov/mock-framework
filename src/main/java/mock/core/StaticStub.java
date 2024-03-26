package mock.core;

public class StaticStub<T> implements AutoCloseable {

    public StaticStub(Class<T> classToMock) {
        saveOriginalClass(classToMock);
    }

    private DelegationClass savedClass;

    public <S> Stub<S> when(S methodCall) {
        return new MethodCallStub<>();
    }

    private <T> void saveOriginalClass(Class<T> classToMock) {
//        savedClass = DelegationClass.ag();
    }

    private void restoreOriginalClass() {

    }

    @Override
    public void close() throws Exception {
        restoreOriginalClass();
    }

    @FunctionalInterface
    public interface MethodApply {

        void apply() throws Throwable;
    }
}
