package io.github.tobyrue.redstonescript;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record Expression(int size, Function<Context, BitString> evaluate) {
    record Context() {}

    public final BitString evaluate(final Context context) {
        return this.evaluate.apply(context);
    }

    private static Expression bop(final Expression lhs, Expression rhs, BiConsumer<BitString, BitString> f) {
        if(lhs.size() != rhs.size()) {
            throw new ScriptException();
        }
        return new Expression(lhs.size(), ctx -> {
            final BitString result;
            f.accept(result = lhs.evaluate(ctx), rhs.evaluate(ctx));
            return result;
        });
    }

    public static Expression and(final Expression lhs, final Expression rhs) {
        return bop(lhs, rhs, BitString::and);
    }

    public static Expression or(final Expression lhs, final Expression rhs) {
        return bop(lhs, rhs, BitString::or);
    }

    public static Expression xor(final Expression lhs, final Expression rhs) {
        return bop(lhs, rhs, BitString::xor);
    }

    public static Expression not(final Expression expression) {
        return new Expression(expression.size(), ctx -> expression.evaluate(ctx).not());
    }

    public static Expression ternary(final Expression i, final Expression t, final Expression e) {
        if(t.size() != e.size()) {
            throw new ScriptException();
        }
        return new Expression(t.size(), ctx -> !i.evaluate(ctx).isEmpty() ? t.evaluate(ctx) : e.evaluate(ctx));
    }

    public static Expression literal(final BitString value) {
        return new Expression(value.size(), ctx -> value.clone());
    }

    public static Expression var(final Script script, final String name) {
        if((Object) script.get(name) instanceof final Expression expression) {
            return expression;
        } else {
            throw new ScriptException();
        }
    }

    public static Expression slice(final Expression expression, final @Nullable Integer end, final @Nullable Integer begin) {
        int iend = end == null ? expression.size() : end, ibegin = begin == null ? 0 : begin;
        if(begin < 0 || end < begin || end >= expression.size()) {
            throw new ScriptException();
        }
        return new Expression(ibegin - iend, ctx -> expression.evaluate(ctx).slice(end, begin));
    }

    public static Expression at(final Expression expression, final Integer index) {
        if(index < 0 || index >= expression.size()) {
            throw new ScriptException();
        }
        return new Expression(1, ctx -> expression.evaluate(ctx).at(index));
    }

    public static Expression concat(final Expression lhs, final Expression rhs) {
        return new Expression(lhs.size() + rhs.size(), ctx -> BitString.concat(lhs.evaluate(ctx), rhs.evaluate(ctx)));
    }

    public static Expression call(final Script script, final String name, final List<Object> args) {
        if((Object) script.getFunction(name) instanceof final ScriptFunction function) {
            return new Expression(function.size(script, args), ctx -> function.evaluate(script, ctx, args));
        } else {
            throw new ScriptException();
        }
    }
}