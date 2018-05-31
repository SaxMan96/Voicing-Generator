package me.tomassetti.examples.MarkupParser;

import javafx.util.Pair;
import jm.JMC;
import jm.gui.show.ShowScore;
import jm.music.data.CPhrase;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Play;
import jm.util.View;
import jm.util.Write;

import java.util.*;

public class MarkupVisitor extends MarkupParserBaseVisitor<String> implements JMC {

    private Note[] currentTrebleChord;
    private Note[] currentBassChord;
    private Vector<Note[]> currentTrebleChordArray;
    private Vector<Note[]> currentBassChordArray;
    private CPhrase currentTreblePhrase;
    private CPhrase currentBassPhrase;
    private Map<String, Pair<CPhrase, CPhrase>> phrasesMap;
    private List<String> formList;
    private Part treblePart;
    private Part bassPart;
    private Score score = new Score("New score");
    private String currentPhraseName;
    private int TEMPO = 180;
    private double currentDuration;

    private void initScore() {
        currentTrebleChordArray = new Vector<>();
        currentBassChordArray = new Vector<>();
        currentTreblePhrase = new CPhrase("treble phrase");
        currentTreblePhrase.setAppend(true);
        currentBassPhrase = new CPhrase("bass phrase");
        currentBassPhrase.setAppend(true);
        treblePart = new Part("PIANO-Right", PIANO, 0);
        bassPart = new Part("PIANO-Left", PIANO, 0);
        treblePart.setTempo(TEMPO);
        bassPart.setTempo(TEMPO);
        score.setTempo(TEMPO);
        formList = new ArrayList();
        phrasesMap = new HashMap<>();
        me.tomassetti.examples.MarkupParser.Util.init();
    }

    private void generateScore() {
        String partName;
        CPhrase cphr;
        for (int i=0;i<formList.size();i++) {
            partName = formList.get(i);
            Pair<CPhrase, CPhrase> phrasePair = phrasesMap.get(partName);
            cphr = phrasePair.getKey().copy();
            cphr.setAppend(true);
            treblePart.addCPhrase(cphr);
            cphr = phrasePair.getValue().copy();
            cphr.setAppend(true);
            bassPart.addCPhrase(cphr);
        }
        score.add(treblePart);
        score.add(bassPart);
        new ShowScore(score);
        Play.midi(score);
//        View.show(score);
        View.sketch(score);
        Write.midi(score);
    }


    @Override
    public String visitFile(MarkupParser.FileContext context) {
        initScore();
        String result = visitSettings(context.settings());
        result += visitParts(context.parts());
        result += visitForm(context.form());
        generateScore();
//        testContent();
        return result;
    }


    @Override
    public String visitSettings(MarkupParser.SettingsContext context) {
        score.setTimeSignature(me.tomassetti.examples.MarkupParser.Util.getBeats(context.beats()), me.tomassetti.examples.MarkupParser.Util.getBeatType(context.beatType()));
        String result = visitBeats(context.beats());
        result += visitBeatType(context.beatType());
        if (context.key() != null) {
            int key = me.tomassetti.examples.MarkupParser.Util.getKeySignature(context.key().NOTENAME().getText());
            treblePart.setKeySignature(key);
            bassPart.setKeySignature(key);
            score.setKeySignature(key);
            result += visitKey(context.key());
        }
        if (context.defaultVoicings() != null)
            result += visitDefaultVoicings(context.defaultVoicings());
        return result;
    }

    @Override
    public String visitParts(MarkupParser.PartsContext context) {
        StringBuilder result = new StringBuilder("\nparts:");
        for (MarkupParser.PartContext node : context.part()) {
            currentPhraseName = node.partName().IDENTIFIER().getText();
            result.append(visitPart(node));
        }
        return result.toString();
    }

    @Override
    public String visitForm(MarkupParser.FormContext context) {
        StringBuilder result = new StringBuilder("\nform:\n");
        for (MarkupParser.PartNameContext node : context.partName()) {
            result.append(visitPartName(node));
            formList.add(node.IDENTIFIER().getText());
        }
        return result.toString();
    }

    @Override
    public String visitBeats(MarkupParser.BeatsContext context) {
        return context.DIGITS().getText();
    }

