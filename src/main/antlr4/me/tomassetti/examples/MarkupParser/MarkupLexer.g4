lexer grammar MarkupLexer;

PARTS:           'parts';
FORM:            'form';
SETTINGS:        'settings';
BEATS:           'beats' -> pushMode(DIGITSMODE);
KEY:             'key';
DEFAULTVOICINGS: 'defaultVoicings';

QUARTER:        'quarter';
HALFNOTE:       'halfnote';
EIGHTH:         'eighth';
NOTENAME:       [A-G](SHARP|FLAT)?;
SHARP:          '#';
FLAT:           'b';


Root :          'R'|'r'|'1';
Eleven :        '11';
Three :         '3';


AddNine: 	'add9';
Augmented: 	('+'|'aug');
Diminished: 	('dim'|'o');
DiminishedMajorSeven: 	'oM7';
DiminishedSeven: 	('dim7'|'o7');
Five: 	'5';
Four: 	'4';
HalfDiminished: 	'h';
Major: 	'^';
MajorAddNine: 	'^add9';
MajorFlatFive: 	'^b5';
MajorNine: 	'^9';
MajorNineFlatFive: 	'^9b5';
MajorNineSharpEleven: 	'^9#11';
MajorNineSharpFive: 	'^9#5';
MajorSeven: 	'^7';
MajorSevenAddThirteen: 	'^7add13';
MajorSevenAugmented: 	'^7+';
MajorSevenFlatFive: 	'^7b5';
MajorSevenFlatNine: 	'^7b9';
MajorSevenSharpEleven: 	'^7#11';
MajorSevenSharpFive: 	'^7#5';
MajorSharpFive: 	'^#5';
MajorSix: 	'^6';
MajorSixNine: 	'^69';
MajorSusFour: 	'^sus4';
MajorSusTwo: 	'^sus2';
MajorThirteen: 	'^13';
MajorThirteenSharpEleven: 	'^13#11';
Minor: 	'-';
MinorEleven: 	'-11';
MinorElevenFlatFive: 	'-11b5';
MinorMajorNine: 	'-^9';
MinorMajorSeven: 	'-^7';
MinorNine: 	'-9';
MinorNineFlatFive: 	'-9b5';
MinorSeven: 	'-7';
MinorSevenFlatFive: 	'-7b5';
MinorSharpFive: 	('-+'|'-#5');
MinorSix: 	'-6';
MinorSixNine: '-69';
MinorThirteen: 	'-13';
Nine: 	'9';
NineAugmented: 	'9+';
NineFlatFive: 	'9b5';
NineSharpEleven: 	'9#11';
NineSharpFive: 	'9#5';
NineSharpFiveSharpEleven: 	'9#5#11';
NineSusFour: 	'9sus4';
NineSusTwo: 	'9sus2';
Seven: 	'7';
SevenAltered: 	'7alt';
SevenAugmented: 	('7+'|'7aug');
SevenFlatFive: 	'7b5';
SevenFlatFiveFlatNine: 	'7b5b9';
SevenFlatNine: 	'7b9';
SevenFlatNineFlatThirteen: 	'7b9b13';
SevenFlatNineFlatThirteenSharpEleven: 	'7b9b13#11';
SevenFlatNineSharpEleven: 	'7b9#11';
SevenFlatNineSusFour: 	'7b9sus4';
SevenSharpEleven: 	'7#11';
SevenSharpFive: 	'7#5';
SevenSharpFiveFlatNine: 	'7#5b9';
SevenSharpFiveSharpNine: 	'7#5#9';
SevenSharpNine: 	'7#9';
SevenSusFour: 	'7sus4';
SevenSusFourFlatNine: 	'7sus4b9';
SevenSusTwo: 	'7sus2';
Six: 	'6';
SixNine: 	'69';
SusFour: 	'sus4';
SusTwo: 	'sus2';
Thirteen: 	'13';
ThirteenFlatFive: 	'13b5';
ThirteenFlatNine: 	'13b9';
ThirteenFlatNineSharpEleven: 	'13b9#11';
ThirteenSharpEleven: 	'13#11';
ThirteenSharpNine: 	'13#9';
ThirteenSusFour: 	'13sus4';
ThirteenSusTwo: 	'13sus2';
Two: 	'2';



IDENTIFIER: Letter+;

//White speces & comments
WS:                 [ \t\r\n\u000C]+ -> skip;
COMMENT:            '/*' .*? '*/'    -> skip;
LINECOMMENT:       '//' ~[\r\n]*    -> skip;

//Special chars
LPAREN:             '(';
RPAREN:             ')';
LBRACE:             '{';
RBRACE:             '}';
SEMI:               ';';
COLON:              ':';
COMMA:              ',';
DOT:                '.';
MOD:                '%';
GT:                 '>';
LT:                 '<';
BITOR:              '|';
MUL:                '*';
EQUALS:             '=';

mode DIGITSMODE;
DIGITS:         Digits;
BEATTYPE:       'beatType' -> popMode;
//White speces & comments at DIGITSMODE
WSDM:                 [ \t\r\n\u000C]+ -> skip;
COMMENTDM:            '/*' .*? '*/'    -> skip;
LINECOMMENTDM:       '//' ~[\r\n]*    -> skip;

fragment Letter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;
fragment LetterOrDigit
    : Letter | Digit ;
fragment Digits
    : [0-9] ([0-9_]* [0-9])*    ;
fragment Digit
    : [0-9] ;