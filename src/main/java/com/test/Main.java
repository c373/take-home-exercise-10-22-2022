package com.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    static String path;         // will store path to the input file

    public static void ListToHashmap(List<Person> personList, HashMap<String, List<Person>> personHash) {

        for (Person peep : personList) {
            String addy = peep.getAddress();

            personHash.computeIfAbsent(addy, k -> new ArrayList<>());
            personHash.get(addy).add(peep);
        }
    }

     @FunctionalInterface
     interface BinaryOperator<T,FileWriter> {
        void writeData(T data, FileWriter writer) throws IOException;
     }

    public static <T> void WriteToCSVFile(String header, T collection, BinaryOperator<T, FileWriter> operator) {
        // The rest is messy code to output data to csv files and the console
        File output = new File(header.replaceAll(",", "-").toLowerCase() + "-" + new Date().getTime() + ".csv");
        try {
            if(output.createNewFile()) {
                try (FileWriter writer = new FileWriter(output.getPath())) {
                    writer.write(header + "\n");
                    operator.writeData(collection, writer);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                throw new RuntimeException("Error creating output file.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        // store list of all persons
        List<Person> allPeeps = new ArrayList<>();

        // store list of persons hashed by address
        HashMap<String, List<Person>> addressDb = new HashMap<>();
        // a hashmap here makes the most sense at scale for speed
        // alternatively a naive approach using arrays would require traversing the entire array each time a new person
        // is added to verify that the address doesn't already exist

        {
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
                data = Formatting.LoadDataFromFile(path);
            } catch (Exception e) {
                return;
            }

            System.out.printf("Loaded file: %s%n%n", path);
            for (String line : data) {
                System.out.println(line);
            }

            System.out.println("\nCleaning up the file...\n");

            for (List<String> list : Formatting.CleanAndFormatData(data)) {
                allPeeps.add(new Person(list));
            }
        }

        ListToHashmap(allPeeps, addressDb);

        allPeeps.sort(Comparator.comparing(Person::getLastName).thenComparing(Person::getFirstName));
        // what??? ^^^ that was quick! I thought I was gonna have to implement my own custom comparator

        // write addressDb to a csv file
        // check out the lambda for specific implementation
        WriteToCSVFile("Household,Occupants", addressDb, (data, writer) -> {
            for (var k : data.keySet()) {
                writer.write(k + "," + addressDb.get(k).size() + "\n");
            }
        });

        // write allPeeps to a csv file
        // check out the lambda for specific implementation
        WriteToCSVFile("FirstName,LastName,Address,Age", allPeeps, (data, writer) -> {
            for (Person peep : data) {
                if (peep.getAge() > 18) {
                    writer.write(peep.getFirstName() + "," + peep.getLastName() + "," + peep.getAddress() + "," + peep.getAge() + "\n");
                }
            }
        });

        // Finally pretty print to the terminal
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
        for (Person peep : allPeeps) {
            if(peep.getAge() > 18) {
                System.out.printf("  %-15s %-15s %-40s %-3d%n", peep.getFirstName(), peep.getLastName(), peep.getAddress(), peep.getAge());
            }
        }
    }
}