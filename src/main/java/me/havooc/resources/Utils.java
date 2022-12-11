package me.havooc.resources;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String getBlockToString(Block[] blocks) {
        String result = "";
        for (Block block : blocks)
            result += block.toString() + "\n";
        return result;
    }

    public static String getFromHtml(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static String getBlockType(String input) {
        return switch (input.toLowerCase()) {
            case "(w)" -> "wykład";
            case "(ć)" -> "ćwiczenia";
            case "(l)" -> "laboratorium";
            default -> null;
        };
    }

    public static String getTextFromLesson(String lesson, int tag) {
        Pattern pattern = Pattern.compile("(?<=<span[^>]*>)(.*?)(?=</span>)");
        Matcher matcher = pattern.matcher(lesson);
        int i=0;
        while(matcher.find()) {
            if (i==0 && tag == Block.BLOCK_DATE)
                return matcher.group();
            else if(i==1 && tag == Block.BLOCK_ID)
                return matcher.group().split("k")[1];
            else if(i==2 && (tag == Block.BLOCK_SHORT_NAME || tag == Block.BLOCK_TYPE || tag == Block.BLOCK_ROOM)) {
                String[] cut = matcher.group().split("<br>");
                if(cut.length < 3)
                    return "err";
                return switch (tag) {
                    case Block.BLOCK_SHORT_NAME -> cut[0];
                    case Block.BLOCK_TYPE -> getBlockType(cut[1]);
                    case Block.BLOCK_ROOM -> cut[2].replaceAll("[^a-zA-Z0-9 ]", "");
                    default -> "error";
                };
            }
            else if (i==3 && tag == Block.BLOCK_NAME)
                return matcher.group().split("-")[0].strip();
            else if (i==4 && tag == Block.BLOCK_COLOR)
                return matcher.group();
            else if (i==5 && tag == Block.BLOCK_TEACHER_CODE)
                return matcher.group();
            i++;
        }
        return "N/A";
    }
}
