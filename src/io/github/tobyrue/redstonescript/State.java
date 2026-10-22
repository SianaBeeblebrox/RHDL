package io.github.tobyrue.redstonescript;

public interface State {
    int size();

    interface ReadableState extends State {
        BitString get();
    }

    interface WritableState extends State {
        default void set(final BitString value) {
            if(this.size() != value.size()) throw new ScriptException();
            setUnchecked(value);
        }
        void setUnchecked(final BitString value);
    }

    final class StoredState implements ReadableState, WritableState {
        private BitString value;

        public StoredState(final BitString value) {
            this.value = value;
        }

        public StoredState(final int size) {
            this.value = new BitString(size);
        }

        public StoredState() {
            this(1);
        }

        @Override
        public int size() {
            return this.value.size();
        }

        @Override
        public BitString get() {
            return this.value.clone();
        }

        @Override
        public void setUnchecked(final BitString value) {
            this.value = value;
        }
    }

    final class StoredOutputState implements ReadableState, WritableState {
        private final WritableState sink;
        private BitString value;

        public StoredOutputState(final WritableState sink, final BitString value) {
            this.sink = sink;
            this.value = value;
            if(sink.size() != value.size()) throw new ScriptException();
        }

        public StoredOutputState(final WritableState sink) {
            this(sink, new BitString(sink.size()));
        }

        @Override
        public int size() {
            return this.value.size();
        }

        @Override
        public BitString get() {
            return this.value;
        }

        @Override
        public void setUnchecked(final BitString value) {
            this.sink.setUnchecked(value);
            this.value = value;
        }
    }
}
