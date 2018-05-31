package me.tomassetti.examples.MarkupParser;

import jm.JMC;
import jm.constants.Pitches;
import jm.music.data.Note;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Util  {

    private static Map<String, me.tomassetti.examples.MarkupParser.Voicing> defaultVoicings;
    private static me.tomassetti.examples.MarkupParser.Scale scale;
    private static me.tomassetti.examples.MarkupParser.Voicing voicing;

    public static void init() {
        defaultVoicings = new HashMap<>();
        voicing = new me.tomassetti.examples.MarkupParser.Voicing();
        scale = new me.tomassetti.examples.MarkupParser.Scale();
    }

    static int getKey(String noteName) {
        int retVal = 0;
        if      (noteName.contains("C")) retVal = JMC.C4;
        else if (noteName.contains("D")) retVal = JMC.D4;
        else if (noteName.contains("E")) retVal = JMC.E4;
        else if (noteName.contains("F")) retVal = JMC.F4;
        else if (noteName.contains("G")) retVal = JMC.G4;
        else if (noteName.contains("A")) retVal = JMC.A4;
        else if (noteName.contains("B")) retVal = JMC.B4;
        if (noteName.contains("#")) retVal++;
        if (noteName.contains("b")) retVal--;
        return retVal;
    }
    static int getKeySignature(String noteName) {
        int retVal = 0;
        if (noteName.equals("Fb")) retVal = -8;
        else if (noteName.equals("Cb")) retVal = -7;
        else if (noteName.equals("Gb")) retVal = -6;
        else if (noteName.equals("Db")) retVal = -5;
        else if (noteName.equals("Ab")) retVal = -4;
        else if (noteName.equals("Eb")) retVal = -3;
        else if (noteName.equals("Bb")) retVal = -2;
        else if (noteName.equals("F")) retVal = -1;
        else if (noteName.equals("C")) retVal = 0;
        else if (noteName.equals("G")) retVal = 1;
        else if (noteName.equals("D")) retVal = 2;
        else if (noteName.equals("A")) retVal = 3;
        else if (noteName.equals("E")) retVal = 4;
        else if (noteName.equals("B")) retVal = 5;
        else if (noteName.equals("C#")) retVal = 6;
        else if (noteName.equals("G#")) retVal = 7;
        else if (noteName.equals("D#")) retVal = 8;
        else if (noteName.equals("A#")) retVal = 9;
        else if (noteName.equals("E#")) retVal = 10;
        else if (noteName.equals("B#")) retVal = 11;
        return retVal;
    }

    static int getBeatType(MarkupParser.BeatTypeContext chordTypeContext) {
        int retVal = 0;
        if (chordTypeContext.HALFNOTE() != null) retVal = 2;
        else if (chordTypeContext.QUARTER() != null) retVal = 4;
        else if (chordTypeContext.EIGHTH() != null) retVal = 8;
        return retVal;
    }
    static int getBeats(MarkupParser.BeatsContext beats) {
        return Integer.parseInt(beats.DIGITS().getText());
    }


    static Note[] buildTrebleChord(int root, double duration, String chordType) {
        Note[] chord;
        me.tomassetti.examples.MarkupParser.Voicing defaultVoicing = findDefaultVoicing(chordType);

        if(voicing.isSet() ){
            chord = setChordVoicing(root,voicing,duration,chordType);
        }
        else if(defaultVoicing != null){
            chord = setChordVoicing(root,defaultVoicing,duration,chordType);
        }
        else{
            //podstawowy voicing 1 3 5 7
            chord = new Note[4];
            for(int i=0;i<chord.length;i++)
                chord[i] = new Note(root+scale.getDegree(2*i+1),duration);
        }
        chord = normalizeTrebleChord(chord);
        return chord;
    }

    private static Note[] setChordVoicing(int root, me.tomassetti.examples.MarkupParser.Voicing voicing, double duration, String chordType) {
        Note[] chord = new Note[voicing.size()];
//        System.out.print(getNoteName(root)+chordType);
//        System.out.print(" V" + voicing.toString());
//        System.out.print(" S" + scale.toString());
        for(int i=0;i<chord.length;i++){
            int nextNotePitch = root + scale.getDegree(voicing.getDegree(i));
//            System.out.print(" "+getNoteName(nextNotePitch));
            while(i>0 && chord[i-1].getPitch()>=nextNotePitch)
                nextNotePitch += 12;
            chord[i] = new Note(nextNotePitch,duration);
        }
        return chord;
    }

    private static me.tomassetti.examples.MarkupParser.Voicing findDefaultVoicing(String chordType) {
        return defaultVoicings.get(chordType);
    }

    private static Note[] normalizeTrebleChord(Note[] chord) {
        while(chord[chord.length-1].getPitch() > Pitches.E5)
            for (int i = 0; i < chord.length; i++)
                chord[i].setPitch(chord[i].getPitch() - 12);
        //ważniejsze jest, aby akord nie przekraczał dolnej granicy,
        //więc ostatecznie i tak zostanie podciągnięty, aby jej nie przekraczać z dołu
        while(chord[0].getPitch() < Pitches.F3)
            for (int i = 0; i < chord.length; i++)
                chord[i].setPitch(chord[i].getPitch() + 12);
        return chord;
    }

    static Note[] buildBassChord(int root, double duration) {
        Note[] chord = new Note[1];
        chord[0] = new Note(root-24,duration);
        return chord;
    }

    static void setScale(MarkupParser.ChordTypeContext context) {
        //ustawiam skalę dla danego typu akordu
        // na przykład 7#11b13    [0,0,0, 1,0,-1,0]
        //                        [1,9,3,11,5,13,7]
        // default scale: mixolydian
        scale = new me.tomassetti.examples.MarkupParser.Scale();
        if (context.AddNine()!=null)                            ;
        else if (context.Augmented()!=null)                     scale.sharp(5);
        else if (context.Diminished()!=null)                    scale.flat(new int[]{3, 5});
        else if (context.DiminishedMajorSeven()!=null)          scale.flat(new int[]{3, 5});
        else if (context.DiminishedSeven()!=null)               scale.flat(new int[]{3, 5});
        else if (context.Five()!=null)                          ;
        else if (context.Four()!=null)                          ;
        else if (context.HalfDiminished()!=null)                scale.sharpflat(11, new int[]{9, 3});
        else if (context.Major()!=null)                         scale.sharp(7);
        else if (context.MajorAddNine()!=null)                  scale.sharp(7);
        else if (context.MajorFlatFive()!=null)                 scale.sharpflat(7, 5);
        else if (context.MajorNine()!=null)                     scale.sharp(7);
        else if (context.MajorNineFlatFive()!=null)             scale.sharpflat(7,5);
        else if (context.MajorNineSharpEleven()!=null)          scale.sharp(new int[]{7, 11});
        else if (context.MajorNineSharpFive()!=null)            scale.sharp(new int[]{7, 5});
        else if (context.MajorSeven()!=null)                    scale.sharp(7);
        else if (context.MajorSevenAddThirteen()!=null)         scale.sharp(7);
        else if (context.MajorSevenAugmented()!=null)           scale.sharp(new int[]{5, 7});
        else if (context.MajorSevenFlatFive()!=null)            scale.sharpflat(7,5);
        else if (context.MajorSevenFlatNine()!=null)            scale.sharpflat(7,9);
        else if (context.MajorSevenSharpEleven()!=null)         scale.sharp(new int[]{7, 11});
        else if (context.MajorSevenSharpFive()!=null)           scale.sharp(new int[]{5, 7});
        else if (context.MajorSharpFive()!=null)                scale.sharp(new int[]{5, 7});
        else if (context.MajorSix()!=null)                      scale.sharp(7);
        else if (context.MajorSixNine()!=null)                  scale.sharp(7);
        else if (context.MajorSusFour()!=null)                  scale.sharp(3);
        else if (context.MajorSusTwo()!=null)                   scale.sharpflat(7, new int[]{3, 3});
        else if (context.MajorThirteen()!=null)                 scale.sharp(7);
        else if (context.MajorThirteenSharpEleven()!=null)      scale.sharp(new int[]{7, 11});
        else if (context.Minor()!=null)                         scale.flat(new int[]{3, 6});
        else if (context.MinorEleven()!=null)                   scale.flat(new int[]{3, 6});
        else if (context.MinorElevenFlatFive()!=null)           scale.flat(new int[]{3, 5,6});
        else if (context.MinorMajorNine()!=null)                scale.sharpflat(7,new int[]{3, 6});
        else if (context.MinorMajorSeven()!=null)               scale.sharpflat(7,new int[]{3, 6});
        else if (context.MinorNine()!=null)                     scale.flat(new int[]{3, 6});
        else if (context.MinorNineFlatFive()!=null)             scale.flat(new int[]{3, 5,6});
        else if (context.MinorSeven()!=null)                    scale.flat(new int[]{3, 6});
        else if (context.MinorSevenFlatFive()!=null)            scale.flat(new int[]{3, 5,6});
        else if (context.MinorSharpFive()!=null)                scale.sharpflat(5,new int[]{3, 6});
        else if (context.MinorSix()!=null)                      scale.flat(3);
        else if (context.MinorSixNine()!=null)                  scale.flat(3);
        else if (context.MinorThirteen()!=null)                 scale.flat(3);
        else if (context.Nine()!=null)                          ;
        else if (context.NineAugmented()!=null)                 scale.sharp(5);
        else if (context.NineFlatFive()!=null)                  scale.flat(5);
        else if (context.NineSharpEleven()!=null)               scale.sharp(11);
        else if (context.NineSharpFive()!=null)                 scale.sharp(5);
        else if (context.NineSharpFiveSharpEleven()!=null)      scale.sharp(new int[]{11, 5});
        else if (context.NineSusFour()!=null)                   scale.sharp(3);
        else if (context.NineSusTwo()!=null)                    scale.flat(new int[]{3, 3});
        else if (context.Seven()!=null)                         ;
        else if (context.SevenAltered()!=null)                  scale.sharp(new int[]{5, 9});
        else if (context.SevenAugmented()!=null)                scale.sharp(5);
        else if (context.SevenFlatFive()!=null)                 scale.flat(5);
        else if (context.SevenFlatFiveFlatNine()!=null)         scale.flat(new int[]{5, 9});
        else if (context.SevenFlatNine()!=null)                 scale.flat(9);
        else if (context.SevenFlatNineFlatThirteen()!=null)     scale.flat(new int[]{9, 13});
        else if (context.SevenFlatNineFlatThirteenSharpEleven()!=null)
                                                                scale.sharpflat(11, new int[]{9, 13});
        else if (context.SevenFlatNineSharpEleven()!=null)      scale.sharpflat(11,9);
        else if (context.SevenFlatNineSusFour()!=null)          scale.sharpflat(3,9);
        else if (context.SevenSharpEleven()!=null)              scale.sharp(11);
        else if (context.SevenSharpFive()!=null)                scale.sharp(5);
        else if (context.SevenSharpFiveFlatNine()!=null)        scale.sharpflat(5,9);
        else if (context.SevenSharpFiveSharpNine()!=null)       scale.sharp(5);
        else if (context.SevenSharpNine()!=null)                scale.sharp(9);
        else if (context.SevenSusFour()!=null)                  scale.sharp(3);
        else if (context.SevenSusFourFlatNine()!=null)          scale.sharpflat(3,9);
        else if (context.SevenSusTwo()!=null)                   scale.flat(new int[]{3, 3});
        else if (context.Six()!=null)                           scale.sharp(7);
        else if (context.SixNine()!=null)                       scale.sharp(7);
        else if (context.SusFour()!=null)                       scale.sharp(3);
        else if (context.SusTwo()!=null)                        scale.flat(new int[]{3, 3});
        else if (context.Thirteen()!=null)                      ;
        else if (context.ThirteenFlatFive()!=null)              scale.flat(5);
        else if (context.ThirteenFlatNine()!=null)              scale.flat(9);
        else if (context.ThirteenFlatNineSharpEleven()!=null)   scale.sharpflat(11,9);
        else if (context.ThirteenSharpEleven()!=null)           scale.sharp(11);
        else if (context.ThirteenSharpNine()!=null)             scale.sharp(9);
        else if (context.ThirteenSusFour()!=null)               scale.sharp(3);
        else if (context.ThirteenSusTwo()!=null)                scale.flat(new int[]{3, 3});
        else if (context.Two()!=null)                           scale.sharp(7);
    }
    static void setVoicing(MarkupParser.VoicingRuleContext voicingRuleContext) {

        if(voicingRuleContext == null || voicingRuleContext.degree().isEmpty())
            return;
        voicing.create(voicingRuleContext);
    }

    static void createVoicingRule(MarkupParser.VoicingRuleContext voicingRuleContext) {
        String chordType = getChordType(voicingRuleContext.chordType());
        me.tomassetti.examples.MarkupParser.Voicing defaultVoicing = new me.tomassetti.examples.MarkupParser.Voicing();
        defaultVoicing.create(voicingRuleContext);
        defaultVoicings.put(chordType,defaultVoicing);
    }

    private static String getChordType(MarkupParser.ChordTypeContext context) {
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
        return result.toString();
    }

    public static void clearVoicing() {
        voicing.clear();
    }
    private static String getNoteName(int note) {
        String[] array  = {"C", "C#", "D", "D#", "E ", "F", "F#", "G ", "G#", "A", " A#", "B"};
        Vector<String> notes = new Vector(Arrays.asList(array));
        return notes.get(note%12);
    }
}

