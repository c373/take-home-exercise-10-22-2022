package com.test;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // loads a file line by line into a List<String>
    private static List<String> LoadDataFromFile(String path) {
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
    private static List<List<String>> CleanAndFormatData(List<String> data) {
        List<List<String>> cleanedData = new ArrayList<>(data.size());

        // initialize the multidimensional List
        for (int i = 0; i < data.size(); i++) {
            cleanedData.add(new ArrayList<>());
        }

        // use regex to parse each line into substrings
        // everything between two corresponding double quotes will be matched
        Pattern rgxPttrn = Pattern.compile("\"(.*?)\"");
        Matcher matcher;

        var iter = cleanedData.iterator();
        List<String> next;

        // for each line (datum) of data
        for (String datum : data) {
            // get the next List<String> in cleanedData
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

        return cleanedData;
    }

    public static void main(String[] args) {

        // will store path to the input file
        String path;

        // a dynamically sized collection of strings where the raw data will be stored line by line
        List<String> data;

        // first check if the input file was provided as an argument
        // if not then try and load the default test data in 'test.data'
        if (args.length < 1)
            path = "./test.data";
        else
            path = args[0];

        try {
            // attempt to load data
            data = LoadDataFromFile(path);
        } catch (Exception e) {
            return;
        }

        System.out.printf("Loaded file: %s%n%n", path);
        for (String line : data) {
            System.out.println(line);
        }

        System.out.println("\nCleaning up the file...\n");


        // store list of all persons
        List<Person> allPeeps = new ArrayList<>();

        for (List<String> list : CleanAndFormatData(data)) {
            allPeeps.add(new Person(list));
        }

        // a hashmap here makes the most sense at scale for speed
        // alternatively a naive approach using arrays would require traversing the entire array each time a new person
        // is added to verify that the address doesn't already exist
        // store list of persons hashed by address; quick and cheap
        HashMap<String, ArrayList<Person>> addressDb = new HashMap<>();

        for (Person peep : allPeeps) {
            String addy = peep.getAddress();

            addressDb.computeIfAbsent(addy, k -> new ArrayList<>());
            addressDb.get(addy).add(peep);
        }

        System.out.println("Each household and number of occupants:");
        System.out.printf("-----------------------------------------------------------------%n");
        System.out.printf("  %-40s %s%n", "Households:", "Occupants:");
        System.out.printf("-----------------------------------------------------------------%n");
        for (var k : addressDb.keySet()) {
            System.out.printf("  %-40s %-4d%n", k, addressDb.get(k).size());
        }

        System.out.println("\nAll persons sorted by lastname then firstname that are older than 18:");
        System.out.printf("--------------------------------------------------------------------------------%n");
        System.out.printf("  %-15s %-15s %-40s %-3s%n", "Firstname", "Lastname", "Address", "Age");
        System.out.printf("--------------------------------------------------------------------------------%n");

        allPeeps.sort(Comparator.comparing(Person::getLastName).thenComparing(Person::getFirstName));
        // what??? ^^^ that was absolute cake! I thought I was gonna have to implement my own custom comparator

        for ( Person peep : allPeeps) {
            if(peep.getAge() > 18) {
                System.out.printf("  %-15s %-15s %-40s %-3d%n", peep.getFirstName(), peep.getLastName(), peep.getAddress(), peep.getAge());
            }
        }
    }
}