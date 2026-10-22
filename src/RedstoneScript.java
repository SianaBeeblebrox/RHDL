/* A Bison parser, made by GNU Bison 3.5.1.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java

   Copyright (C) 2007-2015, 2018-2020 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */




/* "%code imports" blocks.  */
/* "RedstoneScript.y":12  */

    import io.github.tobyrue.redstonescript.*;
    import org.jetbrains.annotations.Nullable;
    import java.io.*;
    import java.util.*;
    import java.util.regex.Pattern;
    import java.util.function.*;

/* "RedstoneScript.java":47  */

/**
 * A Bison parser, automatically generated from <tt>RedstoneScript.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class RedstoneScript
{
    /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "3.5.1";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";


  /**
   * True if verbose error messages are enabled.
   */
  private boolean yyErrorVerbose = true;

  /**
   * Whether verbose error messages are enabled.
   */
  public final boolean getErrorVerbose() { return yyErrorVerbose; }

  /**
   * Set the verbosity of error messages.
   * @param verbose True to request verbose error messages.
   */
  public final void setErrorVerbose(boolean verbose)
  { yyErrorVerbose = verbose; }



  /**
   * A class defining a pair of positions.  Positions, defined by the
   * <code>Position</code> class, denote a point in the input.
   * Locations represent a part of the input through the beginning
   * and ending positions.
   */
  public class Location {
    /**
     * The first, inclusive, position in the range.
     */
    public Position begin;

    /**
     * The first position beyond the range.
     */
    public Position end;

    /**
     * Create a <code>Location</code> denoting an empty range located at
     * a given point.
     * @param loc The position at which the range is anchored.
     */
    public Location (Position loc) {
      this.begin = this.end = loc;
    }

    /**
     * Create a <code>Location</code> from the endpoints of the range.
     * @param begin The first position included in the range.
     * @param end   The first position beyond the range.
     */
    public Location (Position begin, Position end) {
      this.begin = begin;
      this.end = end;
    }

    /**
     * Print a representation of the location.  For this to be correct,
     * <code>Position</code> should override the <code>equals</code>
     * method.
     */
    public String toString () {
      if (begin.equals (end))
        return begin.toString ();
      else
        return begin.toString () + "-" + end.toString ();
    }
  }




  private Location yylloc (YYStack rhs, int n)
  {
    if (0 < n)
      return new Location (rhs.locationAt (n-1).begin, rhs.locationAt (0).end);
    else
      return new Location (rhs.locationAt (0).end);
  }

  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>RedstoneScript</tt>.
   */
  public interface Lexer {
    /** Token returned by the scanner to signal the end of its input.  */
    public static final int EOF = 0;

/* Tokens.  */
    /** Token number,to be returned by the scanner.  */
    static final int IN = 258;
    /** Token number,to be returned by the scanner.  */
    static final int OUT = 259;
    /** Token number,to be returned by the scanner.  */
    static final int STATE = 260;
    /** Token number,to be returned by the scanner.  */
    static final int LET = 261;
    /** Token number,to be returned by the scanner.  */
    static final int ASSIGN = 262;
    /** Token number,to be returned by the scanner.  */
    static final int SWITCH = 263;
    /** Token number,to be returned by the scanner.  */
    static final int CASE = 264;
    /** Token number,to be returned by the scanner.  */
    static final int BEGIN = 265;
    /** Token number,to be returned by the scanner.  */
    static final int END = 266;
    /** Token number,to be returned by the scanner.  */
    static final int NAME = 267;
    /** Token number,to be returned by the scanner.  */
    static final int BITSTRING_LITERAL = 268;
    /** Token number,to be returned by the scanner.  */
    static final int INTEGER_LITERAL = 269;


    /**
     * Method to retrieve the beginning position of the last scanned token.
     * @return the position at which the last scanned token starts.
     */
    Position getStartPos ();

    /**
     * Method to retrieve the ending position of the last scanned token.
     * @return the first position beyond the last scanned token.
     */
    Position getEndPos ();

    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.
     */
    Object getLVal ();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * and beginning/ending positions of the token.
     * @return the token identifier corresponding to the next token.
     */
    int yylex () throws java.io.IOException;

    /**
     * Entry point for error reporting.  Emits an error
     * referring to the given location in a user-defined way.
     *
     * @param loc The location of the element to which the
     *                error message is related
     * @param msg The string for the error message.
     */
     void yyerror (Location loc, String msg);
  }

