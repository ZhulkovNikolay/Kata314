package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.Person;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    //Мы ищем человека по имени, а SpringDataJPA возвращает такого человека из БД
    Optional<Person> findByUsername(String username);
}

/* еще методы автоматически предоставляемые (замена DAO)
*
* userRepository.save(user);          // Сохранение
userRepository.findById(id);        // Поиск по ID
userRepository.delete(user);        // Удаление
userRepository.findAll();           // Получение всех пользователей
* */