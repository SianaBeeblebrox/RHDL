import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RHDLx {
    public static final int MAX_PINS = 6;

    public static void println(final String message, final Object... args) {
        System.out.printf(message + "\n", args);
    }
    public static void main(String[] args) throws Throwable {
        println("=== RHDL ===");

        var x = new StreamTokenizer(new InputStreamReader(new FileInputStream("test.rhdl")));
        x.resetSyntax();
        x.commentChar('#');
        x.wordChars('a', 'z');
        x.wordChars('A', 'Z');
        x.whitespaceChars('\t', '\t');
        x.whitespaceChars(' ', ' ');
        x.wordChars('0', '9');
        x.wordChars('_', '_');

        int token;
        while((token = x.nextToken()) != StreamTokenizer.TT_EOF) {
            switch(token) {
                case StreamTokenizer.TT_EOF: break;
                case StreamTokenizer.TT_WORD: println("WORD:%s", x.sval); break;
                case StreamTokenizer.TT_NUMBER:
                case StreamTokenizer.TT_EOL: continue;
                default: println("CHAR:%c", token);
            }
        }

        /*
        parse("""
        in D;
        in ENABLE(pin = 5, type = REDSTONE);
        
        out D;
        """);*/
    }

    public record Pin(int index, Mode mode, Type type, @Nullable String name) {
        public enum Mode {NONE, INPUT, OUTPUT}
        public enum Type {
            DEFAULT,
            REDSTONE;
            public static Type parse(final HashMap<String, String> layout, final String name, final int linenum) throws ScriptException {
                switch(layout.getOrDefault("type", "default").toLowerCase(Locale.ROOT)) {
                    case "redstone":
                    case "analog":
                        return Type.REDSTONE;
                    case "default":
                    case "digital":
                        return Type.DEFAULT;
                    default:
                        throw new ScriptException("Invalid type '%s' for pin '%s' on line %d", layout.getOrDefault("type", "default").toLowerCase(Locale.ROOT), name, linenum);
                }
            }
        }

        public Pin(final int index) {
            this(index, Mode.NONE, Type.DEFAULT, null);
        }
    }

    public static final class Script {
        public final Pin[] PINS;
        public final HashMap<String, Expression> VARIABLES;

        public Script(final Pin[] pins, HashMap<String, Expression> variables) {
            this.PINS = pins;
            this.VARIABLES = variables;
        }

        public boolean execute() {
            return false;
        }
    }

    private static class Util {
        public record Pair<A,B>(A a, B b) {}
        public static <A,B> Stream<Pair<A, B>> zip(List<A> a, List<B> b) {
            return IntStream.range(0, Math.min(a.size(), b.size())).mapToObj(i -> new Pair(a.get(i), b.get(i)));
        }
        public static <A,B> Stream<Pair<A, B>> zip(A[] a, B[] b) {
            return zip(Arrays.stream(a).toList(), Arrays.stream(b).toList());
        }

        public static <A> Stream<Pair<Integer, A>> enumerated(List<A> a) {
            return IntStream.range(0, a.size()).mapToObj(i -> new Pair(i, a.get(i)));
        }
    }

    @FunctionalInterface
    private interface Expression {
        int evaluate(final HashMap<String, Expression> variables);

        static Expression and(final Expression lhs, final Expression rhs) {
            return vars -> lhs.evaluate(vars) & rhs.evaluate(vars);
        }

        static Expression or(final Expression lhs, final Expression rhs) {
            return vars -> lhs.evaluate(vars) | rhs.evaluate(vars);
        }

        static Expression not(final Expression expression) {
            return vars -> ~expression.evaluate(vars);
        }

        static Expression literal(final int value) {
            return vars -> value;
        }

        static Expression variable(final String name) {
            return vars -> vars.getOrDefault(name, literal(0)).evaluate(vars);
        }

        record SwitchCase(Pattern[] patterns, Expression expression) implements Expression {
            public boolean matches(final Boolean[] values) {
                return this.patterns.length <= values.length && Util.zip(values, this.patterns).allMatch(pair -> pair.b.matches(pair.a));
            }

            @Override
            public int evaluate(final HashMap<String, Expression> variables) {
                return this.expression.evaluate(variables);
            }

            enum Pattern {
                TRUE() {@Override boolean matches(final boolean value) {return value;}},
                FALSE() {@Override boolean matches(final boolean value) {return !value;}},
                ANY() {@Override boolean matches(final boolean value) {return true;}};
                abstract boolean matches(final boolean value);
            }
        }
        static Expression switchexpr(final Expression[] args, final List<SwitchCase> cases) {
            return vars -> {
                final Integer[] values = Arrays.stream(args).map(e -> e.evaluate(vars)).toArray(Integer[]::new);
                for(final var switchCase : cases) {
                    if(switchCase.matches(Arrays.stream(values).map(x -> x > 0).toArray(Boolean[]::new))) { // TODO improve switch
                        return switchCase.evaluate(vars);
                    }
                }
                return 0;
            };
        }
    }

    public static Script parse(final String text) throws ScriptException {
        Pin[] pins = IntStream.range(0, MAX_PINS).mapToObj(i -> new Pin(i)).toArray(Pin[]::new);
        int nextPin = 0;

        Util.Pair<Integer, String> acc = null;
        for(final var pair : Util.enumerated(text.lines().collect(Collectors.toList())).collect(Collectors.toList())) {
            final int linenum = Optional.ofNullable(acc).orElse(pair).a;
            final String line = Optional.ofNullable(acc).map(Util.Pair::b).orElse("") + pair.b;

            if(line.endsWith("\\")) {
                acc = new Util.Pair(linenum, line.substring(0, line.length() - 1));
                continue;
            } else {
                acc = null;
            }

            final String statement = line.replaceFirst("#.*$", "").trim();
            if(statement.isEmpty()) {
                continue;
            }

            Matcher matcher;
            if((matcher = Pattern.compile("^(?<mode>in|out)\\s+(?<name>[A-Z_]+)\\s*(?:\\((?<layout>(?:[^=,]+(?:=[^=,]+)?(?:,[^=,]+(?:=[^=,]+)?)*)?)\\))?\\s*;$").matcher(statement)).matches()) {
                final String name = matcher.group("name");

                final HashMap<String, String> layout = new HashMap<>();
                if(matcher.group("layout") != null) {
                    for(final var entry : matcher.group("layout").split(",")) {
                        final var t= entry.indexOf('=');
                        if(t == -1) {
                            layout.put(entry.trim().toLowerCase(Locale.ROOT), "");
                        } else {
                            layout.put(entry.substring(0, t).trim().toLowerCase(Locale.ROOT), entry.substring(t+1).trim());
                        }
                    }
                }

                int index;
                if(layout.containsKey("pin")) {
                    try {
                        index = Integer.parseInt(layout.get("pin"));
                    } catch(final NumberFormatException e) {
                        throw new ScriptException("Expected integer index for pin '%s' but got '%s' on line %d", name, layout.get("pin"), linenum);
                    }
                } else {
                    index = nextPin++;
                }

                if(index < 0 || index >= pins.length) {
                    throw new ScriptException("Expected integer index [0,%d] for pin '%s' but got %d on line %d", MAX_PINS - 1, name, index, linenum);
                } else if(pins[index].mode != Pin.Mode.NONE) {
                    throw new ScriptException("Cannot reuse pin index %d on line %d", index, linenum);
                } else {
                    pins[index] = new Pin(
                            index,
                            matcher.group("mode").equals("in") ? Pin.Mode.INPUT : Pin.Mode.OUTPUT,
                            Pin.Type.parse(layout, name, linenum),
                            name
                    );
                }
            } else if((matcher = Pattern.compile("^(?<action>assign|var)\\s+(?<name>[A-Z_+])\\s*=(?<expr>)$").matcher(statement)).matches()) {

            } else {
                throw new ScriptException("Unexpected statement \'%s\' on line %d", statement, linenum + 1);
            }
        }

        println(Arrays.stream(pins).toList().toString());

        return null;
    }

    static {

    }

    public static final class ScriptException extends Exception {
        public ScriptException(final String message, final Object... args) {
            super(String.format(message, args));
        }
    }
}
