parser grammar MarkupParser;

options { tokenVocab=MarkupLexer; }

file        : settings parts form <EOF>;

settings    : SETTINGS '{' beats beatType key? defaultVoicings? '}' ;

parts       : PARTS '{' part* '}' ;

form        : FORM '{' partName (',' partName )* '}' ;

beats       : BEATS DIGITS;
beatType    : BEATTYPE (EIGHTH |QUARTER | HALFNOTE);
key         : KEY NOTENAME;
defaultVoicings: DEFAULTVOICINGS '{' (voicingRule)* '}' ;
voicingRule : chordType ('<' ( degree)+ '>')?;

chordType   : ( AddNine | Augmented | Diminished |  DiminishedMajorSeven | DiminishedSeven |
                Five | Four | HalfDiminished | Major | MajorAddNine | MajorFlatFive |
                MajorNine | MajorNineFlatFive | MajorNineSharpEleven | MajorNineSharpFive |
                MajorSeven | MajorSevenAddThirteen | MajorSevenAugmented | MajorSevenFlatFive |
                MajorSevenFlatNine | MajorSevenSharpEleven | MajorSevenSharpFive |
                MajorSharpFive | MajorSix | MajorSixNine | MajorSusFour | MajorSusTwo |
                MajorThirteen | MajorThirteenSharpEleven | Minor | MinorEleven |
                MinorElevenFlatFive | MinorMajorNine | MinorMajorSeven | MinorNine |
                MinorNineFlatFive | MinorSeven | MinorSevenFlatFive | MinorSharpFive |
                MinorSix | MinorSixNine | MinorThirteen | Nine | NineAugmented |
                NineFlatFive | NineSharpEleven | NineSharpFive | NineSharpFiveSharpEleven |
                NineSusFour | NineSusTwo | Seven | SevenAltered | SevenAugmented | SevenFlatFive |
                SevenFlatFiveFlatNine | SevenFlatNine | SevenFlatNineFlatThirteen |
                SevenFlatNineFlatThirteenSharpEleven | SevenFlatNineSharpEleven |
                SevenFlatNineSusFour | SevenSharpEleven | SevenSharpFive |
                SevenSharpFiveFlatNine | SevenSharpFiveSharpNine | SevenSharpNine |
                SevenSusFour | SevenSusFourFlatNine | SevenSusTwo |
                Six | SixNine | SusFour | SusTwo | Thirteen | ThirteenFlatFive |
                ThirteenFlatNine | ThirteenFlatNineSharpEleven | ThirteenSharpEleven |
                ThirteenSharpNine | ThirteenSusFour | ThirteenSusTwo | Two);

degree      : (Two | Three | Four | Five | Six | Seven | Nine | Eleven | Thirteen | Root);
            //TODO usunąć znaki przy stopniach
part        : partName ':' partContent ;
partName    : IDENTIFIER ;
partContent : measure (BITOR measure)* ;
measure     : ((chord)+)|MOD ;
chord       : NOTENAME voicingRule;
