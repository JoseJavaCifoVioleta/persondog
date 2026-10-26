package com.example.persondog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class PersondogApplicationTests {

	// Añade estas líneas para inyectar los repositorios necesarios
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DogRepository dogRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional // Es importante para mantener la sesión abierta con fetch LAZY
	void testJPA() {
		Dog dog1 = Dog.builder().id("D-1").name("Toby").breed("Husky").age(4).build();
		Person person1 = Person.builder().id("P-1").name("Carlos").age(30).build();

		// Vinculamos usando el helper. Pasamos un ID único para la fila intermedia
		person1.addDog(dog1, "ASSOC-1");

		// Guardar a la persona guardará en cascada el registro en la tabla PersonDog
		personRepository.save(person1);
	}
}
