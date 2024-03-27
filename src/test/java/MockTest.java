import mock.core.Mock;
import mock.matchers.Matchers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        var mock2 = Mock.mock(DummyClass.class);
        Mock.when(mock2.plus(Matchers.in(3, 5), Matchers.in(2, 10))).thenReturn(1);
        assertEquals(1, mock2.plus(3, 2));
        assertEquals(1, mock2.plus(5, 10));
        assertEquals(0, mock2.plus(6, 7));
    }

    @Test
    void matchersWithExceptionsTest() {
        var mock = Mock.mock(DummyClass.class);
        Mock.when(mock.plus(Matchers.anyInt(), Matchers.anyInt())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> mock.plus(1, 1));
        assertThrows(RuntimeException.class, () -> mock.plus(0, 0));

        Mock.when(mock.plus(Matchers.anyInt(), Matchers.eq(0))).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> mock.plus(1, 0));

        Mock.when(mock.plus(Matchers.eq(0), Matchers.anyInt())).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> mock.plus(0, 1));

        Mock.when(() -> mock.plus(0, 0)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> mock.plus(1, 1));
        assertThrows(RuntimeException.class, () -> mock.plus(1, 0));
        assertThrows(RuntimeException.class, () -> mock.plus(0, 1));
        assertEquals(0, mock.plus(0, 0));
    }
}
