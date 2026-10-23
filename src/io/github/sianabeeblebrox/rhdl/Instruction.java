package io.github.sianabeeblebrox.rhdl;

record Instruction(Runnable instr) {
    public final void evaluate() {
        this.instr.run();
    }

    public static Instruction assign(final Script script, final String name, final Expression expression) {
        if((Object) script.getState(name) instanceof final State state) {
            if(state instanceof final State.WritableState writable) {
                if(state.size() == expression.size()) {
                    return new Instruction(() -> writable.writer().set(expression.evaluate(new Expression.Context())));
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

    public static Instruction block(final Script script) {
        return new Instruction(script::evaluate);
    }


    public static Instruction expression(final Expression expression) {
        return new Instruction(() -> expression.evaluate(new Expression.Context()));
    }

    public static Instruction sensitivityList(final Instruction inner) {
        throw new UnsupportedOperationException(); // TODO
    }
}
