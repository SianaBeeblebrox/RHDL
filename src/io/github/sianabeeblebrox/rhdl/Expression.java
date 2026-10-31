package io.github.sianabeeblebrox.rhdl;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

interface Expression {
    record Context() {}
    int size();
    BitString evaluate(final Context context);

    static Expression of(final int size, final Function<Context, BitString> impl, boolean constant) {
        return constant ? new ConstExpression(impl.apply(new Context())) : new Expression() {
            @Override
            public int size() {
                return size;
            }

            @Override
            public BitString evaluate(final Context context) {
                return impl.apply(context);
            }
        };
    }

    private static Expression bop(final Expression lhs, final Expression rhs, final BiConsumer<BitString, BitString> f, final String name) {
        if(lhs.size() != rhs.size()) {
            throw new ScriptException("Inconsistent sizes %d and %d for binary operator '%s'", lhs.size(), rhs.size(), name);
        }
        return Expression.of(lhs.size(), ctx -> {
            final BitString result;
            f.accept(result = lhs.evaluate(ctx), rhs.evaluate(ctx));
            return result;
        }, lhs instanceof ConstExpression && rhs instanceof ConstExpression);
    }

    static Expression and(final Expression lhs, final Expression rhs) {
        return bop(lhs, rhs, BitString::and, "&");
    }

    static Expression or(final Expression lhs, final Expression rhs) {
        return bop(lhs, rhs, BitString::or, "|");
    }

    static Expression xor(final Expression lhs, final Expression rhs) {
        return bop(lhs, rhs, BitString::xor, "^");
    }

    static Expression not(final Expression expression) {
        return Expression.of(expression.size(), ctx -> expression.evaluate(ctx).not(), expression instanceof ConstExpression);
    }

    static Expression ternary(final Expression i, final Expression t, final Expression e) {
        if(t.size() != e.size()) {
            throw new ScriptException("Inconsistent sizes %d and %d for ternary branches", t.size(), e.size());
        }
        return Expression.of(t.size(), ctx -> !i.evaluate(ctx).isEmpty() ? t.evaluate(ctx) : e.evaluate(ctx), i instanceof ConstExpression && t instanceof ConstExpression && e instanceof ConstExpression);
    }

    record ConstExpression(BitString value) implements Expression {
        @Override
        public int size() {
            return this.value.size();
        }

        @Override
        public BitString evaluate(final Context context) {
            return this.value.clone();
        }
    }

    static Expression literal(final BitString value) {
        return new ConstExpression(value);
    }

    static Expression var(final Script script, final String name) {
        if((Object) script.getExpression(name) instanceof final Expression expression) {
            return expression;
        } else {
            throw new ScriptException("No such state or variable '%s'", name);
        }
    }

    static Expression slice(final Expression expression, final @Nullable Integer end, final @Nullable Integer begin) {
        return slice(expression, end == null ? expression.size() : end, begin == null ? 0 : begin);
    }

    static Expression slice(final Expression expression, final int end, final int begin) {
        if(begin < 0 || end < begin || end >= expression.size()) {
            throw new ScriptException("Invalid slice bounds [%d:%d] for size %d", end, begin, expression.size());
        }
        return Expression.of(end - begin, ctx -> expression.evaluate(ctx).slice(end, begin), expression instanceof ConstExpression);
    }

    static Expression at(final Expression expression, final Integer index) {
        if(index < 0 || index >= expression.size()) {
            throw new ScriptException("Invalid index %d for size %d", index, expression.size());
        }
        return Expression.of(1, ctx -> expression.evaluate(ctx).at(index), expression instanceof ConstExpression);
    }

    static Expression concat(final Expression lhs, final Expression rhs) {
        return Expression.of(lhs.size() + rhs.size(), ctx -> BitString.concat(lhs.evaluate(ctx), rhs.evaluate(ctx)), lhs instanceof ConstExpression && rhs instanceof ConstExpression);
    }

    static Expression call(final Script script, final String name, final List<Object> args) {
        if((Object) script.getFunction(name) instanceof final ScriptFunction function) {
            return Expression.of(function.size(script, args), ctx -> function.evaluate(script, new Expression.Context(), args), false);
        } else {
            throw new ScriptException("No such function '%s'", name);
        }
    }

    static Expression instruction(final Instruction instruction) {
        if(instruction.size() != null) {
            return Expression.of(instruction.size(), ctx -> instruction.evaluate(), false);
        } else {
            throw new ScriptException("Instruction does not resolve to a value");
        }
    }
}