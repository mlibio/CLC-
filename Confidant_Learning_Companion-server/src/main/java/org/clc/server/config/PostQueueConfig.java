package org.clc.server.config;

import lombok.extern.slf4j.Slf4j;

import org.clc.common.constant.MessageConstant;
import org.clc.common.constant.QueueConstant;
import org.clc.pojo.message.PostUpdateMessage;
import org.clc.server.listener.PostMqListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @description: TODO
 */
@Configuration
@Slf4j
public class PostQueueConfig  {
    /**
     * 创建一个用于处理帖子点赞更新的消息队列。
     * 队列设置为持久化，以确保消息在服务器重启后不会丢失。
     * @return 配置好的队列对象
     */
    @Bean
    public Queue postLikeUpdateQueue() {
        return new Queue(QueueConstant.POST_LIKE_UPDATE_QUEUE, true); // 第二个参数表示队列是否持久化
    }
    /**
     * 创建一个用于处理帖子更新的消息队列。
     * 此队列设置了死信交换机和路由键，以便于未成功处理的消息可以被发送到死信队列。
     * @return 配置好的队列对象
     */
    @Bean
    public Queue postUpdateQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", QueueConstant.DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", QueueConstant.POST_UPDATE_DLX_QUEUE);
        return new Queue(QueueConstant.POST_UPDATE_QUEUE, true, false, false, args);
    }
    /**
     * 创建一个死信队列，用于接收从主队列未能成功处理的消息。
     * 队列设置为持久化，以确保消息在服务器重启后不会丢失。
     * @return 配置好的死信队列对象
     */
    @Bean
    public Queue dlxQueue() {
        return new Queue(QueueConstant.POST_UPDATE_DLX_QUEUE, true);
    }
    /**
     * 创建一个直接交换机，用于死信队列的绑定。
     * @return 配置好的直接交换机对象
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(QueueConstant.DLX_EXCHANGE);
    }
    /**
     * 将死信队列与死信交换机绑定，并指定路由键。
     * @return 绑定对象
     */
    @Bean
    public Binding dlxBinding(Queue dlxQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(QueueConstant.POST_UPDATE_DLX_QUEUE);
    }
    /**
     * 创建一个 RabbitMQ 管理器，用于管理连接工厂。
     * 这个管理器可以帮助我们动态地创建和删除队列、交换机等资源。
     * @param connectionFactory 连接工厂
     * @return RabbitAdmin 对象
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    /**
     * 创建一个 JSON 消息转换器，用于将 Java 对象转换为 JSON 格式的消息体，反之亦然。
     * @return 消息转换器对象
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    /**
     * 创建一个 RabbitTemplate 对象，用于发送消息到 RabbitMQ。
     * 设置消息转换器为之前创建的 JSON 转换器。
     * @param connectionFactory 连接工厂
     * @return RabbitTemplate 对象
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
    /**
     * 创建一个消息监听容器，用于监听帖子更新队列的消息。
     * 设置了并发消费者数量和最大并发消费者数量，以及手动确认模式。
     * 添加了一个重试拦截器来处理消息处理失败的情况。
     * @param connectionFactory 连接工厂
     * @param channelAwareMessageListener          消息监听器
     * @return 监听容器对象
     */
    @Bean
    public SimpleMessageListenerContainer postUpdateListenerContainer(ConnectionFactory connectionFactory,@Qualifier("postUpdateMessageListener")  ChannelAwareMessageListener channelAwareMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(postUpdateQueue());
        container.setMessageListener(channelAwareMessageListener);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置手动确认模式
        container.setAdviceChain(retryOperationsInterceptor().getObject());
        return container;
    }
    /**
     * 创建一个无状态重试操作拦截器工厂，用于处理消息处理失败时的重试逻辑。
     * 如果重试次数达到上限，则会调用消息恢复器来处理消息。
     * @return 重试操作拦截器工厂对象
     */
    @Bean
    public StatelessRetryOperationsInterceptorFactoryBean retryOperationsInterceptor() {
        StatelessRetryOperationsInterceptorFactoryBean factory = new StatelessRetryOperationsInterceptorFactoryBean();
        factory.setRetryOperations(retryTemplate());
        factory.setMessageRecoverer(new RejectAndDontRequeueRecoverer());
        return factory;
    }
    /**
     * 创建一个重试模板，定义了重试策略（如最大重试次数）和重试间隔策略。
     * @return 重试模板对象
     */
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // 设置重试策略
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // 最大重试次数
        retryTemplate.setRetryPolicy(retryPolicy);

        // 设置重试间隔
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000); // 初始延迟
        backOffPolicy.setMultiplier(2.0); // 乘数
        backOffPolicy.setMaxInterval(10000); // 最大延迟
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
    /**
     * 创建一个通道感知消息监听器，用于实际处理来自帖子更新队列的消息。
     * 当消息到达时，此监听器会尝试将消息转换为 PostUpdateMessage 对象并调用相应的处理器方法。
     * 如果处理过程中发生异常，会记录错误日志并抛出异常。
     * @param postMqListener 消息处理器
     * @return 消息监听器对象
     */
    @Bean
    public ChannelAwareMessageListener postUpdateMessageListener(PostMqListener postMqListener) {
        return (message, channel) -> {
            try {
                PostUpdateMessage postUpdateMessage = (PostUpdateMessage) jsonMessageConverter().fromMessage(message);
                postMqListener.receiveMessage(postUpdateMessage, channel, message.getMessageProperties().getDeliveryTag());
            } catch (Exception e) {
                log.error(MessageConstant.FAILED, e);
                throw e;
            }
        };
    }
}
