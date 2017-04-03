package com.almundo.challenge.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Employee implements Comparable<Employee> {
	private static final Logger logger = LoggerFactory.getLogger(Employee.class);

	private final String name;
	private final EmployeeType type;

	private EmployeeState state = EmployeeState.AVAILABLE;
	private int handledCalls;

	private Employee(String name, EmployeeType type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Factory method to create an Employee of type {@link EmployeeType#OPERATOR}
	 *
	 * @param name the name of the employee.
	 *
	 * @return an operator.
	 */
	public static Employee operator(String name) {
		return new Employee(name, EmployeeType.OPERATOR);
	}

	/**
	 * Factory method to create an Employee of type {@link EmployeeType#SUPERVISOR}
	 *
	 * @param name the name of the employee.
	 *
	 * @return a supervisor.
	 */
	public static Employee supervisor(String name) {
		return new Employee(name, EmployeeType.SUPERVISOR);
	}

	/**
	 * Factory method to create an Employee of type {@link EmployeeType#DIRECTOR}
	 *
	 * @param name the name of the employee.
	 *
	 * @return an director.
	 */
	public static Employee director(String name) {
		return new Employee(name, EmployeeType.DIRECTOR);
	}

	/**
	 * Handles a call.
	 *
	 * @param call the call to handle.
	 */
	public void handle(Call call) {
		logger.debug("Employee {} handling call {}", this, call);
		state = EmployeeState.ON_CALL;
		handledCalls++;
		call.handle();
		state = EmployeeState.AVAILABLE;
		logger.debug("Employee {} finished call {}", this, call);
	}

	/**
	 * Sets the state of the employee as assigned, so he cannot be assigned o another call.
	 *
	 * @return the current employee.
	 */
	public Employee assign() {
		logger.debug("Employee {}: {}", this, EmployeeState.ASSIGNED);
		state = EmployeeState.ASSIGNED;
		return this;
	}

	public boolean isAvailable() {
		return state == EmployeeState.AVAILABLE;
	}

	public boolean isAssigned() {
		return state == EmployeeState.ASSIGNED;
	}

	public EmployeeType getType() {
		return type;
	}

	public int getHandledCalls() {
		return handledCalls;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Employee o) {
		return type.getOrder() - o.type.getOrder();
	}

}
