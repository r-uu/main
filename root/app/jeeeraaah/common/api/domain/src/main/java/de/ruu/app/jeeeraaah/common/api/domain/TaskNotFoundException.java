package de.ruu.app.jeeeraaah.common.api.domain;

import java.io.Serial;

public class TaskNotFoundException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	public TaskNotFoundException(String text) {
		super(text);
	}
}
