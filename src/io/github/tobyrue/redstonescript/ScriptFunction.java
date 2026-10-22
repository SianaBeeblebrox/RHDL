package io.github.tobyrue.redstonescript;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A named function callable from within a script
 * @param name the name of the function
 * @param size See {@link ScriptFunction#size(Script, List)}
 * @param evaluate See {@link ScriptFunction#evaluate(Script, Expression.Context, List)}
 */
public record ScriptFunction(String name, BiFunction<Script, List<Object>, Integer> size, TriFunction<Script, Expression.Context, List<Object>, BitString> evaluate) {
    /**
     * Validates and computes the size of the return value for the given arguments without computing the value itself
     * @param script the script the function was called from
     * @param args the arguments to the function, may contain {@link Expression} or {@link Integer}
     * @return the size of the return value
     */
    public int size(final Script script, final List<Object> args) {
        return this.size.apply(script, args);
    }

    /**
     * Evaluates the function for the given arguments, actual return value size must match that computed by
     * {@link ScriptFunction#size(Script, List)} but can rely on preconditions established there
     * @param script the script the function was called from
     * @param context the enclosing expression context
     * @param args the arguments to the function, may contain {@link Expression} or {@link Integer}
     * @return the result of the function
     */
    public BitString evaluate(final Script script, final Expression.Context context, final List<Object> args) {
        return this.evaluate.apply(script, context, args);
    }

    /**
     * Returns the name of this function
     * @return the name of this function
     */
    public String getName() {
        return this.name;
    }

    /**
     * Helper to create a {@link ScriptFunction} that accepts a {@link BitString} and returns one <i>of the same size</i>
     * @param name the name for the new function
     * @param f the function implementation
     * @return the new {@link ScriptFunction}
     */
    public static ScriptFunction ofBitString(final String name, final Function<BitString, BitString> f) {
        return new ScriptFunction(name, (script, args) -> args.size() == 1 && args.get(0) instanceof final Expression expression ? expression.size() : Util.fail(() -> new ScriptException()), (script, ctx, args) -> f.apply(((Expression) args.get(0)).evaluate(ctx)));
    }

    /**
     * Helper to create a {@link ScriptFunction} that accepts an {@link Integer} and returns a {@link BitString} <i>of the that size</i>
     * @param name the name for the new function
     * @param f the function implementation
     * @return the new {@link ScriptFunction}
     */
    public static ScriptFunction ofSized(final String name, final Function<Integer, BitString> f) {
        return new ScriptFunction(name, (script, args) -> args.size() == 1 && args.get(0) instanceof final Integer size ? size : Util.fail(() -> new ScriptException()), (script, ctx, args) -> f.apply((Integer) args.get(0)));
    }

    /**
     * Returns a list of built-in functions available in every script
     * @return a list of built-in functions available in every script
     */
    public static List<ScriptFunction> builtins() {
        return List.of(
                ScriptFunction.ofBitString("inc", BitString::increment),
                ScriptFunction.ofBitString("dec", BitString::decrement),
                ScriptFunction.ofSized("rand", size -> {
                    final BitString t = new BitString(size);
                    for(int i = 0; i < t.size(); i++) {
                        if(Math.random() < 0.5) t.set(i);
                    }
                    return t;
                }),
                ScriptFunction.ofSized("zero", BitString::zero),
                ScriptFunction.ofSized("one", BitString::one),
                ScriptFunction.ofBitString("any", value -> !value.isEmpty() ? BitString.one() : BitString.zero()),
                ScriptFunction.ofBitString("all", value -> value.equals(BitString.one(value.size())) ? BitString.one() : BitString.zero())
        );
    }
}
