📊 Comparativa de Resoluciones de Hoy aplicando CACAJEC

A continuación, puedes escanear de manera directa cómo resolvimos cada punto del checklist de la imagen en tus clases `Person.java` y `Dog.java` a lo largo de la sesión de hoy:

Paso 0: **C** — Cardinality, unidirectional or bidirectional, owner side?

-   **Análisis:** Decidimos realizar una relación **Muchos a Muchos (N:N)** bidireccional.
-   **Elección de Dueño:** Se determinó que **`**Person**` controla la relación** (lleva la clave ajena/tabla intermedia física), mientras que **`**Dog**` es el lado inverso** (mapeo lógico).

Paso 1: **A** — Annotations (write them)

-   ❌ **Fallo Inicial:** Las anotaciones `@ManyToMany` y `@JoinTable` se colocaron erróneamente encima de la clase o sueltas al final del documento.
-   🛠️ **Resolución:** Se recolocaron dentro del cuerpo de la clase, afectando de forma directa e inequívoca a los atributos de colección:

java

// Dentro de Person.java  
@ManyToMany  
private List<Dog> dogs;  

Usa el código con precaución.

Paso 2: **CA** — Configure Annotations

-   ❌ **Fallo Inicial:** Un error tipográfico en Java *camelCase* (`InverseJoinColumns` con la letra **I** mayúscula) hacía que el compilador no reconociera la propiedad de Hibernate.
-   🛠️ **Resolución:** Se modificó a minúscula (`inverseJoinColumns`) y se configuró la propagación en cascada mediante `cascade = CascadeType.PERSIST` para agilizar las operaciones del repositorio del dueño.

Paso 3: **J** — Join

-   ❌ **Fallo Inicial (`**MappingException**`):** Asignaste el mismo nombre genérico (`name = "ID"`) tanto para la clave de origen como para la inversa. Al duplicar el nombre de la columna física, el framework colapsaba al arrancar.
-   🛠️ **Resolución:** Se separaron y nombraron explícitamente como `PERSON_ID` y `DOG_ID` para estructurar la tabla intermedia `PERSON_ADOPTION`.

Paso 4: **EC** — Extra configuration & convenience + Circular-reference protection

-   ❌ **Fallo Inicial (`**LazyInitializationException**`):** Al ejecutar el test e intentar imprimir la entidad `Dog` mediante `System.out.println()`, el método `@ToString` automático de Lombok intentaba cargar perezosamente la lista de personas cuando la transacción e hilo de conexión de Hibernate ya se habían cerrado.
-   🛠️ **Resolución:** Se incorporó el decorador `@ToString.Exclude` sobre el atributo de la colección inversa en la entidad para cortar la llamada cíclica y evitar la necesidad de sesiones transaccionales activas al escribir logs.




# Resumen del Desarrollo JPA - Relación ManyToMany (Método CACAJEC)

## 🛠️ Código Final de las Entidades Resueltas

### 1. Entidad Dueña: `Person.java`
```java
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
@ToString
@EqualsAndHashCode
public class Person {
    @Id
    private String id;
    private String name;
    private int age;

    // Pasos C, A y CA: Relación ManyToMany y persistencia en cascada
    @ManyToMany(cascade = CascadeType.PERSIST)
    // Paso J: Tabla intermedia física con nombres de columna unívocos
    @JoinTable(
            name = "PERSON_ADOPTION",
            joinColumns = @JoinColumn(name = "PERSON_ID"),
            inverseJoinColumns = @JoinColumn(name = "DOG_ID")
    )
    // Paso EC: Inicialización de colección para prevenir NullPointerException
    private List<Dog> dogs = new ArrayList<>();
}
```

### 2. Entidad Inversa: `Dog.java`
```java
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
@ToString
@EqualsAndHashCode
public class Dog {

    @Id
    private String id;
    private String name;
    private String breed;
    private int age;

    // Pasos C, A y CA: Mapeo lógico bidireccional apuntando al atributo de Person
    @ManyToMany(mappedBy = "dogs") 
    // Paso EC: Exclusión para solucionar la 'LazyInitializationException'
    @ToString.Exclude 
    private List<Person> persons = new ArrayList<>();
}
```

---

## 🧪 Estrategias de Persistencia descubiertas hoy (Evitando Rollbacks)

### Opción A: Forzar sincronización desde el Test (`.flush()`)
A diferencia de la relación simple de ayer (`OneToMany`) donde Hibernate enviaba el SQL de inmediato para chequear la clave foránea del objeto, en un `@ManyToMany` se manipulan colecciones en memoria de manera perezosa. Si no se fuerza el envío, el test aplica un rollback automático y limpia el archivo físico `.db`. Para evitarlo sin mutar el flujo de la aplicación se añade `.flush()`:

```java
@Test
void testJPA() {
    // 1. Guardar primero los objetos independientes para evitar ObjectNotFoundException
    dogRepository.save(dog1);
    dogRepository.save(dog2);
    dogRepository.save(dog3);

    // 2. Asociar colecciones
    person.getDogs().add(dog1);
    
    // 3. Persistir dueño y volcar a disco duro de inmediato
    personRepository.save(person);
    personRepository.flush(); // <-- Escribe en el archivo .db antes del Rollback del test
}
```