    @Override
    public String visitBeatType(MarkupParser.BeatTypeContext context) {
        StringBuilder result = new StringBuilder();
        if (context.EIGHTH() != null)
            result.append(context.EIGHTH().getText());
        else if (context.QUARTER() != null)
            result.append(context.QUARTER().getText());
        else if (context.HALFNOTE() != null)
            result.append(context.HALFNOTE().getText());
        return result.toString();
    }

    @Override
    public String visitKey(MarkupParser.KeyContext context) {
        return context.NOTENAME().getText() + '\n';
    }

    @Override
    public String visitDefaultVoicings(MarkupParser.DefaultVoicingsContext context) {
        StringBuilder result = new StringBuilder("default voicings:\n");
        for (MarkupParser.VoicingRuleContext node : context.voicingRule()) {
            me.tomassetti.examples.MarkupParser.Util.createVoicingRule(node);
            result.append(visitVoicingRule(node));
        }
        return result.toString();
    }

    @Override
    public String visitVoicingRule(MarkupParser.VoicingRuleContext context) {
        StringBuilder result = new StringBuilder();
        for (MarkupParser.DegreeContext node : context.degree())
            result.append(visitDegree(node));
        result.append('\t');
        return result.toString();
    }

    @Override
    public String visitChordType(MarkupParser.ChordTypeContext context) {
        StringBuilder result = new StringBuilder();

        if (context.AddNine()!=null) result.append(context.AddNine().getText());
        else if (context.Augmented()!=null) result.append(context.Augmented().getText());
        else if (context.Diminished()!=null) result.append(context.Diminished().getText());
        else if (context.DiminishedMajorSeven()!=null) result.append(context.DiminishedMajorSeven().getText());
        else if (context.DiminishedSeven()!=null) result.append(context.DiminishedSeven().getText());
        else if (context.Five()!=null) result.append(context.Five().getText());
        else if (context.Four()!=null) result.append(context.Four().getText());
        else if (context.HalfDiminished()!=null) result.append(context.HalfDiminished().getText());
        else if (context.Major()!=null) result.append(context.Major().getText());
        else if (context.MajorAddNine()!=null) result.append(context.MajorAddNine().getText());
        else if (context.MajorFlatFive()!=null) result.append(context.MajorFlatFive().getText());
        else if (context.MajorNine()!=null) result.append(context.MajorNine().getText());
        else if (context.MajorNineFlatFive()!=null) result.append(context.MajorNineFlatFive().getText());
        else if (context.MajorNineSharpEleven()!=null) result.append(context.MajorNineSharpEleven().getText());
        else if (context.MajorNineSharpFive()!=null) result.append(context.MajorNineSharpFive().getText());
        else if (context.MajorSeven()!=null) result.append(context.MajorSeven().getText());
        else if (context.MajorSevenAddThirteen()!=null) result.append(context.MajorSevenAddThirteen().getText());
        else if (context.MajorSevenAugmented()!=null) result.append(context.MajorSevenAugmented().getText());
        else if (context.MajorSevenFlatFive()!=null) result.append(context.MajorSevenFlatFive().getText());
        else if (context.MajorSevenFlatNine()!=null) result.append(context.MajorSevenFlatNine().getText());
        else if (context.MajorSevenSharpEleven()!=null) result.append(context.MajorSevenSharpEleven().getText());
        else if (context.MajorSevenSharpFive()!=null) result.append(context.MajorSevenSharpFive().getText());
        else if (context.MajorSharpFive()!=null) result.append(context.MajorSharpFive().getText());
        else if (context.MajorSix()!=null) result.append(context.MajorSix().getText());
        else if (context.MajorSixNine()!=null) result.append(context.MajorSixNine().getText());
        else if (context.MajorSusFour()!=null) result.append(context.MajorSusFour().getText());
        else if (context.MajorSusTwo()!=null) result.append(context.MajorSusTwo().getText());
        else if (context.MajorThirteen()!=null) result.append(context.MajorThirteen().getText());
        else if (context.MajorThirteenSharpEleven()!=null) result.append(context.MajorThirteenSharpEleven().getText());
        else if (context.Minor()!=null) result.append(context.Minor().getText());
        else if (context.MinorEleven()!=null) result.append(context.MinorEleven().getText());
        else if (context.MinorElevenFlatFive()!=null) result.append(context.MinorElevenFlatFive().getText());
        else if (context.MinorMajorNine()!=null) result.append(context.MinorMajorNine().getText());
        else if (context.MinorMajorSeven()!=null) result.append(context.MinorMajorSeven().getText());
        else if (context.MinorNine()!=null) result.append(context.MinorNine().getText());
        else if (context.MinorNineFlatFive()!=null) result.append(context.MinorNineFlatFive().getText());
        else if (context.MinorSeven()!=null) result.append(context.MinorSeven().getText());
        else if (context.MinorSevenFlatFive()!=null) result.append(context.MinorSevenFlatFive().getText());
        else if (context.MinorSharpFive()!=null) result.append(context.MinorSharpFive().getText());
        else if (context.MinorSix()!=null) result.append(context.MinorSix().getText());
        else if (context.MinorSixNine()!=null) result.append(context.MinorSixNine().getText());
        else if (context.MinorThirteen()!=null) result.append(context.MinorThirteen().getText());
        else if (context.Nine()!=null) result.append(context.Nine().getText());
        else if (context.NineAugmented()!=null) result.append(context.NineAugmented().getText());
        else if (context.NineFlatFive()!=null) result.append(context.NineFlatFive().getText());
        else if (context.NineSharpEleven()!=null) result.append(context.NineSharpEleven().getText());
        else if (context.NineSharpFive()!=null) result.append(context.NineSharpFive().getText());
        else if (context.NineSharpFiveSharpEleven()!=null) result.append(context.NineSharpFiveSharpEleven().getText());
        else if (context.NineSusFour()!=null) result.append(context.NineSusFour().getText());
        else if (context.NineSusTwo()!=null) result.append(context.NineSusTwo().getText());
        else if (context.Seven()!=null) result.append(context.Seven().getText());
        else if (context.SevenAltered()!=null) result.append(context.SevenAltered().getText());
        else if (context.SevenAugmented()!=null) result.append(context.SevenAugmented().getText());
        else if (context.SevenFlatFive()!=null) result.append(context.SevenFlatFive().getText());
        else if (context.SevenFlatFiveFlatNine()!=null) result.append(context.SevenFlatFiveFlatNine().getText());
        else if (context.SevenFlatNine()!=null) result.append(context.SevenFlatNine().getText());
        else if (context.SevenFlatNineFlatThirteen()!=null) result.append(context.SevenFlatNineFlatThirteen().getText());
        else if (context.SevenFlatNineFlatThirteenSharpEleven()!=null) result.append(context.SevenFlatNineFlatThirteenSharpEleven().getText());
        else if (context.SevenFlatNineSharpEleven()!=null) result.append(context.SevenFlatNineSharpEleven().getText());
        else if (context.SevenFlatNineSusFour()!=null) result.append(context.SevenFlatNineSusFour().getText());
        else if (context.SevenSharpEleven()!=null) result.append(context.SevenSharpEleven().getText());
        else if (context.SevenSharpFive()!=null) result.append(context.SevenSharpFive().getText());
        else if (context.SevenSharpFiveFlatNine()!=null) result.append(context.SevenSharpFiveFlatNine().getText());
        else if (context.SevenSharpFiveSharpNine()!=null) result.append(context.SevenSharpFiveSharpNine().getText());
        else if (context.SevenSharpNine()!=null) result.append(context.SevenSharpNine().getText());
        else if (context.SevenSusFour()!=null) result.append(context.SevenSusFour().getText());
        else if (context.SevenSusFourFlatNine()!=null) result.append(context.SevenSusFourFlatNine().getText());
        else if (context.SevenSusTwo()!=null) result.append(context.SevenSusTwo().getText());
        else if (context.Six()!=null) result.append(context.Six().getText());
        else if (context.SixNine()!=null) result.append(context.SixNine().getText());
        else if (context.SusFour()!=null) result.append(context.SusFour().getText());
        else if (context.SusTwo()!=null) result.append(context.SusTwo().getText());
        else if (context.Thirteen()!=null) result.append(context.Thirteen().getText());
        else if (context.ThirteenFlatFive()!=null) result.append(context.ThirteenFlatFive().getText());
        else if (context.ThirteenFlatNine()!=null) result.append(context.ThirteenFlatNine().getText());
        else if (context.ThirteenFlatNineSharpEleven()!=null) result.append(context.ThirteenFlatNineSharpEleven().getText());
        else if (context.ThirteenSharpEleven()!=null) result.append(context.ThirteenSharpEleven().getText());
        else if (context.ThirteenSharpNine()!=null) result.append(context.ThirteenSharpNine().getText());
        else if (context.ThirteenSusFour()!=null) result.append(context.ThirteenSusFour().getText());
        else if (context.ThirteenSusTwo()!=null) result.append(context.ThirteenSusTwo().getText());
        else if (context.Two()!=null) result.append(context.Two().getText());
        result.append(' ');

        return result.toString();
    }

