package com.architecturedays.day015.despues;

public record CreateCompanyRequest(String name, String email, boolean requiresApproval, String approverEmail) {
}
