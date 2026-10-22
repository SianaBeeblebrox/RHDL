%language "Java"

%define api.parser.class {RedstoneScript}
%define api.parser.public
//%define lr.type lalr

%define parse.error verbose
%locations

%parse-param {Stack<Script> stack}

%code imports {
    import io.github.tobyrue.redstonescript.*;
    import org.jetbrains.annotations.Nullable;
    import java.io.*;
    import java.util.*;
    import java.util.regex.Pattern;
    import java.util.function.*;
}

%code {
    public static Script parse(final InputStream stream) throws IOException {
        final Script script = Script.root();
        final RedstoneScript parser = new RedstoneScript(new RedstoneScriptLexer(stream), new Stack<>());
        parser.stack.push(script);
        parser.parse();
        return script;
    }

    public static void main(String args[]) throws IOException {
        RedstoneScript p = new RedstoneScript(new FileInputStream("test.rl"));
        p.parse();
    }

    private final Script script() {
        return this.stack.peek();
    }
}

/* Bison Declarations */
%token IN "in"
%token OUT "out"
%token STATE "state"
%token LET "let"
%token ASSIGN "assign"
%token SWITCH "switch"
%token CASE "case"
%token BEGIN "begin"
%token END "end"

%token <String> NAME
%token <BitString> BITSTRING_LITERAL
%token <Integer> INTEGER_LITERAL

%type <Map<String, Object>> layout
%type <ArrayList<Map.Entry<String, Object>>> layout_entries
%type <Map.Entry<String, Object>> layout_entry
%type <Object> layout_value

%type <List<SensitivityList.Entry>> edge_list
%type <SensitivityList.Entry> edge

%type <Consumer<Script>> instruction
%type <Expression> expr

%type <List<Object>> function_arg_list
%type <Object> function_arg

%precedence '[' ']'
%nonassoc '~'
%left '<' '>'
%left '&'
%left '^'
%left '|'
%right '?' ':'

/* Grammar follows */
%%
statements: statement | statements statement;

statement: '@' '(' ')' instruction { this.script().addInstruction(Script.createSensitivityFilter(this.script(), 0, 0)); }
         | instruction             { this.script().addInstruction($1);                                                  }
         | declaration             {}
;

instruction: "begin" { this.stack.push(this.script().derive()); } statements "end" { final Script script = this.stack.pop(); $$ = (Consumer<Script>)(_script -> script.evaluate());    }
           | "assign" NAME '=' expr ';'                                            { $$ = (Consumer<Script>)(script -> script.assign($2, $4.evaluate(new Expression.Context()))); }
;

// State and Let Declarations
declaration: "let" NAME '=' expr ';' { this.script().let($2, $4);                                }
           | "in" NAME layout ';'    { this.script().declare($2, Script.StateType.IN, $3);       }
           | "out" NAME layout ';'   { this.script().declare($2, Script.StateType.OUT, $3);      }
           | "state" NAME layout ';' { this.script().declare($2, Script.StateType.INTERNAL, $3); }
;

/*
// Sensitivity Filter
sensitivity_filter: '@' '(' edge_list ')' {}
                  | '@' '(' ')' {}
;

edge_list: edge               { $$ = new ArrayList<SensitivityFilter.Edge>(List.of($1)); }
         | edge_list ',' edge { $$ = $1; $1.add($3);                                     }
;

edge: NAME     { $$ = new SensitivityFilter.Entry($1, SensitivityFilter.Edge.EITHER);   }
    | '+' NAME { $$ = new SensitivityFilter.Entry($1, SensitivityFilter.Edge.POSITIVE); }
    | '-' NAME { $$ = new SensitivityFilter.Entry($1, SensitivityFilter.Edge.NEGATIVE); }
;
*/

// State Declaration Layout Lists
layout: '(' layout_entries ')' { $$ = Map.ofEntries($2.toArray(new Map.Entry[$2.size()])); }
        | '(' ')'              { $$ = Map.of(); }
        | %empty               { $$ = Map.of(); }
;
layout_entries: layout_entries ',' layout_entry { $$ = $1; $1.add($3); }
                | layout_entry                  { $$ = new ArrayList<Map.Entry<String, Object>>(List.of($1)); }
;
layout_entry: NAME '=' layout_value { $$ = Map.entry($1, $3); }
              | NAME                { $$ = Map.entry($1, ""); }
;
layout_value: NAME              { $$ = $1; }
            | BITSTRING_LITERAL { $$ = $1; }
            | INTEGER_LITERAL   { $$ = $1; }
;

