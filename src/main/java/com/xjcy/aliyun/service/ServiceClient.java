package com.xjcy.aliyun.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.xjcy.aliyun.event.PutObjectListener;
import com.xjcy.aliyun.utils.SignUtils;

public abstract class ServiceClient
{
	private static final Logger logger = Logger.getLogger(ServiceClient.class);

	protected String accessKeyId;
	protected String secretAccessKey;
	private static final int CHUNK_SIZE = 10240;

	protected synchronized boolean sign(String resourcePath, Map<String, String> headers)
	{
		return SignUtils.sign(resourcePath, headers, accessKeyId, secretAccessKey);
	}

	protected void sendRequestCore(String uri, InputStream input, Map<String, String> headers,
			PutObjectListener listener) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection) new URL(uri).openConnection();
		// 发送POST请求必须设置如下两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setChunkedStreamingMode(CHUNK_SIZE);
		conn.setRequestMethod("PUT");
		// 赋值header
		addHeaders(conn, headers);
		// 连接
		conn.connect();

		// 发送请求参数
		if (input != null)
		{
			byte[] data = new byte[CHUNK_SIZE];
			OutputStream os = conn.getOutputStream();
			int ch;
			if (listener != null)
			{
				int sum = 0;
				int total = input.available();
				while ((ch = input.read(data, 0, data.length)) != -1)
				{
					os.write(data, 0, ch);
					sum += ch;
					listener.upload(ch, sum, total);
				}
			}
			else
			{
				while ((ch = input.read(data, 0, data.length)) != -1)
				{
					os.write(data, 0, ch);
				}
			}
			os.flush();
			os.close();
			input.close();
		}
		// ResponseMessage msg = new ResponseMessage(request);
		// msg.setStatusCode(conn.getResponseCode());
		// msg.setUrl(request.getUri());
		// msg.setResponseHeaders(conn.getHeaderFields());
		if (logger.isDebugEnabled())
		{
			logger.debug("responseCode=" + conn.getResponseCode());
			Set<Entry<String, List<String>>> headers2 = conn.getHeaderFields().entrySet();
			for (Entry<String, List<String>> entry : headers2)
			{
				logger.debug(entry.getKey() + ":" + entry.getValue().get(0));
			}
		}
		conn.disconnect();
	}

	private static void addHeaders(HttpURLConnection conn, Map<String, String> map)
	{
		for (Entry<String, String> entry : map.entrySet())
		{
			conn.addRequestProperty(entry.getKey(), entry.getValue());
		}
	}
}