/**
   * The object doing lexical analysis for us.
   */
  private Lexer yylexer;

  
    /* User arguments.  */
    protected final Stack<Script> stack;



  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  public RedstoneScript (Lexer yylexer, Stack<Script> stack) 
  {
    
    this.yylexer = yylexer;
    this.stack = stack;
          
  }



  /**
   * Print an error message via the lexer.
   * Use a <code>null</code> location.
   * @param msg The error message.
   */
  public final void yyerror (String msg)
  {
    yylexer.yyerror ((Location)null, msg);
  }

  /**
   * Print an error message via the lexer.
   * @param loc The location associated with the message.
   * @param msg The error message.
   */
  public final void yyerror (Location loc, String msg)
  {
    yylexer.yyerror (loc, msg);
  }

  /**
   * Print an error message via the lexer.
   * @param pos The position associated with the message.
   * @param msg The error message.
   */
  public final void yyerror (Position pos, String msg)
  {
    yylexer.yyerror (new Location (pos), msg);
  }


  private final class YYStack {
    private int[] stateStack = new int[16];
    private Location[] locStack = new Location[16];
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;

    public final void push (int state, Object value                            , Location loc) {
      height++;
      if (size == height)
        {
          int[] newStateStack = new int[size * 2];
          System.arraycopy (stateStack, 0, newStateStack, 0, height);
          stateStack = newStateStack;
          
          Location[] newLocStack = new Location[size * 2];
          System.arraycopy (locStack, 0, newLocStack, 0, height);
          locStack = newLocStack;

          Object[] newValueStack = new Object[size * 2];
          System.arraycopy (valueStack, 0, newValueStack, 0, height);
          valueStack = newValueStack;

          size *= 2;
        }

      stateStack[height] = state;
      locStack[height] = loc;
      valueStack[height] = value;
    }

    public final void pop () {
      pop (1);
    }

    public final void pop (int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (0 < num) {
        java.util.Arrays.fill (valueStack, height - num + 1, height + 1, null);
        java.util.Arrays.fill (locStack, height - num + 1, height + 1, null);
      }
      height -= num;
    }

    public final int stateAt (int i) {
      return stateStack[height - i];
    }

    public final Location locationAt (int i) {
      return locStack[height - i];
    }

    public final Object valueAt (int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print (java.io.PrintStream out) {
      out.print ("Stack now");

      for (int i = 0; i <= height; i++)
        {
          out.print (' ');
          out.print (stateStack[i]);
        }
      out.println ();
    }
  }

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return success (<tt>true</tt>).
   */
  public static final int YYACCEPT = 0;

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return failure (<tt>false</tt>).
   */
  public static final int YYABORT = 1;



  /**
   * Returned by a Bison action in order to start error recovery without
   * printing an error message.
   */
  public static final int YYERROR = 2;

  /**
   * Internal return codes that are not supported for user semantic
   * actions.
   */
  private static final int YYERRLAB = 3;
  private static final int YYNEWSTATE = 4;
  private static final int YYDEFAULT = 5;
  private static final int YYREDUCE = 6;
  private static final int YYERRLAB1 = 7;
  private static final int YYRETURN = 8;


  private int yyerrstatus_ = 0;


  /**
   * Whether error recovery is being done.  In this state, the parser
   * reads token until it reaches a known state, and then restarts normal
   * operation.
   */
  public final boolean recovering ()
  {
    return yyerrstatus_ == 0;
  }

  /** Compute post-reduction state.
   * @param yystate   the current state
   * @param yysym     the nonterminal to push on the stack
   */
  private int yyLRGotoState (int yystate, int yysym)
  {
    int yyr = yypgoto_[yysym - yyntokens_] + yystate;
    if (0 <= yyr && yyr <= yylast_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - yyntokens_];
  }

  private int yyaction (int yyn, YYStack yystack, int yylen) 
  {
    /* If YYLEN is nonzero, implement the default value of the action:
       '$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    Object yyval = (0 < yylen) ? yystack.valueAt (yylen - 1) : yystack.valueAt (0);
    Location yyloc = yylloc (yystack, yylen);

    switch (yyn)
      {
          case 4:
  if (yyn == 4)
    /* "RedstoneScript.y":81  */
                                   { this.script().addInstruction(Script.createSensitivityFilter(this.script(), 0, 0)); };
  break;
    

  case 5:
  if (yyn == 5)
    /* "RedstoneScript.y":82  */
                                   { this.script().addInstruction(((Consumer<Script>)(yystack.valueAt (0))));                                                  };
  break;
    

  case 6:
  if (yyn == 6)
    /* "RedstoneScript.y":83  */
                                   {};
  break;
    

  case 7:
  if (yyn == 7)
    /* "RedstoneScript.y":86  */
                     { this.stack.push(this.script().derive()); };
  break;
    

  case 8:
  if (yyn == 8)
    /* "RedstoneScript.y":86  */
                                                                                   { final Script script = this.stack.pop(); yyval = (Consumer<Script>)(_script -> script.evaluate());    };
  break;
    

  case 9:
  if (yyn == 9)
    /* "RedstoneScript.y":87  */
                                                                                   { yyval = (Consumer<Script>)(script -> script.assign(((String)(yystack.valueAt (3))), ((Expression)(yystack.valueAt (1))).evaluate(new Expression.Context()))); };
  break;
    

  case 10:
  if (yyn == 10)
    /* "RedstoneScript.y":91  */
                                     { this.script().let(((String)(yystack.valueAt (3))), ((Expression)(yystack.valueAt (1))));                                };
  break;
    

  case 11:
  if (yyn == 11)
    /* "RedstoneScript.y":92  */
                                     { this.script().declare(((String)(yystack.valueAt (2))), Script.StateType.IN, ((Map<String, Object>)(yystack.valueAt (1))));       };
  break;
    

  case 12:
  if (yyn == 12)
    /* "RedstoneScript.y":93  */
                                     { this.script().declare(((String)(yystack.valueAt (2))), Script.StateType.OUT, ((Map<String, Object>)(yystack.valueAt (1))));      };
  break;
    

  case 13:
  if (yyn == 13)
    /* "RedstoneScript.y":94  */
                                     { this.script().declare(((String)(yystack.valueAt (2))), Script.StateType.INTERNAL, ((Map<String, Object>)(yystack.valueAt (1)))); };
  break;
    

  case 14:
  if (yyn == 14)
    /* "RedstoneScript.y":114  */
                               { yyval = Map.ofEntries(((ArrayList<Map.Entry<String, Object>>)(yystack.valueAt (1))).toArray(new Map.Entry[((ArrayList<Map.Entry<String, Object>>)(yystack.valueAt (1))).size()])); };
  break;
    

  case 15:
  if (yyn == 15)
    /* "RedstoneScript.y":115  */
                               { yyval = Map.of(); };
  break;
    

  case 16:
  if (yyn == 16)
    /* "RedstoneScript.y":116  */
                               { yyval = Map.of(); };
  break;
    

  case 17:
  if (yyn == 17)
    /* "RedstoneScript.y":118  */
                                                { yyval = ((ArrayList<Map.Entry<String, Object>>)(yystack.valueAt (2))); ((ArrayList<Map.Entry<String, Object>>)(yystack.valueAt (2))).add(((Map.Entry<String, Object>)(yystack.valueAt (0)))); };
  break;
    

  case 18:
  if (yyn == 18)
    /* "RedstoneScript.y":119  */
                                                { yyval = new ArrayList<Map.Entry<String, Object>>(List.of(((Map.Entry<String, Object>)(yystack.valueAt (0))))); };
  break;
    

  case 19:
  if (yyn == 19)
    /* "RedstoneScript.y":121  */
                                    { yyval = Map.entry(((String)(yystack.valueAt (2))), ((Object)(yystack.valueAt (0)))); };
  break;
    

  case 20:
  if (yyn == 20)
    /* "RedstoneScript.y":122  */
                                    { yyval = Map.entry(((String)(yystack.valueAt (0))), ""); };
  break;
    

  case 21:
  if (yyn == 21)
    /* "RedstoneScript.y":124  */
                                { yyval = ((String)(yystack.valueAt (0))); };
  break;
    

  case 22:
  if (yyn == 22)
    /* "RedstoneScript.y":125  */
                                { yyval = ((BitString)(yystack.valueAt (0))); };
  break;
    

  case 23:
  if (yyn == 23)
    /* "RedstoneScript.y":126  */
                                { yyval = ((Integer)(yystack.valueAt (0))); };
  break;
    

  case 24:
  if (yyn == 24)
    /* "RedstoneScript.y":135  */
                                                       { yyval = Expression.var(this.script(), ((String)(yystack.valueAt (0))));      };
  break;
    

  case 25:
  if (yyn == 25)
    /* "RedstoneScript.y":136  */
                                                       { yyval = Expression.literal(((BitString)(yystack.valueAt (0))));                 };
  break;
    

  case 26:
  if (yyn == 26)
    /* "RedstoneScript.y":137  */
                                                       { yyval = Expression.not(((Expression)(yystack.valueAt (0))));                     };
  break;
    

  case 27:
  if (yyn == 27)
    /* "RedstoneScript.y":138  */
                                                       { yyval = Expression.and(((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))));                 };
  break;
    

  case 28:
  if (yyn == 28)
    /* "RedstoneScript.y":139  */
                                                       { yyval = Expression.xor(((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))));                 };
  break;
    

  case 29:
  if (yyn == 29)
    /* "RedstoneScript.y":140  */
                                                       { yyval = Expression.or(((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))));                  };
  break;
    

  case 30:
  if (yyn == 30)
    /* "RedstoneScript.y":141  */
                                                       { yyval = Expression.ternary(((Expression)(yystack.valueAt (4))), ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))));         };
  break;
    

  case 31:
  if (yyn == 31)
    /* "RedstoneScript.y":142  */
                                                       { yyval = Expression.slice(((Expression)(yystack.valueAt (5))), ((Integer)(yystack.valueAt (3))), ((Integer)(yystack.valueAt (1))));           };
  break;
    

  case 32:
  if (yyn == 32)
    /* "RedstoneScript.y":143  */
                                                       { yyval = Expression.slice(((Expression)(yystack.valueAt (4))), null, ((Integer)(yystack.valueAt (1))));         };
  break;
    

  case 33:
  if (yyn == 33)
    /* "RedstoneScript.y":144  */
                                                       { yyval = Expression.slice(((Expression)(yystack.valueAt (4))), ((Integer)(yystack.valueAt (2))), null);         };
  break;
    

  case 34:
  if (yyn == 34)
    /* "RedstoneScript.y":145  */
                                                       { yyval = Expression.slice(((Expression)(yystack.valueAt (3))), null, null);       };
  break;
    

  case 35:
  if (yyn == 35)
    /* "RedstoneScript.y":146  */
                                                       { yyval = Expression.at(((Expression)(yystack.valueAt (3))), ((Integer)(yystack.valueAt (1))));                  };
  break;
    

  case 36:
  if (yyn == 36)
    /* "RedstoneScript.y":147  */
                                                       { yyval = Expression.concat(((Expression)(yystack.valueAt (3))), ((Expression)(yystack.valueAt (0))));              };
  break;
    

  case 37:
  if (yyn == 37)
    /* "RedstoneScript.y":148  */
                                                       { yyval = Expression.call(this.script(), ((String)(yystack.valueAt (3))), ((List<Object>)(yystack.valueAt (1)))); };
  break;
    

  case 38:
  if (yyn == 38)
    /* "RedstoneScript.y":149  */
                                                       { yyval = ((Expression)(yystack.valueAt (1)));                                     };
  break;
    

  case 39:
  if (yyn == 39)
    /* "RedstoneScript.y":152  */
                                                      { yyval = ((List<Object>)(yystack.valueAt (2))); ((List<Object>)(yystack.valueAt (2))).add(((Object)(yystack.valueAt (0))));                      };
  break;
    

  case 40:
  if (yyn == 40)
    /* "RedstoneScript.y":153  */
                                                      { yyval = new LinkedList<Object>(List.of(((Object)(yystack.valueAt (0))))); };
  break;
    

  case 41:
  if (yyn == 41)
    /* "RedstoneScript.y":155  */
                                { yyval = ((Integer)(yystack.valueAt (0))); };
  break;
    

  case 42:
  if (yyn == 42)
    /* "RedstoneScript.y":156  */
                                { yyval = ((BitString)(yystack.valueAt (0))); };
  break;
    


