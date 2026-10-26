package com.example.persondog;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"person", "dog"}) // EC: Previene bucles infinitos en String
@EqualsAndHashCode(exclude = {"person", "dog"})
public class PersonDog {

    @Id
    private String id; // Clave primaria independiente para esta fila de asociación

    // --- AQUÍ PUEDES AÑADIR ATRIBUTOS EXTRA EN EL FUTURO (Ej: private LocalDate adoptionDate;) ---

    // C: N:1 (Muchos registros de asociación apuntan a una Persona) - Owner Side
    // A: @ManyToOne
    // J: @JoinColumn define la FK física en la tabla intermedia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID")
    private Person person;

    // C: N:1 (Muchos registros de asociación apuntan a un Perro) - Owner Side
    // A: @ManyToOne
    // J: @JoinColumn define la FK física en la tabla intermedia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOG_ID")
    private Dog dog;
}
