package me.tomassetti.examples.MarkupParser;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import me.tomassetti.examples.MarkupParser.MarkupLexer;
import org.antlr.v4.runtime.*;

public class App 
{
    public static void main( String[] args )
    {
        String input = " ";
        try {
            input = readFile("files/VoicingGenerator.txt", StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ANTLRInputStream inputStream = new ANTLRInputStream(input);
//        ANTLRInputStream inputStream = new ANTLRInputStream("settings  {  beats 4  beatType quarter      key F  defaultVoicings  {  7 <3 7 13 9>  7 <3 7 b9 13>   7 <5 7 9 #11>  -6 <3 5 6 9>  -7 <3 5 7 9>  -7b5 <3 b5 7 9>  -^7 <3 5 #7 9>  ^7 <9 3 5 7>  }  }  parts{      /*Wersja z wykorzystaniem edycji voicingĂłw, powtÄ‚Ĺ‚rzeÄąâ€ž i ustawieÄąâ€ž voicingÄ‚Ĺ‚w */      PartA:  Eb7 <7 9 3 13>  | D-6 <3 5 6 9> | Eb7 <7 9 3 13>     | D-6 <3 5 6 9> |  Eb7 <7 9 3 13>  | D-6 <3 5 6 9> | E-7b5<b5 7 9 11> A7| D-7   PartB:  A-7b5 |  D7  | G-7 D7   |    G-7   |  G-7b5 | C7b9 |   F^7    | E-7b5 A7   PartC:  E-7b5 |  %   | Eb7#11   |  %       |   D-7   |  %   | G7#11    |  %       |  G-^7  | G-7  | Gb7#11   |  %       |  F^7   |  %   | E-7b5    | A7b9       }  form  {  PartA,PartA,PartB,PartA,PartC  }");
        MarkupLexer markupLexer = new MarkupLexer(inputStream);
//        System.out.println(markupLexer.getAllTokens());
        CommonTokenStream commonTokenStream = new CommonTokenStream(markupLexer);
        MarkupParser markupParser = new MarkupParser(commonTokenStream);
//        markupParser.setBuildParseTree(true);

        MarkupParser.FileContext fileContext = markupParser.file();
       MarkupVisitor visitor = new MarkupVisitor();
        visitor.visit(fileContext);
//        System.out.print(visitor.visit(fileContext));

    }
    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
