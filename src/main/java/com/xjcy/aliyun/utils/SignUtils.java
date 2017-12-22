package com.xjcy.aliyun.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.dsig.SignatureMethod;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;

public class SignUtils
{
	private static final Logger logger = Logger.getLogger(SignUtils.class);

	static Mac mac = null;
	static Object obj = new Object();
	static Mac mac2 = null;
	static Object obj2 = new Object();

	private static final String NEW_LINE = "\n";
	private static final String SEPARATOR = "&";
	private static final String EQUAL = "=";
	private static final String ALGORITHM = "HmacSHA1";
	private static final String ENCODE_TYPE = "UTF-8";

	public static boolean sign(String resourcePath, Map<String, String> headers, String accessKeyId,
			String secretAccessKey)
	{
		try
		{
			StringBuilder signStr = new StringBuilder();
			signStr.append("PUT").append(NEW_LINE);
			signStr.append(NEW_LINE);
			signStr.append(headers.get("Content-Type")).append(NEW_LINE);
			signStr.append(headers.get("Date")).append(NEW_LINE);
			signStr.append(resourcePath);
			synchronized (obj)
			{
				if (mac == null)
				{
					mac = Mac.getInstance(ALGORITHM);
					mac.init(new SecretKeySpec(secretAccessKey.getBytes(ENCODE_TYPE), ALGORITHM));
				}
			}
			byte[] bytes = mac.doFinal(signStr.toString().getBytes(ENCODE_TYPE));
			String signature = Base64.encodeBase64String(bytes);
			headers.put("Authorization", "OSS " + accessKeyId + ":" + signature);
			return true;
		}
		catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e)
		{
			logger.error("加密失败", e);
		}
		return false;
	}

	public static String signURL(Map<String, String> parameterMap, String accessKeySecret, String url)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException
	{
		// 对参数进行排序
		List<String> sortedKeys = new ArrayList<>(parameterMap.keySet());
		Collections.sort(sortedKeys);

		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append("GET").append(SEPARATOR);
		stringToSign.append(percentEncode("/")).append(SEPARATOR);
		StringBuilder canonicalizedQueryString = new StringBuilder();
		for (String key : sortedKeys)
		{
			// 此处需要对key和value进行编码
			String value = parameterMap.get(key);
			canonicalizedQueryString.append(SEPARATOR).append(percentEncode(key)).append(EQUAL)
					.append(percentEncode(value));
		}
		// 此处需要对canonicalizedQueryString进行编码
		stringToSign.append(percentEncode(canonicalizedQueryString.toString().substring(1)));
		synchronized (obj2)
		{
			if (mac2 == null)
			{
				mac2 = Mac.getInstance(ALGORITHM);
				mac2.init(new SecretKeySpec((accessKeySecret + SEPARATOR).getBytes(ENCODE_TYPE),
						SignatureMethod.HMAC_SHA1));
			}
		}
		byte[] bytes = mac2.doFinal(stringToSign.toString().getBytes(ENCODE_TYPE));
		String signature = URLEncoder.encode(Base64.encodeBase64String(bytes), ENCODE_TYPE);
		// 生成请求URL
		StringBuilder requestURL = new StringBuilder(url);
		requestURL.append("?Signature=").append(signature);
		for (Map.Entry<String, String> e : parameterMap.entrySet())
		{
			requestURL.append("&").append(e.getKey()).append("=").append(percentEncode(e.getValue()));
		}
		return requestURL.toString();
	}

	private static String percentEncode(String value) throws UnsupportedEncodingException
	{
		if (value == null)
			return "";
		String encoded = URLEncoder.encode(value, ENCODE_TYPE);
		return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
	}
}
