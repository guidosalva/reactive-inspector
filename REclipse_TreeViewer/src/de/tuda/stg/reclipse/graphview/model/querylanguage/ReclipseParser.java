// Generated from Reclipse.g4 by ANTLR 4.4
package de.tuda.stg.reclipse.graphview.model.querylanguage;
import java.util.List;

import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ReclipseParser extends Parser {
  static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache =
          new PredictionContextCache();
  public static final int
  T__7=1, T__6=2, T__5=3, T__4=4, T__3=5, T__2=6, T__1=7, T__0=8, NODE_NAME=9,
  VALUE=10, Identifier=11, StringLiteral=12, WS=13;
  public static final String[] tokenNames = {
    "<INVALID>", "'nodeEvaluated('", "'nodeValueSet('", "'evaluationException('",
    "'evaluationYielded('", "'nodeCreated('", "')'", "'dependencyCreated('",
    "','", "NODE_NAME", "VALUE", "Identifier", "StringLiteral", "WS"
  };
  public static final int
  RULE_query = 0, RULE_nodeCreatedQuery = 1, RULE_nodeEvaluatedQuery = 2,
  RULE_nodeValueSet = 3, RULE_dependencyCreated = 4, RULE_evaluationYielded = 5,
  RULE_evaluationException = 6;
  public static final String[] ruleNames = {
    "query", "nodeCreatedQuery", "nodeEvaluatedQuery", "nodeValueSet", "dependencyCreated",
    "evaluationYielded", "evaluationException"
  };

  @Override
  public String getGrammarFileName() { return "Reclipse.g4"; }

  @Override
  public String[] getTokenNames() { return tokenNames; }

  @Override
  public String[] getRuleNames() { return ruleNames; }

  @Override
  public String getSerializedATN() { return _serializedATN; }

  @Override
  public ATN getATN() { return _ATN; }

  public ReclipseParser(final TokenStream input) {
    super(input);
    _interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
  }
  public static class QueryContext extends ParserRuleContext {
    public NodeEvaluatedQueryContext nodeEvaluatedQuery() {
      return getRuleContext(NodeEvaluatedQueryContext.class,0);
    }
    public EvaluationYieldedContext evaluationYielded() {
      return getRuleContext(EvaluationYieldedContext.class,0);
    }
    public NodeValueSetContext nodeValueSet() {
      return getRuleContext(NodeValueSetContext.class,0);
    }
    public NodeCreatedQueryContext nodeCreatedQuery() {
      return getRuleContext(NodeCreatedQueryContext.class,0);
    }
    public DependencyCreatedContext dependencyCreated() {
      return getRuleContext(DependencyCreatedContext.class,0);
    }
    public EvaluationExceptionContext evaluationException() {
      return getRuleContext(EvaluationExceptionContext.class,0);
    }
    public QueryContext(final ParserRuleContext parent, final int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_query; }
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof ReclipseVisitor ) {
        return ((ReclipseVisitor<? extends T>)visitor).visitQuery(this);
      }
      else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final QueryContext query() throws RecognitionException {
    final QueryContext _localctx = new QueryContext(_ctx, getState());
    enterRule(_localctx, 0, RULE_query);
    try {
      setState(20);
      switch (_input.LA(1)) {
        case T__3:
          enterOuterAlt(_localctx, 1);
          {
            setState(14); nodeCreatedQuery();
          }
          break;
        case T__7:
          enterOuterAlt(_localctx, 2);
          {
            setState(15); nodeEvaluatedQuery();
          }
          break;
        case T__6:
          enterOuterAlt(_localctx, 3);
          {
            setState(16); nodeValueSet();
          }
          break;
        case T__1:
          enterOuterAlt(_localctx, 4);
          {
            setState(17); dependencyCreated();
          }
          break;
        case T__4:
          enterOuterAlt(_localctx, 5);
          {
            setState(18); evaluationYielded();
          }
          break;
        case T__5:
          enterOuterAlt(_localctx, 6);
          {
            setState(19); evaluationException();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    }
    catch (final RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NodeCreatedQueryContext extends ParserRuleContext {
    public TerminalNode NODE_NAME() { return getToken(ReclipseParser.NODE_NAME, 0); }
    public NodeCreatedQueryContext(final ParserRuleContext parent, final int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_nodeCreatedQuery; }
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof ReclipseVisitor ) {
        return ((ReclipseVisitor<? extends T>)visitor).visitNodeCreatedQuery(this);
      }
      else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final NodeCreatedQueryContext nodeCreatedQuery() throws RecognitionException {
    final NodeCreatedQueryContext _localctx = new NodeCreatedQueryContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_nodeCreatedQuery);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(22); match(T__3);
        setState(23); match(NODE_NAME);
        setState(24); match(T__2);
      }
    }
    catch (final RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NodeEvaluatedQueryContext extends ParserRuleContext {
    public TerminalNode NODE_NAME() { return getToken(ReclipseParser.NODE_NAME, 0); }
    public NodeEvaluatedQueryContext(final ParserRuleContext parent, final int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_nodeEvaluatedQuery; }
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof ReclipseVisitor ) {
        return ((ReclipseVisitor<? extends T>)visitor).visitNodeEvaluatedQuery(this);
      }
      else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final NodeEvaluatedQueryContext nodeEvaluatedQuery() throws RecognitionException {
    final NodeEvaluatedQueryContext _localctx = new NodeEvaluatedQueryContext(_ctx, getState());
    enterRule(_localctx, 4, RULE_nodeEvaluatedQuery);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(26); match(T__7);
        setState(27); match(NODE_NAME);
        setState(28); match(T__2);
      }
    }
    catch (final RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NodeValueSetContext extends ParserRuleContext {
    public TerminalNode NODE_NAME() { return getToken(ReclipseParser.NODE_NAME, 0); }
    public NodeValueSetContext(final ParserRuleContext parent, final int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_nodeValueSet; }
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof ReclipseVisitor ) {
        return ((ReclipseVisitor<? extends T>)visitor).visitNodeValueSet(this);
      }
      else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final NodeValueSetContext nodeValueSet() throws RecognitionException {
    final NodeValueSetContext _localctx = new NodeValueSetContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_nodeValueSet);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(30); match(T__6);
        setState(31); match(NODE_NAME);
        setState(32); match(T__2);
      }
    }
    catch (final RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DependencyCreatedContext extends ParserRuleContext {
    public TerminalNode NODE_NAME(final int i) {
      return getToken(ReclipseParser.NODE_NAME, i);
    }
    public List<TerminalNode> NODE_NAME() { return getTokens(ReclipseParser.NODE_NAME); }
    public DependencyCreatedContext(final ParserRuleContext parent, final int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_dependencyCreated; }
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof ReclipseVisitor ) {
        return ((ReclipseVisitor<? extends T>)visitor).visitDependencyCreated(this);
      }
      else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final DependencyCreatedContext dependencyCreated() throws RecognitionException {
    final DependencyCreatedContext _localctx = new DependencyCreatedContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_dependencyCreated);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(34); match(T__1);
        setState(35); match(NODE_NAME);
        setState(36); match(T__0);
        setState(37); match(NODE_NAME);
        setState(38); match(T__2);
      }
    }
    catch (final RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class EvaluationYieldedContext extends ParserRuleContext {
    public TerminalNode VALUE() { return getToken(ReclipseParser.VALUE, 0); }
    public TerminalNode NODE_NAME() { return getToken(ReclipseParser.NODE_NAME, 0); }
    public EvaluationYieldedContext(final ParserRuleContext parent, final int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_evaluationYielded; }
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof ReclipseVisitor ) {
        return ((ReclipseVisitor<? extends T>)visitor).visitEvaluationYielded(this);
      }
      else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final EvaluationYieldedContext evaluationYielded() throws RecognitionException {
    final EvaluationYieldedContext _localctx = new EvaluationYieldedContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_evaluationYielded);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(40); match(T__4);
        setState(41); match(NODE_NAME);
        setState(42); match(T__0);
        setState(43); match(VALUE);
        setState(44); match(T__2);
      }
    }
    catch (final RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static class EvaluationExceptionContext extends ParserRuleContext {
    public TerminalNode NODE_NAME() { return getToken(ReclipseParser.NODE_NAME, 0); }
    public EvaluationExceptionContext(final ParserRuleContext parent, final int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_evaluationException; }
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof ReclipseVisitor ) {
        return ((ReclipseVisitor<? extends T>)visitor).visitEvaluationException(this);
      }
      else {
        return visitor.visitChildren(this);
      }
    }
  }

  public final EvaluationExceptionContext evaluationException() throws RecognitionException {
    final EvaluationExceptionContext _localctx = new EvaluationExceptionContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_evaluationException);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(46); match(T__5);
        setState(48);
        _la = _input.LA(1);
        if (_la==NODE_NAME) {
          {
            setState(47); match(NODE_NAME);
          }
        }

        setState(50); match(T__2);
      }
    }
    catch (final RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    }
    finally {
      exitRule();
    }
    return _localctx;
  }

  public static final String _serializedATN =
          "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\17\67\4\2\t\2\4\3"+
                  "\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\2\3\2\3\2\3\2\5"+
                  "\2\27\n\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6"+
                  "\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\5\b\63\n\b\3\b\3\b\3\b\2"+
                  "\2\t\2\4\6\b\n\f\16\2\2\65\2\26\3\2\2\2\4\30\3\2\2\2\6\34\3\2\2\2\b \3"+
                  "\2\2\2\n$\3\2\2\2\f*\3\2\2\2\16\60\3\2\2\2\20\27\5\4\3\2\21\27\5\6\4\2"+
                  "\22\27\5\b\5\2\23\27\5\n\6\2\24\27\5\f\7\2\25\27\5\16\b\2\26\20\3\2\2"+
                  "\2\26\21\3\2\2\2\26\22\3\2\2\2\26\23\3\2\2\2\26\24\3\2\2\2\26\25\3\2\2"+
                  "\2\27\3\3\2\2\2\30\31\7\7\2\2\31\32\7\13\2\2\32\33\7\b\2\2\33\5\3\2\2"+
                  "\2\34\35\7\3\2\2\35\36\7\13\2\2\36\37\7\b\2\2\37\7\3\2\2\2 !\7\4\2\2!"+
                  "\"\7\13\2\2\"#\7\b\2\2#\t\3\2\2\2$%\7\t\2\2%&\7\13\2\2&\'\7\n\2\2\'(\7"+
                  "\13\2\2()\7\b\2\2)\13\3\2\2\2*+\7\6\2\2+,\7\13\2\2,-\7\n\2\2-.\7\f\2\2"+
                  "./\7\b\2\2/\r\3\2\2\2\60\62\7\5\2\2\61\63\7\13\2\2\62\61\3\2\2\2\62\63"+
                  "\3\2\2\2\63\64\3\2\2\2\64\65\7\b\2\2\65\17\3\2\2\2\4\26\62";
  public static final ATN _ATN =
          new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}