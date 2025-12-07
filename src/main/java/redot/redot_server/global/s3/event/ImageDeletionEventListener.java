package redot.redot_server.global.s3.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import redot.redot_server.global.s3.service.ImageStorageService;

@Component
@RequiredArgsConstructor
public class ImageDeletionEventListener {

    private final ImageStorageService imageStorageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ImageDeletionEvent event) {
        if (event == null || event.imageUrl() == null || event.imageUrl().isBlank()) {
            return;
        }
        imageStorageService.delete(event.imageUrl());
    }
}
