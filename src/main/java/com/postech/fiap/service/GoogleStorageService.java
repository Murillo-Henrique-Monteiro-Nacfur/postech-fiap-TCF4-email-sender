package com.postech.fiap.service;

import com.google.cloud.storage.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.nio.file.Paths;


@ApplicationScoped
public class GoogleStorageService {

    private final Storage storage;

    public GoogleStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public GoogleStorageService(Storage storage) {
        this.storage = storage;
    }


    public File downloadFile(String bucketName, String objectName, String destPath) {
        BlobId blobId = BlobId.of(bucketName, objectName);
        Blob blob = storage.get(blobId);

        if (blob == null) {
            throw new RuntimeException("File not found in bucket: " + objectName);
        }

        blob.downloadTo(Paths.get(destPath));
        return new File(destPath);
    }
}
