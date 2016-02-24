package com.babeeta.butterfly.application.management.util;

import java.util.Random;

public class RandomGenerator {

	
	private static final String RANGE="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static synchronized String getRadomString()
	{
		Random rd=new Random();
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<4;i++)
		{
			sb.append(RANGE.charAt(rd.nextInt(RANGE.length())));
		}
		return sb.toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(RandomGenerator.getRadomString());
	}

}
