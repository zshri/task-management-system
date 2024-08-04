package org.example.taskmanagementsystem.repository;

import org.example.taskmanagementsystem.model.User;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}