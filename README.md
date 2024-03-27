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

### Matchers:

* any() - любой аргумент
* anyInt(), anyLong(), ... - любой аргумент соответствующего примитивного типа
* eq() - проверка на equals
* in(...) - агрумент лежит в множестве переданных объектов

## Примеры использования

