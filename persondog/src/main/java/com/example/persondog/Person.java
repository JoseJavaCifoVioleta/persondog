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
public class Person {
    @Id
    private String id;
    private String name;
    private int age;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "PERSON_ADOPTION",
            joinColumns = @JoinColumn(name = "PERSON_ID"),
            inverseJoinColumns = @JoinColumn(name = "DOG_ID")
    )
    private List<Dog> dogs = new ArrayList<>();
}
