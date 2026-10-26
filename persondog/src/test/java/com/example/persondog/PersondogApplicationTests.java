package com.example.persondog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
class PersondogApplicationTests {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DogRepository dogRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional // Mantiene la sesión abierta para poder leer las colecciones LAZY sin errores
	void testJPA() {
		// 1. Creamos los perros (sin asociar dueño todavía)
		Dog dog1 = Dog.builder()
				.id(UUID.randomUUID().toString())
				.name("Toby")
				.breed("Golden Retriever")
				.age(3)
				.build();

		Dog dog2 = Dog.builder()
				.id(UUID.randomUUID().toString())
				.name("Luna")
				.breed("Poodle")
				.age(2)
				.build();

		// 2. Creamos las personas
		Person person1 = Person.builder()
				.id(UUID.randomUUID().toString())
				.name("Carlos")
				.age(30)
				.build();

		Person person2 = Person.builder()
				.id(UUID.randomUUID().toString())
				.name("Ana")
				.age(25)
				.build();

		// 3. Establecemos la relación Many-to-Many usando los métodos Helper [EC]
		// Carlos es dueño de Toby y Luna
		person1.addDog(dog1);
		person1.addDog(dog2);

		// Ana comparte la propiedad de Luna con Carlos
		person2.addDog(dog2);

		// 4. Guardamos en la base de datos (Persistencia)
		// Como configuramos CascadeType.PERSIST en la relación, al guardar a las personas
		// JPA guardará automáticamente los perros y rellenará la tabla intermedia PERSON_DOG
		personRepository.save(person1);
		personRepository.save(person2);

		// 5. Verificación visual en la consola
		System.out.println("--- DATOS GUARDADOS CORRECTAMENTE ---");
		personRepository.findAll().forEach(p -> {
			System.out.println("Persona: " + p.getName() + " tiene los perros:");
			p.getDogs().forEach(d -> System.out.println(" - " + d.getName()));
		});
	}
}
