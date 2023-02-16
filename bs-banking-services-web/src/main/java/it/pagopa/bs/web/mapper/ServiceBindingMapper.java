package it.pagopa.bs.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.api.request.config.binding.SearchServiceBindingRequest;
import it.pagopa.bs.checkiban.model.persistence.ServiceBinding;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
import it.pagopa.bs.common.enumeration.ConnectorType;
import it.pagopa.bs.common.enumeration.ServiceCode;
import it.pagopa.bs.common.model.api.shared.SortingModel;

@Mapper
public interface ServiceBindingMapper {
    
    List<ServiceBinding> searchPspServiceBinding(
            @Param("filter") SearchServiceBindingRequest filter,
            @Param("sortingItems") List<SortingModel> sortingItems,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int bindService(@Param("serviceBinding") ServiceBinding serviceBinding);

    int unbindService(
            @Param("entityId") long entityId,
            @Param("southConfigId") long southConfigId,
            @Param("serviceId") long serviceId
    );

    void unbindServiceById(long serviceBindingId);

    void unbindAllByPspId(long pspId);
    void unbindAllByServiceId(long serviceId);
    void unbindAllBySouthConfigId(long southConfigId);

    ServiceBinding getOneById(long serviceBindingId);

    ServiceBinding getOneByIdIncludeEnded(long serviceBindingId);

    ServiceBinding getOneByEntityIdAndServiceCodeAndSouthConfigCode(
            @Param("entityId") String entityId,
            @Param("serviceCode") String serviceCode,
            @Param("southConfigCode") String southConfigCode
    );

    ServiceBinding getOneByEntityIdAndServiceCode(
            @Param("entityId") String entityId,
            @Param("serviceCode") String serviceCode
    );

    List<ServiceBinding> getAllByBindingByServiceCodeAndConnectorType(
            @Param("serviceCode") ServiceCode serviceCode,
            @Param("connectorType") ConnectorType connectorType
    );

    List<SouthConfig> getAllConfigsByServiceCodeAndConnectorType(
            @Param("serviceCode") ServiceCode serviceCode,
            @Param("connectorType") ConnectorType connectorType
    );

    SouthConfig getConfigByServiceCodeAndConnectorTypeAndConnectorName(
            @Param("serviceCode") ServiceCode serviceCode,
            @Param("connectorType") ConnectorType connectorType,
            @Param("connectorName") String connectorName
    );

    ServiceBinding getOneByBindingByServiceCodeAndConnectorTypeAndNationalCode(
            @Param("serviceCode") ServiceCode serviceCode,
            @Param("connectorType") ConnectorType connectorType,
            @Param("nationalCode") String nationalCode
    );

    ServiceBinding getBindingByServiceCodeAndConnectorTypeAndNationalCodeAndCountryCode(
            @Param("serviceCode") ServiceCode serviceCode,
            @Param("connectorType") ConnectorType connectorType,
            @Param("nationalCode") String nationalCode,
            @Param("countryCode") String countryCode
    );
}
