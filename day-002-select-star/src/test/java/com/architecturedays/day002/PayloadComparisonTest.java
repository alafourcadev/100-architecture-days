package com.architecturedays.day002;

import com.architecturedays.day002.dto.UserSummary;
import com.architecturedays.day002.model.User;
import com.architecturedays.day002.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PayloadComparisonTest {

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        for (int i = 1; i <= 10; i++) {
            byte[] fakeImage = new byte[100_000];
            String photoBase64 = Base64.getEncoder().encodeToString(fakeImage);
            repository.save(new User("user" + i + "@test.com", "User " + i, photoBase64, "Bio " + i));
        }
    }

    @Test
    @DisplayName("SELECT * trae datos innecesarios (fotos)")
    void selectStarBringsUnnecessaryData() {
        List<User> users = repository.findAll();

        long totalPhotoSize = users.stream()
            .mapToLong(u -> u.getPhotoBase64().length())
            .sum();

        assertThat(users).hasSize(10);
        assertThat(totalPhotoSize).isGreaterThan(1_000_000);
    }

    @Test
    @DisplayName("Interface Projection trae solo lo necesario")
    void projectionBringsOnlyNecessaryData() {
        List<UserSummary> summaries = repository.findAllProjectedBy();

        long totalSize = summaries.stream()
            .mapToLong(u -> (u.getEmail() + u.getName()).length())
            .sum();

        assertThat(summaries).hasSize(10);
        assertThat(totalSize).isLessThan(1_000);
    }

    @Test
    @DisplayName("Interface Projection es 99% mas liviana que SELECT *")
    void projectionIsMuchLighter() {
        List<User> fullUsers = repository.findAll();
        List<UserSummary> summaries = repository.findAllProjectedBy();

        long fullSize = fullUsers.stream()
            .mapToLong(u -> u.getPhotoBase64().length())
            .sum();

        long summarySize = summaries.stream()
            .mapToLong(u -> (u.getEmail() + u.getName()).length())
            .sum();

        double reduction = ((double)(fullSize - summarySize) / fullSize) * 100;

        assertThat(reduction).isGreaterThan(99.0);
    }
}
