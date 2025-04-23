package com.robert.leave_ms_bn.services;

import com.robert.leave_ms_bn.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@AllArgsConstructor
@Service
public class UserService  implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails   loadUserByUsername (String email ) throws UsernameNotFoundException{
      var user =   userRepository.findByEmail((email).describeConstable().orElseThrow(
              () -> new UsernameNotFoundException("User not found")
      ));

      return new User(
              user.getEmail(),
              user.getPassword(),
              Collections.emptyList()
      );
    }
}
