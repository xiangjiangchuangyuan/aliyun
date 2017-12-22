package com.xjcy.aliyun;

public class AcsException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9145588361899021994L;

	public AcsException()
	{
		super();
	}

	public AcsException(String errorMessage)
	{
		super(errorMessage);
	}
}
