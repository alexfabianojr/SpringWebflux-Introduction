package guru.springframework.examples.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class PersonRepositoryImplTest {

    final PersonRepositoryImpl personRepository = new PersonRepositoryImpl();

    @BeforeEach
    void setUp() {
    }

    /**
     * You may think Mono as an java object
     * */

    @Test
    void getByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);
        Person person = personMono.block();
        System.out.println(person.toString());
    }

    @Test
    void getByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
    }

    @Test
    void getByIdMapFunction() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.map(person -> {
            //Won't print anything cuz happens before the backpressure of the subscribe that triggers the function
            System.out.println(person.toString());
            return person.getFirstName();
        }).subscribe(firstName -> System.out.println(firstName));
    }

    /**
     * You may think Flux as an java optinal with a list of object
     * */

    @Test
    void fluxTestBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();
        Person person = personFlux.blockFirst();
        System.out.println(person.toString());
    }

    @Test
    void testFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();
        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();
    }

    @Test
    void testFluxToListMono() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<List<Person>> personListMono = personFlux.collectList();
        personListMono.subscribe(list -> {
            list.forEach(person -> System.out.println(person.toString()));
        });
    }

    @Test
    void testFindPersonById() {
        final Integer id = 3;
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();
        personMono.subscribe(Object::toString);
    }

    @Test
    void testFindPersonByIdNotFound() {
        final Integer id = 8;
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();
        personMono.subscribe(Object::toString);
    }

    @Test
    void testFindPersonByIdNotFoundWithException() {
        final Integer id = 8;
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId().equals(id)).single();
        personMono.doOnError(Throwable::printStackTrace).onErrorReturn(Person.builder().id(id).firstName("").lastName("").build()).subscribe(Object::toString);
    }
}