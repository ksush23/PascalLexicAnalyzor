package com.company;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static char[] limiters = {',', '.', '(', ')', '[', ']', ':', ';', '@'};
    static String[] reservedWords = {"program", "var", "real", "integer", "begin", "for", "downto", "do", "begin", "end",
                                    "if", "then", "else", "case", "for", "while", "repeat", "until", "with", "goto", "label",
                                    "read", "write", "rewrite", "close", "reset", "writeln", "readln", "procedure", "to"};
    static String[] reservedOperators = {":=", "<", ">", "<=", ">=", "=", "not", "mod", "div", "and", "or", "+", "-",
                                        "in", "<>", "*", "/"};
    static String temp = "";
    static int i = 0;
    static char[] chars;
    static int type = 0;
    static List<String> tableR = new ArrayList<>();
    static List<String> LConv = new ArrayList<>();
    static List<String> tableI = new ArrayList<>();
    static List<String> tableC = new ArrayList<>();
    static List<String> tableL = new ArrayList<>();
    static List<String> tableO = new ArrayList<>();
    static List<String> tableComments = new ArrayList<>();

    public static void main(String[] args) throws java.io.FileNotFoundException {
        FileReader fileReader = new FileReader("input");
        Scanner scanner = new Scanner(fileReader);
        String allProgram = "";
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            int index = nextLine.indexOf("//");
            if (index != -1){
                String comment = nextLine.substring(index);
                tableComments.add(comment);
                nextLine = nextLine.replace(comment, "");
            }
            allProgram += nextLine;
        }

        chars = allProgram.toCharArray();
        type = 0;
        try {
            for (i = 0; i < allProgram.length(); i++) {
                if (chars[i] == '\'') {
                    if (temp == null) {
                        temp = "";
                    }
                    temp += '\'';
                    i++;
                    while (chars[i] != '\'') {
                        temp += chars[i];
                        i++;
                    }
                    temp += '\'';
                    type = 2;
                    Result(temp);
                    temp = null;
                }
                if (chars[i] == '{') {
                    int chet = 1;
                    while (chet != 0) {
                        i++;
                        if (chars[i] == '{')
                            chet++;
                        if (chars[i] == '}')
                            chet--;
                    }
                }
                Analysis(chars[i]);
            }

            System.out.println("Numbers:");
            for (String s : tableC
            ) {
                System.out.println(s);
            }
            System.out.println("Identifiers:");
            for (String s : tableI
            ) {
                System.out.println(s);
            }
            System.out.println("Keywords:");
            for (String s : tableR
            ) {
                System.out.println(s);
            }
            System.out.println("Separating characters:");
            for (String s : tableL
            ) {
                System.out.println(s);
            }
            System.out.println("Operators: ");
            for (String s: tableO
                 ) {
                System.out.println(s);
            }
            System.out.println("Comments: ");
            for (String s: tableComments
                 ) {
                System.out.println(s);
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println("Unequal number of parentheses");
        }
    }

    static int tryParse(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    static void Analysis(char nextChar) {
        int acsiiCode = (int) nextChar; // Получаем код символа
        // Проверяем относится ли код символа к буквам английского алфавита и знаку нижнего подчеркивания
        if (((acsiiCode >= 65) && (acsiiCode <= 90)) || ((acsiiCode >= 97) && (acsiiCode <= 122)) || (acsiiCode == 95)) {
            if (temp == null) {
                temp = "";
                type = 1;
            }
            temp += nextChar;
            return;
        }
        // Проверяем относится ли код символа к цифрам или к точке
        if (((acsiiCode >= 48) && (acsiiCode <= 57)) || (acsiiCode == 46) || (acsiiCode == 36) || (acsiiCode == 37)) {
            // Отдельная проверка, если код символа относиться к точке проверяем, чтобы в переменной temp     // было число. Если в temp число, значит теперь
            // мы считаем его дробным, в противном случае это что-то другое
            boolean ok = true;
            if (acsiiCode == 46) {
                int out_r = 0;
                if (tryParse(temp, out_r) == 0) {
                    ok = false;
                }
            }

            if (ok) {
                if (temp == null) {
                    type = 2;
                    temp = "";
                }
                temp += nextChar;
                return;
            }
        }

        if ((nextChar == ' ' || nextChar == '\n') && temp != null) {
            Result(temp);
            temp = null;
            return;
        }

        // И последняя проверка на принадлежность к массиву разделителей. Не забываем о двойном разделителе «:=» и двойных условных разделителях.
        for (char c : limiters
        ) {
            if (nextChar == c) {
                if (temp != null)
                    Result(temp);
                type = 3;
                if (nextChar == ':' && chars[i + 1] == '=') {
                    temp = Character.toString(nextChar) + chars[i + 1];
                    Result(temp);
                    temp = null;
                    return;
                }
                if (nextChar == '<' && (chars[i + 1] == '>' || chars[i + 1] == '=')) {
                    temp = Character.toString(nextChar) + chars[i + 1];
                    Result(temp);
                    temp = null;
                    return;
                }
                if (nextChar == '>' && chars[i + 1] == '=') {
                    temp = Character.toString(nextChar) + chars[i + 1];
                    Result(temp);
                    temp = null;
                    return;
                }
                temp = Character.toString(nextChar);
                Result(temp);
                temp = null;
                return;
            }
        }

        for (int j = 0; j < reservedOperators.length; j++) {
            if (Character.toString(nextChar).equals(reservedOperators[j])) {
                for (int i = 0; i < tableO.size(); i++) {
                    if (Character.toString(nextChar).equals((tableO.get(i)))) {
                        LConv.add("3" + i);
                        return;
                    }
                }
                tableO.add(Character.toString(nextChar));
                LConv.add("3" + (tableR.size() - 1));
                return;
            }
        }
    }

    static void Result(String t) {
        for (int j = 0; j < reservedWords.length; j++) {
            if (t.equals(reservedWords[j])) {
                for (int i = 0; i < tableR.size(); i++) {
                    if (t.equals(tableR.get(i))) {
                        LConv.add("3" + i);
                        return;
                    }
                }
                tableR.add(t);
                LConv.add("3" + (tableR.size() - 1));
                return;
            }
        }
        for (int j = 0; j < reservedOperators.length; j++) {
            if (t.equals(reservedOperators[j])) {
                for (int i = 0; i < tableO.size(); i++) {
                    if (t.equals((tableO.get(i)))) {
                        LConv.add("3" + i);
                        return;
                    }
                }
                tableO.add(t);
                LConv.add("3" + (tableR.size() - 1));
                return;
            }
        }
        switch (type) {
            case 1:
                for (int j = 0; j < tableI.size(); j++) {
                    if (t.equals(tableI.get(j))) {
                        LConv.add("1" + j);
                        return;
                    }
                }
                tableI.add(t);
                LConv.add("1" + (tableI.size() - 1));
                break;
            case 2:
                for (int j = 0; j < tableC.size(); j++) {
                    if (t.equals(tableC.get(j))) {
                        LConv.add("2" + j);
                        return;
                    }
                }
                tableC.add(t);
                LConv.add("2" + (tableC.size() - 1));
                break;
            case 3:
                for (int j = 0; j < tableL.size(); j++) {
                    if (t.equals(tableL.get(j))) {
                        LConv.add("4" + j);
                        return;
                    }
                }
                tableL.add(t);
                LConv.add("4" + (tableL.size() - 1));
                break;
        }
    }
}

