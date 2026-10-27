En una relación `@ManyToMany` **unidireccional**, solo una de las clases conoce a la otra. La base de datos y la tabla intermedia se crean exactamente igual, pero el código de las entidades se simplifica notablemente.

Para que te quede el apunte completo, he actualizado y regenerado tu archivo Markdown en la carpeta del proyecto.

Aquí tienes tu archivo actualizado: **resumen\_jpa\_cacajec.md**

A continuación, te muestro el código detallado de cómo cambia el diseño aplicando el método **CACAJEC** si decides hacer la relación en un solo sentido.

🗺️ El enfoque Unidireccional (Person conoce a Dog, pero Dog no sabe nada de Person)

En este escenario, **`**Person**` sigue siendo la dueña absoluta de la relación**, pero la clase `Dog` queda completamente limpia de anotaciones de mapeo. No tiene ni idea de que pertenece a una persona.

1\. Entidad Dueña: **`**Person.java**`** (Sigue los pasos C, A, CA, J y EC)

Se queda prácticamente igual que en el modelo bidireccional, ya que ella es la que define la estructura física en la base de datos:

java

package com.example.persondog;  
  
import jakarta.persistence.\*;  
import lombok.\*;  
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
  
    // Pasos C, A y CA: Relación ManyToMany unidireccional con persistencia en cascada  
    @ManyToMany(cascade = CascadeType.PERSIST)  
    // Paso J: Se encarga de crear físicamente la tabla intermedia "PERSON\_ADOPTION"  
    @JoinTable(  
            name = "PERSON\_ADOPTION",  
            joinColumns = @JoinColumn(name = "PERSON\_ID"),  
            inverseJoinColumns = @JoinColumn(name = "DOG\_ID")  
    )  
    // Paso EC: Inicialización de la colección  
    private List<Dog> dogs = new ArrayList<>();  
}  

Usa el código con precaución.

2\. Entidad Destino: **`**Dog.java**`** (¡Súper simplificada!)

Como la relación es unidireccional, **eliminamos por completo la lista de personas**. Al quitar la lista, desaparece el riesgo de referencias circulares y llamadas perezosas en los logs. Por lo tanto, **ya no necesitas `**@ToString.Exclude**`**:

java

package com.example.persondog;  
  
import jakarta.persistence.\*;  
import lombok.\*;  
  
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
  
    // ❌ AQUÍ YA NO HAY LISTA DE PERSONAS NI ANOTACIONES DE RELACIÓN.  
    // Dog es una entidad independiente y no sabe quién lo ha adoptado.  
}  

Usa el código con precaución.

🧪 ¿Cómo afecta esto al Test? (Paso EC - Conveniencia)

Al testearlo en tu archivo `PersondogApplicationTests.java`, la lógica de guardado es idéntica a la que descubrimos hoy, pero te ahorras tener que sincronizar la relación en ambos sentidos (ya no existe un `dog.getPersons().add(person)`):

java

@Test  
void testJPAUnidireccional() {  
    // 1. Instanciamos los objetos de forma normal  
    Person person = Person.builder().id("1").name("John").age(30).dogs(new ArrayList<>()).build();  
    Dog dog1 = Dog.builder().id("1").name("Buddy").age(12).breed("Pastor").build();  
    Dog dog2 = Dog.builder().id("2").name("Max").age(8).breed("Sephard").build();  
  
    // 2. Guardamos primero los perros para que existan en la base de datos  
    dogRepository.save(dog1);  
    dogRepository.save(dog2);  
  
    // 3. Establecemos la relación en la única dirección que existe (Person -> Dog)  
    person.getDogs().add(dog1);  
    person.getDogs().add(dog2);  
  
    // 4. Guardamos la dueña de la relación y volcamos al archivo físico .db  
    personRepository.save(person);  
    personRepository.flush();   
}  

Usa el código con precaución.

⚖️ Resumen de ventajas del modelo Unidireccional:

-   **Adiós a los errores de ciclo:** Te olvidas para siempre del `LazyInitializationException` provocado por el `toString()` de Lombok al imprimir el perro, ya que este no tiene colecciones perezosas.
-   **Código más limpio:** Menos líneas que mantener en la entidad inversa.
-   **Misma Base de Datos:** La tabla intermedia `PERSON_ADOPTION` se genera exactamente igual en H2.