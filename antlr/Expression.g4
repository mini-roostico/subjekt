grammar Expression;

@header {
package io.github.subjekt;
}

expression
    : expression '+' expression #plusExpr
    | macroCall                 #call
    | dotCall                   #moduleCall
    | ID                        #variable
    | STRING                    #literal
    ;

macroCall
    : ID '(' (expression (',' expression)*)? ')'
    ;

dotCall
    : ID '.' ID '(' (expression (',' expression)*)? ')'
    ;

STRING : '"' (ESC | ~[\r\n"\\])* '"' | '\'' (ESC | ~[\r\n'\\])* '\'' ;
ESC : '\\' . ;
ID  	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9' | '_')* ;

WHITESP  : ( '\t' | ' ' | '\n' | '\r' )+    -> channel(HIDDEN) ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
