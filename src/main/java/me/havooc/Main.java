package me.havooc;

import me.havooc.resources.Block;
import me.havooc.resources.BlockCollideException;
import me.havooc.resources.Day;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        System.out.println("Dla ilu grup chcesz porównać plany?");
        Scanner scanner = new Scanner(System.in);
        int groupsSize;
        try {
            groupsSize = Integer.parseInt(scanner.next());
            //groupsSize = 5;
        }catch (NumberFormatException e) {
            System.out.println("Wprowadzona wartość nie jest liczbą.");
            return;
        }
        System.out.println("Wprowadź nazwy " + groupsSize + " grup: ");
        String[] groups = new String[groupsSize];
        for (int i = 0; i < groupsSize; i++) {
            String groupCode = scanner.next();
            //String groupCode = "WCY22IY"+(i+1)+"S1";
            if (!groupCode.matches("WCY22IY[1-5]S1")) {
                System.out.println("Wprowadzona nazwa grupy jest błędna, spróbuj ponownie");
                i--;
            }
            else
                groups[i] = groupCode;
        }
        try {
            Document[] docs = new Document[groupsSize];
            for (int i = 0; i < groupsSize; i++) {
                System.out.println("Pobieranie planu zajęć " + groups[i]);
                docs[i] = Jsoup.connect("https://wcy.wat.edu.pl/pl/rozklad?grupa_id=" + groups[i]).get();
                System.out.println("Pobrano plan " + groups[i]);
            }
            System.out.println("Wprowadź szukaną datę w formacie YYYY_MM_DD. Uwaga - dla DD<10 i MM<10 dodaj 0, np 2022_05_03: ");
            String date = scanner.next();
            //String date = "2022_12_20";
            if (!date.matches("2022_(0[1-9]|1[0-2])_(0[1-9]|[1-2][0-9]|3[0-1])")) {
                System.out.println("Wprowadzona data jest nieprawidłowa.");
                return;
            }
            Day[] days = new Day[groupsSize];
            for (int i = 0; i < groupsSize; i++) {
                System.out.println("Wczytywanie planu " + groups[i] + " dla daty " + date);
                Element lessonsHtml = docs[i].select("div.lessons").first();
                String[] lessons = getLessonsOfTheDay(getLessons(lessonsHtml.toString()), date);
                Block[] blocks = new Block[lessons.length];
                for (int j = 0; j < lessons.length; j++) {
                    blocks[j] = new Block(lessons[j]);
                }
                days[i] = new Day(blocks, groups[i]);
                System.out.println("Wczytano plan " + groups[i]);
            }
            System.out.println("Co chcesz zrobić?\n1. Sprawdź, czy plany nie kolidują ze sobą przez cały dzień\n2. Sprawdź konkretny numer bloku\nWybór: ");
            int choice;
            do {
                try {
                    choice = Integer.parseInt(scanner.next());
                }catch (NumberFormatException e) {
                    choice = -1;
                }
                if (choice < 1 || choice > 2)
                    System.out.println("Wprowadzona wartość jest nieprawidłowa, oczekuje 1 lub 2, otrzymano " + choice);
            }
            while (choice < 1 || choice > 2);
            if(choice==1) {
                try {
                    Day.isColliding(days);
                    System.out.println("Nie znaleziono kolizji planów!");
                } catch (BlockCollideException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                System.out.println("Wprowadź numer bloku, który chcesz sprawdzić: ");
                do {
                    try {
                        choice = Integer.parseInt(scanner.next());
                    }catch (NumberFormatException e) {
                        choice = -1;
                    }
                    if (choice < 1 || choice > 7)
                        System.out.println("Wprowadzona wartość jest nieprawidłowa, oczekuje 1-7, otrzymano " + choice);
                }
                while (choice < 1 || choice > 7);
                try {
                    Day.isColliding(days,choice);
                    System.out.println("Nie znaleziono kolizji planów!");
                } catch (BlockCollideException e) {
                    System.out.println(e.getMessage());
                }            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String[] getLessons(String input) {
        Pattern pattern = Pattern.compile("<div class=\"lesson\">([\\s\\S]*?)</div>");
        Matcher matcher = pattern.matcher(input);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list.toArray(new String[0]);
    }
    private static String[] getLessonsOfTheDay(String[] lessons, String date) {
        List<String> result = new ArrayList<>();
        for (String lesson : lessons)
            if (lesson.contains(date))
                result.add(lesson);
        return result.toArray(new String[0]);
    }
}