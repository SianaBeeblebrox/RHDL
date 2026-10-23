package io.github.sianabeeblebrox.rhdl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface State {
    enum Type {
        IN, OUT, STATE
    }

    int size();

    interface ReadableState extends State {
        Reader reader();
        abstract class Reader {
            abstract BitString get();
        }
    }

    interface WritableState extends State {
        Writer writer();
        abstract class Writer {
            abstract void set(final BitString value);
        }
    }

    abstract class ValueBackedState implements State {
        protected BitString value;

        public ValueBackedState(final BitString value) {
            this.value = value;
        }
        public ValueBackedState(final int size) {
            this(new BitString(size));
        }
        public ValueBackedState() {
            this(1);
        }

        @Override
        public final int size() {
            return this.value.size();
        }

        public final void set(final BitString value) {
            if(value.size() != this.size()) throw new ScriptException("Cannot assign value of size %d to state of size %d", value.size(), this.size());
            this.value = value;
        }

        public final BitString get() {
            return this.value.clone();
        }

        static final class Readable extends ValueBackedState implements ReadableState {
            public Readable(final BitString value) {
                super(value);
            }
            @Override
            public Reader reader() {
                return new Reader() {
                    @Override
                    BitString get() {
                        return Readable.this.value;
                    }
                };
            }
        }

        static final class Writable extends ValueBackedState implements WritableState {
            public Writable(final BitString value) {
                super(value);
            }
            @Override
            public Writer writer() {
                return new Writer() {
                    @Override
                    void set(final BitString value) {
                        Writable.this.value = value;
                    }
                };
            }
        }

        static final class Stored extends ValueBackedState implements ReadableState, WritableState {
            public Stored(final BitString value) {
                super(value);
            }
            @Override
            public Reader reader() {
                return new Reader() {
                    @Override
                    BitString get() {
                        return Stored.this.value;
                    }
                };
            }
            @Override
            public Writer writer() {
                return new Writer() {
                    @Override
                    void set(final BitString value) {
                        Stored.this.value = value;
                    }
                };
            }
        }
    }

    static State create(final String name, final Type type, final Map<String, Object> layout) {
        final var size = getLayoutParameter(layout, "size", Integer.class);
        final var value = getLayoutParameter(layout, "default", BitString.class);

        if((Object) layout.keySet().stream().filter(key -> !List.of("size", "default").contains(key)).findFirst().orElse(null) instanceof final String key) {
            throw new ScriptException("Unexpected layout entry '%s'", key);
        }

        if(size.isPresent() && value.isPresent()) {
            if(size.get() != value.get().size()) {
                throw new ScriptException("Inconsistent 'size' and 'default' values %d and %d for state '%s' layout", size.get(), value.get().size(), name);
            }
        }

        return (switch(type) {
            case IN -> (Function<BitString, State>) ValueBackedState.Readable::new;
            case OUT -> (Function<BitString, State>) ValueBackedState.Writable::new;
            case STATE -> (Function<BitString, State>) ValueBackedState.Stored::new;
        }).apply(value.orElseGet(() -> new BitString(size.orElse(1))));
    }

    static <T> Optional<T> getLayoutParameter(final Map<String, Object> layout, final String name, final Class<T> type) {
        if(layout.containsKey(name)) {
            final var value = layout.get(name);
            if(type.isInstance(value)) {
                return Optional.of((T) value);
            } else {
                throw new ScriptException(
                        "Expected %s for layout parameter '%s' but got %s (%s)",
                        type.getSimpleName().toLowerCase(Locale.ROOT),
                        name,
                        value == null ? "nothing" : value.getClass().getSimpleName().toLowerCase(Locale.ROOT),
                        value instanceof final String string ? "'" + string + "'" : value
                );
            }
        } else {
            return Optional.empty();
        }
    }
}
