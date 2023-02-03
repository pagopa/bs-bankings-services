package it.pagopa.bs.web.service.checkiban.bulk;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import it.pagopa.bs.checkiban.model.persistence.BatchRegistry;
import it.pagopa.bs.checkiban.model.persistence.BulkElement;
import it.pagopa.bs.checkiban.model.persistence.BulkRegistry;
import it.pagopa.bs.web.mapper.bulk.BatchRegistryMapper;
import it.pagopa.bs.web.mapper.bulk.BulkElementMapper;
import it.pagopa.bs.web.mapper.bulk.BulkRegistryMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckIbanBulkOperationsService {
    
    private final SqlSessionFactory sqlSessionFactory;
    private final TransactionTemplate transactionTemplate;

    private final BulkRegistryMapper bulkRegistryMapper;

    public void insertBulkElements(List<BulkElement> bulkElements) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            BulkElementMapper mapper = sqlSession.getMapper(BulkElementMapper.class);
            bulkElements.forEach(mapper::insertBulkElement);
            sqlSession.commit();
        }
    }

    // from bulk processor
    public void insertBatchElements(
            String bulkRequestId,
            ConcurrentMap<String, BatchRegistry> batchRegistryToAddMap,
            ConcurrentMap<String, BulkElement> bulkElementsToUpdateMap
    ) {
        transactionTemplate.executeWithoutResult(status -> {

            // bulkInsertBatchRegistry
            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                BatchRegistryMapper mapper = sqlSession.getMapper(BatchRegistryMapper.class);
                new LinkedList<>(batchRegistryToAddMap.values()).forEach(mapper::addToRegistry);
                sqlSession.commit();
            }

            // bulkUpdateElements
            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                BulkElementMapper mapper = sqlSession.getMapper(BulkElementMapper.class);
                bulkElementsToUpdateMap.forEach(mapper::setDataAndPendingBatch);
                sqlSession.commit();
            }

            bulkRegistryMapper.setHasBatchElements(bulkRequestId);
        });
    }

    // from batch handler
    public void updatePendingBatchElementsAndSetBulkCompleted(ConcurrentMap<String, String> elementsToUpdateMap, List<String> bulkRequestIds) {
        transactionTemplate.executeWithoutResult(status -> {

            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                BulkElementMapper mapper = sqlSession.getMapper(BulkElementMapper.class);
                elementsToUpdateMap.forEach(mapper::updateElementWithBatchResult);
                sqlSession.commit();
            }

            bulkRegistryMapper.completeCountAndSetCompleted(bulkRequestIds);
        });
    }

    // from cleaner
    public void setElementsTimeoutAndBulkCompleted(BulkRegistry bulkRegistry, List<BulkElement> bulkElementsToTimeout) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                BulkElementMapper mapper = sqlSession.getMapper(BulkElementMapper.class);
                bulkElementsToTimeout.forEach(be ->  mapper.setResponseRequestIdAndTimeout(be.getRequestId(), be.getResponseJson()));
                sqlSession.commit();
            }
            bulkRegistryMapper.completeCountAndSetCompleted(
                    Collections.singletonList(bulkRegistry.getBulkRequestId())
            );
        });
    }
}
