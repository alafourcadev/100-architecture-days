package com.architecturedays.day015.antes;

import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * El intento heroico de "no repetirnos".
 *
 * Tres flujos parecidos (User, Company, Partner) terminaron forzados
 * en una unica firma generica con parametros booleanos, switch interno,
 * casteos y un Map<String, Object> para "lo demas".
 *
 * Cada vez que cambia uno de los flujos, esta clase crece. Cada nuevo
 * tipo de entidad agrega otra rama al switch y otro flag al request.
 *
 * Sandi Metz: "duplication is far cheaper than the wrong abstraction".
 */
@Service
public class EntityCreationService {

    private final EntityRepository repository;
    private final ValidationService validationService;
    private final EmailService emailService;

    public EntityCreationService(
            EntityRepository repository,
            ValidationService validationService,
            EmailService emailService) {
        this.repository = repository;
        this.validationService = validationService;
        this.emailService = emailService;
    }

    @SuppressWarnings("unchecked")
    public <T> T createEntity(
            T entity,
            String entityType,
            String email,
            boolean sendWelcome,
            boolean requiresApproval,
            String approverEmail,
            Map<String, Object> extraData) {

        validationService.validate(entity, entityType);

        switch (entityType) {
            case "USER" -> {
                User user = (User) entity;
                user.setRole("BASIC");
                if (extraData != null && extraData.containsKey("referralCode")) {
                    user.setReferralCode((String) extraData.get("referralCode"));
                }
                T saved = (T) repository.save(user);
                if (sendWelcome) {
                    emailService.send(email, "Welcome");
                }
                return saved;
            }
            case "COMPANY" -> {
                Company company = (Company) entity;
                company.setStatus("PENDING");
                if (requiresApproval) {
                    company.setApproverEmail(approverEmail);
                    emailService.send(approverEmail, "Approval needed");
                }
                T saved = (T) repository.save(company);
                if (sendWelcome) {
                    emailService.send(email, "Onboarding");
                }
                return saved;
            }
            case "PARTNER" -> {
                Partner partner = (Partner) entity;
                if (extraData != null && extraData.containsKey("tier")) {
                    partner.setTier((String) extraData.get("tier"));
                }
                return (T) repository.save(partner);
            }
            default -> throw new IllegalArgumentException("Unknown entityType: " + entityType);
        }
    }
}
