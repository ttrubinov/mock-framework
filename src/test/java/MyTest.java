import mock.MyClass;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MyTest {

    @Test
    void myTest() {
        MyClass myClass = Mockito.mock(MyClass.class);
        Mockito.doNothing().when(myClass).some(0, 0);
    }
}
