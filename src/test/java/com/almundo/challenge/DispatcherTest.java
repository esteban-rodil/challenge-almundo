package com.almundo.challenge;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.almundo.challenge.domain.Call;
import com.almundo.challenge.domain.Employee;
import com.almundo.challenge.domain.EmployeeType;

public class DispatcherTest {

	private Dispatcher dispatcher = new Dispatcher();
	private List<Employee> employees;

	@Before
	public void setUp() {
		employees = Arrays.asList(Employee.operator("Operator 1"),
				Employee.operator("Operator 2"),
				Employee.operator("Operator 3"),

				Employee.supervisor("Supervisor 1"),
				Employee.supervisor("Supervisor 1"),

				Employee.director("Director 1"));

		employees.forEach(dispatcher::addEmployee);
	}

	private void runTest(List<Call> calls) {

		calls.forEach(dispatcher::dispatchCall);

		dispatcher.closeAndWait();

		checkIfAllCallsWhereFinished(calls);
	}

	private void checkIfAllCallsWhereFinished(List<Call> calls) {
		Assert.assertFalse("Some calls were not finished", calls.stream()
				.filter(c -> c.getState() != Call.CallState.FINISHED)
				.peek(System.out::println)
				.findAny()
				.isPresent());
	}

	private void addExtraEmployees() {
		Arrays.asList(Employee.operator("Operator 4"),
				Employee.operator("Operator 5"),
				Employee.operator("Operator 6"),
				Employee.operator("Operator 7"),
				Employee.operator("Operator 8"),
				Employee.operator("Operator 9"),
				Employee.supervisor("Operator 10"),
				Employee.supervisor("Operator 11"),
				Employee.supervisor("Operator 12"))
		.forEach(dispatcher::addEmployee);
	}

	@Test
	public void receive10CallsWithEnoughEmployeeToHandleThemSimultaneously() throws InterruptedException {

		addExtraEmployees();

		List<Call> calls = Arrays.asList(new Call("Llamada 1"),
				new Call("Llamada 2"),
				new Call("Llamada 3"),
				new Call("Llamada 4"),
				new Call("Llamada 5"),
				new Call("Llamada 6"),
				new Call("Llamada 7"),
				new Call("Llamada 8"),
				new Call("Llamada 9"),
				new Call("Llamada 10"));

		runTest(calls);
	}

	@Test
	public void receiveANumberOfCallsWhichCanBeHandledSimultaneusly() throws InterruptedException {

		List<Call> calls = Arrays.asList(new Call("Llamada 1"),
				new Call("Llamada 2"),
				new Call("Llamada 3"),
				new Call("Llamada 4"),
				new Call("Llamada 5"));

		runTest(calls);
	}

	@Test
	public void receiveMoreCallsThanEmployees() throws InterruptedException {

		List<Call> calls = Arrays.asList(new Call("Llamada 1"),
				new Call("Llamada 2"),
				new Call("Llamada 3"),
				new Call("Llamada 4"),
				new Call("Llamada 5"),
				new Call("Llamada 6"),
				new Call("Llamada 7"));

		runTest(calls);
	}

	@Test
	public void receiveMoreThanMaximumSimultaneusCallsWithEnoughEmployeesToHandleThemAll() throws InterruptedException {

		addExtraEmployees();

		List<Call> calls = Arrays.asList(new Call("Llamada 1"),
				new Call("Llamada 2"),
				new Call("Llamada 3"),
				new Call("Llamada 4"),
				new Call("Llamada 5"),
				new Call("Llamada 6"),
				new Call("Llamada 7"),
				new Call("Llamada 8"),
				new Call("Llamada 9"),
				new Call("Llamada 10"),
				new Call("Llamada 11"),
				new Call("Llamada 12"),
				new Call("Llamada 13"));

		runTest(calls);
	}

	@Test
	public void receiveMoreThanMaximumSimultaneusCalls() throws InterruptedException {

		List<Call> calls = Arrays.asList(new Call("Llamada 1"),
				new Call("Llamada 2"),
				new Call("Llamada 3"),
				new Call("Llamada 4"),
				new Call("Llamada 5"),
				new Call("Llamada 6"),
				new Call("Llamada 7"),
				new Call("Llamada 8"),
				new Call("Llamada 9"),
				new Call("Llamada 10"),
				new Call("Llamada 11"),
				new Call("Llamada 12"),
				new Call("Llamada 13"));

		runTest(calls);
	}

	@Test
	public void onlyOperatorsTakeCalls() {

		List<Call> calls = Arrays.asList(new Call("Llamada 1"),
				new Call("Llamada 2"),
				new Call("Llamada 3"));

		runTest(calls);

		assertEquals("Nor SUPERVISOR or DIRECOTR should take calls", 0, employees.stream()
			.filter(e -> e.getHandledCalls() > 0)
			.filter(e -> e.getType() == EmployeeType.SUPERVISOR || e.getType() == EmployeeType.DIRECTOR)
			.count());
	}

	@Test
	public void onlyOperatorsAndSupervisorTakeCalls() {

		List<Call> calls = Arrays.asList(new Call("Llamada 1"),
				new Call("Llamada 2"),
				new Call("Llamada 3"),
				new Call("Llamada 4"));

		runTest(calls);

		assertEquals("DIRECTOR should not take calls", 0, employees.stream()
			.filter(e -> e.getHandledCalls() > 0)
			.filter(e -> e.getType() == EmployeeType.DIRECTOR)
			.count());

		assertEquals("One SUPERVISOR should take calls", 1, employees.stream()
			.filter(e -> e.getHandledCalls() > 0)
			.filter(e -> e.getType() == EmployeeType.SUPERVISOR)
			.count());
	}

	@Test
	public void everyoneShouldHandleCalls() {

		List<Call> calls = Arrays.asList(new Call("Llamada 1"),
				new Call("Llamada 2"),
				new Call("Llamada 3"),
				new Call("Llamada 4"),
				new Call("Llamada 5"),
				new Call("Llamada 6"));

		runTest(calls);

		assertEquals("Everyone sholud have handled a call", 0, employees.stream()
			.filter(e -> e.getHandledCalls() == 0)
			.count());
	}

}
