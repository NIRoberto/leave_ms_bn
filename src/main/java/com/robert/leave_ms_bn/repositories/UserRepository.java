package com.robert.leave_ms_bn.repositories;

import com.robert.leave_ms_bn.entities.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository
   extends JpaRepository<User, Long>
  {

    User findUserByEmail(@NotNull(message = "Email is Required") String email);

   User findByEmail(@Size(max = 255) @NotNull(message = "Email is required") String email);

      User findUserById(Long id);
  }
