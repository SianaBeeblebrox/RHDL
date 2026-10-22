package io.github.tobyrue.redstonescript;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class Script {
    private final TriFunction<String, StateType, HashMap<String, Object>, State> getOrCreateState;

    /**
     * The parent script or {@code null} iff script root
     */
    private final @Nullable Script parent;

    private final @Nullable Map<String, State> __user_states;

    /**
     * A map of functions in the script, {@code null} iff not script root
     */
    private final @Nullable Map<String, ScriptFunction> functions;

    private final @Nullable HashMap<String, BitString> iv;
    private final HashMap<String, Expression> expressions;

    private final LinkedList<Consumer<Script>> instructions;

    private Script(
        final @Nullable Script parent,
        final @Nullable Map<String, State> __user_states,
        final @Nullable Map<String, ScriptFunction> functions,
        final @Nullable HashMap<String, BitString> iv,
        final HashMap<String, Expression> expressions,
        final LinkedList<Consumer<Script>> instructions
    ) {
        this.parent = parent;
        this.__user_states = __user_states;
        this.functions = functions;
        this.iv = iv;
        this.expressions = expressions;
        this.instructions = instructions;
    }

    public Script derive() {
        return new Script(this, null, null, null, new HashMap<>(), new LinkedList<>());
    }

    public static Script root(final Map<String, State> states, final List<ScriptFunction> functions, final Map<String, BitString> iv) {
        return new Script(
                null,
                Map.ofEntries(states.entrySet().toArray(Map.Entry[]::new)),
                Map.ofEntries(Stream.concat(ScriptFunction.builtins().stream(), functions.stream()).map(function -> Map.entry(function.getName(), function)).toArray(Map.Entry[]::new)),
                new HashMap<>(iv),
                new HashMap<>(),
                new LinkedList<>()
        );
    }

    /**
     * Returns true iff this script does not have a parent
     * @return true iff this script does not have a parent
     */
    protected boolean isRoot() {
        return this.parent == null;
    }

    /**
     * Returns the root script (possibly {@code this})
     * @return the root script (possibly {@code this})
     */
    protected Script getRoot() {
        return this.isRoot() ? this : this.getRoot();
    }

    private static <T> Optional<T> getFromLayout(final HashMap<String, Object> layout, final String name, final Class<T> type) {
        if(layout.containsKey(name)) {
            if(type.isInstance(layout.get(name))) {
                return Optional.of((T) layout.get(name));
            } else {
                throw new ScriptException();
            }
        } else {
            return Optional.empty();
        }
    }

    private static void ensureSizesMatch(final Optional<Integer> a, final String strA, final Optional<Integer> b, final String strB) {
        if(a.isPresent() && b.isPresent() && !a.get().equals(b.get())) throw new ScriptException();
    }

    protected enum StateType {IN,OUT,INTERNAL}

    protected void declare(final String name, final StateType type, final HashMap<String, Object> layout) {
        if(!this.isRoot()) throw new ScriptException();
        final State result;
        if(this.hasFunction(name) || this.__user_states.putIfAbsent(name, result = switch(type) {
            case IN -> {
                final Optional<String> target = getFromLayout(layout, "target", String.class);
                final Optional<State> state = target.flatMap(t -> Optional.ofNullable(statePool.get(t)));
                final Optional<Integer> size = getFromLayout(layout, "size", Integer.class);

                ensureSizesMatch(state.map(State::size), "target", size, "size");

                if(state.orElse(null) instanceof final State.ReadableState rs) {
                    yield rs;
                } else {
                    throw new ScriptException();
                }
            }
            case OUT -> {
                final Optional<String> target = getFromLayout(layout, "target", String.class);
                final Optional<State> state = target.flatMap(t -> Optional.ofNullable(statePool.get(t)));
                final Optional<BitString> value = getFromLayout(layout, "default", BitString.class);
                final Optional<Integer> size = getFromLayout(layout, "size", Integer.class);

                ensureSizesMatch(state.map(State::size), "target", size, "size");
                ensureSizesMatch(state.map(State::size), "target", value.map(BitString::size), "default");

                if(state.orElse(null) instanceof final State.WritableState ws) {
                    yield value.map(v -> new State.StoredOutputState(ws, v)).orElseGet(() -> new State.StoredOutputState(ws));
                } else {
                    throw new ScriptException();
                }
            }
            case INTERNAL -> {
                //TODO: make default pull from IV
                final Optional<BitString> value = getFromLayout(layout, "default", BitString.class);
                final Optional<Integer> size = getFromLayout(layout, "size", Integer.class);

                ensureSizesMatch(size, "size", value.map(BitString::size), "default");

                yield value.map(State.StoredState::new).or(() -> size.map(State.StoredState::new)).orElseGet(State.StoredState::new);
            }
        }) != null) throw new ScriptException();

        if(result instanceof final State.ReadableState rs) {
            this.let(name, new Expression(rs.size(), ctx -> rs.get().clone()));
            this.iv.putIfAbsent(name, rs.get());
        }
    }

    protected void let(final String name, final Expression expression) {
        if(this.hasFunction(name) || this.expressions.putIfAbsent(name, expression) != null) throw new ScriptException();
    }

    /**
     * Returns the expression for variable named {@code name} or {@code null} iff it does not exist on this or a parent script
     * @param name the variable to get
     * @return the expression for variable named {@code name} or {@code null} iff it does not exist on this or a parent script
     */
    protected Expression get(final String name) {
        if(this.expressions.containsKey(name)) {
            return this.expressions.get(name);
        } else if(!this.isRoot()) {
            return this.parent.get(name);
        } else {
            throw new ScriptException();
        }
    }

    /**
     * Returns true iff there exists a variable named {@code name} on this or a parent script
     * @param name the variable to check
     * @return true iff there exists a variable named {@code name} on this or a parent script
     */
    protected boolean has(final String name) {
        return this.expressions.containsKey(name) || (this.parent != null && this.parent.has(name));
    }

    protected void assign(final String name, final BitString value) {
        if(this.getState(name) instanceof final State.WritableState ws) {
            ws.set(value);
        } else {
            throw new ScriptException();
        }
    }

    public void evaluate() {
        this.instructions.forEach(statement -> statement.accept(this));
        this.iv.replaceAll((k, v) -> ((State.ReadableState) this.__user_states.get(k)).get());

    }

    // TODO statepool

    /**
     * Returns true iff there exists a function named {@code name} on script root
     * @param name the function to check
     * @return true iff there exists a function named {@code name} on script root
     */
    protected boolean hasFunction(final String name) {
        return this.getRoot().functions.containsKey(name);
    }

    /**
     * Returns the function named {@code name} or {@code null} iff it does not exist on script root
     * @param name the function to get
     * @return the function named {@code name} or {@code null} iff it does not exist on script root
     */
    protected @Nullable ScriptFunction getFunction(final String name) {
        return this.getRoot().functions.get(name);
    }

    /**
     * Adds an instruction to this script, will be called with argument {@code this} on {@link Script#evaluate()}
     * @param instruction the instruction to add
     */
    protected void addInstruction(final Consumer<Script> instruction) {
        this.instructions.add(instruction);
    }

    protected enum Edge { POSITIVE, NEGATIVE, EITHER }
    protected record EdgeEntry(String name, Edge edge) {}

    protected static Consumer<Script> createSensitivityFilter(final Script script, final List<EdgeEntry> list, final Consumer<Script> instruction) {
        throw new RuntimeException("NYI");
    }

}
