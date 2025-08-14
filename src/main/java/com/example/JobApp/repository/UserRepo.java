package com.example.JobApp.repository;

import com.example.JobApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername (String username);
}
