package com.klef.ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klef.ms.entity.User;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> 
{
  // from the User where email=?1 and password=?2
  Optional<User> findByEmailAndPassword(String email, String password);

  Optional<User> findByEmail(String email);
}
