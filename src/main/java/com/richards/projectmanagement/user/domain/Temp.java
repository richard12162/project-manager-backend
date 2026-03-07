package com.richards.projectmanagement.user.domain;

import com.richards.projectmanagement.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Temp implements CommandLineRunner {

    private final UserRepository userRepository;

    public Temp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        userRepository.deleteAll();
//        User user = new User();
//        user.setId(UUID.randomUUID());
//        user.setEmail("test");
//        user.setPasswordHash("test");
//        user.setRole(UserRole.USER);
//        user.setCreatedAt(OffsetDateTime.now());
//        user.setUpdatedAt(OffsetDateTime.now());
//
//        userRepository.save(user);
//
//        System.out.println(userRepository.findByEmail("test"));
    }
}
