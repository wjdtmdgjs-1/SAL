package com.sparta.sal.domain.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.common.service.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 s3client;

    @InjectMocks
    private S3Service s3Service;

    private MultipartFile mockFile;

    @Test
    void uploadFile_ShouldUploadFile_WhenFileIsValid() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockFile = mock(MultipartFile.class);
        ReflectionTestUtils.setField(s3Service, "s3client", s3client);

        String fileName = "test.jpg";
        byte[] content = "test content".getBytes();
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getSize()).thenReturn((long) content.length);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        String uploadedFileName = s3Service.uploadFile(mockFile);

        assertNotNull(uploadedFileName);
        verify(s3client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void uploadFile_ShouldThrowException_WhenFileTypeIsInvalid() {
        MockitoAnnotations.openMocks(this);
        mockFile = mock(MultipartFile.class);
        ReflectionTestUtils.setField(s3Service, "s3client", s3client);

        when(mockFile.getContentType()).thenReturn("text/plain");

        InvalidRequestException thrown = assertThrows(InvalidRequestException.class, () -> {
            s3Service.uploadFile(mockFile);
        });
        assertEquals("지원되지 않는 파일 형식입니다.", thrown.getMessage());
    }

    @Test
    void uploadFile_ShouldThrowException_WhenFileSizeExceedsLimit() {
        MockitoAnnotations.openMocks(this);
        mockFile = mock(MultipartFile.class);
        ReflectionTestUtils.setField(s3Service, "s3client", s3client);

        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn(6 * 1024 * 1024L);

        InvalidRequestException thrown = assertThrows(InvalidRequestException.class, () -> {
            s3Service.uploadFile(mockFile);
        });
        assertEquals("파일 크기가 제한을 초과했습니다.", thrown.getMessage());
    }

    @Test
    void deleteFile_ShouldDeleteFile_WhenFileExists() {
        MockitoAnnotations.openMocks(this);
        mockFile = mock(MultipartFile.class);
        ReflectionTestUtils.setField(s3Service, "s3client", s3client);

        String fileName = "test.jpg";
        when(s3client.getObjectMetadata(anyString(), anyString())).thenReturn(new ObjectMetadata());

        s3Service.deleteFile(fileName);

        verify(s3client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_ShouldDoNothing_WhenFileDoesNotExist() {
        MockitoAnnotations.openMocks(this);
        mockFile = mock(MultipartFile.class);
        ReflectionTestUtils.setField(s3Service, "s3client", s3client);

        String fileName = "nonexistent.jpg";
        when(s3client.getObjectMetadata(anyString(), anyString())).thenThrow(new RuntimeException());

        s3Service.deleteFile(fileName);

        verify(s3client, times(0)).deleteObject(any(DeleteObjectRequest.class));
    }
}
