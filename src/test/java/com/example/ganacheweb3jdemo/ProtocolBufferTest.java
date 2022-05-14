package com.example.ganacheweb3jdemo;

import com.example.ganacheweb3jdemo.protos.AddressBook;
import com.example.ganacheweb3jdemo.protos.AddressBookProtos;
import com.example.ganacheweb3jdemo.protos.Person;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Protocol Buffer Related Self-Study Test
 *
 * @author Roylic
 * @date 2022/4/24
 */
public class ProtocolBufferTest {

    @Test
    public void createInstance () throws IOException {
        // build inner class
        Person john = Person.newBuilder()
                .setId(1234)
                .setName("John Doe")
                .setEmail("jdoe@example.com")
                .addPhones(
                        Person.PhoneNumber.newBuilder()
                                .setNumber("555-4321")
                                .setType(Person.PhoneType.HOME)
                ).build();

        // build outer class
        AddressBook addressBook = AddressBook.newBuilder().addPeople(john).build();

        // flush out to file
        FileOutputStream fos = new FileOutputStream("addressBook_PersonJohn.proto");
        addressBook.writeTo(fos);

        // decode the file
        AddressBook fromFile = AddressBook.newBuilder().mergeFrom(new FileInputStream("addressBook_PersonJohn.proto")).build();
        System.out.println(fromFile.getPeople(0).getId());
        System.out.println(fromFile.getPeople(0).getName());
        System.out.println(fromFile.getPeople(0).getEmail());
        System.out.println(fromFile.getPeople(0).getPhones(0).getNumber());
        System.out.println(fromFile.getPeople(0).getPhones(0).getType());

    }
}