    @Override
    public String visitDegree(MarkupParser.DegreeContext context) {
        StringBuilder result = new StringBuilder();
        if (context.Eleven() != null) result.append(context.Eleven().getText());
        if (context.Five() != null) result.append(context.Five().getText());
        if (context.Four() != null) result.append(context.Four().getText());
        if (context.Nine() != null) result.append(context.Nine().getText());
        if (context.Seven() != null) result.append(context.Seven().getText());
        if (context.Six() != null) result.append(context.Six().getText());
        if (context.Thirteen() != null) result.append(context.Thirteen().getText());
        if (context.Three() != null) result.append(context.Three().getText());
        if (context.Two() != null) result.append(context.Two().getText());
        if (context.Root() != null) result.append(context.Root().getText());
        return result.toString();
    }

    @Override
    public String visitPart(MarkupParser.PartContext context) {
        String result = visitPartName(context.partName());
        result += visitPartContent(context.partContent());
        return result;
    }

    @Override
    public String visitPartName(MarkupParser.PartNameContext context) {
        return '\n' + context.IDENTIFIER().getText() + '\n';
    }

    @Override
    public String visitPartContent(MarkupParser.PartContentContext context) {
        StringBuilder result = new StringBuilder();
        for (MarkupParser.MeasureContext node : context.measure()) {
            result.append(visitMeasure(node));
            result.append(context.BITOR().get(0).toString());

        }
        Pair phrasePair = new Pair(currentTreblePhrase.copy(), currentBassPhrase.copy());
        currentTreblePhrase = new CPhrase();
        currentBassPhrase = new CPhrase();
        currentTreblePhrase.setAppend(true);
        currentBassPhrase.setAppend(true);

        phrasesMap.put(currentPhraseName, phrasePair);
        return result.toString();
    }

