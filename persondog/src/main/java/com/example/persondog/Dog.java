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
@ToString(exclude = "people") // EC: Protege contra ciclos en el ToString
@EqualsAndHashCode(exclude = "people")
public class Dog {

    @Id
    private String id;
    private String name;
    private String breed;
    private int age;

    // C: N:N Bidireccional (Inverse Side)
    // A: @ManyToMany
    // CA: mappedBy apunta al atributo correspondiente en la clase Person
    // EC: @JsonIgnore evita bucles JSON, e inicializamos la lista
    @JsonIgnore
    @ManyToMany(mappedBy = "dogs", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Person> people = new ArrayList<>();
}
