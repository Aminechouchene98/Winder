package com.example.pidev.Repository;

import com.example.pidev.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    public Optional<User> findByEmail(String email);
    public User findByToken(String token);
    public boolean existsByEmail(String email);
}
