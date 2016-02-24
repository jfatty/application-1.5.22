package com.babeeta.butterfly.application.third.resource;

public enum MessageLifeType {

	DAY ("day",24),
	WEEK ("week",168),
	MONTH("month",720),
	YEAR("year",8760),
	NEVER("never",-1);
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	private String type;
	
	private int value;
	
	private MessageLifeType(String type,int value)
	{
		this.type=type;
		this.value=value;
	}
	
	public static int getLift(String life)
	{
		if(life.equals(MessageLifeType.DAY.type))
		{
			return MessageLifeType.DAY.getValue();
		}
		if(life.equals(MessageLifeType.WEEK.type))
		{
			return MessageLifeType.WEEK.getValue();
		}
		if(life.equalsIgnoreCase(MessageLifeType.MONTH.type))
		{
			return MessageLifeType.MONTH.getValue();
		}
		if(life.equals(MessageLifeType.YEAR.type))
		{
			return MessageLifeType.YEAR.getValue();
		}
		return MessageLifeType.DAY.getValue();
	}
}
