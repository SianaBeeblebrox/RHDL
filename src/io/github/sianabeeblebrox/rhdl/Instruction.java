package io.github.sianabeeblebrox.rhdl;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

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

    static final class SensitivityList {
        enum Edge {
            POSITIVE {
                @Override
                boolean test(final @Nullable BitString prev, final BitString curr) {
                    return Objects.equals(prev, BitString.zero()) && Objects.equals(curr, BitString.one());
                }
                @Override
                void validate(final Expression expression) {
                    if(expression.size() != 1) {
                        throw new ScriptException("Positive edge filter cannot be applied to multi-bit value of size %d", expression.size());
                    }
                }
            },
            NEGATIVE {
                @Override
                boolean test(final @Nullable BitString prev, final BitString curr) {
                    return Objects.equals(prev, BitString.one()) && Objects.equals(curr, BitString.zero());
                }
                @Override
                void validate(final Expression expression) {
                    if(expression.size() != 1) {
                        throw new ScriptException("Negative edge filter cannot be applied to multi-bit value of size %d", expression.size());
                    }
                }
            },
            CHANGE {
                @Override
                boolean test(final @Nullable BitString prev, final BitString curr) {
                    return !Objects.equals(prev, curr);
                }
            };
            void validate(final Expression expression) {}
            abstract boolean test(final @Nullable BitString prev, final BitString cur);
        }
        record Entry(Edge edge, Expression expression) {}
        public static Instruction of(final Script script, final Instruction instruction, final List<Entry> entries) {
            entries.forEach(entry -> entry.edge.validate(entry.expression()));

            final int ptr = script.getPreviousState().size();
            script.getPreviousState().addAll(Collections.nCopies(entries.size(), null));

            return new Instruction(null, () -> {
                if(IntStream.range(0, entries.size()).mapToObj(offset -> {
                    final var entry = entries.get(offset);
                    final var curr = entry.expression.evaluate(new Expression.Context());
                    final boolean t = entry.edge.test(script.getPreviousState(ptr + offset), curr);
                    script.setPreviousState(ptr + offset, curr);
                    return t;
                }).toList().stream().anyMatch(b -> b)) {
                    instruction.evaluate();
                }
                return null;
            });
        }
    }

    public static Instruction conditional(final Expression expression, final Instruction instruction) {
        return new Instruction(null, () -> {
            if(!expression.evaluate(new Expression.Context()).isEmpty()) {
                instruction.evaluate();
            }
            return null;
        });
    }

    public static Instruction block(final Script script) {
        return new Instruction(script.size(), script::evaluate);
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
            if(size != null && !isExhaustive(arg, cases)) {
                throw new ScriptException("Cases for switch expression are not exhaustive");
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

        private static boolean isExhaustive(final Expression arg, final List<Case> cases) {
            final Set<BitString> literals = new HashSet<>();
            for(final Case c : cases) {
                if(c.expression == null) {
                    return true;
                } else if(c.expression instanceof final Expression.ConstExpression expr) {
                    literals.add(expr.value());
                }
            }
            return literals.size() == 1 << arg.size();
        }
    }
}
