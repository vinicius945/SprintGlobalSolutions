package com.fiap.gestaoltakn.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CACHE_SYNC_QUEUE = "cache.sync.queue";
    public static final String RELATORIO_QUEUE = "relatorio.queue";
    public static final String EXCHANGE = "gestao.exchange";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue cacheSyncQueue() {
        return new Queue(CACHE_SYNC_QUEUE, true);
    }

    @Bean
    public Queue relatorioQueue() {
        return new Queue(RELATORIO_QUEUE, true);
    }

    @Bean
    public Binding cacheSyncBinding(Queue cacheSyncQueue, TopicExchange exchange) {
        return BindingBuilder.bind(cacheSyncQueue).to(exchange).with("cache.sync.*");
    }

    @Bean
    public Binding relatorioBinding(Queue relatorioQueue, TopicExchange exchange) {
        return BindingBuilder.bind(relatorioQueue).to(exchange).with("relatorio.*");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

}
