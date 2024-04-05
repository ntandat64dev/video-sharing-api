package com.example.videosharingapi.repository;

import com.example.videosharingapi.config.AuditingConfig;
import com.example.videosharingapi.model.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    private @Autowired UserRepository userRepository;

    @Value("${spring.jpa.auditor}")
    private String auditor;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void givenUserObject_whenSave_thenReturnSavedUser() {
        var user = User.builder()
                .email("user@gmail.com")
                .password("00000000")
                .build();

        var savedUser = userRepository.save(user);

        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(savedUser.getGender()).isNull();
        assertThat(savedUser.getCountry()).isNull();
        assertThat(savedUser.getDateOfBirth()).isNull();
        assertThat(savedUser.getPhoneNumber()).isNull();
        assertThat(savedUser.getCreatedBy()).isEqualTo(auditor);
        assertThat(savedUser.getCreatedDate().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(savedUser.getModifiedBy()).isEqualTo(auditor);
        assertThat(savedUser.getModifiedDate().toLocalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    public void givenUserList_whenSave_thenReturnListOfUser() {
        var user1 = User.builder()
                .email("user1@gmail.com")
                .password("11111111")
                .build();
        var user2 = User.builder()
                .email("user2@gmail.com")
                .password("22222222")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        var users = userRepository.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    public void givenUserObject_whenFindById_thenReturnFoundUser() {
        var user = User.builder()
                .email("user@gmail.com")
                .password("00000000")
                .build();
        var savedUser = userRepository.save(user);

        var foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(savedUser);
    }

    @Test
    public void givenUserObject_whenFindByEmailAndPassword_thenReturnFoundUser() {
        var user = User.builder()
                .email("user@gmail.com")
                .password("00000000")
                .build();
        var savedUser = userRepository.save(user);

        var foundUser = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser).isEqualTo(savedUser);
    }

    @Test
    public void giveUserObject_whenCheckExistByEmail_thenReturnTrue() {
        var user = User.builder()
                .email("user@gmail.com")
                .password("00000000")
                .build();
        userRepository.save(user);

        var isExist = userRepository.existsByEmail(user.getEmail());

        assertThat(isExist).isTrue();
    }

    @Test
    public void givenUserObject_whenUpdateAndFindById_thenReturnUpdatedUser() {
        var user = User.builder()
                .email("user@gmail.com")
                .password("00000000")
                .build();
        var savedUser = userRepository.save(user);

        user.setEmail("user_updated@gmail.com");
        user.setPassword("11111111");
        userRepository.save(user);
        var foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("user_updated@gmail.com");
        assertThat(foundUser.get().getPassword()).isEqualTo("11111111");
    }
}
