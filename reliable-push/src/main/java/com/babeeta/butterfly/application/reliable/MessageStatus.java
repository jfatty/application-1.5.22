package com.babeeta.butterfly.application.reliable;

/**
 * @author lisong
 *
 */
public enum MessageStatus {
	/**
	 * 投递中，未ack
	 */
	DELIVERING,
	/**
	 * 已确认
	 */
	ACKED,
	
	/**
	 * 
	 */
	APP_ACKED,
	/**
	 * 已经过期
	 */
	EXPIRED,
	
	/**
	 * 
	 */
	EXPIRED_NOTIFIED,
	/**
	 *过期且已经ack
	 */
	EXPIRED_ACKED,
	
	/**
	 * 
	 */
	DELETED,
	
	/**
	 * 
	 */
	DELAYING
}
