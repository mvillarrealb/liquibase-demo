package org.mvillabe.books.api.services;

import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;


@RequiredArgsConstructor
@Slf4j
@Service
public class StorageService {

    private final Storage storage;

    public void uploadFile(BlobId blobId, FilePart filePart, Consumer<String> onUpload) {
        storage.create(BucketInfo.of("attachments"), Storage.BucketTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
        BlobInfo blobInfo = BlobInfo
                .newBuilder(blobId)
                .setContentType("image/png")
                .build();
        filePart.content()
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .map(it -> storage.create(blobInfo, it))
                .map(Blob::getMediaLink)
                .switchIfEmpty(Mono.just("/"))
                .subscribe(onUpload, this::onError);
    }
    private void onError(Throwable throwable) {
        log.error("Error processing bucket upload {}", throwable.getMessage(), throwable);
    }
}
