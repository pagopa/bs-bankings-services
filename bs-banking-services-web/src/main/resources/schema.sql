CREATE TABLE pagopa_ma_institution(
    ins_id                  number                          PRIMARY KEY,
    ins_name                varchar2(255)                                              NOT NULL,
    ins_code                varchar2(100)                                              NOT NULL, 
    ins_cdc_code            varchar2(50)                    DEFAULT 'CDC-001'          NOT NULL, 
    ins_cdc_description     varchar2(255),
    ins_credential_id       varchar2(50)                                               NOT NULL,
    ins_fabrick_user        number                                                     NOT NULL,
    ins_fiscal_code	    varchar2(100),
    ins_is_deleted          number(1,0)                     DEFAULT 0                  NOT NULL,
    ins_created_datetime    timestamp with time zone        DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    ins_updated_datetime    timestamp with time zone        DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    ins_deleted_datetime    timestamp,
    CONSTRAINT PAGOPA_UQ_INS_CREDENTIAL_ID_DELETED UNIQUE (ins_credential_id, ins_deleted_datetime)
);
CREATE SEQUENCE PAGOPA_SQ_INS_ID START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE INDEX pagopa_idx_ins_credential_id ON pagopa_ma_institution (ins_credential_id);
COMMIT;


CREATE TABLE pagopa_ma_service(
    srv_id                   number                       PRIMARY KEY,
    srv_service_code         varchar2(100)                		                       NOT NULL,
    srv_description  	     varchar2(255),
    srv_is_deleted           number(1,0)                  DEFAULT 0                    NOT NULL,
    srv_created_datetime     timestamp with time zone     DEFAULT CURRENT_TIMESTAMP    NOT NULL,
    srv_updated_datetime     timestamp with time zone     DEFAULT CURRENT_TIMESTAMP    NOT NULL,
    srv_deleted_datetime     timestamp,
	CONSTRAINT PAGOPA_UQ_SRV_SERVICE_CODE_DELETED UNIQUE (srv_service_code, srv_deleted_datetime)
);
CREATE SEQUENCE PAGOPA_SQ_SRV_ID START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
COMMIT;


CREATE TABLE pagopa_ma_entity(
    en_id                   number                      PRIMARY KEY,
    en_name                 varchar2(150)               NOT NULL,
    en_support_email        varchar2(255),
    en_is_deleted           number(1,0)     DEFAULT 0   NOT NULL,
    en_created_datetime     timestamp with time zone    NOT NULL,
    en_updated_datetime     timestamp with time zone,
    en_deleted_datetime     timestamp,
    en_type                 varchar2(50)                NOT NULL,
    CONSTRAINT pagopa_uq_en_name_type_deleted UNIQUE (en_name, en_type, en_deleted_datetime)
);
CREATE SEQUENCE PAGOPA_SQ_ENTITY_ID START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
COMMIT;


CREATE TABLE pagopa_ma_psp(
    psp_id                          number          PRIMARY KEY,
    psp_national_code               varchar2(20)    NOT NULL,
    psp_country_code                varchar2(2)     DEFAULT 'IT'    NOT NULL,
    psp_bic_code                    varchar2(11),                      
    psp_account_value_type          varchar2(10)    DEFAULT 'IBAN'  NOT NULL,
    psp_is_blacklisted              number(1,0)     DEFAULT 0       NOT NULL,
    psp_deleted_datetime_mirror     timestamp,
    FOREIGN KEY (psp_id) REFERENCES pagopa_ma_entity (en_id) ON DELETE CASCADE,
    CONSTRAINT PAGOPA_UQ_PSP_NATIONAL_CODE_COUNTRY_CODE_DELETED UNIQUE (psp_national_code, psp_country_code, psp_deleted_datetime_mirror),
    CONSTRAINT PAGOPA_UQ_PSP_BIC_CODE_DELETED UNIQUE (psp_bic_code, psp_deleted_datetime_mirror)
);
CREATE INDEX pagopa_idx_psp_national_code ON pagopa_ma_psp (psp_national_code);
COMMIT;


CREATE TABLE pagopa_ma_south_config(
    scf_id                      number                          PRIMARY KEY,
    scf_code                    varchar2(50)                    NOT NULL,
    scf_description             varchar2(255),
    scf_connector_name          varchar2(50)    NOT NULL,
    scf_connector_type          varchar2(155),
    scf_model_version           number          DEFAULT 1       NOT NULL,
    scf_model_config            clob            NOT NULL,
    scf_is_deleted              number(1,0)                     DEFAULT 0                  NOT NULL,
    scf_created_datetime        timestamp with time zone        DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    scf_updated_datetime        timestamp with time zone        DEFAULT CURRENT_TIMESTAMP  NOT NULL,
    scf_deleted_datetime        timestamp,
    CONSTRAINT scf_model_config_is_json CHECK (scf_model_config IS JSON),
    CONSTRAINT PAGOPA_UQ_SCF_CODE_DELETED UNIQUE (scf_code, scf_deleted_datetime)
);
CREATE SEQUENCE PAGOPA_SQ_SCF_ID START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
COMMIT;


