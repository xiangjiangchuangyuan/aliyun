package com.xjcy.aliyun.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xjcy.aliyun.OSSException;
import com.xjcy.aliyun.event.PutObjectListener;
import com.xjcy.aliyun.utils.Mimetypes;
import com.xjcy.aliyun.utils.OSSUtils;
import com.xjcy.util.STR;

public class DefaultServiceClient extends ServiceClient
{
	private static final Logger logger = Logger.getLogger(DefaultServiceClient.class);

	private static final String endpoint = "http://%s.oss-cn-beijing.aliyuncs.com";

	public DefaultServiceClient(String accessKeyId, String secretAccessKey)
	{
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
	}

	public boolean sendRequest(String bucketName, String key, File file, PutObjectListener listener) throws OSSException
	{
		InputStream input;
		try
		{
			input = new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			throw new OSSException("文件不存在");
		}
		return sendRequest(bucketName, key, input, listener);
	}

	public boolean sendRequest(String bucketName, String key, InputStream inputStream, PutObjectListener listener)
			throws OSSException
	{
		Map<String, String> headers = new HashMap<>();
		headers.put(STR.HEADER_DATE, OSSUtils.formatRfc822Date(new Date()));
		headers.put(STR.HEADER_CONTENT_TYPE, Mimetypes.getInstance().getMimetype(key));
		String resourcePath = "/" + bucketName + "/" + key;
		if (sign(resourcePath, headers))
		{
			String uri = String.format(endpoint, bucketName) + "/" + key;
			try
			{
				long start = System.currentTimeMillis();
				if (logger.isDebugEnabled())
					logger.debug("开始上传=>" + resourcePath);
				sendRequestCore(uri, inputStream, headers, listener);
				if (logger.isDebugEnabled())
					logger.debug(resourcePath + "上传完成=>" + (System.currentTimeMillis() - start) + "ms");
				return true;
			}
			catch (IOException e)
			{
				logger.error("PutObject失败", e);
			}
			return false;
		}
		logger.debug("签名失败");
		throw new OSSException("签名失败");
	}

}
