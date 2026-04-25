package com.architecturedays.day015.despues;

import org.springframework.stereotype.Service;

/**
 * Crear companies. El flujo de aprobacion vive aca, donde se entiende.
 * Si compartiera codigo con UserService seria por accidente, no por diseno.
 */
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmailService emailService;
    private final ApprovalService approvalService;

    public CompanyService(
            CompanyRepository companyRepository,
            EmailService emailService,
            ApprovalService approvalService) {
        this.companyRepository = companyRepository;
        this.emailService = emailService;
        this.approvalService = approvalService;
    }

    public Company createCompany(CreateCompanyRequest request) {
        Company company = new Company();
        company.setName(request.name());
        company.setEmail(request.email());
        company.setStatus("PENDING");
        if (request.requiresApproval()) {
            company.setApproverEmail(request.approverEmail());
            approvalService.requestApproval(company);
        }
        Company saved = companyRepository.save(company);
        emailService.sendOnboarding(saved.getEmail());
        return saved;
    }
}
