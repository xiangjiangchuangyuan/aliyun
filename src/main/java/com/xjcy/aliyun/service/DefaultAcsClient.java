package com.xjcy.aliyun.service;

import java.util.Map;

import org.apache.log4j.Logger;

import com.xjcy.aliyun.AcsClient;
import com.xjcy.util.StringUtils;
import com.xjcy.util.http.WebClient;

public class DefaultAcsClient extends AcsClient
{
	private static final Logger logger = Logger.getLogger(DefaultAcsClient.class);

	private static final String URL_MTS = "http://mts.cn-beijing.aliyuncs.com/";
	private static final String URL_SMS = "http://dysmsapi.aliyuncs.com/";
	private static final String URL_STS = "https://sts.aliyuncs.com/";
	private static final String URL_RAM = "https://ram.aliyuncs.com/";

	private static final String JSON_INPUT = "{\"Bucket\":\"%s\",\"Location\":\"oss-cn-beijing\",\"Object\":\"%s\"}";
	private static final String JSON_CONFIG = "{\"OutputFile\":{\"Bucket\":\"%s\",\"Location\":\"oss-cn-beijing\",\"Object\":\"%s\"},\"Time\":\"5\"}";
	private static final String JSON_OUTPUTS = "[{\"OutputObject\":\"%s\",\"TemplateId\":\"%s\"}]";

	public DefaultAcsClient(String accessKeyId, String secretAccessKey, String bucket, String pipeline)
	{
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = secretAccessKey;
		this.defaultBucket = bucket;
		this.defaultPipelineId = pipeline;
	}

	@Override
	public boolean cutFirst(String pipelineId, String bucketName, String videoKey, String picKey)
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("SubmitSnapshotJob");
		// 加入方法特有参数
		parameterMap.put("PageSize", "2");
		parameterMap.put("PipelineId", pipelineId);
		parameterMap.put("Input", String.format(JSON_INPUT, bucketName, videoKey));
		parameterMap.put("SnapshotConfig", String.format(JSON_CONFIG, bucketName, picKey));
		String requestURL = signRequest(parameterMap, URL_MTS);
		String json = WebClient.downloadString(requestURL);
		if (logger.isDebugEnabled())
			logger.debug("CutFirst result=>" + json);
		if (json.contains("\"State\":\"Success\""))
			return true;
		return false;
	}

	@Override
	public String queryMediaInfo(String bucketName, String videoKey)
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("SubmitMediaInfoJob");
		// 加入方法特有参数
		parameterMap.put("Input", String.format(JSON_INPUT, bucketName, videoKey));

		String requestURL = signRequest(parameterMap, URL_MTS);
		String json = WebClient.downloadString(requestURL);
		if (logger.isDebugEnabled())
			logger.debug("CutFirst result=>" + json);
		return json;
	}

	@Override
	public boolean amr2mp3(String pipelineId, String bucketName, String amrKey, String mp3Key, String templeteId)
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("SubmitJobs");
		// 加入方法特有参数
		parameterMap.put("OutputBucket", bucketName);
		parameterMap.put("OutputLocation", "oss-cn-beijing");
		parameterMap.put("PipelineId", pipelineId);
		parameterMap.put("Input", String.format(JSON_INPUT, bucketName, amrKey));
		parameterMap.put("Outputs", String.format(JSON_OUTPUTS, mp3Key, templeteId));

		String requestURL = signRequest(parameterMap, URL_MTS);
		String json = WebClient.downloadString(requestURL);
		if (logger.isDebugEnabled())
			logger.debug("Amr2mp3 result=>" + json);
		if (json.contains("\"Success\":true"))
			return true;
		return false;
	}

	@Override
	public String queryPipelineId()
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("SearchPipeline");
		// 加入方法特有参数
		parameterMap.put("State", "All");

		String requestURL = signRequest(parameterMap, URL_MTS);
		return WebClient.downloadString(requestURL.toString());
	}

	@Override
	public String AddTemplate()
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("AddTemplate");
		// 加入方法特有参数
		parameterMap.put("Name", "aac2mp3");
		parameterMap.put("Audio", "{\"Codec\":\"MP3\"}");
		
		String requestURL = signRequest(parameterMap, URL_MTS);
		return WebClient.downloadString(requestURL.toString());
	}

	@Override
	public boolean sendSms(String signName, String mobile, String templateCode, String templateParam)
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("SendSms");
		// 加入方法特有参数
		parameterMap.put("Version", "2017-05-25");
		parameterMap.put("RegionId", "cn-hangzhou");
		parameterMap.put("PhoneNumbers", mobile);
		parameterMap.put("SignName", signName);
		parameterMap.put("TemplateCode", templateCode);
		if (!StringUtils.isEmpty(templateParam))
			parameterMap.put("TemplateParam", templateParam);

		String requestURL = signRequest(parameterMap, URL_SMS);
		String json = WebClient.downloadString(requestURL.toString());
		if (logger.isDebugEnabled())
			logger.debug("Sendsms result=>" + json);
		if (json.contains("\"Message\":\"OK\""))
			return true;
		return false;
	}

	@Override
	public String createAssumeRole(String roleArn, String session, int seconds)
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("AssumeRole");
		parameterMap.put("Version", "2015-04-01");
		// 加入方法特有参数
		parameterMap.put("RoleArn", roleArn);
		parameterMap.put("RoleSessionName", session);
		parameterMap.put("DurationSeconds", seconds + "");

		String requestURL = signRequest(parameterMap, URL_STS);
		String json = WebClient.downloadString(requestURL.toString());
		if (logger.isDebugEnabled())
			logger.debug("Sendsms result=>" + json);
		return json;
	}

	@Override
	public String queryRoles()
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("ListRoles");
		parameterMap.put("Version", "2015-05-01");

		String requestURL = signRequest(parameterMap, URL_RAM);
		String json = WebClient.downloadString(requestURL.toString());
		if (logger.isDebugEnabled())
			logger.debug("ListRoles result=>" + json);
		return json;
	}

	static final String ROLE_SS = "{\"Statement\":[{\"Action\":\"sts:AssumeRole\",\"Effect\":\"Allow\",\"Principal\":{\"RAM\":[\"acs:ram::%s:root\"]}}],\"Version\":\"1\"}";

	@Override
	public String createRole(String roleName, long accountId)
	{
		// 加入请求公共参数
		Map<String, String> parameterMap = getCommonPara("CreateRole");
		parameterMap.put("Version", "2015-05-01");
		// 加入方法特有参数
		parameterMap.put("RoleName", roleName);
		parameterMap.put("AssumeRolePolicyDocument", String.format(ROLE_SS, accountId));

		String requestURL = signRequest(parameterMap, URL_RAM);
		String json = WebClient.downloadString(requestURL.toString());
		if (logger.isDebugEnabled())
			logger.debug("Sendsms result=>" + json);
		return json;
	}

}
