%language "Java"

%define package {io.github.sianabeeblebrox.rhdl} // Update to api.package in Bison 3.7+!
%define api.parser.class {RHDLParser}
%define api.parser.final
//%define lr.type lalr

%define parse.error verbose
%locations

%parse-param {Stack<Script> stack}

%code imports {
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.function.Supplier;
}

%code {
    private final Script script() {
        return this.stack.peek();
    }

    private static <T> T attach(final Supplier<T> f, final Location loc) {
        try {
            return f.get();
        } catch(final ScriptException e) {
            e.setLocation(loc);
            throw e;
        }
    }

    private static void attach(final Runnable f, final Location loc) {
        try {
            f.run();
        } catch(final ScriptException e) {
            e.setLocation(loc);
            throw e;
        }
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

%type <Instruction> instruction
%type <Instruction> block_expr
%type <Expression> expr

%type <Map<String, Object>> layout
%type <ArrayList<Map.Entry<String, Object>>> layout_entries
%type <Map.Entry<String, Object>> layout_entry

%type <List<Instruction.SensitivityList.Entry>> edges
%type <Instruction.SensitivityList.Entry> edge

%type <List<Object>> arg_list

%type <List<Instruction.Switch.Case>> switch_cases
%type <Instruction.Switch.Case> switch_case

%type <Object> literal
%type <Object> arg

%precedence NAME
%precedence '(' ')'
%precedence '[' ']'
%nonassoc '~'
%left '<' '>'
%left '&'
%left '^'
%left '|'
%right '?' ':'

%%
statements: %empty | statements statement;

statement: instruction             { this.script().getInstructions().add($1); }
         | declaration             {}
;

instruction: "assign" NAME '=' expr ';'                                            { $$ = this.attach(() -> Instruction.assign(this.script(), $2, $4), @$); }
           | expr                                                                  { $$ = this.attach(() -> Instruction.expression($1), @$);                }
           | expr ';'                                                              { $$ = this.attach(() -> Instruction.discard($1), @$);                   }
           | block_expr                                                            { $$ = $1;                                                               }
           | block_expr ';'                                                        { $$ = this.attach(() -> Instruction.discard($1), @$);                   }
           | '@' '(' edges ')' instruction                                         { $$ = this.attach(() -> Instruction.SensitivityList.of(this.script(), $5, $3), @$);    }
;

block_expr: "begin" { this.stack.push(this.script().derive()); } statements "end" { $$ = this.attach(() -> Instruction.block(this.stack.pop()), @$); }
          | "switch" expr switch_cases "end"                                      { $$ = this.attach(() -> Instruction.Switch.of($2, $3), @$);     }
          | NAME '(' arg_list ')'                                                 { $$ = this.attach(() -> Instruction.call(this.script(), $1, $3), @$); }
;

// Sensitivity Lists
edges: %empty         { $$ = new LinkedList<Object>();            }
     | edges ',' edge { $$ = $1; $1.add($3);                      }
     | edge           { $$ = new LinkedList<Object>(List.of($1)); }
;

edge: '+' expr { $$ = new Instruction.SensitivityList.Entry(Instruction.SensitivityList.Edge.POSITIVE, $2); }
    | '-' expr { $$ = new Instruction.SensitivityList.Entry(Instruction.SensitivityList.Edge.NEGATIVE, $2); }
    | expr     { $$ = new Instruction.SensitivityList.Entry(Instruction.SensitivityList.Edge.CHANGE, $1);   }
;

// Switches
switch_cases: switch_cases switch_case { $$ = $1; $1.add($2); }
            | switch_case              { $$ = new ArrayList<Object>(List.of($1)); }
;

switch_case: "case" ':' instruction      { $$ = new Instruction.Switch.Case(null, $3); }
           | "case" expr ':' instruction { $$ = new Instruction.Switch.Case($2, $4); }
;

// State and Let Declarations
declaration: "let" NAME '=' expr ';' { this.attach(() -> this.script().let($2, $4), @$);                       }
           | "in" NAME layout ';'    { this.attach(() -> this.script().declare($2, State.Type.IN, $3), @$);    }
           | "out" NAME layout ';'   { this.attach(() -> this.script().declare($2, State.Type.OUT, $3), @$);   }
           | "state" NAME layout ';' { this.attach(() -> this.script().declare($2, State.Type.STATE, $3), @$); }
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
layout_entry: NAME '=' literal { $$ = Map.entry($1, $3); }
              | NAME           { $$ = Map.entry($1, ""); }
;

arg: INTEGER_LITERAL   { $$ = $1; }
   | expr              { $$ = $1; }
;

literal: NAME              { $$ = $1; }
       | BITSTRING_LITERAL { $$ = $1; }
       | INTEGER_LITERAL   { $$ = $1; }
;

// Expressions
expr: NAME                                             { $$ = this.attach(() -> Expression.var(this.script(), $1), @$);      }
    | BITSTRING_LITERAL                                { $$ = this.attach(() -> Expression.literal($1), @$);                 }
    | '~' expr                                         { $$ = this.attach(() -> Expression.not($2), @$);                     }
    | expr '&' expr                                    { $$ = this.attach(() -> Expression.and($1, $3), @$);                 }
    | expr '^' expr                                    { $$ = this.attach(() -> Expression.xor($1, $3), @$);                 }
    | expr '|' expr                                    { $$ = this.attach(() -> Expression.or($1, $3), @$);                  }
    | expr '?' expr ':' expr                           { $$ = this.attach(() -> Expression.ternary($1, $3, $5), @$);         }
    | expr '[' INTEGER_LITERAL ':' INTEGER_LITERAL ']' { $$ = this.attach(() -> Expression.slice($1, $3, $5), @$);           }
    | expr '['                 ':' INTEGER_LITERAL ']' { $$ = this.attach(() -> Expression.slice($1, null, $4), @$);         }
    | expr '[' INTEGER_LITERAL ':'                 ']' { $$ = this.attach(() -> Expression.slice($1, $3, null), @$);         }
    | expr '['                 ':'                 ']' { $$ = this.attach(() -> Expression.slice($1, null, null), @$);       }
    | expr '[' INTEGER_LITERAL ']'                     { $$ = this.attach(() -> Expression.at($1, $3), @$);                  }
    | expr '<' '>' expr                                { $$ = this.attach(() -> Expression.concat($1, $4), @$);              }
    | '(' block_expr ')'                               { $$ = this.attach(() -> Expression.instruction($2), @$);             }
    | '(' expr ')'                                     { $$ = $2;                                                            }
;

arg_list: %empty           { $$ = new LinkedList<Object>();            }
        | arg_list ',' arg { $$ = $1; $1.add($3);                      }
        | arg              { $$ = new LinkedList<Object>(List.of($1)); }
;

%%

record Position(int line, int token) {
    @Override
    public String toString() {
        return String.format("%d:%d", this.line, this.token);
    }
}

final class RHDLLexer implements RHDLParser.Lexer {
    private final StreamTokenizer tokenizer;
    private Position yypos = new Position(1, 0);
    private Object yylval;

    public RHDLLexer(final InputStream stream) {
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

    public void yyerror(final RHDLParser.Location location, final String message) {
        final var e = new ScriptException(message.length() > 0 ? message.substring(0, 1).toUpperCase(Locale.ROOT) + message.substring(1) : message);
        e.setLocation(location);
        throw e;
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