    @Override
    public String visitMeasure(MarkupParser.MeasureContext context) {
        StringBuilder result = new StringBuilder();
        double chordNumber = context.chord().size();
        double beats = score.getTimeSignature().getX();
        double beatsType = score.getTimeSignature().getY();
        currentDuration = (4.0*beats)/(chordNumber*beatsType);

        //TODO: Util du wyliczenia jak rozłożyć akordy w takcie
        if (context.MOD() != null) {
            result.append(context.MOD().getText());
            if(currentTrebleChordArray.equals(null) || currentBassChordArray.equals(null)) {
                return "";
            }
        }else{
            currentTrebleChordArray.clear();
            currentBassChordArray.clear();
            for (MarkupParser.ChordContext node : context.chord()) {
                result.append(visitChord(node));
            }
        }
        for(Note[] chord: currentTrebleChordArray)
            currentTreblePhrase.addChord(chord);
        for(Note[] chord: currentBassChordArray)
            currentBassPhrase.addChord(chord);

        return result.toString();
    }

    @Override
    public String visitChord(MarkupParser.ChordContext context) {
        int root = me.tomassetti.examples.MarkupParser.Util.getKey(context.NOTENAME().getText());
        me.tomassetti.examples.MarkupParser.Util.setScale(context.voicingRule().chordType());
        me.tomassetti.examples.MarkupParser.Util.setVoicing(context.voicingRule());
        currentTrebleChordArray.add(me.tomassetti.examples.MarkupParser.Util.buildTrebleChord(root,currentDuration,context.voicingRule().chordType().getText()));
        currentBassChordArray.add(me.tomassetti.examples.MarkupParser.Util.buildBassChord(root,currentDuration));
        me.tomassetti.examples.MarkupParser.Util.clearVoicing();
        return context.NOTENAME().getText() +visitChordType(context.voicingRule().chordType()) + visitVoicingRule(context.voicingRule());
    }
}