package com.github.leeyazhou.crpc.core.lifecyle;

/**
 * control the life cyle of a component.
 * 
 * @author leeyazhou
 */
public interface Lifecycle {

	/**
	 * init
	 */
	void init();

	/**
	 * start
	 */
	void start();

	/**
	 * stop
	 */
	void stop();

	/**
	 * whether is running?
	 * 
	 * @return true if the component is running, or false.
	 */
	boolean isRunning();

}
