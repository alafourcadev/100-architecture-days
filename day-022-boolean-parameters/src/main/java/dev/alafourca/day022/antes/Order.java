package dev.alafourca.day022.antes;

/**
 * Simple order entity shared by the ANTES design.
 */
public class Order {

    private final String id;
    private final String customerEmail;
    private final String customerPhone;
    private final String status;

    public Order(String id, String customerEmail, String customerPhone, String status) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getStatus() {
        return status;
    }
}
