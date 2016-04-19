package org.lambadaframework.aws;

import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class S3 extends AWSTools {


    /**
     * Checks if the given file exists on S3 Bucket
     *
     * @return True on existing file, false on not existing
     */
    public static boolean doesFileExists(String bucket, String key) {
        return getS3Client().doesObjectExist(bucket, key);
    }

    /**
     * Reads the file from S3 bucket and returns as a string.
     *
     * @return File content
     * @throws IOException
     */
    public static String getFile(String bucket, String key) throws IOException {
        S3Object object = getS3Client().getObject(
                new GetObjectRequest(bucket, key));
        InputStream objectData = object.getObjectContent();
        String theString = IOUtils.toString(objectData);
        objectData.close();
        return theString;
    }

    /**
     * Creates new bucket if not existing
     *
     * @param bucket Bucket
     * @param region Region
     * @return true on success, false on failure
     */
    public static boolean createBucketIfNotExists(String bucket, String region) {

        if (getS3Client().doesBucketExist(bucket)) {
            return false;
        }

        getS3Client().createBucket(new CreateBucketRequest(bucket, region));
        return true;
    }
}
