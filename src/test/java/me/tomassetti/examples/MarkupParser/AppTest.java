package me.tomassetti.examples.MarkupParser;

import org.antlr.v4.runtime.*;

import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    private MarkupParser setup(String input)
    {
        ANTLRInputStream inputStream = new ANTLRInputStream(input);
        this.markupLexer = new MarkupLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(markupLexer);
        MarkupParser markupParser = new MarkupParser(commonTokenStream);

        StringWriter writer = new StringWriter();
        this.errorListener = new MarkupErrorListener(writer);
        markupLexer.removeErrorListeners();
        //markupLexer.addErrorListener(errorListener);
        markupParser.removeErrorListeners();
        markupParser.addErrorListener(errorListener);

        return markupParser;
    }

    private MarkupErrorListener errorListener;
    private MarkupLexer markupLexer;

    public void test()
    {
        ANTLRInputStream inputStream = new ANTLRInputStream(
                "settings{" +
                    "beats 4" +
                    "beatType quarter    " +
                    "key F " +
                    "defaultVoicings{" +
                        "7 <3 7 9 13>" +
                        "7alt <7 9 3 5>" +
                        "-6 <3 5 6 9>" +
                        "-7 <3 5 7 9>" +
                        "-7b5 <3 5 7 9>" +
                        "-^7 <3 5 7 9>" +
                        "^7 <3 5 7 9>" +
                    "}" +
                "}" +
                "parts{" +
                    "PartA:" +
                        "Eb7 <7 9 3 13>  | D-6 <3 5 6 9> | Eb7      | D-6  |" +
                        "Eb7   | D-6  | E-7b5<5 7 9 11> A7alt| D-7 " +
                    "PartB:" +
                        "A-7b5 |  D7  | G-7 D7   |    G-7   |" +
                        "G-7b5 | C7b5b9 |   F^7    | E-7b5 A7 " +
                    "PartC:" +
                        "E-7b5 |  %   | Eb7#11   |  %       | " +
                        "D-7   |  %   | G7#11    |  %       |" +
                        "G-^7  | G-7  | Gb7#11   |  %       |" +
                        "F^7   |  %   | E-7b5    | A7b9     " +
                "}" +
                "form{" +
                "   PartA,PartA,PartB,PartA,PartC" +
                "}"
        );
        MarkupLexer markupLexer = new MarkupLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(markupLexer);
        MarkupParser markupParser = new MarkupParser(commonTokenStream);
        MarkupParser.FileContext fileContext = markupParser.file();
        MarkupVisitor visitor = new MarkupVisitor();
        visitor.visit(fileContext);
    }





}
