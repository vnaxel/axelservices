package com.axel.customer;

import com.axel.amqp.RabbitMQMessageProducer;
import com.axel.clients.fraud.FraudCheckResponse;
import com.axel.clients.fraud.FraudClient;
import com.axel.clients.notification.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
public record CustomerService(
        CustomerRepository customerRepository,
        FraudClient fraudClient,
        RabbitMQMessageProducer rabbitMQMessageProducer
) {
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        // todo: check if email is valid
        // todo: check if email is not taken
        customerRepository.saveAndFlush(customer);

        // check if fraudster
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        // send notification
        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome !", customer.getFirstName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }
}
