package io.github.sianabeeblebrox.rhdl;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Script {
    private final @Nullable Script parent;
    private final Map<String, Expression> expressions = new HashMap<>();
    private final List<Instruction> instructions = new LinkedList<>();

    private final @Nullable TriFunction<String, State.Type, Map<String, Object>, State> factory;
    private final @Nullable Map<String, ScriptFunction> functions;
    private final @Nullable Map<String, State> states;

    private Script(final Script parent) {
        this.parent = parent;
        this.factory = null;
        this.functions = null;
        this.states = null;
    }

    public Script(
            final TriFunction<String, State.Type, Map<String, Object>, State> factory,
            final List<ScriptFunction> functions,
            final Map<String, BitString> constants
    ) {
        this.parent = null;
        this.factory = factory;
        this.functions = functions.stream().collect(Collectors.toMap(ScriptFunction::getName, Function.identity()));
        this.states = new HashMap<>();

        constants.forEach((name, value) -> this.let(name, Expression.literal(value)));
    }

    @Nullable Script getParent() {
        return this.parent;
    }

    boolean isRoot() {
        return this.getParent() == null;
    }

    Script getRoot() {
        return this.getParent() == null ? this : this.getParent();
    }

    List<Instruction> getInstructions() {
        return this.instructions;
    }

    Script derive() {
        return new Script(this);
    }

    public @Nullable State getState(final String name) {
        return this.getRoot().states.get(name);
    }

    public Map<String, State> getStates() {
        return Collections.unmodifiableMap(this.getRoot().states);
    }

    public boolean hasState(final String name) {
            return this.getRoot().states.containsKey(name);
    }

    public @Nullable ScriptFunction getFunction(final String name) {
        return this.getRoot().functions.get(name);
    }

    public boolean hasFunction(final String name) {
        return this.getRoot().functions.containsKey(name);
    }

    public @Nullable Expression getExpression(final String name) {
        if(this.expressions.containsKey(name)) {
            return this.expressions.get(name);
        } else if(this.getParent() != null) {
            return this.getParent().getExpression(name);
        } else {
            return null;
        }
    }

    public boolean hasExpression(final String name) {
        return this.expressions.containsKey(name) || (this.getParent() != null && this.getParent().hasExpression(name));
    }

    void let(final String name, final Expression expression) {
        if(this.hasFunction(name) || this.hasState(name) || this.hasExpression(name)) throw new ScriptException("Cannot shadow name '%s'", name);
        this.expressions.put(name, expression);
    }

    void declare(final String name, final State.Type type, final Map<String, Object> layout) {
        if(!this.isRoot()) throw new ScriptException("Cannot declare state '%s' outside of top-level", name);
        if(this.hasFunction(name) || this.hasState(name) || this.hasExpression(name)) throw new ScriptException("Cannot shadow name '%s'", name);

        final var state = this.factory.apply(name, type, layout);
        if(state instanceof final State.ReadableState readableState) {
            this.let(name, new Expression(readableState.size(), ctx -> readableState.reader().get().clone()));
        }
        this.states.put(name, state);
    }

    public @Nullable BitString evaluate() {
        for(int i = 0; i < this.instructions.size(); i++) {
            if(i == this.instructions.size() - 1) {
                return this.instructions.get(i).evaluate();
            } else {
                this.instructions.get(i).evaluate();
            }
        }
        return null;
    }

    public @Nullable Integer size() {
        return this.instructions.isEmpty() ? null : this.instructions.getLast().size();
    }
}
