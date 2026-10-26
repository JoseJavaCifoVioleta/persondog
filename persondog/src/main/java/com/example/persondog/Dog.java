package com.example.persondog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "personDogs") // EC: Previene bucles en String
@EqualsAndHashCode(exclude = "personDogs")
public class Dog {

    @Id
    private String id;
    private String name;
    private String breed;
    private int age;

    // C: 1:N Bidireccional hacia la tabla intermedia - Inverse Side
    // A: @OneToMany
    // CA: mappedBy apunta al atributo "dog" dentro de la clase PersonDog
    // EC: @JsonIgnore evita bucles recursivos en JSON + Inicialización de lista
    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PersonDog> personDogs = new ArrayList<>();
}
