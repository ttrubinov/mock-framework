import mock.core.Mock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockTest {

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
    void tst() {
        var r = Mock.mock(Aboba.class);
        Mock.when(r.f()).thenReturn(2);
        Assertions.assertEquals(2, r.f());
    }
}
