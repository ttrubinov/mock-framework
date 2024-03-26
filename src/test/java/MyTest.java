import mock.core.ObjectMock;
import org.junit.jupiter.api.Test;

public class MyTest {

    @Test
    void myTest() throws Exception {
        try (var mock = ObjectMock.mockStatic(MyClass.class)) {
            System.out.println(mock);
//            mock.when(MyClass::world).thenReturn("ABOBA");
        }
//        try (MockedStatic<MyClass> utilities = Mockito.mockStatic(MyClass.class)) {
//            utilities.when(() -> MyClass.world("5"))
//                    .thenReturn(Arrays.asList(10, 11, 12));
//        }
    }
}
