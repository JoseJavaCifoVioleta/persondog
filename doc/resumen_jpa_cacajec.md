# Resumen del Proceso de Configuración y Solución de Errores en JPA (Person-Dog)

Este documento resume la evolución del código y la resolución de errores en el proyecto Spring Boot (`PersonDog`), contrastando las decisiones tomadas frente a la metodología **CACAJEC** proporcionada en la guía visual.

---

## 📊 Comparativa Metodológica: CACAJEC vs. Soluciones Aplicadas

A continuación se detalla cómo se abordó paso a paso cada fase de la guía **CACAJEC** para solucionar los problemas del mapeo Muchos a Muchos (`@ManyToMany`) y su persistencia.

| Paso | Sigla | Concepto / Guía Visual | Implementación y Errores Resueltos | Estado |
| :--- | :---: | :--- | :--- | :---: |
| **0** | **C** | **Cardinality, unidirectional or bidirectional, owner side?** <br>• ¿Cuál es la cardinalidad? (1:1, 1:N, N:1, N:N)<br>• ¿Uni o bidireccional?<br>• ¿Qué lado es el dueño (controla la FK)? | Se definió una relación **N:N (Muchos a Muchos)** de tipo **bidireccional**.<br>• **Dueño de la relación:** `Person` (posee la tabla intermedia).<br>• **Lado inverso:** `Dog` (utiliza el mapeo inverso). | 🟢 **Hecho** |
| **1** | **A** | **Annotations (write them)** <br>• Colocar `@ManyToMany` en los lados correctos.<br>• `@ManyToMany` crea una tabla puente. | ❌ **Error Inicial:** Las anotaciones `@ManyToMany` y `@JoinTable` se colocaron erróneamente sobre la clase (`public class Dog`) o sueltas al final del archivo.<br>🛠️ **Solución:** Se movieron al interior de las clases, aplicándolas estrictamente sobre las propiedades de tipo colección (`List<Dog>` y `List<Person>`). | 🟢 **Hecho** |
| **2** | **CA** | **Configure Annotations** <br>• Ajustar `mappedBy=...`, `cascade=...`, `fetch=LAZY/EAGER`. | 🛠️ **Configuración:** <br>• En `Dog`: Se añadió `mappedBy = "dogs"` para enlazar con la propiedad de la clase dueña.<br>• En `Person`: Se añadió `cascade = CascadeType.PERSIST`.<br>❌ **Error detectado:** Uso de `InverseJoinColumns` (con 'I' mayúscula). Se corrigió a camelCase: **`inverseJoinColumns`**. | 🟢 **Hecho** |
| **3** | **J** | **Join** <br>• Añadir la definición física: `@JoinTable` en el lado dueño. | ❌ **Error crítico (`MappingException`):** Se mapearon ambas columnas del `@JoinTable` con el mismo nombre (`name = "ID"`), provocando el error de columna duplicada.<br>🛠️ **Solución:** Se independizaron los nombres: `joinColumns = @JoinColumn(name = "PERSON_ID")` e `inverseJoinColumns = @JoinColumn(name = "DOG_ID")`. | 🟢 **Hecho** |
| **4** | **EC** | **Extra configuration & convenience** <br>• Inicializar colecciones (`= new ArrayList<>()`).<br>• Evitar ciclos JSON / Problemas de `toString()`. | ❌ **Error de Ciclo/Carga (`LazyInitializationException`):** Al ejecutar los tests, el método `.toString()` generado por Lombok intentaba cargar perezosamente la lista de relaciones fuera de la transacción.<br>🛠️ **Solución:** Se añadió la anotación **`@ToString.Exclude`** en la colección `private List<Person> persons` dentro de `Dog.java`. | 🟢 **Hecho** |

---

## 🔄 El Orden de los Hechos y el Ciclo de Vida de los Datos

Durante las pruebas y la persistencia de datos, surgieron diferencias clave en el orden de las operaciones en comparación con implementaciones anteriores (como `OneToMany`):

1. **El error de Objeto Transitorio (`ObjectRetrievalFailureException`):** 
   Al intentar guardar directamente la entidad `Person` pasando identificadores manuales (`id("1")`), JPA arrojaba un error buscando el perro asociado en la base de datos sin encontrarlo.
   * **Orden corregido:** Primero se guardan de forma aislada los objetos dependientes (`dogRepository.save(dog)`) y, posteriormente, se asocian a la lista del dueño y se guarda el padre (`personRepository.save(person)`).

2. **Diferencia entre Ejecucción de Tests (con Transacciones) y Aplicación Principal (`CommandLineRunner`):**
   * En la relación `OneToMany` de ayer, al salvar el objeto hijo (`Dog`) que tenía la clave foránea directa, Hibernate realizaba una sincronización forzada con el archivo físico en disco de inmediato.
   * En la relación `ManyToMany` actual, el uso de colecciones hace que Hibernate retenga los cambios en memoria. En un entorno de pruebas (`@Test`), al finalizar el método se ejecuta un **Rollback automático**, dejando las tablas de H2 vacías (`no rows`).
   * **Solución para Tests:** Añadir un `.flush()` explícito en el repositorio o utilizar la anotación `@Commit`.
   * **Solución para Producción/Arranque:** Mover la lógica de carga a un `@Bean` de tipo `CommandLineRunner` dentro de `PersondogApplication.java` para persistir los datos de forma orgánica al iniciar el servidor sin el contexto efímero de los tests.
