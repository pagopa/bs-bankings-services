package it.pagopa.bs.web.service.checkiban.connector.batch;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import it.pagopa.bs.checkiban.enumeration.BulkElementStatus;
import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import it.pagopa.bs.checkiban.model.api.response.bulk.ValidateAccountHolderBulkElementResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.AccountHolderResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.AccountResponse;
import it.pagopa.bs.checkiban.model.persistence.BulkElement;
import it.pagopa.bs.common.util.parser.JsonUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
// created for TEST purposes, it takes only the first one element
// in order to test you must conclude the batch round when the component is still running
public class BatchTestConnector implements ABatchConnector {

    private String batchElementId = "";

    @Override
    public Map<String, BulkElement> readFile() {

        if(StringUtils.isBlank(batchElementId)) {
            return Collections.emptyMap();
        }

        return Collections.singletonMap(
            batchElementId,
            BulkElement.builder()
                .batchElementId(batchElementId)
                .elementStatus(BulkElementStatus.SUCCESS)
                .responseJson(
                    JsonUtil.toStringOrThrow(
                        ValidateAccountHolderBulkElementResponse.builder()
                            .account(
                                AccountResponse.builder()
                                    .value("IT58S0305883465368752287752")
                                    .build()
                            )
                            .accountHolder(
                                AccountHolderResponse.builder()
                                    .vatCode(null)
                                    .fiscalCode("NXYWDZ97H27D365G")
                                    .build()
                            )
                            .validationStatus(ValidationStatus.OK)
                            .build()
                    )
                )
                .build()
        );
    }

    @Override
    public String writeFile(List<BulkElement> batchElements) {
        batchElementId = batchElements.get(0).getBatchElementId();
        return "TEST.clear";
    }

    @Override
    public boolean isInFolderEmpty() {
        return StringUtils.isBlank(batchElementId);
    }

    @Override
    public boolean isOutFolderEmpty() {
        return StringUtils.isBlank(batchElementId);
    }

    @Override
    public void purgeInFile() {
        batchElementId = "";
    }

    @Override
    public String getConnectorName() {
        return "BATCH_TEST";
    }
}
