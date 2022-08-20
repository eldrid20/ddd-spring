package com.xyz.order.infrastructure.repository.jpa;

import com.xyz.order.domain.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderJpaRepository extends CrudRepository<Order, Long> {}
