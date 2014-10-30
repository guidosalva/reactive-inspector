// Generated from Reclipse.g4 by ANTLR 4.2.2
package de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ReclipseLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__7=1, T__6=2, T__5=3, T__4=4, T__3=5, T__2=6, T__1=7, T__0=8, NODE_NAME=9, 
		VALUE=10, Identifier=11, StringLiteral=12, WS=13;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'evaluationYielded('", "')'", "','", "'nodeValueSet('", "'dependencyCreated('", 
		"'nodeCreated('", "'nodeEvaluated('", "'evaluationException('", "NODE_NAME", 
		"VALUE", "Identifier", "StringLiteral", "WS"
	};
	public static final String[] ruleNames = {
		"T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", "NODE_NAME", 
		"VALUE", "Identifier", "JavaLetter", "JavaLetterOrDigit", "StringLiteral", 
		"StringCharacters", "StringCharacter", "EscapeSequence", "OctalEscape", 
		"UnicodeEscape", "OctalDigit", "ZeroToThree", "HexDigit", "WS"
	};


	public ReclipseLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Reclipse.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 11: return JavaLetter_sempred((RuleContext)_localctx, predIndex);

		case 12: return JavaLetterOrDigit_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean JavaLetterOrDigit_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2: return Character.isJavaIdentifierPart(_input.LA(-1));

		case 3: return Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}
	private boolean JavaLetter_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return Character.isJavaIdentifierStart(_input.LA(-1));

		case 1: return Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\17\u00eb\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n"+
		"\3\n\3\13\3\13\3\f\3\f\7\f\u00a1\n\f\f\f\16\f\u00a4\13\f\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\5\r\u00ac\n\r\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00b4\n\16"+
		"\3\17\3\17\5\17\u00b8\n\17\3\17\3\17\3\20\6\20\u00bd\n\20\r\20\16\20\u00be"+
		"\3\21\3\21\5\21\u00c3\n\21\3\22\3\22\3\22\3\22\5\22\u00c9\n\22\3\23\3"+
		"\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00d6\n\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\6\30"+
		"\u00e6\n\30\r\30\16\30\u00e7\3\30\3\30\2\2\31\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\2\33\2\35\16\37\2!\2#\2%\2\'\2)\2+\2-\2"+
		"/\17\3\2\r\6\2&&C\\aac|\4\2\2\u0101\ud802\udc01\3\2\ud802\udc01\3\2\udc02"+
		"\ue001\7\2&&\62;C\\aac|\4\2$$^^\n\2$$))^^ddhhppttvv\3\2\629\3\2\62\65"+
		"\5\2\62;CHch\5\2\13\f\16\17\"\"\u00ed\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\35\3\2\2\2\2/\3\2\2\2\3\61\3\2"+
		"\2\2\5D\3\2\2\2\7F\3\2\2\2\tH\3\2\2\2\13V\3\2\2\2\ri\3\2\2\2\17v\3\2\2"+
		"\2\21\u0085\3\2\2\2\23\u009a\3\2\2\2\25\u009c\3\2\2\2\27\u009e\3\2\2\2"+
		"\31\u00ab\3\2\2\2\33\u00b3\3\2\2\2\35\u00b5\3\2\2\2\37\u00bc\3\2\2\2!"+
		"\u00c2\3\2\2\2#\u00c8\3\2\2\2%\u00d5\3\2\2\2\'\u00d7\3\2\2\2)\u00de\3"+
		"\2\2\2+\u00e0\3\2\2\2-\u00e2\3\2\2\2/\u00e5\3\2\2\2\61\62\7g\2\2\62\63"+
		"\7x\2\2\63\64\7c\2\2\64\65\7n\2\2\65\66\7w\2\2\66\67\7c\2\2\678\7v\2\2"+
		"89\7k\2\29:\7q\2\2:;\7p\2\2;<\7[\2\2<=\7k\2\2=>\7g\2\2>?\7n\2\2?@\7f\2"+
		"\2@A\7g\2\2AB\7f\2\2BC\7*\2\2C\4\3\2\2\2DE\7+\2\2E\6\3\2\2\2FG\7.\2\2"+
		"G\b\3\2\2\2HI\7p\2\2IJ\7q\2\2JK\7f\2\2KL\7g\2\2LM\7X\2\2MN\7c\2\2NO\7"+
		"n\2\2OP\7w\2\2PQ\7g\2\2QR\7U\2\2RS\7g\2\2ST\7v\2\2TU\7*\2\2U\n\3\2\2\2"+
		"VW\7f\2\2WX\7g\2\2XY\7r\2\2YZ\7g\2\2Z[\7p\2\2[\\\7f\2\2\\]\7g\2\2]^\7"+
		"p\2\2^_\7e\2\2_`\7{\2\2`a\7E\2\2ab\7t\2\2bc\7g\2\2cd\7c\2\2de\7v\2\2e"+
		"f\7g\2\2fg\7f\2\2gh\7*\2\2h\f\3\2\2\2ij\7p\2\2jk\7q\2\2kl\7f\2\2lm\7g"+
		"\2\2mn\7E\2\2no\7t\2\2op\7g\2\2pq\7c\2\2qr\7v\2\2rs\7g\2\2st\7f\2\2tu"+
		"\7*\2\2u\16\3\2\2\2vw\7p\2\2wx\7q\2\2xy\7f\2\2yz\7g\2\2z{\7G\2\2{|\7x"+
		"\2\2|}\7c\2\2}~\7n\2\2~\177\7w\2\2\177\u0080\7c\2\2\u0080\u0081\7v\2\2"+
		"\u0081\u0082\7g\2\2\u0082\u0083\7f\2\2\u0083\u0084\7*\2\2\u0084\20\3\2"+
		"\2\2\u0085\u0086\7g\2\2\u0086\u0087\7x\2\2\u0087\u0088\7c\2\2\u0088\u0089"+
		"\7n\2\2\u0089\u008a\7w\2\2\u008a\u008b\7c\2\2\u008b\u008c\7v\2\2\u008c"+
		"\u008d\7k\2\2\u008d\u008e\7q\2\2\u008e\u008f\7p\2\2\u008f\u0090\7G\2\2"+
		"\u0090\u0091\7z\2\2\u0091\u0092\7e\2\2\u0092\u0093\7g\2\2\u0093\u0094"+
		"\7r\2\2\u0094\u0095\7v\2\2\u0095\u0096\7k\2\2\u0096\u0097\7q\2\2\u0097"+
		"\u0098\7p\2\2\u0098\u0099\7*\2\2\u0099\22\3\2\2\2\u009a\u009b\5\27\f\2"+
		"\u009b\24\3\2\2\2\u009c\u009d\5\35\17\2\u009d\26\3\2\2\2\u009e\u00a2\5"+
		"\31\r\2\u009f\u00a1\5\33\16\2\u00a0\u009f\3\2\2\2\u00a1\u00a4\3\2\2\2"+
		"\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\30\3\2\2\2\u00a4\u00a2"+
		"\3\2\2\2\u00a5\u00ac\t\2\2\2\u00a6\u00a7\n\3\2\2\u00a7\u00ac\6\r\2\2\u00a8"+
		"\u00a9\t\4\2\2\u00a9\u00aa\t\5\2\2\u00aa\u00ac\6\r\3\2\u00ab\u00a5\3\2"+
		"\2\2\u00ab\u00a6\3\2\2\2\u00ab\u00a8\3\2\2\2\u00ac\32\3\2\2\2\u00ad\u00b4"+
		"\t\6\2\2\u00ae\u00af\n\3\2\2\u00af\u00b4\6\16\4\2\u00b0\u00b1\t\4\2\2"+
		"\u00b1\u00b2\t\5\2\2\u00b2\u00b4\6\16\5\2\u00b3\u00ad\3\2\2\2\u00b3\u00ae"+
		"\3\2\2\2\u00b3\u00b0\3\2\2\2\u00b4\34\3\2\2\2\u00b5\u00b7\7$\2\2\u00b6"+
		"\u00b8\5\37\20\2\u00b7\u00b6\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b9\3"+
		"\2\2\2\u00b9\u00ba\7$\2\2\u00ba\36\3\2\2\2\u00bb\u00bd\5!\21\2\u00bc\u00bb"+
		"\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf"+
		" \3\2\2\2\u00c0\u00c3\n\7\2\2\u00c1\u00c3\5#\22\2\u00c2\u00c0\3\2\2\2"+
		"\u00c2\u00c1\3\2\2\2\u00c3\"\3\2\2\2\u00c4\u00c5\7^\2\2\u00c5\u00c9\t"+
		"\b\2\2\u00c6\u00c9\5%\23\2\u00c7\u00c9\5\'\24\2\u00c8\u00c4\3\2\2\2\u00c8"+
		"\u00c6\3\2\2\2\u00c8\u00c7\3\2\2\2\u00c9$\3\2\2\2\u00ca\u00cb\7^\2\2\u00cb"+
		"\u00d6\5)\25\2\u00cc\u00cd\7^\2\2\u00cd\u00ce\5)\25\2\u00ce\u00cf\5)\25"+
		"\2\u00cf\u00d6\3\2\2\2\u00d0\u00d1\7^\2\2\u00d1\u00d2\5+\26\2\u00d2\u00d3"+
		"\5)\25\2\u00d3\u00d4\5)\25\2\u00d4\u00d6\3\2\2\2\u00d5\u00ca\3\2\2\2\u00d5"+
		"\u00cc\3\2\2\2\u00d5\u00d0\3\2\2\2\u00d6&\3\2\2\2\u00d7\u00d8\7^\2\2\u00d8"+
		"\u00d9\7w\2\2\u00d9\u00da\5-\27\2\u00da\u00db\5-\27\2\u00db\u00dc\5-\27"+
		"\2\u00dc\u00dd\5-\27\2\u00dd(\3\2\2\2\u00de\u00df\t\t\2\2\u00df*\3\2\2"+
		"\2\u00e0\u00e1\t\n\2\2\u00e1,\3\2\2\2\u00e2\u00e3\t\13\2\2\u00e3.\3\2"+
		"\2\2\u00e4\u00e6\t\f\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7"+
		"\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9\u00ea\b\30"+
		"\2\2\u00ea\60\3\2\2\2\f\2\u00a2\u00ab\u00b3\u00b7\u00be\u00c2\u00c8\u00d5"+
		"\u00e7\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}