package com.babeeta.butterfly.application.third.resource;

public class MessageConfig {
	private int lifeValueMin;
	private int lifeValueMax;
	private int lifeValueDefault;

	private int delayValueMin;
	private int delayValueMax;
	private int delayValueDefault;

	private int lengthValueMax;

	public int getLifeValueMin() {
		return lifeValueMin;
	}

	public void setLifeValueMin(int lifeValueMin) {
		this.lifeValueMin = lifeValueMin;
	}

	public void setLifeValueMax(int lifeValueMax) {
		this.lifeValueMax = lifeValueMax;
	}

	public void setLifeValueDefault(int lifeValueDefault) {
		this.lifeValueDefault = lifeValueDefault;
	}

	public void setDelayValueMin(int delayValueMin) {
		this.delayValueMin = delayValueMin;
	}

	public void setDelayValueMax(int delayValueMax) {
		this.delayValueMax = delayValueMax;
	}

	public void setDelayValueDefault(int delayValueDefault) {
		this.delayValueDefault = delayValueDefault;
	}

	public void setLengthValueMax(int lengthValueMax) {
		this.lengthValueMax = lengthValueMax;
	}

	public int getLifeValueMax() {
		return lifeValueMax;
	}

	public int getLifeValueDefault() {
		return lifeValueDefault;
	}

	public int getDelayValueMin() {
		return delayValueMin;
	}

	public int getDelayValueMax() {
		return delayValueMax;
	}

	public int getDelayValueDefault() {
		return delayValueDefault;
	}

	public int getLengthValueMax() {
		return lengthValueMax;
	}

	public int verifyLifeValue(int old) {
		if (old >= lifeValueMin && old <= lifeValueMax) {
			return old;
		} else {
			return lifeValueDefault;
		}
	}

	public int verifyDelayValue(int old) {
		if (old >= delayValueMin && old <= delayValueMax) {
			return old;
		} else {
			return delayValueDefault;
		}
	}
}