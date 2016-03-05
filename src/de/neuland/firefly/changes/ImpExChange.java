package de.neuland.firefly.changes;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.cronjob.ImpExImportCronJob;

import java.io.ByteArrayInputStream;


public class ImpExChange extends Change {
    public ImpExChange(ChangeBasic changeBasic, ChangeDependency changeDependency) {
        super(changeBasic, changeDependency);
    }

    @Override void executeChange() throws ChangeExecutionException {
        try {
            ImpExImportCronJob result = ImpExManager.getInstance().importData(
                            new ByteArrayInputStream(getChangeContent().getBytes("UTF-8")),
                            "UTF-8",
                            true);

            if (result != null &&
                result.getResult() != null &&
                !CronJobResult.SUCCESS.getCode().equals(result.getResult().getCode())) {
                throw new RuntimeException("Error executing impEx - please check logs");
            }
        } catch (Exception e) {
            throw new ChangeExecutionException(e, this);
        }
    }
}
