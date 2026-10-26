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
@ToString(exclude = "dogs") // EC: Protege contra ciclos en el ToString
@EqualsAndHashCode(exclude = "dogs")
public class Person {

    @Id
    private String id;
    private String name;
    private int age;

    // C: N:N Bidireccional (Owning Side)
    // A: @ManyToMany
    // CA: Configuración de persistencia en cascada controlada
    // J: @JoinTable define físicamente la tabla intermedia en la DB
    // EC: Inicialización de la lista
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "PERSON_DOG",
            joinColumns = @JoinColumn(name = "PERSON_ID"),
            inverseJoinColumns = @JoinColumn(name = "DOG_ID")
    )
    private List<Dog> dogs = new ArrayList<>();

    // EC: Métodos Helper de Sincronización
    public void addDog(Dog dog) {
        this.dogs.add(dog);
        dog.getPeople().add(this); // Sincroniza la lista del perro
    }

    public void removeDog(Dog dog) {
        this.dogs.remove(dog);
        dog.getPeople().remove(this); // Sincroniza la lista del perro
    }
}
