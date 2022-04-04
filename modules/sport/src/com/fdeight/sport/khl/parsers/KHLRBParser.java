package com.fdeight.sport.khl.parsers;

import com.fdeight.sport.khl.data.KHLMatchInfo;
import com.fdeight.sport.khl.data.KHLStorage;
import com.fdeight.sport.parsers.TxtParser;
import com.fdeight.sport.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KHLRBParser extends TxtParser {
    private static final String MATCH_LINE_MAIN =
            "(\\d{2}/\\d{2}/\\d{2})[\\s\\t]+(.*) - (.*)[\\s\\t]+(\\d+):(\\d+)[\\s\\t]+";
    private static final String MATCH_LINE_3_PERIODS = "(\\d+):(\\d+)[\\s\\t]+(\\d+):(\\d+)[\\s\\t]+(\\d+):(\\d+)";
    private static final Pattern MATCH_LINE_ADDITIONAL_PERIOD = Pattern.compile(
            "[\\s\\t]+(\\d+):(\\d+)");
    private static final Pattern[] MATCH_LINES = {
            Pattern.compile(MATCH_LINE_MAIN + "\\(" + MATCH_LINE_3_PERIODS + "\\)"),
            Pattern.compile(MATCH_LINE_MAIN + "\\(" + MATCH_LINE_3_PERIODS + MATCH_LINE_ADDITIONAL_PERIOD + "\\)"),
            Pattern.compile(MATCH_LINE_MAIN + "\\(" + MATCH_LINE_3_PERIODS + MATCH_LINE_ADDITIONAL_PERIOD
                            + MATCH_LINE_ADDITIONAL_PERIOD + "\\)"),
    };

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

    public enum ParseType {
        UNDEFINED(KHLMatchInfo.Type.UNDEFINED, Pattern.compile("UNDEFINED")),
        REGULAR(KHLMatchInfo.Type.REGULAR, Pattern.compile(".*Регулярный\\s+чемпионат.*")),
        FINAL(KHLMatchInfo.Type.FINAL, Pattern.compile(".*Финал.*")),
        SEMIFINAL(KHLMatchInfo.Type.SEMIFINAL, Pattern.compile(".*Полуфинал.*")),
        QUARTERFINAL(KHLMatchInfo.Type.QUARTERFINAL, Pattern.compile(".*Четвертьфинал.*")),
        EIGHTFINAL(KHLMatchInfo.Type.EIGHTFINAL, Pattern.compile(".*1/8\\s+финал.*")),
        //
        ;

        public final KHLMatchInfo.Type type;
        public final Pattern pattern;

        ParseType(final KHLMatchInfo.Type type, final Pattern pattern) {
            this.type = type;
            this.pattern = pattern;
        }
    }

    private final KHLStorage storage;

    private KHLMatchInfo.Type type;

    public KHLRBParser(final KHLStorage storage) {
        this.storage = storage;
        type = KHLMatchInfo.Type.UNDEFINED;
    }

    @Override
    protected void processLine(final int lineNumber, final String line) throws ParseException {
        System.out.println(line);
        for (final ParseType parseType : ParseType.values()) {
            final Matcher typeMatcher = parseType.pattern.matcher(line);
            if (typeMatcher.matches()) {
                type = parseType.type;
                return;
            }
        }
        Matcher matcher = null;
        int scorePeriodsLength = -1;
        for (int i = 0; i < MATCH_LINES.length; i++) {
            matcher = MATCH_LINES[i].matcher(line);
            if (matcher.matches()) {
                scorePeriodsLength = 3 + i;
                break;
            }
        }
        if (scorePeriodsLength < 0) {
            System.out.println(String.format("Unparsed: [%s]", line));
            return;
        }
        Utils.checkInterval(scorePeriodsLength, 3, 5, () -> "Score periods length");
        final Date date = dateFormat.parse(matcher.group(1));
        final KHLMatchInfo.Score score = createScore(matcher.group(4), matcher.group(5));
        final KHLMatchInfo.Score[] scorePeriods = new KHLMatchInfo.Score[scorePeriodsLength];
        for (int i = 0; i < scorePeriodsLength; i++) {
            scorePeriods[i] = createScore(matcher.group(6 + i * 2), matcher.group(7 + i * 2));
        }
        final KHLMatchInfo info = new KHLMatchInfo(date, lineNumber, type, matcher.group(2), matcher.group(3),
                score, scorePeriods);
        storage.add(info);
    }

    private KHLMatchInfo.Score createScore(final String strFirst, final String strSecond) {
        final int scoreFirst = Integer.parseInt(strFirst);
        final int scoreSecond = Integer.parseInt(strSecond);
        return new KHLMatchInfo.Score(scoreFirst, scoreSecond);
    }
}
