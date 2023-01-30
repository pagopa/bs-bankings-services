ALTER SESSION SET current_schema = SYSTEM;

INSERT INTO pagopa_ma_response_whitelist(RWL_RESPONSE_KEY,RWL_SERVICE_CODE,RWL_RESPONSE_VALUE) VALUES ('170###IT19N0300203280545126635511###SRBNGL63A03G371K','CHECK_IBAN_SIMPLE','{"validationStatus":"OK","account":{"value":"IT19N0300203280545126635511","valueType":"IBAN"},"accountHolder":{"type":"PERSON_NATURAL","fiscalCode":"SRBNGL63A03G371K"},"bankInfo":null}');
COMMIT;

INSERT INTO pagopa_ma_institution VALUES (PAGOPA_SQ_INS_ID.nextval, 'TEST', '8df987c8-936d-4a1b-9d00-7114c1e274ad', 'CDC-001', 'default cdc', '1700', '11111111111', 0, FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), null);
COMMIT;

INSERT INTO pagopa_ma_service VALUES (PAGOPA_SQ_SRV_ID.nextval, 'CHECK_IBAN_SIMPLE', 'AKA: Validate Account Holder', 0, FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), null);
INSERT INTO pagopa_ma_service VALUES (PAGOPA_SQ_SRV_ID.nextval, 'CO_BADGE', 'AKA: Search Payment Instruments', 0, FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), null);
COMMIT;

INSERT INTO pagopa_ma_entity VALUES(PAGOPA_SQ_ENTITY_ID.nextval, 'BANCA MOCK S.P.A', null, 0, FROM_TZ(TIMESTAMP '2020-06-17 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-17 07:00:00', 'Europe/Berlin'), null, 'PSP');
INSERT INTO pagopa_ma_entity VALUES(PAGOPA_SQ_ENTITY_ID.nextval, 'BANCA MOCK 2 S.P.A', null, 0, FROM_TZ(TIMESTAMP '2020-06-17 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-17 07:00:00', 'Europe/Berlin'), null, 'PSP');
INSERT INTO pagopa_ma_entity VALUES(PAGOPA_SQ_ENTITY_ID.nextval, 'BANCA STELLA S.P.A.', null, 0, FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), null, 'PSP');
COMMIT;

INSERT INTO pagopa_ma_psp VALUES(1, '00000', 'IT', 'MOCKITMBXXX', 'IBAN', 0, null);
INSERT INTO pagopa_ma_psp VALUES(2, '00001', 'IT', 'MOCKITMCXXX', 'IBAN', 0, null);
INSERT INTO pagopa_ma_psp VALUES(3, '00002', 'IT', 'MOCKITMDXXX', 'IBAN', 0, null);
COMMIT;

INSERT INTO pagopa_ma_south_config VALUES(PAGOPA_SQ_SCF_ID.nextval, 'PSP_MOCK_API', 'Mock Bank Connector', 'MOCK', 'PSP_API_STANDARD', 1, '{"southPath":"/api/pagopa/banking/v4.0/utils/validate-account-holder/south/mock"}', 0, FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), NULL);
INSERT INTO pagopa_ma_south_config VALUES(PAGOPA_SQ_SCF_ID.nextval, 'PSP_STELLA_API', 'Banca Stella Connector', 'STELLA', 'PSP_API_STANDARD', 1, '{"southPath":"/api/pagopa/banking/v4.0/utils/validate-account-holder/south/stella"}', 0, FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), NULL);

INSERT INTO pagopa_ma_south_config VALUES(PAGOPA_SQ_SCF_ID.nextval, 'SERVICE_PROVIDER_FAST_MOCK_API', 'Fast Mock Service Provider Connector', 'FAST_MOCK', 'SERVICE_PROVIDER_API_STANDARD', 1, '{"southPath":null,"hasGenericSearch":true,"isMock":true,"isPrivative":false,"isActive":true}', 0, FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), NULL);
INSERT INTO pagopa_ma_south_config VALUES(PAGOPA_SQ_SCF_ID.nextval, 'SERVICE_PROVIDER_SLOW_MOCK_API', 'Slow Mock Service Provider Connector', 'SLOW_MOCK', 'SERVICE_PROVIDER_API_STANDARD', 1, '{"southPath":null,"hasGenericSearch":true,"isMock":true,"isPrivative":false,"isActive":true}', 0, FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), FROM_TZ(TIMESTAMP '2020-06-18 07:00:00', 'Europe/Berlin'), NULL);
COMMIT;

INSERT INTO pagopa_ma_service_binding VALUES(PAGOPA_SQ_SBI_ID.nextval, FROM_TZ(TIMESTAMP '2020-06-17 07:00:01', 'Europe/Berlin'), null, 1, 1, 1);
INSERT INTO pagopa_ma_service_binding VALUES(PAGOPA_SQ_SBI_ID.nextval, FROM_TZ(TIMESTAMP '2020-06-18 07:00:01', 'Europe/Berlin'), null, 2, 1, 1);
INSERT INTO pagopa_ma_service_binding VALUES(PAGOPA_SQ_SBI_ID.nextval, FROM_TZ(TIMESTAMP '2020-06-18 07:00:01', 'Europe/Berlin'), null, 3, 2, 1);

INSERT INTO pagopa_ma_service_binding VALUES(PAGOPA_SQ_SBI_ID.nextval, FROM_TZ(TIMESTAMP '2021-02-18 07:00:01', 'Europe/Berlin'), null, 1, 3, 2);
INSERT INTO pagopa_ma_service_binding VALUES(PAGOPA_SQ_SBI_ID.nextval, FROM_TZ(TIMESTAMP '2021-02-18 07:00:01', 'Europe/Berlin'), null, 2, 3, 2);
INSERT INTO pagopa_ma_service_binding VALUES(PAGOPA_SQ_SBI_ID.nextval, FROM_TZ(TIMESTAMP '2021-02-18 07:00:01', 'Europe/Berlin'), null, 3, 4, 2);

COMMIT;
