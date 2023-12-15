package com.example.GraplerEnhancemet.Repository;

import com.example.GraplerEnhancemet.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GraplerEnhancemet.entity.User;

import java.util.Optional;


@Repository
public interface UserRepository  extends JpaRepository<User, Long>   {
Optional<User> findByEmail(String email);
}
