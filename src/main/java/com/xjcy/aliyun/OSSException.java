package com.xjcy.aliyun;

public class OSSException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2911926107629032799L;
	
	public OSSException(String errMsg)
	{
		super(errMsg);
	}
}
