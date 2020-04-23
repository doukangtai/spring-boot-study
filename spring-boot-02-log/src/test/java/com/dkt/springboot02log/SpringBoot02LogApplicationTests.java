package com.dkt.springboot02log;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBoot02LogApplicationTests {

    // 日志级别由低到高：trace<debug<info<warn<error
    @Test
    void contextLoads() {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.trace("trace日志...");
        logger.debug("debug日志...");
        logger.info("info日志...");
        logger.warn("warn日志...");
        logger.error("error日志...");
    }

}
