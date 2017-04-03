package com.almundo.challenge;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.almundo.challenge.domain.Call;
import com.almundo.challenge.domain.Employee;

/**
 * Main class of the system, handles the logic of the call center.
 */
public class Dispatcher {

	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	private static final int MAXIMUM_SIMULTANEUS_CALLS = 10;

	private Queue<Call> pendingCalls = new ConcurrentLinkedQueue<>();

	private List<Employee> employees = new LinkedList<>();

	private ExecutorService executor = Executors.newFixedThreadPool(MAXIMUM_SIMULTANEUS_CALLS);

	private boolean isShutdown;

	/**
	 * Receives a call and dispatch it to the proper Employee to handle it.
	 *
	 * It can process a maximum of {@value #MAXIMUM_SIMULTANEUS_CALLS} and the
	 * rest are queued. If there are no employees available, when an employee
	 * finishes with a call, it is assigned a new from the pending ones.
	 *
	 * If {@link #close()} was called, the methods returns without performing
	 * any operation.
	 *
	 * @param call
	 *            The call to handle.
	 */
	public void dispatchCall(Call call) {
		if (isShutdown) {
			return;
		}
		dispatchCallInternal(call);
	}

	/**
	 * Obtains the employee which will handle the call and make him handle it.
	 * If no employee is available, enqueues the call.
	 *
	 * @param call the call
	 */
	private void dispatchCallInternal(Call call) {
		Optional<Employee> assignEmployee = assignNextEmployee();
		if (assignEmployee.isPresent()) {
			handleCall(assignEmployee.get(), call);
		} else {
			enqueue(call);
		}

	}

	/**
	 * Creates a new Runnable to be added to the Executor to handle the call.
	 *
	 * @param e the employee which will handle the call.
	 * @param c the call to be handled.
	 */
	private void handleCall(Employee e, Call c) {
		executor.execute(() -> run(e, c));
	}

	/**
	 * The Runnable task.
	 *
	 * @param e the employee which will handle the call.
	 * @param c the call to be handled.
	 */
	private void run(Employee e, Call c) {
		e.handle(c);
		notifyCallFinalization();
	}

	/**
	 * Enqueues a Call which could not be assigned to an employee, because there
	 * was none available.
	 *
	 * @param call
	 *            the call to enqueue
	 */
	private void enqueue(Call call) {
		pendingCalls.add(call);
	}

	/**
	 * Handles the finalization of the calls.
	 *
	 * If there are pending calls to handle, they are dispatched, if not, it
	 * tries to shutdown the dispatcher, if it was requested.
	 */
	private void notifyCallFinalization() {
		Call pendingCall = pendingCalls.poll();
		if (pendingCall != null) {
			dispatchCallInternal(pendingCall);
		} else {
			shutdownIfRequired();
		}
	}

	/**
	 * Shuts down the executor if the Dispatcher is closed.
	 */
	private void shutdownIfRequired() {
		if (isShutdown && ! areEmployeesAssigned()) {
			executor.shutdown();
			try {
				executor.awaitTermination(20L, TimeUnit.SECONDS);
				synchronized(this) {
					notify();
				}
			} catch (InterruptedException e) {
				logger.error("There was an error waiting for the calls to finish.", e);
			}
		}
	}

	private boolean areEmployeesAssigned() {
		return employees.stream().anyMatch(Employee::isAssigned);
	}

	/**
	 * Adds an employee to the dispatcher so that he can handle a call.
	 *
	 * @param e a new employee.
	 */
	public void addEmployee(Employee e) {
		employees.add(e);
	}

	/**
	 * Closes the Dispatcher, preventing new calls from being handled, but
	 * ensures the ones being handled, or queued, finish.
	 *
	 * It stops the current thread execution until all Calls finished.
	 */
	public synchronized void closeAndWait() {
		isShutdown = true;
		try {
			wait();
			logger.info("Dispatcher closed");
		} catch (InterruptedException e) {
			logger.error("There was an error waiting to close the dispatcher.", e);
		}
	}

	/**
	 * Assigns the next employee which has to take a call.
	 *
	 * It will always assign calls to Operators, then Supervisors and then
	 * Directors, in the case the preceding are not available to handle a new
	 * call.
	 *
	 * @return the assigned employee, or {@link Optional#empty()} if there is no
	 *         one available.
	 */
	private synchronized Optional<Employee> assignNextEmployee() {
		return employees.stream()
				.filter(Employee::isAvailable)
				.sorted()
				.map(Employee::assign)
				.findFirst();
	}
}
