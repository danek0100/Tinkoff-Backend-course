package edu.java.scheduler;

import edu.java.configuration.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class LinkUpdaterScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkUpdaterScheduler.class);
    private final ApplicationConfig.Scheduler schedulerConfig;

    @Autowired
    public LinkUpdaterScheduler(ApplicationConfig applicationConfig) {
        this.schedulerConfig = applicationConfig.scheduler();
    }

    @Scheduled(fixedDelayString = "#{T(java.time.Duration).parse('${app.scheduler.interval}').toMillis()}",
               initialDelayString = "#{T(java.time.Duration).parse('${app.scheduler.force-check-delay}').toMillis()}")
    public void update() {
        if (schedulerConfig.enable()) {
            LOGGER.info("Updating...");
            // Выполните свои задачи здесь
        } else {
            LOGGER.info("Scheduler is disabled.");
        }
    }
}
