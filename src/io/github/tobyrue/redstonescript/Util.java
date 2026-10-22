package io.github.tobyrue.redstonescript;

import java.util.LinkedList;
import java.util.Stack;
import java.util.function.Supplier;

public final class Util {
    public static <T, R extends Throwable> T fail(Supplier<R> t) throws R {
        throw t.get();
    }
}
