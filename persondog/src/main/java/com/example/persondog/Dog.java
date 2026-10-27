package com.example.persondog;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode

public class Dog {

    @Id
    private String id;
    private String name;
    private String breed;
    private int age;

   // @ManyToMany(mappedBy = "dogs", fetch = FetchType.EAGER) // <-- Asegúrate de que apunte exactamente al nombre del atributo en Person.java

    // lado que NO ES propietario para indicar el nombre del CAMPO en el lado propietario
   @ManyToMany(mappedBy = "dogs")
   @ToString.Exclude
   private List<Person> persons = new ArrayList<>();

}
