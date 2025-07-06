grammar Expression;

// Parser rules
expr
    : receiver=expr '.' call=ID '(' (expr (',' expr)*)? ')'     # qualifiedCall
    | receiver=ID '[' sliceExpr ']'                     # slice
    | ID '(' (expr (',' expr)*)? ')'             # call
    | castType '(' expr ')'                      # cast
    | (plus='+' | minus='-') expr                           # unaryPlusMinus
    | left=expr (mul='*' | div='/' | mod='%') right=expr                # mulDivMod
    | left=expr (plus='+' | minus='-') right=expr                      # addSub
    | left=expr '..' right=expr                             # concat
    | ID                                         # identifier
    | INT                                        # intLiteral
    | FLOAT                                      # floatLiteral
    | STRING                                     # stringLiteral
    | '(' expr ')'                               # parenthesized
    ;

sliceExpr
    : index=expr                                       # singleSlice
    | startSlice=expr ':' endSlice=expr                              # sliceStartEnd
    | ':' endSlice=expr                                   # sliceEnd
    | startSlice=expr ':'                                   # sliceStart
    | startSlice=expr ':' endSlice=expr ':' stepSlice=expr                     # sliceWithStep
    | startSlice=expr ':' ':' stepSlice=expr                          # sliceStartStep
    | ':' endSlice=expr ':' stepSlice=expr                          # sliceEndStep
    ;

castType
    : 'int' #intCast
    | 'float' #floatCast
    | 'string' #stringCast
    ;

// Lexer rules
fragment DIGIT : [0-9] ;
fragment LETTER : [a-zA-Z] ;
fragment UNDERSCORE : '_' ;

INT     : DIGIT+ ;
FLOAT   : DIGIT+ '.' DIGIT+ ;
STRING  : '"' (~["\r\n] | '\\"')* '"'
        | '\'' (~['\r\n] | '\\\'')* '\''
        ;
ID      : (LETTER | UNDERSCORE) (LETTER | DIGIT | UNDERSCORE)* ;

// Operators and punctuation
CONCAT  : '..' ;
DOT     : '.' ;
LPAREN  : '(' ;
RPAREN  : ')' ;
LBRACKET: '[' ;
RBRACKET: ']' ;
COMMA   : ',' ;
COLON   : ':' ;
PLUS    : '+' ;
MINUS   : '-' ;
MULT    : '*' ;
DIV     : '/' ;
MOD     : '%' ;

// Whitespace and comments
WS      : [ \t\r\n]+ -> skip ;
COMMENT : '/*' .*? '*/' -> skip ;