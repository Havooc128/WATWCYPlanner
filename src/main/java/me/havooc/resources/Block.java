package me.havooc.resources;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static me.havooc.resources.Utils.*;

public class Block {

    public static final int BLOCK_ID=0;
    public static final int BLOCK_NAME=1;
    public static final int BLOCK_SHORT_NAME=2;
    public static final int BLOCK_TEACHER_CODE =3;
    public static final int BLOCK_ROOM=4;
    public static final int BLOCK_COLOR=5;
    public static final int BLOCK_TYPE=6;
    public static final int BLOCK_DATE=7;
    private final String shortName;
    private final Date date;
    private final int id;
    private final String name;
    private final String teacherCode;
    private final String room;
    private final Color color;
    private final String type;

    public Block(int id, String date) {
        this(id, date, null, null, null, null, null, null);
    }

    public Block(String lesson) {
        this(
                Integer.parseInt(Objects.requireNonNull(getTextFromLesson(lesson, BLOCK_ID))), /*id*/
                getTextFromLesson(lesson,BLOCK_DATE), /*date*/
                getTextFromLesson(lesson,BLOCK_NAME), /*name*/
                getTextFromLesson(lesson,BLOCK_SHORT_NAME), /*shortName*/
                getTextFromLesson(lesson,BLOCK_TYPE), /*type*/
                getTextFromLesson(lesson,BLOCK_TEACHER_CODE), /*teacher_code*/
                getTextFromLesson(lesson,BLOCK_ROOM), /*room*/
                getTextFromLesson(lesson,BLOCK_COLOR) /*color*/
        );
    }

    public static String getDifference(Block block, Block block1) {
        if(!block.isEmpty() && block1.isEmpty())
            return "pierwsza grupa {"+ block.getType() + " " + block.getShortName() +" w sali " + block.getRoom() + " z " + block.getTeacherCode() + "}, druga grupa {brak zajęć}";
        if(block.isEmpty() && !block1.isEmpty())
            return "druga grupa {"+ block1.getType() + " " + block1.getShortName() +" w sali " + block1.getRoom() + " z " + block1.getTeacherCode() + "}, pierwsza grupa {brak zajęć}";
        String result = "";
        if(!block.getShortName().equals(block1.getShortName()))
            result += "Nazwy {" + block.getShortName() + " i " + block1.getShortName() + "} ";
        if(!block.getType().equals(block1.getType()))
            result += "Typy {" +block.getType() + " i " + block1.getType() + "} ";
        if(!block.getRoom().equals(block1.getRoom()))
            result += "Miejsca {" +block.getRoom() + " i " + block1.getRoom() + "} ";
        if(!block.getTeacherCode().equals(block1.getTeacherCode()))
            result += "Prowadzący {" +block.getTeacherCode() + " i " + block1.getTeacherCode() + "} ";
        if (result.isEmpty())
            result = "Brak różnic";
        return result.replaceAll("null", "brak_zajęć");
    }

    public boolean isEmpty() {
        return this.shortName == null && this.name == null && this.teacherCode == null && this.type == null && this.color == null && this.room == null;
    }

    public Block(int block_number, String date, String name, String shortName, String type, String info, String room, String color) {
        if (block_number < 1 || block_number > 7)
            throw new IndexOutOfBoundsException("Wprowadzony numer bloku jest nieprawidłowy (Oczekuje 1-7, otrzymano "+block_number+")");
        this.id = block_number;
        this.shortName = shortName;
        this.name = name;
        this.teacherCode = info;
        this.type = type;
        this.room = room;
        this.color = color == null ? null : Color.decode(color);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        try {
            this.date = sdf.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Wprowadzona data jest nieprawidłowa! Wymagany format YYYY_MM_DD, otrzymano " + date);
        }
    }

    /**
     * @param id Numer bloku
     * @return Godzinę rozpoczęcia i zakończenia bloku
     */
    public static String getHours(int id) {
        return switch (id) {
            case 1 -> "8:00 - 9:35";
            case 2 -> "9:50 - 11:25";
            case 3 -> "11:40 - 13:15";
            case 4 -> "13:30 - 15:05";
            case 5 -> "15:45 - 17:20";
            case 6 -> "17:35 - 19:10";
            case 7 -> "19:25 - 21:00";
            default -> null;
        };
    }

    public String toString() {
        return "[id="+getId()+",date="+getDate()+",shortName="+getShortName()+",name="+getName()+",teacherCode="+ getTeacherCode()+",type="+getType()+",room="+getRoom()+",color="+getColor()+"]";
    }

    public boolean equals(Object b) {
        if (!(b instanceof Block block))
            return false;
        if(this.isEmpty() && block.isEmpty())
            return true;
        if(this.isEmpty() && !block.isEmpty() || !this.isEmpty() && block.isEmpty())
            return false;
        return block.getDate().equals(this.getDate()) && block.getId() == this.getId()
                && block.getShortName().equals(this.getShortName()) && block.getRoom().equals(this.getRoom());
    }
    /**
     * @return Zwraca datę w formacie YYYY_MM_DD, kiedy odbywa się blok
     */
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        return sdf.format(date);
    }

    /**
     * @return Zwraca numer bloku
     */
    public int getId() {
        return id;
    }

    /**
     * @return Zwraca pełną nazwę bloku
     */
    public String getName() {
        return name;
    }

    /**
     * @return Zwraca skróconą nazwę bloku
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @return Zwraca informacje o bloku
     */
    public String getTeacherCode() {
        return teacherCode;
    }

    /**
     * @return Zwraca pomieszczenie i budynek, w którym odbywa się blok
     */
    public String getRoom() {
        return room;
    }

    /**
     * @return Zwraca kolor bloku
     */
    public Color getColor() {
        return color;
    }

    /**
    * @return Zwraca typ bloku, np. wykład, ćwiczenia
    */
    public String getType() {
        return type;
    }
}
