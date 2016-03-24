package org.lambadaframework.aws;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class S3File extends AWSTools {


    private String bucket;

    private String key;

    public S3File(String bucket, String key) {
        this.bucket = bucket;
        this.key = key;
    }

    /**
     * Checks if the given file exists on S3 Bucket
     *
     * @return True on existing file, false on not existing
     */
    public boolean isFileExists() {
        return getS3Client().doesObjectExist(bucket, key);
    }

    /**
     * Reads the file from S3 bucket and returns as a string.
     *
     * @return File content
     * @throws IOException
     */
    public String getFile() throws IOException {
        S3Object object = getS3Client().getObject(
                new GetObjectRequest(bucket, key));
        InputStream objectData = object.getObjectContent();
        String theString = IOUtils.toString(objectData);
        objectData.close();
        return theString;
    }
}
