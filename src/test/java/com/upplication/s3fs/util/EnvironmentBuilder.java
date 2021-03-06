package com.upplication.s3fs.util;

import static com.upplication.s3fs.AmazonS3Factory.ACCESS_KEY;
import static com.upplication.s3fs.AmazonS3Factory.SECRET_KEY;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;
import com.upplication.s3fs.FilesOperationsIT;

/**
 * Test Helper
 */
public abstract class EnvironmentBuilder {

	public static final String BUCKET_NAME_KEY = "bucket_name";

	/**
	 * Get credentials from environment vars, and if not found from amazon-test.properties
	 * @return Map with the credentials
	 */
	public static Map<String, Object> getRealEnv() {
		Map<String, Object> env = null;

		String accessKey = System.getenv(ACCESS_KEY);
		String secretKey = System.getenv(SECRET_KEY);

		if (accessKey != null && secretKey != null) {
			env = ImmutableMap.<String, Object> builder().put(ACCESS_KEY, accessKey).put(SECRET_KEY, secretKey).build();
		} else {
			final Properties props = new Properties();
			try {
				props.load(EnvironmentBuilder.class.getResourceAsStream("/amazon-test.properties"));
			} catch (IOException e) {
				throw new RuntimeException("not found amazon-test.properties in the classpath", e);
			}
			env = ImmutableMap.<String, Object> builder().put(ACCESS_KEY, props.getProperty(ACCESS_KEY)).put(SECRET_KEY, props.getProperty(SECRET_KEY)).build();
		}

		return env;
	}

	/**
	 * get default bucket name
	 * @return String without end separator
	 */
	public static String getBucket() {

		String bucketName = System.getenv(BUCKET_NAME_KEY);
		if (bucketName != null) {
			return bucketName;
		}
		final Properties props = new Properties();
		try {
			props.load(FilesOperationsIT.class.getResourceAsStream("/amazon-test.properties"));
			return props.getProperty(BUCKET_NAME_KEY);
		} catch (IOException e) {
			throw new RuntimeException("needed /amazon-test.properties in the classpath");
		}
	}

    public static URI getS3URI(URI s3GlobalUri) {
        Map<String, Object> env = getRealEnv();
        return URI.create(s3GlobalUri.getScheme() + "://" +
                env.get(ACCESS_KEY) + ":" + env.get(SECRET_KEY) +
                "@" + s3GlobalUri.getHost());
    }
}
