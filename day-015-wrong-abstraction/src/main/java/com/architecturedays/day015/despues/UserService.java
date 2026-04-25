package com.architecturedays.day015.despues;

import org.springframework.stereotype.Service;

/**
 * Crear usuarios. Sin switches. Sin flags. Sin Map<String, Object>.
 * Si manana cambia el flujo, cambia esta clase. Y solo esta.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole("BASIC");
        if (request.referralCode() != null) {
            user.setReferralCode(request.referralCode());
            // aplicar referralBonus
        }
        User saved = userRepository.save(user);
        emailService.sendWelcome(saved.getEmail());
        return saved;
    }
}
