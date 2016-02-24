package com.babeeta.butterfly.application.third.service.message;

public interface FeedbackService {
	public void expiredMessageFeedback();
	
	public boolean initFeedbackAccount();
	
	public void release();
}
