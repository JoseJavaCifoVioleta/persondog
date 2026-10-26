package com.example.persondog;

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
public class Person {

    @Id
    private String id;
    private String name;
    private int age;

    // C: 1:N Bidireccional hacia la tabla intermedia - Inverse Side
    // A: @OneToMany
    // CA: mappedBy apunta al atributo "person" dentro de la clase PersonDog
    // EC: Inicialización obligatoria de la colección
    @Builder.Default
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PersonDog> personDogs = new ArrayList<>();

    // EC: Método Helper de Sincronización para añadir relaciones en memoria
    public void addDog(Dog dog, String associationId) {
        PersonDog association = PersonDog.builder()
                .id(associationId)
                .person(this)
                .dog(dog)
                .build();

        this.personDogs.add(association);
        dog.getPersonDogs().add(association); // Sincroniza el lado del perro
    }

    // EC: Método Helper para eliminar relaciones
    public void removeDog(Dog dog) {
        this.personDogs.removeIf(association -> {
            if (association.getDog().equals(dog)) {
                dog.getPersonDogs().remove(association);
                return true;
            }
            return false;
        });
    }
}
