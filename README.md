## API для Mock Framework

### Мок объектов:

* ClassToMock mocked = Mock.mock(ClassToMock.class) - создаётся мок объекта
* Stub\<ClassToMock> when = Mock.when(mocked.method(...)) - задаётся условие
* when.thenReturn(...) - задаётся возвращаемое значение по условию
* when.thenThrow(...) - задаётся выбрасываемый exception по условию

### Mock классов (для мока статик-методов):

* StaticStub\<ClassToMock> mock = Mock.mockStatic(ClassToMock.class) - создаётся мок класса
* Stub\<ClassToMock> when = mock.when(ClassToMock.method(...)) - задаётся условие
* thenReturn() и thenThrow() аналогично моку объектов
* Для того, чтобы класс вернулся в исходное состояние, мок нужно закрывать:
  можно использовать try-with-resources, а можно использовать аннотацию **@MockTest**
  на тестовом классе, тогда мок будет автоматически закрываться после каждого теста

### Matchers (работают для мока как динамических, так и для статических методов):

* any() - любой аргумент
* anyInt(), anyLong(), ... - любой аргумент соответствующего примитивного типа
* eq() - проверка на equals
* in(...) - агрумент лежит в множестве переданных объектов

## Примеры использования

### Мок класса:

```java

@MockTest // аннотация для закрытия мока после теста
public class MockExtensionTest {
    @Test
    void test1() {
        var mock = Mock.mockStatic(DummyClass.class);
        mock.when(DummyClass.numberToString(5)).thenReturn("Mock number");

        assertEquals("Mock number", DummyClass.numberToString(5));
    }

    @Test
    void test2() {
        assertEquals("5", DummyClass.numberToString(5));
    }
}
```

### Мок объекта:

```java
public class MockExtensionTest {
    @Test
    void mockTest() {
        var mock = Mock.mock(DummyClass.class);
        Mock.when(mock.plus(3, 2)).thenReturn(1);
        assertEquals(1, mock.plus(3, 2));
    }
}
```

### Использование matchers:

```java
public class MockExtensionTest {
    @Test
    void mockTest() {
        var mock = Mock.mock(DummyClass.class);
        Mock.when(mock.plus(Matchers.anyInt(), Matchers.anyInt())).thenReturn(1);
        assertEquals(1, mock.plus(3, 2));
        assertEquals(1, mock.plus(5, 10));
    }
}
```
