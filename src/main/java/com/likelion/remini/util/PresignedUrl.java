package com.likelion.remini.util;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PresignedUrl {
    private static final String S3_BUCKET = "remini-bucket";

    private static AmazonS3Client amazonS3Client;

    @Autowired
    private PresignedUrl(AmazonS3Client amazonS3Client) {
        PresignedUrl.amazonS3Client = amazonS3Client;
    }

    public static String getPresignedUrl(String filename) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(S3_BUCKET, filename)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(getExpiration());

        return amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    private static Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
