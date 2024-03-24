import mock.core.ObjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

public class MyTest {

    @Test
    void myTest() throws Exception {
        try (var mock = ObjectMock.mockStatic(MyTest.class)) {
            mock.when(() -> MyClass.world()).thenReturn("ABOBA");
        }
//        try (MockedStatic<MyClass> utilities = Mockito.mockStatic(MyClass.class)) {
//            utilities.when(() -> MyClass.world("5"))
//                    .thenReturn(Arrays.asList(10, 11, 12));
//        }
    }
    class MyClass {
        public int aboba(int x, int y) {
            return x + y;
        }

        public static String world() {
            return "HELLO WORLD";
        }
    }
}
