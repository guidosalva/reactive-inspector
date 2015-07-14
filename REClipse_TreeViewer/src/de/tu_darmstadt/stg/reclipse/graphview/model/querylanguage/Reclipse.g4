grammar Reclipse;

@header{package de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage;}

query: nodeCreatedQuery
	|
	nodeEvaluatedQuery
	|
	nodeValueSet
	|
	dependencyCreated
	|
	evaluationYielded
	|
	evaluationException
	;

nodeCreatedQuery: 'nodeCreated(' NODE_NAME ')' ;
nodeEvaluatedQuery: 'nodeEvaluated(' NODE_NAME ')' ;
nodeValueSet: 'nodeValueSet(' NODE_NAME ')' ;
dependencyCreated: 'dependencyCreated(' NODE_NAME ',' NODE_NAME ')' ;
evaluationYielded: 'evaluationYielded(' NODE_NAME ',' VALUE ')' ;
evaluationException: 'evaluationException(' (NODE_NAME)? ')' ;

NODE_NAME: Identifier ;

VALUE: StringLiteral ;

// taken from the official ANTLR v4 Java grammar
// @see https://github.com/antlr/grammars-v4/blob/master/java/Java.g4
Identifier
    :   JavaLetter JavaLetterOrDigit*
    ;

fragment
JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

// §3.10.5 String Literals

StringLiteral
    :   '"' StringCharacters? '"'
    ;

fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;

// §3.10.6 Escape Sequences for Character and String Literals

fragment
EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape
    ;

fragment
OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
ZeroToThree
    :   [0-3]
    ;

fragment
HexDigit
    :   [0-9a-fA-F]
    ;

WS:  [ \t\r\n\u000C]+ -> skip ;
