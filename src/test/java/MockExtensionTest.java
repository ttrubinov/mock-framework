import mock.core.Mock;
import mock.junitextension.MockTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MockTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MockExtensionTest {
    @Test
    @Order(1)
    void test1() {
        var mock = Mock.mockStatic(DummyClass.class);
        mock.when(DummyClass.numberToString(5)).thenReturn("Mock number");

        System.out.println("A");

        assertEquals("Mock number", DummyClass.numberToString(5));
    }

    @Test
    @Order(2)
    void test2() {
        System.out.println("B");
        assertEquals("5", DummyClass.numberToString(5));
    }
}
