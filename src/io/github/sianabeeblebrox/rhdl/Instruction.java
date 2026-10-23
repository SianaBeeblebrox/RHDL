package io.github.sianabeeblebrox.rhdl;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

record Instruction(@Nullable Integer size, Supplier<@Nullable BitString> instr) {
    public @Nullable BitString evaluate() {
        return this.instr.get();
    }

    public static Instruction assign(final Script script, final String name, final Expression expression) {
        if((Object) script.getState(name) instanceof final State state) {
            if(state instanceof final State.WritableState writable) {
                if(state.size() == expression.size()) {
                    return new Instruction(null, () -> {
                        writable.writer().set(expression.evaluate(new Expression.Context()));
                        return null;
                    });
                } else {
                    throw new ScriptException("Cannot assign value of size %d to state '%s' of size %d", expression.size(), name, state.size());
                }
            } else {
                throw new ScriptException("State '%s' is not writable", name);
            }
        } else {
            throw new ScriptException("No such state '%s'", name);
        }
    }

    public static Instruction expression(final Expression expression) {
        return new Instruction(expression.size(), () -> expression.evaluate(new Expression.Context()));
    }

    public static Instruction discard(final Expression expression) {
        return new Instruction(null, () -> {
            expression.evaluate(new Expression.Context());
            return null;
        });
    }

    public static Instruction discard(final Instruction instruction) {
        return new Instruction(null, () -> {
            instruction.evaluate();
            return null;
        });
    }

    public static Instruction sensitivityList(final Instruction inner) {
        throw new UnsupportedOperationException(); // TODO
    }

    public static Instruction block(final Script script) {
        return new Instruction(script.size(), script::evaluate);
    }

    public static Instruction call(final Script script, final String name, final List<Object> args) {
        if((Object) script.getFunction(name) instanceof final ScriptFunction function) {
            return new Instruction(function.size(script, args), () -> function.evaluate(script, new Expression.Context(), args));
        } else {
            throw new ScriptException("No such function '%s'", name);
        }
    }

    static final class Switch {
        record Case(@Nullable Expression expression, Instruction branch) {}
        public static Instruction of(final Expression arg, final List<Case> cases) {
            final @Nullable Integer size = cases.isEmpty() ? null : cases.getFirst().branch().size();
            for(int i = 0; i < cases.size(); i++) {
                // Check all patterns match size of arg
                if((Object) cases.get(i).expression instanceof final Expression expr && expr.size() != arg.size()) {
                    throw new ScriptException("Inconsistent sizes %d and %d for switch argument and case %d", arg.size(), expr.size(), i);
                }

                // Check all branches are same size
                if(!Objects.equals(cases.get(i).branch.size(), size)) {
                    if(size == null || cases.get(i).branch.size() == null) {
                        throw new ScriptException("Unsupported mixture of expression and instruction branches");
                    } else {
                        throw new ScriptException("Inconsistent sizes %d and %d for switch branches %d and %d", size, cases.get(i).branch.size(), 0, i);
                    }
                }
            }

            // Require default case if size is not null
            if(size != null && cases.stream().noneMatch(c -> c.expression == null)) {
                throw new ScriptException("Missing default branch for switch expression");
            }

            return new Instruction(size, () -> {
                final var value = arg.evaluate(new Expression.Context());
                for(final var c : cases) {
                    if(c.expression == null || c.expression.evaluate(new Expression.Context()).equals(value)) {
                        return c.branch().evaluate();
                    }
                }
                return null;
            });
        }
    }
}
