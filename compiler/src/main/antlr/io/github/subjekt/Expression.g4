grammar Expression;

@header {
package io.github.subjekt;
}

expression
    : expression '+' expression #plusExpr
    | macroCall                 #call
    | ID                        #variable
    | STRING                    #literal
    ;

macroCall
    : ID '(' (expression (',' expression)*)? ')'
    ;

STRING : '"' ~('\r' | '\n' | '"')* '"' ;
ID  	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9' | '_')* ;

WHITESP  : ( '\t' | ' ' | '\n' | '\r' )+    -> channel(HIDDEN) ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
