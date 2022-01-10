drop table if exists OIP_API_ARGS;
create table OIP_API_ARGS
(
    APIID     bigint                   not null comment '唯一标识'
        primary key,
    ServiceID varchar(50)              null comment 'ServiceID',
    SID       bigint                   not null comment '服务标识',
    ARG_NAME  varchar(50)              not null comment '参数名称',
    ARG_DESC  varchar(250)             null comment '参数描述',
    ARG_TYPE  varchar(250) default '0' not null comment '参数类型',
    ARG_ORDER decimal                  not null comment '参数顺序'
);

drop table if exists OIP_API_VISIT_LOG;
create table OIP_API_VISIT_LOG
(
    ID          decimal(20)      not null comment '唯一标识'
        primary key,
    ServiceID   varchar(50)      null comment 'ServiceID',
    ServiceName varchar(200)     null comment '服务名称',
    ServiceType varchar(50)      null comment '服务类型',
    REMOTE_IP   varchar(50)      null comment '访问IP',
    USERID      varchar(50)      null comment '访问用户',
    URL         varchar(200)     not null comment 'URL',
    VISIT_TIME  datetime         not null comment '访问时间',
    SUCCESS     char default 'Y' not null comment '访问状态 Y：成功 N：失败'
);

drop table if exists OIP_APIS;
create table OIP_APIS
(
    APIID      bigint           not null comment '唯一标识'
        primary key,
    ServiceID  varchar(50)      null comment 'ServiceID',
    SID        bigint           not null comment '服务标识',
    API_NAME   varchar(500)     null,
    API_URL    varchar(250)     null,
    API_STATUS char default '0' not null comment '接口状态 0：下线 1：上线 2：未知'
);

drop table if exists OIP_SERVICE_CONFIG;
create table OIP_SERVICE_CONFIG
(
    ID          bigint        not null comment '唯一标识'
        primary key,
    ServiceID   varchar(50)   not null comment 'ServiceID',
    ServiceName varchar(200)  not null comment '服务名称',
    ServiceType varchar(50)   not null comment '服务类型',
    VIP         varchar(50)   null comment '服务IP',
    VPort       int           null comment '服务端口',
    Metadata    varchar(2000) null comment '元数据',
    CREATE_BY   varchar(100)  null comment '创建人',
    UPDATE_BY   varchar(100)  null comment '更新人',
    CREATE_TIME datetime      null comment '创建时间',
    UPDATE_TIME datetime      null comment '更新时间'
);

drop table if exists OIP_SERVICE_CONFIG_METADATA;
create table OIP_SERVICE_CONFIG_METADATA
(
    ID          bigint       not null comment '唯一标识'
        primary key,
    SID         bigint       null comment '服务标识',
    META_CODE   varchar(50)  null comment '元数据代码',
    META_NAME   varchar(50)  null comment '元数据名称',
    META_VALUE  varchar(200) null comment '元数据值',
    CREATE_BY   varchar(100) null comment '创建人',
    UPDATE_BY   varchar(100) null comment '更新人',
    CREATE_TIME datetime     null comment '创建时间',
    UPDATE_TIME datetime     null comment '更新时间'
);

drop table if exists OIP_SERVICE_CONFIG_PLUGINS;
create table OIP_SERVICE_CONFIG_PLUGINS
(
    PID             bigint        not null comment '唯一标识',
    SID             bigint        not null comment '服务标识',
    PLUGIN_TYPE     varchar(255)  null,
    PLUGIN_NAME     varchar(200)  not null comment '插件名称',
    PLUGIN_PRIORITY int default 0 not null comment '插件优先级',
    CREATE_BY       varchar(100)  null comment '创建人',
    UPDATE_BY       varchar(100)  null comment '更新人',
    CREATE_TIME     datetime      null comment '创建时间',
    UPDATE_TIME     datetime      null comment '更新时间'
);

drop table if exists OIP_VIRTUAL_SERVICE_REGITRY;
create table OIP_VIRTUAL_SERVICE_REGITRY
(
    ID          bigint           not null comment '唯一标识'
        primary key,
    ServiceID   varchar(50)      null comment 'ServiceID',
    ServiceName varchar(200)     null comment '服务名称',
    ServiceType varchar(50)      null comment '服务类型',
    IP          varchar(50)      null comment 'IP',
    PORT        decimal(5)       null comment 'PORT',
    METADATA    varchar(2000)    null comment 'METADATA',
    ADDRESS     varchar(200)     null comment '注册中心地址',
    REG_TIME    datetime         not null comment '注册时间',
    LAST_TIME   datetime         not null comment '检测时间',
    STATUS      char default '0' not null comment '注册状态 0：失败 1：成功 2：未知'
);
