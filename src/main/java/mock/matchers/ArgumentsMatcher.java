package mock.matchers;

import mock.exception.MatcherException;
import mock.exception.MockException;
import mock.matchers.Matchers.AbstractMatcher;
import mock.matchers.Matchers.AnyMatcher;
import mock.matchers.Matchers.CollectionMatcher;
import mock.matchers.Matchers.EqualsMatcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArgumentsMatcher {
    public static MatcherGroup last;

    static {
        last = new MatcherGroup();
    }

    public static void clearMatchers() {
        last = new MatcherGroup();
    }

    public static <T> T eq(T object) {
        last.add(new EqualsMatcher<>(object));
        return object;
    }

    public static <T> T in(Collection<T> objects) {
        if (objects.isEmpty()) {
            throw new MatcherException("Where is no Elements in CollectionMatcher, called method 'in'");
        }
        last.add(new CollectionMatcher<>(objects));
        return objects.stream().findAny().get();
    }

    public static <T> T in(T... objects) {
        return in(Arrays.stream(objects).toList());
    }

    public static <T> T any(Class<T> tClass) {
        last.add(new AnyMatcher<>(tClass));
        return (T) null;
    }

    public static int anyInt() {
        any(int.class);
        return 0;
    }


    public static <T> T any() {
        last.add(new AnyMatcher<>());
        return (T) null;
    }


    /**
     * Matchers position in list is important
     */
    public static class MatcherGroup {
        final List<AbstractMatcher> matchers = new ArrayList<>();

        public void add(AbstractMatcher<?> abstractMatcher) {
            matchers.addLast(abstractMatcher);
        }

        public boolean match(List<Object> objects) {
            if (matchers.size() != objects.size()) {
                return false;
            }
            for (int i = 0; i < objects.size(); i++) {
                if (!matchers.get(i).match(objects.get(i))) {
                    return false;
                }
            }
            return true;
        }

        public boolean correctForMethod(Method method) {
            if (method.getParameterCount() != matchers.size()) {
                throw new MockException("There are different argumentMatchers: " + matchers);
//                return false;
            }
            var methodArgumentsTypes = method.getParameterTypes();
            int i = 0;
            for (AbstractMatcher<?> matcher : matchers) {
//                if (!matcher.correspondToClass(methodArgumentsTypes[i])) {
//                    return false;
//                }
                i++;
            }
            return true;
        }

        public static MatcherGroup defaultMatcherGroup(int parametersAmount) {
            MatcherGroup matcherGroup = new MatcherGroup();
            for (int i = 0; i < parametersAmount; i++) {
                matcherGroup.add(new Matchers.AnyMatcher<>());
            }
            return matcherGroup;
        }

        public boolean isEmpty() {
            return matchers.isEmpty();
        }
    }
}
