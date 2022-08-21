package com.xyz.order.infrastructure.repository.jpa;

import com.xyz.order.domain.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepositoryJpa extends CrudRepository<Order, Long> {}
