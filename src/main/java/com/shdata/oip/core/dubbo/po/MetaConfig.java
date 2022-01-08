package com.shdata.oip.core.dubbo.po;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wangwj
 * @version 1.0
 * @date 2021/12/30
 */

@NoArgsConstructor
@Data
public class MetaConfig {

    /**
     * parameters : {"side":"provider","release":"2.7.8","methods":"sayHelloUser,sayHello","deprecated":"false","dubbo":"2.0.2","interface":"com.dave.dubbo.provider.api.MessageService_2_7_8","version":"1.0.0","qos.enable":"false","generic":"false","revision":"1.0.0","metadata-type":"remote","application":"dubbo-nacos-provider-2.7.8","dynamic":"true","anyhost":"true"}
     * canonicalName : com.dave.dubbo.provider.api.MessageService_2_7_8
     * codeSource : file:/D:/workspace/dave-getaway/dave-dubbo-provider-api/target/classes/
     * methods : [{"name":"sayHello","parameterTypes":["java.lang.String"],"returnType":"java.lang.String"},{"name":"sayHelloUser","parameterTypes":["com.dave.dubbo.provider.api.entity.UserTest"],"returnType":"java.lang.String"}]
     * types : [{"type":"char","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"},{"type":"int","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"},{"type":"com.dave.dubbo.provider.api.entity.UserTest","properties":{"datetime":{"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"},"name":{"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"},"id":{"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"}},"typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"},{"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"}]
     */

    @JsonProperty("parameters")
    private ParametersDTO parameters;
    @JsonProperty("canonicalName")
    private String canonicalName;
    @JsonProperty("codeSource")
    private String codeSource;
    @JsonProperty("methods")
    private List<Methods> methods;
    @JsonProperty("types")
    private List<TypesDTO> types;

    @NoArgsConstructor
    @Data
    public static class ParametersDTO {
        /**
         * side : provider
         * release : 2.7.8
         * methods : sayHelloUser,sayHello
         * deprecated : false
         * dubbo : 2.0.2
         * interface : com.dave.dubbo.provider.api.MessageService_2_7_8
         * version : 1.0.0
         * qos.enable : false
         * generic : false
         * revision : 1.0.0
         * metadata-type : remote
         * application : dubbo-nacos-provider-2.7.8
         * dynamic : true
         * anyhost : true
         */

        @JsonProperty("side")
        private String side;
        @JsonProperty("release")
        private String release;
        @JsonProperty("methods")
        private String methods;
        @JsonProperty("deprecated")
        private String deprecated;
        @JsonProperty("dubbo")
        private String dubbo;
        @JsonProperty("interface")
        private String interfaceX;
        @JsonProperty("version")
        private String version;
        @JsonProperty("qos.enable")
        private String _$QosEnable21;// FIXME check this code
        @JsonProperty("generic")
        private String generic;
        @JsonProperty("revision")
        private String revision;
        @JsonProperty("metadata-type")
        private String metadatatype;
        @JsonProperty("application")
        private String application;
        @JsonProperty("dynamic")
        private String dynamic;
        @JsonProperty("anyhost")
        private String anyhost;
    }


    @NoArgsConstructor
    @Data
    public static class TypesDTO {
        /**
         * type : char
         * typeBuilderName : org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder
         * properties : {"datetime":{"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"},"name":{"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"},"id":{"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"}}
         */

        @JsonProperty("type")
        private String type;
        @JsonProperty("typeBuilderName")
        private String typeBuilderName;
        @JsonProperty("properties")
        private PropertiesDTO properties;

        @NoArgsConstructor
        @Data
        public static class PropertiesDTO {
            /**
             * datetime : {"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"}
             * name : {"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"}
             * id : {"type":"java.lang.String","typeBuilderName":"org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder"}
             */

            @JsonProperty("datetime")
            private DatetimeDTO datetime;
            @JsonProperty("name")
            private NameDTO name;
            @JsonProperty("id")
            private IdDTO id;

            @NoArgsConstructor
            @Data
            public static class DatetimeDTO {
                /**
                 * type : java.lang.String
                 * typeBuilderName : org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder
                 */

                @JsonProperty("type")
                private String type;
                @JsonProperty("typeBuilderName")
                private String typeBuilderName;
            }

            @NoArgsConstructor
            @Data
            public static class NameDTO {
                /**
                 * type : java.lang.String
                 * typeBuilderName : org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder
                 */

                @JsonProperty("type")
                private String type;
                @JsonProperty("typeBuilderName")
                private String typeBuilderName;
            }

            @NoArgsConstructor
            @Data
            public static class IdDTO {
                /**
                 * type : java.lang.String
                 * typeBuilderName : org.apache.dubbo.metadata.definition.builder.DefaultTypeBuilder
                 */

                @JsonProperty("type")
                private String type;
                @JsonProperty("typeBuilderName")
                private String typeBuilderName;
            }
        }
    }
}
