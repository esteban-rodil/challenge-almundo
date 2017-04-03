package com.almundo.challenge.domain;

public enum EmployeeType {

	OPERATOR(0), SUPERVISOR(1), DIRECTOR(2);

	private final int order;

	private EmployeeType(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

}
