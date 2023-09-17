package com.sigga.ecommerce.core.rabbit;

import com.sigga.ecommerce.order.purchase.PurchaseOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailConsumer {

    @RabbitListener(bindings = @QueueBinding(value = @Queue(MailConfig.QUEUE_MAIL),
            exchange = @Exchange(name = MailConfig.EXCHANGE_MAIL),
            key = MailConfig.ROUTING_KEY))
    public void processMessage(PurchaseOrder purchaseOrder) {

        log.info("MESSAGE: E-mail Enviado com sucesso!!!");
        log.info("CONSUMER: Purchase Order {}", purchaseOrder);

    }

}
