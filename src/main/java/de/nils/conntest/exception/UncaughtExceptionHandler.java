package de.nils.conntest.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
	private static final Logger log = LoggerFactory.getLogger(UncaughtExceptionHandler.class);
	
	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
		try
		{
			log.error("Uncaught Exception", e);
		}
		catch(Throwable t2)
		{
			// Do nothing to prevent more exceptions
		}
	}
}
