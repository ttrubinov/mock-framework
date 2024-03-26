import mock.core.ObjectMock;
import mock.matchers.ArgumentsMatcher;
import org.junit.jupiter.api.Test;

public class MyTest {

    @Test
    void myTest() throws Exception {
        try (var mock = ObjectMock.mockStatic(MyClass.class)) {
//            MyClass.world();
//            System.out.println(mock);
            mock.when(MyClass.world(5)).thenReturn("ABOBA");
            System.out.println(MyClass.world(5));
        }
        System.out.println(MyClass.world(5));
//        try (MockedStatic<MyClass> utilities = Mockito.mockStatic(MyClass.class)) {
//            utilities.when(() -> MyClass.world("5"))
//                    .thenReturn(Arrays.asList(10, 11, 12));
//        }

//        var a = ObjectMock.mock(MyClass.class);
//        ObjectMock.when(a.world()).thenReturn("ABOBA");
//        System.out.println(a.world());
    }
}
