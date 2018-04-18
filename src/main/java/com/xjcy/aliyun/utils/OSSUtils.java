package com.xjcy.aliyun.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.UUID;

import com.xjcy.util.STR;

public class OSSUtils
{
	static final SimpleDateFormat dateFormat = new SimpleDateFormat(STR.DATE_SHORT);// 设置日期格式
	static final TimeZone gmtTZ = new SimpleTimeZone(0, "GMT");
	
	public static String createOssKey(String suffix)
	{
		StringBuffer ossKey = new StringBuffer(64);
		ossKey.append(dateFormat.format(new Date()));
		ossKey.append(STR.SLASH_LEFT);
		ossKey.append(UUID.randomUUID().toString().replaceAll(STR.HYPHEN, STR.EMPTY));
		ossKey.append(suffix);
		return ossKey.toString();
	}
	
	/**
	 * Formats Date to GMT string.
	 */
	public static String formatRfc822Date(Date date)
	{
		SimpleDateFormat rfc822DateFormat = new SimpleDateFormat(STR.DATE_RFC822, Locale.US);
		rfc822DateFormat.setTimeZone(gmtTZ);
		return rfc822DateFormat.format(date);
	}
	
	public static String formatIso8601Date(Date date)
	{
		SimpleDateFormat df = new SimpleDateFormat(STR.DATE_ISO8601);
		df.setTimeZone(gmtTZ);
		return df.format(date);
	}
}
