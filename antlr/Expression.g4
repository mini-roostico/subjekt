grammar Expression;

expression
    : resolvableExpr             #resolvable
    | atomicExpr                 #atomic
    | '(' expression ')'         #parenthesis
    ;

resolvableExpr
    : rangeSlice                #sliceExpr
    ;

atomicExpr
    : castExpr                  #cast
    | macroCall                 #call
    | dotCall                   #moduleCall
    | singleSlice               #singleSliceExpr
    | atomicExpr (mod='%'|mul='*'|div='/') atomicExpr #modMulDiv
    | atomicExpr (plus='+'|minus='-') atomicExpr #plusMinus
    | atomicExpr '..' atomicExpr #concat
    | '(' atomicExpr ')'        #atomicParenthesis
    | ID                        #identifier
    | NUMBER                    #intLiteral
    | FLOAT                     #floatLiteral
    | STRING                    #stringLiteral
    ;


macroCall
    : ID '(' (expression (',' expression)*)? ')'
    ;

dotCall
    : ID '.' ID '(' (expression (',' expression)*)? ')'
    ;

singleSlice
    : ID '[' atomicExpr ']'
    ;

castExpr
    : 'int' '(' atomicExpr ')'     #intCast
    | 'float' '(' atomicExpr ')'   #floatCast
    | 'string' '(' atomicExpr ')'  #stringCast
    ;

rangeSlice
    : ID '[' startExpr=atomicExpr ':' endExpr=atomicExpr ']' #sliceStartEnd
    | ID '[' ':' endExpr=atomicExpr ']'            #sliceEnd
    | ID '[' startExpr=atomicExpr ':' ']'            #sliceStart
    | ID '[' startExpr=atomicExpr? ':' endExpr=atomicExpr? ':' stepExpr=atomicExpr ']' #sliceWithStep
    ;

STRING : '"' (ESC | ~[\r\n"\\])* '"' | '\'' (ESC | ~[\r\n'\\])* '\'' ;
ESC : '\\' . ;
ID  	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9' | '_')* ;
FLOAT : ('0'..'9')+ '.' ('0'..'9')+ ;
NUMBER : ('0'..'9')+ ;

WHITESP  : ( '\t' | ' ' | '\n' | '\r' )+    -> channel(HIDDEN) ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
