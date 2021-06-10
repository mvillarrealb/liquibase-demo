package org.mvillabe.books.api.services;

import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvillabe.books.domain.exceptions.FileNotFoundException;
import org.springframework.core.io.buffer.DataBuffer;
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
        String contentType = "image/jpeg";
        if(filePart.headers().getContentType() != null) {
            contentType = filePart.headers().getContentType().toString();
        }
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        Storage.BlobTargetOption targetOption = Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ);
        Mono<DataBuffer> dataBufferMono = DataBufferUtils.join(filePart.content());
        dataBufferMono
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .map(it -> {
                    storage.create(blobInfo, it, targetOption);
                    return storage.get(blobId);
                })
                .map(Blob::getGeneratedId)
                .subscribe(onUpload, this::onError);
    }

    private void onError(Throwable throwable) {
        log.error("Error processing bucket upload {}", throwable.getMessage(), throwable);
    }

    public byte[] getFileBytes(BlobId blobId) {
        Blob blob = storage.get(blobId);
        if(blob == null) {
            throw new FileNotFoundException();
        }
        return blob.getContent();
    }
}
