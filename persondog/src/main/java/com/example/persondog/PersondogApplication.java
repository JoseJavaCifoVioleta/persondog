package com.example.persondog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.ArrayList;

@SpringBootApplication
public class PersondogApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersondogApplication.class, args);
	}
/*
	@Bean
	public CommandLineRunner initData(PersonRepository personRepository, DogRepository dogRepository) {
		return args -> {
			System.out.println("======= CARGANDO DATOS INICIALES =======");

			// 1. Instanciamos la persona
			Person person = Person.builder()
					.id("1")
					.name("John")
					.age(30)
					.dogs(new ArrayList<>())
					.build();

			// 2. Instanciamos los perros
			Dog dog1 = Dog.builder().id("1").name("Buddy").age(12).breed("Pastor").build();
			Dog dog2 = Dog.builder().id("2").name("Max").age(8).breed("Sephard").build();
			Dog dog3 = Dog.builder().id("3").name("Nano").age(3).breed("Pastor").build();

			// 3. Guardamos primero los perros de forma individual
			dogRepository.save(dog1);
			dogRepository.save(dog2);
			dogRepository.save(dog3);

			// 4. Los asociamos a la persona
			person.getDogs().add(dog1);
			person.getDogs().add(dog2);
			person.getDogs().add(dog3);

			// 5. Guardamos la persona (aquí se insertará automáticamente la tabla intermedia)
			personRepository.save(person);

			System.out.println("======= ¡DATOS CARGADOS CON ÉXITO! =======");
		};
	}
	*/
}



/*
package com.example.persondog;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersondogApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersondogApplication.class, args);
	}

}
*/