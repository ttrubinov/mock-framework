package ttrubinov.test;

import net.bytebuddy.ByteBuddy;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Mock {

    private Map<Object, Integer> mockMap;

    public static <T> T mock(Class<T> classToMock) {
        try (var builder = new ByteBuddy().subclass(classToMock).make()) {
            var dynamicType = builder.load(classToMock.getClassLoader()).getLoaded();
            return dynamicType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

//    public static <T> Stub<T> when(T methodCall) {
//        return
//    }

    public interface Stub<T> {
        Stub<T> thenReturn(T var);

        Stub<T> thenThrow(Throwable throwable);
    }
}

