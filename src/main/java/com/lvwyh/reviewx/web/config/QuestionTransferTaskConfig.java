package com.lvwyh.reviewx.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/** 题库 JSON 导入导出后台任务线程池。 */
@Configuration
public class QuestionTransferTaskConfig {

    @Bean("questionTransferExecutor")
    public Executor questionTransferExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("question-transfer-");
        executor.initialize();
        return executor;
    }
}
