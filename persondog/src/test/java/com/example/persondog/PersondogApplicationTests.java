package com.example.persondog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class PersondogApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private DogRepository dogRepository;

	@Test
	void testJPA(){
		// 1. Instanciamos la persona normalmente
		Person person = Person.builder()
				.id("1")
				.name("John")
				.age(30)
				.dogs(new ArrayList<>()) // Inicializamos la lista para evitar NullPointerException
				.build();

		// 2. Instanciamos los perros
		Dog dog1 = Dog.builder()
				.id("1")
				.name("Buddy")
				.age(123)
				.breed("Pastor")
				.build();

		Dog dog2 = Dog.builder()
				.id("2")
				.name("Max")
				.age(83)
				.breed("Sephard")
				.build();

		Dog dog3 = Dog.builder()
				.id("3")
				.name("Nano")
				.age(33)
				.breed("Pastor")
				.build();

		// Nuevo 4
		dogRepository.save(dog1);
		dogRepository.save(dog2);
		dogRepository.save(dog3);

		// 3. Relacionamos los objetos usando las LISTAS (al ser ManyToMany)
		person.getDogs().add(dog1);
		person.getDogs().add(dog2);
		person.getDogs().add(dog3);

		// 4. IMPORTANTE: Como Person es la dueña de la relación (lleva el @JoinTable),
		// debemos guardar la Persona para que se inserte en la tabla intermedia "PERSON_ADOPTION".
		// Descomenta el PersonRepository arriba e inyéctalo (@Autowired) para poder hacer esto:

		personRepository.save(person);
		personRepository.flush();

		// 5. Comprobamos recuperando los datos desde el repositorio de perros
		dogRepository.findById("1").ifPresent(System.out::println);
		dogRepository.findById("2").ifPresent(System.out::println);
		dogRepository.findById("3").ifPresent(System.out::println);

		/*
		dogRepository.findById("1").ifPresent(dog -> System.out.println(dog));
		dogRepository.findById("2").ifPresent(dog -> System.out.println(dog));
		dogRepository.findById("3").ifPresent(dog -> System.out.println(dog));
		 */
	}

}
