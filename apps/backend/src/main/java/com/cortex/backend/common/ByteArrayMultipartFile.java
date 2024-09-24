package com.cortex.backend.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

public class ByteArrayMultipartFile implements MultipartFile {
  private final byte[] content;
  private final String name;
  private final String originalFilename;
  private final String contentType;

  public ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
    this.name = name;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
    this.content = content;
  }

  @Override
  @NonNull
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return originalFilename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return content == null || content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  @NonNull
  public byte[] getBytes() {
    return content;
  }

  @Override
  @NonNull
  public InputStream getInputStream() {
    return new ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(@NonNull File dest) {
    throw new UnsupportedOperationException("transferTo() is not supported");
  }
}