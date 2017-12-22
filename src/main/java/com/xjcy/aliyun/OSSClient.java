package com.xjcy.aliyun;

import java.io.File;
import java.io.InputStream;

import com.xjcy.aliyun.event.PutObjectListener;
import com.xjcy.aliyun.service.DefaultServiceClient;
import com.xjcy.util.StringUtils;

public class OSSClient
{
	private static OSSClient oss;
	private final String defaultBucket;
	private final DefaultServiceClient client;

	private OSSClient(String accessKeyId, String secretAccessKey, String bucket)
	{
		this.client = new DefaultServiceClient(accessKeyId, secretAccessKey);
		this.defaultBucket = bucket;
	}

	public static void register(String accessKeyId, String secretAccessKey)
	{
		register(accessKeyId, secretAccessKey, null);
	}

	public static void register(String accessKeyId, String secretAccessKey, String bucket)
	{
		oss = new OSSClient(accessKeyId, secretAccessKey, bucket);
	}

	public static OSSClient getInstance()
	{
		return oss;
	}
	
	public boolean putObject(String key, File file) throws OSSException
	{
		if (StringUtils.isEmpty(defaultBucket))
			throw new OSSException("请调用OSSClient.register时增加bucket参数");
		return putObject(defaultBucket, key, file, null);
	}
	
	public boolean putObject(String key, File file, PutObjectListener listener) throws OSSException
	{
		if (StringUtils.isEmpty(defaultBucket))
			throw new OSSException("请调用OSSClient.register时增加bucket参数");
		return putObject(defaultBucket, key, file, listener);
	}
	
	public boolean putObject(String bucketName, String key, File file, PutObjectListener listener) throws OSSException
	{
		return this.client.sendRequest(bucketName, key, file, listener);
	}

	public boolean putObject(String key, InputStream input) throws OSSException
	{
		if (StringUtils.isEmpty(defaultBucket))
			throw new OSSException("请调用OSSClient.register时增加bucket参数");
		return putObject(defaultBucket, key, input, null);
	}

	public boolean putObject(String key, InputStream input, PutObjectListener listener) throws OSSException
	{
		if (StringUtils.isEmpty(defaultBucket))
			throw new OSSException("请调用OSSClient.register时增加bucket参数");
		return putObject(defaultBucket, key, input, listener);
	}

	public boolean putObject(String bucketName, String key, InputStream input, PutObjectListener listener) throws OSSException
	{
		return this.client.sendRequest(bucketName, key, input, listener);
	}
}
