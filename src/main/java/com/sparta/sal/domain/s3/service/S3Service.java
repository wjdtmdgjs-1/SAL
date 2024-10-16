package com.sparta.sal.domain.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.sal.common.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3client;
    private final String bucketName = "juho-first-s3";
    private final String region = "ap-northeast-2";

    public String uploadFile(MultipartFile file) throws IOException {
        // 파일 형식 체크
        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new InvalidRequestException("지원되지 않는 파일 형식입니다.");
        }
        // 파일 크기 체크
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new InvalidRequestException("파일 크기가 제한을 초과했습니다.");
        }
        // S3에 파일 업로드
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), new ObjectMetadata()));

        return fileName;
    }

    // 조회
    public String changeToURL(String fileName) {
        if (isFileExist(fileName)) {
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
        }
        throw new NullPointerException("no attachment in s3");
    }

    // 삭제
    public void deleteFile(String attachment) {
        if (isFileExist(attachment)) {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, attachment);
            s3client.deleteObject(deleteObjectRequest);
        }
    }

    public boolean isFileExist(String fileName) {
        try {
            s3client.getObjectMetadata(bucketName, fileName);
            return true; // 파일이 존재함
        } catch (Exception e) {
            return false; // 파일이 존재하지 않음
        }
    }

    // 확장자명 확인
    private boolean isValidFileType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png") ||
                contentType.equals("application/pdf") || contentType.equals("text/csv");
    }
}