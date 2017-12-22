package com.xjcy.aliyun;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.xjcy.aliyun.service.DefaultAcsClient;
import com.xjcy.aliyun.utils.OSSUtils;
import com.xjcy.aliyun.utils.SignUtils;
import com.xjcy.util.StringUtils;

public abstract class AcsClient
{
	private static final Logger logger = Logger.getLogger(AcsClient.class);

	protected String accessKeyId;
	protected String accessKeySecret;
	protected String defaultBucket;
	protected String defaultPipelineId;

	private static DefaultAcsClient acs;

	public static void register(String accessKeyId, String secretAccessKey)
	{
		register(accessKeyId, secretAccessKey, null, null);
	}

	public static void register(String accessKeyId, String secretAccessKey, String bucket, String pipeline)
	{
		acs = new DefaultAcsClient(accessKeyId, secretAccessKey, bucket, pipeline);
	}

	public static AcsClient getInstance()
	{
		return acs;
	}

	public abstract String queryPipelineId();

	public boolean cutFirst(String videoKey, String picKey) throws AcsException
	{
		if (StringUtils.isEmpty(defaultPipelineId))
			throw new AcsException("请调用AcsClient.register时增加pipeline参数");
		if (StringUtils.isEmpty(defaultBucket))
			throw new AcsException("请调用AcsClient.register时增加bucket参数");
		return cutFirst(defaultPipelineId, defaultBucket, videoKey, picKey);
	}

	public abstract boolean cutFirst(String pipelineId, String bucketName, String videoKey, String picKey);

	public boolean amr2mp3(String amrKey, String mp3Key) throws AcsException
	{
		if (StringUtils.isEmpty(defaultPipelineId))
			throw new AcsException("请调用AcsClient.register时增加pipeline参数");
		if (StringUtils.isEmpty(defaultBucket))
			throw new AcsException("请调用AcsClient.register时增加bucket参数");
		return amr2mp3(defaultPipelineId, defaultBucket, amrKey, mp3Key);
	}

	public abstract boolean amr2mp3(String pipelineId, String bucketName, String amrKey, String mp3Key);

	public abstract boolean sendSms(String signName, String mobile, String templateCode, String templateParam);

	public abstract String createAssumeRole(String roleArn, String session, int seconds);

	public abstract String createRole(String roleName, long accountId);

	public abstract String queryRoles();
	
	public int queryMediaInfo(String videoKey)
	{
		if (StringUtils.isEmpty(defaultBucket))
			throw new AcsException("请调用AcsClient.register时增加bucket参数");
		return queryMediaInfo(defaultBucket, videoKey);
	}
	
	public abstract int queryMediaInfo(String bucketName, String videoKey);

	protected Map<String, String> getCommonPara(String action)
	{
		Map<String, String> parameterMap = new HashMap<>();
		parameterMap.put("Action", action);
		parameterMap.put("Version", "2014-06-18");
		parameterMap.put("AccessKeyId", accessKeyId); // 此处请替换成您自己的AccessKeyId
		parameterMap.put("Timestamp", OSSUtils.formatIso8601Date(new Date()));
		parameterMap.put("SignatureMethod", "HMAC-SHA1");
		parameterMap.put("SignatureVersion", "1.0");
		parameterMap.put("SignatureNonce", UUID.randomUUID().toString());
		parameterMap.put("Format", "JSON");
		return parameterMap;
	}

	protected String signRequest(Map<String, String> parameterMap, String url)
	{
		try
		{
			return SignUtils.signURL(parameterMap, accessKeySecret, url);
		}
		catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException e)
		{
			logger.error("AcsClient签名失败", e);
		}
		return null;
	}
}
