package mock.matchers;

import mock.exception.MatcherException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class MatchersUtils {
    public static abstract class AbstractMatcher<T> {
        abstract boolean match(T object);
        abstract boolean correspondToClass(Class<?> clazz);
    }

    static class CollectionMatcher<T> extends AbstractMatcher<T> {
        private final Collection<T> objects;

        CollectionMatcher(Collection<T> objects) {
            if (objects.isEmpty()){
                throw new MatcherException("There is no objects for collectionMatcher");
            }
            this.objects = objects.stream().toList();
        }

        static <T> CollectionMatcher<T> of(T... objects) {
            return new CollectionMatcher<>(Arrays.stream(objects).toList());
        }

        @Override
        boolean match(T object) {
            return objects.contains(object);
        }

        @Override
        boolean correspondToClass(Class<?> clazz) {
            return clazz.isInstance(objects.stream().findFirst().get());
        }
    }

    public static class EqualsMatcher<T> extends AbstractMatcher<T> {
        private final T wanted;

        public EqualsMatcher(T wanted) {
            this.wanted = wanted;
        }

        @Override
        boolean match(T object) {
            return Objects.equals(wanted, object);
        }

        @Override
        boolean correspondToClass(Class<?> clazz) {
            return clazz.isAssignableFrom(wanted.getClass());
        }
    }

    public static class AnyMatcher<T> extends AbstractMatcher<T> {
        private final Class<T> tClass;

        public AnyMatcher() {
            tClass = null;
        }

        public AnyMatcher(Class<T> tClass) {
            this.tClass = tClass;
        }

        @Override
        boolean match(T object) {
            return true;
        }

        @Override
        boolean correspondToClass(Class<?> clazz) {

            return tClass == null || tClass.isAssignableFrom(clazz);
        }
    }
}
