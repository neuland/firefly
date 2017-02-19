package de.neuland.firefly.changes;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.neuland.firefly.model.FireflyChangeModel;
import de.neuland.firefly.model.FireflyLogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;


@Repository
@Scope("prototype")
public class LogRepository {
    @Autowired private MediaService mediaService;

    public FireflyLogModel create(FireflyChangeModel change) {
        Assert.notNull(change, "change");
        FireflyLogModel result = createInternal(change);
        return result;
    }

    public void addLog(FireflyLogModel fireflyLog, String log) {
        try {
            mediaService.setStreamForMedia(fireflyLog,
                                           new ByteArrayInputStream(log.getBytes("UTF-8")),
                                           fireflyLog.getCode().concat(".txt"),
                                           "text/plain");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private FireflyLogModel createInternal(ItemModel owner) {
        FireflyLogModel result = new FireflyLogModel();
        result.setCode(UUID.randomUUID().toString());
        result.setOwner(owner);
        result.setRealFileName(result.getCode().concat(".txt"));
        return result;
    }
}
