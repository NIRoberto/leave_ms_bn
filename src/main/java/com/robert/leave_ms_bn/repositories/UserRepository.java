package com.robert.leave_ms_bn.repositories;

import com.robert.leave_ms_bn.entities.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository
   extends JpaRepository<User, Long>
  {

    User findUserByEmail(@NotNull(message = "Email is Required") String email);
  }
