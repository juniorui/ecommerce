package com.sigga.ecommerce.core.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    public static final String QUEUE_MAIL = "queue-mail";
    public static final String EXCHANGE_MAIL = "exchange-mail";
    public static final String ROUTING_KEY = "send-mail";

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_MAIL);
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(QUEUE_MAIL).build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

}
