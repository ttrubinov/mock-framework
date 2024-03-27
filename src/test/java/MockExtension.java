import mock.core.ObjectMock;
import mock.core.StaticStub;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.AfterEachCallback;

public class MockExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        for (StaticStub<?> staticStub : ObjectMock.staticStabs) {
            staticStub.close();
        }
    }
}
