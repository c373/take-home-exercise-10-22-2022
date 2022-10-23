package com.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatting {

    // loads a file line by line into a List<String>
    public static List<String> LoadDataFromFile(String path) {
        List<String> data = new ArrayList<>();
        File testData = new File(path);

        try {
            Scanner scnr = new Scanner(testData);

            while (scnr.hasNextLine()) {
                String d = scnr.nextLine();
                if (d.length() > 0) {
                    data.add(d);
                }
            }

            scnr.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        return data;
    }

    // the meat and potatoes of this lil program
    public static List<List<String>> CleanAndFormatData(List<String> data) {
        List<List<String>> cleanData = new ArrayList<>(data.size());

        // initialize the multidimensional List
        for (int i = 0; i < data.size(); i++) {
            cleanData.add(new ArrayList<>());
        }

        // use regex to parse each line into substrings
        // everything between two corresponding double quotes will be matched
        Pattern rgxPttrn = Pattern.compile("\"(.*?)\"");
        Matcher matcher;

        var iter = cleanData.iterator();
        List<String> next;

        // for each line (datum) of data
        for (String datum : data) {
            // get the next List<String> in cleanData
            next = iter.next();

            // match datum against the regex
            matcher = rgxPttrn.matcher(datum);

            // find each match, clean it, and add it as a substring to the list
            while (matcher.find()) {
                next.add(
                        datum
                                .substring(matcher.start(), matcher.end())
                                .replaceAll("\"*,*\"", "")
                                .replaceAll("\\.*,*", "")
                                .toUpperCase()
                                .trim()
                );
            }
        }

        return cleanData;
    }
}
