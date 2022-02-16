/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Employee;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, String>{
	public Flux<Employee> findAllByDevice(Iterable<String> devices);
	public Mono<Employee> findByDevice(String device);
}
