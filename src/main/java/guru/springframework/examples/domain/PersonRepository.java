package guru.springframework.examples.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {
    Mono<Person> getById(Integer id);
    Flux<Person> findAll();
    Mono<Person> findById(Integer id);
}
