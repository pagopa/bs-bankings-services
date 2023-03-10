package it.pagopa.bs.web.service.checkiban.connector.batch;

import java.util.List;
import java.util.Map;

import it.pagopa.bs.checkiban.model.persistence.BulkElement;

public interface ABatchConnector {
    
    Map<String, BulkElement> readFile();
    String writeFile(List<BulkElement> batchElements);
    boolean isInFolderEmpty();
    boolean isOutFolderEmpty();
    void purgeInFile();
    String getConnectorName();
}
