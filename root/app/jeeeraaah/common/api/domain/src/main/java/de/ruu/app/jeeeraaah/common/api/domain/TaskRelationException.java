package de.ruu.app.jeeeraaah.common.api.domain;

import java.io.Serial;

public class TaskRelationException extends RuntimeException
{
	@Serial private static final long serialVersionUID = 1L;
	
	public TaskRelationException(String text) { super(text); }
}
