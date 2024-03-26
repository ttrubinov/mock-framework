import mock.core.ObjectMock;
import mock.matchers.ArgumentsMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTest {

    @Test
    void mockTest() {
        var mock = ObjectMock.mock(DummyClass.class);
        ObjectMock.when(mock.plus(3, 2)).thenReturn(1);
        assertEquals(1, mock.plus(3, 2));
    }

    @Test
    void staticMockTest() {
        try (var mock = ObjectMock.mockStatic(DummyClass.class)) {
            mock.when(DummyClass.printNumber(5)).thenReturn("Mock number");
            assertEquals("Mock number", DummyClass.printNumber(5));
        }
        assertEquals("5", DummyClass.printNumber(5));

        try (var mock = ObjectMock.mockStatic(DummyClass.class)) {
            mock.when(DummyClass.printNumber(5)).thenReturn("Second mock number");
            assertEquals("Second mock number", DummyClass.printNumber(5));
        }
        assertEquals("5", DummyClass.printNumber(5));
    }

    @Test
    void defaultWithStaticMockTest() {
        var mock = ObjectMock.mock(DummyClass.class);
        ObjectMock.when(mock.plus(3, 2)).thenReturn(1);
        assertEquals(1, mock.plus(3, 2));
        try (var mock2 = ObjectMock.mockStatic(DummyClass.class)) {
            mock2.when(DummyClass.printNumber(5)).thenReturn("Mock number");
            assertEquals("Mock number", DummyClass.printNumber(5));
        }
        assertEquals("5", DummyClass.printNumber(5));
        assertEquals(1, mock.plus(3, 2));
    }

    @Test
    void matchersTest() {
        try (var mock = ObjectMock.mockStatic(DummyClass.class)) {
            mock.when(DummyClass.printNumber(ArgumentsMatcher.anyInt())).thenReturn("Mock number");
            assertEquals("Mock number", DummyClass.printNumber(5));
            assertEquals("Mock number", DummyClass.printNumber(3));
        }
        assertEquals("5", DummyClass.printNumber(5));
        assertEquals("3", DummyClass.printNumber(3));

        var mock = ObjectMock.mock(DummyClass.class);
        ObjectMock.when(mock.plus(ArgumentsMatcher.anyInt(), ArgumentsMatcher.anyInt())).thenReturn(1);
        assertEquals(1, mock.plus(3, 2));
        assertEquals(1, mock.plus(5, 10));
    }
}
