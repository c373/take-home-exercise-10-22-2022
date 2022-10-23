package com.test;

import java.util.List;

public class Person {
    private static class Address {
       private final String streetAddress;
       private final String city;
       private final String state;

       public Address(String streetAddress, String city, String state) {
           this.streetAddress = streetAddress;
           this.city = city;
           this.state = state;
       }

        @Override
        public String toString() {
            return this.streetAddress + " " + this.city + " " + this.state;
        }

        // TODO : getter methods
    }

    private final String firstName;
    private final String lastName;
    private final Address address;
    private final int age;

    public Person(List<String> rawData) {
        try {
            var iter = rawData.iterator();
            this.firstName = iter.next();
            this.lastName = iter.next();
            this.address = new Address(iter.next(), iter.next(), iter.next());
            this.age = Integer.parseInt(iter.next());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Malformed input to Record()");
        }
    }

    public Person(String first, String last, String street, String city, String state, int age) {
        this.firstName = first;
        this.lastName = last;
        this.address = new Address(street, city, state);
        this.age = age;
    }

    @Override
    public String toString() {
        final String delim = ", ";
        return this.firstName + delim + this.lastName + delim + this.address.toString() + delim + this.age;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getAddress() {
        return this.address.toString();
    }

    public int getAge() {
        return this.age;
    }
}
