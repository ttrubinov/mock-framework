import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.AfterEachCallback;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MockExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var inst = context.getTestInstance().get();
        var fields = Arrays.stream(inst.getClass().getDeclaredFields()).toList();
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = field.get(context.getRequiredTestInstance());
            if (fieldValue instanceof AutoCloseable autoCloseable) {
                autoCloseable.close();
            }
        }
    }
}
