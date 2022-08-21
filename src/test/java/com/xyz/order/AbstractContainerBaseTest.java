package com.xyz.order;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public abstract class AbstractContainerBaseTest {

  private static final String MYSQL_DOCKER_IMAGE = "mysql:8.0.24";
  private static final MySQLContainer<?> mySQLContainer;

  static {
    mySQLContainer = new MySQLContainer<>(MYSQL_DOCKER_IMAGE);
    mySQLContainer.start();
  }

  @DynamicPropertySource
  static void postgresqlProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", mySQLContainer::getUsername);
    registry.add("spring.datasource.password", mySQLContainer::getPassword);
  }
}
