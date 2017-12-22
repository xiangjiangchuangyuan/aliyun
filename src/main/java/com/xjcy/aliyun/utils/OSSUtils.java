package com.xjcy.aliyun.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.UUID;

public class OSSUtils
{
	public static String createOssKey(String suffix)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		String date = df.format(new Date());
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		return date + "/" + uuid + suffix;
	}
	
	// RFC 822 Date Format
	private static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

	/**
	 * Formats Date to GMT string.
	 */
	public static String formatRfc822Date(Date date)
	{
		SimpleDateFormat rfc822DateFormat = new SimpleDateFormat(RFC822_DATE_FORMAT, Locale.US);
		rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
		return rfc822DateFormat.format(date);
	}
	
	private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static String formatIso8601Date(Date date)
	{
		SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
		df.setTimeZone(new SimpleTimeZone(0, "GMT"));
		return df.format(date);
	}
}
