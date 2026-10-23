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

    public static void main(final String args[]) throws IOException {
        if(args.length == 1) {
            final Script script = parse(new FileInputStream(args[0]), State::create, ScriptFunction.builtins(), Map.of());
            script.evaluate();
            System.out.println(script.getStates().entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), ((State.ValueBackedState) entry.getValue()).get())).collect(Collectors.joining("\n")));
        } else {
            throw new IllegalArgumentException("Expected single RHDL file argument");
        }
    }
}