/* "RedstoneScript.java":685  */

        default: break;
      }

    yystack.pop (yylen);
    yylen = 0;

    /* Shift the result of the reduction.  */
    int yystate = yyLRGotoState (yystack.stateAt (0), yyr1_[yyn]);
    yystack.push (yystate, yyval, yyloc);
    return YYNEWSTATE;
  }


  /* Return YYSTR after stripping away unnecessary quotes and
     backslashes, so that it's suitable for yyerror.  The heuristic is
     that double-quoting is unnecessary unless the string contains an
     apostrophe, a comma, or backslash (other than backslash-backslash).
     YYSTR is taken from yytname.  */
  private final String yytnamerr_ (String yystr)
  {
    if (yystr.charAt (0) == '"')
      {
        StringBuffer yyr = new StringBuffer ();
        strip_quotes: for (int i = 1; i < yystr.length (); i++)
          switch (yystr.charAt (i))
            {
            case '\'':
            case ',':
              break strip_quotes;

            case '\\':
              if (yystr.charAt(++i) != '\\')
                break strip_quotes;
              /* Fall through.  */
            default:
              yyr.append (yystr.charAt (i));
              break;

            case '"':
              return yyr.toString ();
            }
      }
    else if (yystr.equals ("$end"))
      return "end of input";

    return yystr;
  }




  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
  public boolean parse () throws java.io.IOException

  {
    /* @$.  */
    Location yyloc;


    /* Lookahead and lookahead in internal form.  */
    int yychar = yyempty_;
    int yytoken = 0;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;
    YYStack yystack = new YYStack ();
    int label = YYNEWSTATE;

    /* Error handling.  */
    int yynerrs_ = 0;
    /* The location where the error started.  */
    Location yyerrloc = null;

    /* Location. */
    Location yylloc = new Location (null, null);

    /* Semantic value of the lookahead.  */
    Object yylval = null;

    yyerrstatus_ = 0;

    /* Initialize the stack.  */
    yystack.push (yystate, yylval , yylloc);



    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
           pushed when we come here.  */
      case YYNEWSTATE:

        /* Accept?  */
        if (yystate == yyfinal_)
          return true;

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yyPactValueIsDefault (yyn))
          {
            label = YYDEFAULT;
            break;
          }

        /* Read a lookahead token.  */
        if (yychar == yyempty_)
          {

            yychar = yylexer.yylex ();
            yylval = yylexer.getLVal ();
            yylloc = new Location (yylexer.getStartPos (),
                            yylexer.getEndPos ());

          }

        /* Convert token to internal form.  */
        yytoken = yytranslate_ (yychar);

        /* If the proper action on seeing token YYTOKEN is to reduce or to
           detect an error, take that action.  */
        yyn += yytoken;
        if (yyn < 0 || yylast_ < yyn || yycheck_[yyn] != yytoken)
          label = YYDEFAULT;

        /* <= 0 means reduce or error.  */
        else if ((yyn = yytable_[yyn]) <= 0)
          {
            if (yyTableValueIsError (yyn))
              label = YYERRLAB;
            else
              {
                yyn = -yyn;
                label = YYREDUCE;
              }
          }

        else
          {
            /* Shift the lookahead token.  */
            /* Discard the token being shifted.  */
            yychar = yyempty_;

            /* Count tokens shifted since error; after three, turn off error
               status.  */
            if (yyerrstatus_ > 0)
              --yyerrstatus_;

            yystate = yyn;
            yystack.push (yystate, yylval, yylloc);
            label = YYNEWSTATE;
          }
        break;

      /*-----------------------------------------------------------.
      | yydefault -- do the default action for the current state.  |
      `-----------------------------------------------------------*/
      case YYDEFAULT:
        yyn = yydefact_[yystate];
        if (yyn == 0)
          label = YYERRLAB;
        else
          label = YYREDUCE;
        break;

      /*-----------------------------.
      | yyreduce -- Do a reduction.  |
      `-----------------------------*/
      case YYREDUCE:
        yylen = yyr2_[yyn];
        label = yyaction (yyn, yystack, yylen);
        yystate = yystack.stateAt (0);
        break;

      /*------------------------------------.
      | yyerrlab -- here on detecting error |
      `------------------------------------*/
      case YYERRLAB:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
            ++yynerrs_;
            if (yychar == yyempty_)
              yytoken = yyempty_;
            yyerror (yylloc, yysyntax_error (yystate, yytoken));
          }

        yyerrloc = yylloc;
        if (yyerrstatus_ == 3)
          {
            /* If just tried and failed to reuse lookahead token after an
               error, discard it.  */

            if (yychar <= Lexer.EOF)
              {
                /* Return failure if at end of input.  */
                if (yychar == Lexer.EOF)
                  return false;
              }
            else
              yychar = yyempty_;
          }

        /* Else will try to reuse lookahead token after shifting the error
           token.  */
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------.
      | errorlab -- error raised explicitly by YYERROR.  |
      `-------------------------------------------------*/
      case YYERROR:
        yyerrloc = yystack.locationAt (yylen - 1);
        /* Do not reclaim the symbols of the rule which action triggered
           this YYERROR.  */
        yystack.pop (yylen);
        yylen = 0;
        yystate = yystack.stateAt (0);
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------------------.
      | yyerrlab1 -- common code for both syntax error and YYERROR.  |
      `-------------------------------------------------------------*/
      case YYERRLAB1:
        yyerrstatus_ = 3;       /* Each real token shifted decrements this.  */

        for (;;)
          {
            yyn = yypact_[yystate];
            if (!yyPactValueIsDefault (yyn))
              {
                yyn += yy_error_token_;
                if (0 <= yyn && yyn <= yylast_ && yycheck_[yyn] == yy_error_token_)
                  {
                    yyn = yytable_[yyn];
                    if (0 < yyn)
                      break;
                  }
              }

            /* Pop the current state because it cannot handle the
             * error token.  */
            if (yystack.height == 0)
              return false;

            yyerrloc = yystack.locationAt (0);
            yystack.pop ();
            yystate = yystack.stateAt (0);
          }

        if (label == YYABORT)
            /* Leave the switch.  */
            break;


        /* Muck with the stack to setup for yylloc.  */
        yystack.push (0, null, yylloc);
        yystack.push (0, null, yyerrloc);
        yyloc = yylloc (yystack, 2);
        yystack.pop (2);

        /* Shift the error token.  */

        yystate = yyn;
        yystack.push (yyn, yylval, yyloc);
        label = YYNEWSTATE;
        break;

        /* Accept.  */
      case YYACCEPT:
        return true;

        /* Abort.  */
      case YYABORT:
        return false;
      }
}




  // Generate an error message.
  private String yysyntax_error (int yystate, int tok)
  {
    if (yyErrorVerbose)
      {
        /* There are many possibilities here to consider:
           - If this state is a consistent state with a default action,
             then the only way this function was invoked is if the
             default action is an error action.  In that case, don't
             check for expected tokens because there are none.
           - The only way there can be no lookahead present (in tok) is
             if this state is a consistent state with a default action.
             Thus, detecting the absence of a lookahead is sufficient to
             determine that there is no unexpected or expected token to
             report.  In that case, just report a simple "syntax error".
           - Don't assume there isn't a lookahead just because this
             state is a consistent state with a default action.  There
             might have been a previous inconsistent state, consistent
             state with a non-default action, or user semantic action
             that manipulated yychar.  (However, yychar is currently out
             of scope during semantic actions.)
           - Of course, the expected token list depends on states to
             have correct lookahead information, and it depends on the
             parser not to perform extra reductions after fetching a
             lookahead from the scanner and before detecting a syntax
             error.  Thus, state merging (from LALR or IELR) and default
             reductions corrupt the expected token list.  However, the
             list is correct for canonical LR with one exception: it
             will still contain any token that will not be accepted due
             to an error action in a later state.
        */
        if (tok != yyempty_)
          {
            /* FIXME: This method of building the message is not compatible
               with internationalization.  */
            StringBuffer res =
              new StringBuffer ("syntax error, unexpected ");
            res.append (yytnamerr_ (yytname_[tok]));
            int yyn = yypact_[yystate];
            if (!yyPactValueIsDefault (yyn))
              {
                /* Start YYX at -YYN if negative to avoid negative
                   indexes in YYCHECK.  In other words, skip the first
                   -YYN actions for this state because they are default
                   actions.  */
                int yyxbegin = yyn < 0 ? -yyn : 0;
                /* Stay within bounds of both yycheck and yytname.  */
                int yychecklim = yylast_ - yyn + 1;
                int yyxend = yychecklim < yyntokens_ ? yychecklim : yyntokens_;
                int count = 0;
                for (int x = yyxbegin; x < yyxend; ++x)
                  if (yycheck_[x + yyn] == x && x != yy_error_token_
                      && !yyTableValueIsError (yytable_[x + yyn]))
                    ++count;
                if (count < 5)
                  {
                    count = 0;
                    for (int x = yyxbegin; x < yyxend; ++x)
                      if (yycheck_[x + yyn] == x && x != yy_error_token_
                          && !yyTableValueIsError (yytable_[x + yyn]))
                        {
                          res.append (count++ == 0 ? ", expecting " : " or ");
                          res.append (yytnamerr_ (yytname_[x]));
                        }
                  }
              }
            return res.toString ();
          }
      }

    return "syntax error";
  }

  /**
   * Whether the given <code>yypact_</code> value indicates a defaulted state.
   * @param yyvalue   the value to check
   */
  private static boolean yyPactValueIsDefault (int yyvalue)
  {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code>
   * value indicates a syntax error.
   * @param yyvalue the value to check
   */
  private static boolean yyTableValueIsError (int yyvalue)
  {
    return yyvalue == yytable_ninf_;
  }

  private static final byte yypact_ninf_ = -27;
  private static final byte yytable_ninf_ = -1;

  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final byte yypact_[] = yypact_init();
  private static final byte[] yypact_init()
  {
    return new byte[]
    {
      27,    -6,     7,    63,    75,    95,   -27,   -16,    11,   -27,
     -27,   -27,    83,    83,    83,    85,    86,    27,    89,   -27,
     -27,    -5,    81,    90,    91,    38,    38,    -2,    60,    87,
     -27,    50,   -27,   -27,   -27,   -27,    96,   -27,    38,    38,
      20,    39,   -27,   -27,    88,   -27,    99,    33,    68,    51,
      34,    93,    38,    38,    38,    38,   -27,   -27,   -27,   -27,
     -27,   -27,   -27,   -27,   -27,    69,   -27,   -27,    29,    49,
      38,    82,     2,    94,    61,   -27,    33,   -27,    92,   102,
     -27,    72,    38,   -27,   105,   -27,   -27,    94,   -27
    };
  }

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
  private static final byte yydefact_[] = yydefact_init();
  private static final byte[] yydefact_init()
  {
    return new byte[]
    {
       0,     0,     0,     0,     0,     0,     7,     0,     0,     2,
       5,     6,    16,    16,    16,     0,     0,     0,     0,     1,
       3,     0,     0,     0,     0,     0,     0,     0,     0,    20,
      15,     0,    18,    11,    12,    13,    24,    25,     0,     0,
       0,     0,     8,     4,     0,    14,     0,     0,    26,     0,
       0,     0,     0,     0,     0,     0,    10,     9,    21,    22,
      23,    19,    17,    42,    41,     0,    40,    38,     0,     0,
       0,    27,    28,    29,     0,    37,     0,    35,     0,     0,
      34,    36,     0,    39,     0,    33,    32,    30,    31
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final byte yypgoto_[] = yypgoto_init();
  private static final byte[] yypgoto_init()
  {
    return new byte[]
    {
     -27,   106,    12,    97,   -27,   -27,    84,   -27,    78,   -27,
     -26,   -27,    52
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final byte yydefgoto_[] = yydefgoto_init();
  private static final byte[] yydefgoto_init()
  {
    return new byte[]
    {
      -1,     8,     9,    10,    17,    11,    22,    31,    32,    61,
      40,    65,    66
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final byte yytable_[] = yytable_init();
  private static final byte[] yytable_init()
  {
    return new byte[]
    {
      41,     1,     2,     3,     4,     5,    12,    29,     6,    42,
      18,    19,    48,    49,     1,     2,     3,     4,     5,    13,
      20,     6,    30,     7,    54,    55,    71,    72,    73,    74,
       1,     2,     3,     4,     5,    50,     7,     6,    51,    20,
      52,    53,    54,    55,    81,    77,    63,    64,    68,    56,
      36,    37,     7,    78,    50,    38,    87,    51,    69,    52,
      53,    54,    55,    79,    39,    80,    50,     5,    57,    51,
       6,    52,    53,    54,    55,    14,    50,    45,    67,    51,
      46,    52,    53,    54,    55,    82,    51,    15,    52,    53,
      54,    55,    52,    53,    54,    55,    75,    23,    24,    76,
      58,    59,    60,    53,    54,    55,    84,    16,    85,    21,
      33,    29,    70,    25,    26,    44,    28,    55,    86,    34,
      35,    88,    47,    27,    62,    43,     0,     0,    83
    };
  }

private static final byte yycheck_[] = yycheck_init();
  private static final byte[] yycheck_init()
  {
    return new byte[]
    {
      26,     3,     4,     5,     6,     7,    12,    12,    10,    11,
      26,     0,    38,    39,     3,     4,     5,     6,     7,    12,
       8,    10,    27,    25,    22,    23,    52,    53,    54,    55,
       3,     4,     5,     6,     7,    15,    25,    10,    18,    27,
      20,    21,    22,    23,    70,    16,    13,    14,    14,    29,
      12,    13,    25,    24,    15,    17,    82,    18,    24,    20,
      21,    22,    23,    14,    26,    16,    15,     7,    29,    18,
      10,    20,    21,    22,    23,    12,    15,    27,    27,    18,
      30,    20,    21,    22,    23,    24,    18,    12,    20,    21,
      22,    23,    20,    21,    22,    23,    27,    13,    14,    30,
      12,    13,    14,    21,    22,    23,    14,    12,    16,    26,
      29,    12,    19,    28,    28,    28,    27,    23,    16,    29,
      29,    16,    26,    17,    46,    28,    -1,    -1,    76
    };
  }

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
  private static final byte yystos_[] = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,     3,     4,     5,     6,     7,    10,    25,    32,    33,
      34,    36,    12,    12,    12,    12,    12,    35,    26,     0,
      33,    26,    37,    37,    37,    28,    28,    32,    27,    12,
      27,    38,    39,    29,    29,    29,    12,    13,    17,    26,
      41,    41,    11,    34,    28,    27,    30,    26,    41,    41,
      15,    18,    20,    21,    22,    23,    29,    29,    12,    13,
      14,    40,    39,    13,    14,    42,    43,    27,    14,    24,
      19,    41,    41,    41,    41,    27,    30,    16,    24,    14,
      16,    41,    24,    43,    14,    16,    16,    41,    16
    };
  }

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  private static final byte yyr1_[] = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    31,    32,    32,    33,    33,    33,    35,    34,    34,
      36,    36,    36,    36,    37,    37,    37,    38,    38,    39,
      39,    40,    40,    40,    41,    41,    41,    41,    41,    41,
      41,    41,    41,    41,    41,    41,    41,    41,    41,    42,
      42,    43,    43
    };
  }

/* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
  private static final byte yyr2_[] = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     2,     4,     1,     1,     0,     4,     5,
       5,     4,     4,     4,     3,     2,     0,     3,     1,     3,
       1,     1,     1,     1,     1,     1,     2,     3,     3,     3,
       5,     6,     5,     5,     4,     4,     4,     4,     3,     3,
       1,     1,     1
    };
  }


  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  private static final String yytname_[] = yytname_init();
  private static final String[] yytname_init()
  {
    return new String[]
    {
  "$end", "error", "$undefined", "\"in\"", "\"out\"", "\"state\"",
  "\"let\"", "\"assign\"", "\"switch\"", "\"case\"", "\"begin\"",
  "\"end\"", "NAME", "BITSTRING_LITERAL", "INTEGER_LITERAL", "'['", "']'",
  "'~'", "'<'", "'>'", "'&'", "'^'", "'|'", "'?'", "':'", "'@'", "'('",
  "')'", "'='", "';'", "','", "$accept", "statements", "statement",
  "instruction", "$@1", "declaration", "layout", "layout_entries",
  "layout_entry", "layout_value", "expr", "function_arg_list",
  "function_arg", null
    };
  }



  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final byte yytranslate_ (int t)
  {
    int user_token_number_max_ = 269;
    byte undef_token_ = 2;

    if (t <= 0)
      return Lexer.EOF;
    else if (t <= user_token_number_max_)
      return yytranslate_table_[t];
    else
      return undef_token_;
  }
  private static final byte yytranslate_table_[] = yytranslate_table_init();
  private static final byte[] yytranslate_table_init()
  {
    return new byte[]
    {
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    20,     2,
      26,    27,     2,     2,    30,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,    24,    29,
      18,    28,    19,    23,    25,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,    15,     2,    16,    21,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,    22,     2,    17,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14
    };
  }


  private static final byte yy_error_token_ = 1;

  private static final int yylast_ = 128;
  private static final int yynnts_ = 13;
  private static final int yyempty_ = -2;
  private static final int yyfinal_ = 19;
  private static final int yyntokens_ = 31;

/* User implementation code.  */
/* Unqualified %code blocks.  */
/* "RedstoneScript.y":21  */

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

/* "RedstoneScript.java":1325  */

}

/* "RedstoneScript.y":159  */


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

