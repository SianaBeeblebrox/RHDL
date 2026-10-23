package io.github.sianabeeblebrox.rhdl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public final class RHDL {
    public static Script parse(final InputStream stream, final TriFunction<String, State.Type, Map<String, Object>, State> factory, final List<ScriptFunction> functions, final Map<String, BitString> constants) throws IOException {
        final RHDLParser parser = new RHDLParser(new RHDLLexer(stream), new Stack<>());
        final Script script = new Script(factory, functions, constants);
        parser.stack.push(script);
        parser.parse();
        return script;
    }

    public static void main(String args[]) throws IOException {
        final Script s = parse(new FileInputStream("test.rhdl"), State::create, ScriptFunction.builtins(), Map.of());
        s.evaluate();
        System.out.println(s.getStates().entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), ((State.ValueBackedState) entry.getValue()).get())).collect(Collectors.joining(", ")));
    }
}
