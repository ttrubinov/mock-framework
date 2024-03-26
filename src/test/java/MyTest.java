import mock.core.Mock;
import mock.matchers.Matchers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTest {

    @Test
    void mockTest() {
        var mock = Mock.mock(DummyClass.class);
        Mock.when(mock.plus(3, 2)).thenReturn(1);
        assertEquals(1, mock.plus(3, 2));
    }

    @Test
    void staticMockTest() {
        try (var mock = Mock.mockStatic(DummyClass.class)) {
            mock.when(DummyClass.numberToString(5)).thenReturn("Mock number");
            assertEquals("Mock number", DummyClass.numberToString(5));
        }
        assertEquals("5", DummyClass.numberToString(5));

        try (var mock = Mock.mockStatic(DummyClass.class)) {
            mock.when(DummyClass.numberToString(5)).thenReturn("Second mock number");
            assertEquals("Second mock number", DummyClass.numberToString(5));
        }
        assertEquals("5", DummyClass.numberToString(5));
    }

    @Test
    void defaultWithStaticMockTest() {
        var mock = Mock.mock(DummyClass.class);
        Mock.when(mock.plus(3, 2)).thenReturn(1);
        assertEquals(1, mock.plus(3, 2));
        try (var mock2 = Mock.mockStatic(DummyClass.class)) {
            mock2.when(DummyClass.numberToString(5)).thenReturn("Mock number");
            assertEquals("Mock number", DummyClass.numberToString(5));
        }
        assertEquals("5", DummyClass.numberToString(5));
        assertEquals(1, mock.plus(3, 2));
    }

    @Test
    void matchersTest() {
        try (var mock = Mock.mockStatic(DummyClass.class)) {
            mock.when(DummyClass.numberToString(Matchers.anyInt())).thenReturn("Mock number");
            assertEquals("Mock number", DummyClass.numberToString(5));
            assertEquals("Mock number", DummyClass.numberToString(3));
        }
        assertEquals("5", DummyClass.numberToString(5));
        assertEquals("3", DummyClass.numberToString(3));

        var mock = Mock.mock(DummyClass.class);
        Mock.when(mock.plus(Matchers.anyInt(), Matchers.anyInt())).thenReturn(1);
        assertEquals(1, mock.plus(3, 2));
        assertEquals(1, mock.plus(5, 10));
    }
}
