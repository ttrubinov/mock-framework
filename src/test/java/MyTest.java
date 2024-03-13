import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ttrubinov.test.Main;

public class MyTest {

    @Test
    void myTest() {
        var mockedMain = Mockito.mock(Main.class);
        Mockito.when(mockedMain.doSth()).thenReturn("aboba");

        System.out.println(mockedMain.doSth());
    }
}
