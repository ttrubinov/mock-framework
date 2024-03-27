package mock.junitextension;

import mock.core.ObjectMock;
import mock.core.StaticStub;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MockExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        for (StaticStub<?> staticStub : ObjectMock.staticStabs) {
            staticStub.close();
        }
    }
}
