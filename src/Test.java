import io.github.tobyrue.redstonescript.BitString;
import io.github.tobyrue.redstonescript.State;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class Test {
    public static HashMap<String, State> statePool = new HashMap<>();

    public static <T, R extends Throwable> T fail(Supplier<R> t) throws R {
        throw t.get();
    }

    static class UnspecifiedException extends RuntimeException {};

    public static <T> Optional<T> getFromLayout(final HashMap<String, Object> layout, final String name, final Class<T> type) {
        if(layout.containsKey(name)) {
            if(type.isInstance(layout.get(name))) {
                return Optional.of((T) layout.get(name));
            } else {
                throw new UnspecifiedException();
            }
        } else {
            return Optional.empty();
        }
    }

    public static void ensureSizesMatch(final Optional<Integer> a, final String strA, final Optional<Integer> b, final String strB) {
        if (a.isPresent() && b.isPresent() && !a.get().equals(b.get())) throw new UnspecifiedException();
    }



    public static void main(String[] args) {

    }
}