// Switches
//switch_expr: "switch" expr switch_expr_cases "end";
//switch_expr_cases: switch_expr_cases switch_expr_case | switch_expr_case;
//switch_expr_case: "case" BITSTRING_LITERAL ':' expr;

// Expressions
expr: NAME                                             { $$ = Expression.var(this.script(), $1);      }
    | BITSTRING_LITERAL                                { $$ = Expression.literal($1);                 }
    | '~' expr                                         { $$ = Expression.not($2);                     }
    | expr '&' expr                                    { $$ = Expression.and($1, $3);                 }
    | expr '^' expr                                    { $$ = Expression.xor($1, $3);                 }
    | expr '|' expr                                    { $$ = Expression.or($1, $3);                  }
    | expr '?' expr ':' expr                           { $$ = Expression.ternary($1, $3, $5);         }
    | expr '[' INTEGER_LITERAL ':' INTEGER_LITERAL ']' { $$ = Expression.slice($1, $3, $5);           }
    | expr '['                 ':' INTEGER_LITERAL ']' { $$ = Expression.slice($1, null, $4);         }
    | expr '[' INTEGER_LITERAL ':'                 ']' { $$ = Expression.slice($1, $3, null);         }
    | expr '['                 ':'                 ']' { $$ = Expression.slice($1, null, null);       }
    | expr '[' INTEGER_LITERAL ']'                     { $$ = Expression.at($1, $3);                  }
    | expr '<' '>' expr                                { $$ = Expression.concat($1, $4);              }
    | NAME '(' function_arg_list ')'                   { $$ = Expression.call(this.script(), $1, $3); }
    | '(' expr ')'                                     { $$ = $2;                                     }
;

function_arg_list: function_arg_list ',' function_arg { $$ = $1; $1.add($3);                      }
                 | function_arg                       { $$ = new LinkedList<Object>(List.of($1)); }
;
function_arg: INTEGER_LITERAL   { $$ = $1; }
            | BITSTRING_LITERAL { $$ = $1; }
;

%%

record Position(int line, int token) {
    @Override
    public String toString() {
        return String.format("%d:%d", this.line, this.token);
    }
}

class RedstoneScriptLexer implements RedstoneScript.Lexer {
    private final StreamTokenizer tokenizer;
    private Position yypos = new Position(1, 0);
    private Object yylval;

    public RedstoneScriptLexer(final InputStream stream) {
        this.tokenizer = new StreamTokenizer(new InputStreamReader(stream));
        this.tokenizer.resetSyntax();
        this.tokenizer.commentChar('#');
        this.tokenizer.eolIsSignificant(true);
        this.tokenizer.whitespaceChars('\t', '\r');
        this.tokenizer.whitespaceChars(' ', ' ');
        this.tokenizer.wordChars('0', '9');
        this.tokenizer.wordChars('a', 'z');
        this.tokenizer.wordChars('A', 'Z');
        this.tokenizer.wordChars('_', '_');
    }

    @Override
    public Position getStartPos() {
        return this.yypos;
    }

    @Override
    public Position getEndPos() {
        return this.yypos;
    }

    @Override
    public Object getLVal() {
        return yylval;
    }

    public void yyerror(final RedstoneScript.Location l, final String s) {
        if (l == null)
            System.err.println(s);
        else
            System.err.println(l + ": " + s);
    }

    @Override
    public int yylex() throws IOException {
        int ttype = this.tokenizer.nextToken();
        this.yypos = new Position(this.yypos.line(), this.yypos.token() + 1);
        if(ttype == tokenizer.TT_EOF) {
            return EOF;
        } else if(ttype == this.tokenizer.TT_EOL) {
            this.yypos = new Position(this.yypos.line() + 1, 0);
            return this.yylex();
        } else if (ttype == this.tokenizer.TT_WORD) {
            switch(this.tokenizer.sval) {
                case "in": return IN;
                case "out": return OUT;
                case "state": return STATE;
                case "assign": return ASSIGN;
                case "let": return LET;
                case "switch": return SWITCH;
                case "case": return CASE;
                case "begin": return BEGIN;
                case "end": return END;
            }

            if(Pattern.matches("^0b[01_]*$", this.tokenizer.sval)) {
                  this.yylval = BitString.valueOf(this.tokenizer.sval);
                  return BITSTRING_LITERAL;
            } else if(Pattern.matches("^[0-9][0-9_]*$", this.tokenizer.sval)) {
                  this.yylval = Integer.parseInt(this.tokenizer.sval);
                  return INTEGER_LITERAL;
            } else {
                this.yylval = this.tokenizer.sval;
                return NAME;
            }
        } else {
            return this.tokenizer.ttype;
        }
    }
}

