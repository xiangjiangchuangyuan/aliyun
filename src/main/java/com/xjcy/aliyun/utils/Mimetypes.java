/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.xjcy.aliyun.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Utility class used to determine the mimetype of files based on file
 * extensions.
 */
public class Mimetypes
{

	private static final Logger logger = Logger.getLogger(Mimetypes.class);

	/* The default MIME type */
	public static final String DEFAULT_MIMETYPE = "application/octet-stream";

	private static Mimetypes mimetypes = null;

	private final Map<String, String> mimeMap = new HashMap<>();

	public synchronized static Mimetypes getInstance()
	{
		if (mimetypes == null)
			mimetypes = new Mimetypes();
		return mimetypes;
	}

	public String getMimetype(String fileName)
	{
		int lastPeriodIndex = fileName.lastIndexOf(".");
		if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length())
		{
			String ext = fileName.substring(lastPeriodIndex + 1).toLowerCase();
			if (mimeMap.containsKey(ext))
				return mimeMap.get(ext);
		}
		return DEFAULT_MIMETYPE;
	}

	private Mimetypes()
	{
		if (mimeMap.isEmpty())
		{
			try
			{
				InputStream is = Mimetypes.class.getClassLoader().getResourceAsStream("mime.types");
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = null;

				while ((line = br.readLine()) != null)
				{
					line = line.trim();
					if (line.startsWith("#") || line.length() == 0)
					{
						// Ignore comments and empty lines.
					}
					else
					{
						StringTokenizer st = new StringTokenizer(line, " \t");
						if (st.countTokens() > 1)
						{
							String extension = st.nextToken();
							if (st.hasMoreTokens())
							{
								String mimetype = st.nextToken();
								mimeMap.put(extension.toLowerCase(), mimetype);
							}
						}
					}
				}
			}
			catch (IOException e)
			{
				logger.error("加载mime.types失败", e);
			}
		}
	}
}