CREATE TABLE pagopa_tr_event_log(
    elo_uid                             varchar2(36)                PRIMARY KEY,
    elo_delivered                       number(1,0)                 DEFAULT 0       NOT NULL,
    elo_failures_count                  number                      DEFAULT 0       NOT NULL,
    elo_credential_id                   varchar2(50)                NOT NULL,
    elo_reference_local_date            date                        NOT NULL,
    elo_service_code                    varchar2(100),
    elo_destination_topic               varchar2(100)               NOT NULL,
    elo_x_obo_header                    varchar2(250),
    elo_x_obo_primary                   varchar2(7),
    elo_x_correlation_id                varchar2(250),
    elo_inserted_datetime               timestamp with time zone    DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    elo_updated_datetime                timestamp with time zone    DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    elo_event_model_version             number                      DEFAULT 1                   NOT NULL,
    elo_event_model                     clob                        NOT NULL,
    CONSTRAINT elo_event_model_is_json  CHECK (elo_event_model IS JSON)
)
PARTITION BY RANGE (elo_reference_local_date) 
INTERVAL(NUMTOYMINTERVAL(3, 'MONTH')) 
(  
   PARTITION pagopa_tr_event_log_p0 VALUES LESS THAN (TO_DATE('01/04/2021', 'DD/MM/YYYY'))
);
CREATE INDEX pagopa_idx_elo_reference_local_date ON pagopa_tr_event_log (elo_reference_local_date) LOCAL;
CREATE INDEX pagopa_idx_elo_inserted_datetime ON pagopa_tr_event_log (elo_inserted_datetime) LOCAL;
CREATE INDEX pagopa_idx_elo_credential_id ON pagopa_tr_event_log (elo_credential_id) LOCAL;
COMMIT;


CREATE TABLE pagopa_ma_response_whitelist(
    rwl_response_key        varchar2(250)   PRIMARY KEY,
    rwl_service_code        varchar2(100)   NOT NULL,
    rwl_response_value      clob            NOT NULL
);
COMMIT;


CREATE TABLE pagopa_ma_service_binding(
    sbi_id                              number                                                      PRIMARY KEY,
    sbi_validity_started_datetime       timestamp with time zone       DEFAULT CURRENT_TIMESTAMP    NOT NULL,
    sbi_validity_ended_datetime         timestamp,
    sbi_entity_id                       number                                                      NOT NULL,
    sbi_south_config_id                 number                                                      NOT NULL,
    sbi_service_id                      number                                                      NOT NULL,
    CONSTRAINT PAGOPA_FK_SBI_EN_ID FOREIGN KEY (sbi_entity_id) REFERENCES pagopa_ma_entity (en_id) ON DELETE CASCADE,
    CONSTRAINT PAGOPA_FK_SBI_SCF_ID FOREIGN KEY (sbi_south_config_id) REFERENCES pagopa_ma_south_config (scf_id) ON DELETE CASCADE,
    CONSTRAINT PAGOPA_FK_SBI_SRV_ID FOREIGN KEY (sbi_service_id) REFERENCES pagopa_ma_service (srv_id) ON DELETE CASCADE,
    CONSTRAINT PAGOPA_UQ_SBI_ENTITY_SERVICE_VALIDITY_ENDED UNIQUE(sbi_entity_id, sbi_service_id, sbi_validity_ended_datetime)
);
CREATE SEQUENCE PAGOPA_SQ_SBI_ID START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
COMMIT;


CREATE TABLE pagopa_tr_bulk_registry(
    bur_bulk_request_id                     VARCHAR2(36)    NOT NULL,
    bur_elements_processed_count            NUMBER          DEFAULT 0 NOT NULL,
    bur_elements_count                      NUMBER          DEFAULT 0 NOT NULL,
    bur_service_code                        VARCHAR2(100)   NOT NULL,
    bur_inserted_datetime                   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    bur_completed_datetime                  TIMESTAMP,
    bur_bulk_status                         VARCHAR2(16)    DEFAULT 'PENDING' NOT NULL,
    bur_x_correlation_id                    VARCHAR2(250),
    bur_x_credential_id                     VARCHAR2(50),
    bur_has_batch_elements                  NUMBER(1,0),
	bur_routing_time_ms                  	NUMBER,
    CONSTRAINT pagopa_tr_bulk_registry_pk   PRIMARY KEY (bur_bulk_request_id)
);
COMMIT;


CREATE TABLE pagopa_tr_bulk_element(
    bue_bulk_request_id                                         VARCHAR2(36)    NOT NULL,
    bue_request_id                                              VARCHAR2(36)    NOT NULL,
    bue_response_json                                           VARCHAR2(500)   NOT NULL,
    bue_element_status                                          VARCHAR2(16)    DEFAULT 'PENDING',
    bue_inserted_datetime                                       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    bue_last_updated_datetime                                   TIMESTAMP       NOT NULL,
	bue_batch_element_connector                                 VARCHAR2(50),
    bue_batch_element_id                                        VARCHAR2(32),
	bue_batch_psp_id 			                                NUMBER,
    FOREIGN KEY (bue_bulk_request_id)                           REFERENCES pagopa_tr_bulk_registry(bur_bulk_request_id) ON DELETE CASCADE,
    CONSTRAINT pagopa_tr_bulk_element_pk                        PRIMARY KEY (bue_request_id),
    CONSTRAINT pagopa_tr_bulk_element_batch_element_id_uq       UNIQUE (bue_batch_element_id)
);
CREATE INDEX pagopa_idx_bue_bulk_and_request_id ON pagopa_tr_bulk_element (bue_bulk_request_id, bue_request_id);
COMMIT;


CREATE TABLE pagopa_tr_batch_registry(
    bar_bulk_request_id            			VARCHAR2(36)    NOT NULL,
    bar_connector_code             			VARCHAR2(50)    NOT NULL,
    bar_batch_created_datetime     			TIMESTAMP,
	bar_batch_filename             			VARCHAR2(200),
    bar_batch_elements_count       			NUMBER          DEFAULT 0 NOT NULL,
    CONSTRAINT pagopa_tr_batch_registry_pk  PRIMARY KEY (bar_bulk_request_id, bar_connector_code),
    FOREIGN KEY (bar_bulk_request_id)       REFERENCES pagopa_tr_bulk_registry(bur_bulk_request_id) ON DELETE CASCADE
);
COMMIT;

