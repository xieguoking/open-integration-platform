package com.shdata.osp.web.plugin.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shenyu.common.utils.GsonUtils;
import org.springframework.util.LinkedMultiValueMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangwj
 * @version 1.0
 * @date 2022/1/3
 */

public class BodyParamUtils {

    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

    private BodyParamUtils() {
    }

    /**
     * buildBodyParams.
     *
     * @param param param.
     * @return the string change to linkedMultiValueMap.
     */
    public static LinkedMultiValueMap<String, String> buildBodyParams(final String param) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Matcher matcher = QUERY_PARAM_PATTERN.matcher(param);
        while (matcher.find()) {
            String name = matcher.group(1);
            String eq = matcher.group(2);
            String value = matcher.group(3);
            params.add(name, value != null ? value : (StringUtils.isNotBlank(eq) ? "" : null));
        }
        return params;
    }

    /**
     * build single parameter.
     *
     * @param body           the parameter body.
     * @param parameterTypes the parameter types.
     * @return the parameters.
     */
    public static Pair<String[], Object[]> buildSingleParameter(final String body, final String parameterTypes) {
        final Map<String, Object> paramMap = GsonUtils.getInstance().toObjectMap(body);
        for (String key : paramMap.keySet()) {
            Object obj = paramMap.get(key);
            if (obj instanceof JsonObject) {
                paramMap.put(key, GsonUtils.getInstance().convertToMap(obj.toString()));
            } else if (obj instanceof JsonArray) {
                paramMap.put(key, GsonUtils.getInstance().fromList(obj.toString(), Object.class));
            } else {
                paramMap.put(key, obj);
            }
        }
        return new ImmutablePair<>(new String[]{parameterTypes}, new Object[]{paramMap});
    }

    /**
     * build multi parameters.
     *
     * @param body           the parameter body.
     * @param parameterTypes the parameter types.
     * @return the parameters.
     */
    public static Pair<String[], Object[]> buildParameters(final String body, final String parameterTypes) {
        List<String> paramNameList = new ArrayList<>();
        List<String> paramTypeList = new ArrayList<>();

        if (isNameMapping(parameterTypes)) {
            Map<String, String> paramNameMap = GsonUtils.getInstance().toObjectMap(parameterTypes, String.class);
            paramNameList.addAll(paramNameMap.keySet());
            paramTypeList.addAll(paramNameMap.values());
        } else {
            Map<String, Object> paramMap = GsonUtils.getInstance().toObjectMap(body);
            paramNameList.addAll(paramMap.keySet());
            paramTypeList.addAll(Arrays.asList(StringUtils.split(parameterTypes, ",")));
        }

        if (paramTypeList.size() == 1 && !isBaseType(paramTypeList.get(0))) {
            return buildSingleParameter(body, parameterTypes);
        }
        Map<String, Object> paramMap = GsonUtils.getInstance().toObjectMap(body);
        Object[] objects = paramNameList.stream().map(key -> {
            Object obj = paramMap.get(key);
            if (obj instanceof JsonObject) {
                return GsonUtils.getInstance().convertToMap(obj.toString());
            } else if (obj instanceof JsonArray) {
                return GsonUtils.getInstance().fromList(obj.toString(), Object.class);
            } else {
                return obj;
            }
        }).toArray();
        String[] paramTypes = paramTypeList.toArray(new String[0]);
        return new ImmutablePair<>(paramTypes, objects);
    }

    private static boolean isNameMapping(final String parameterTypes) {
        return parameterTypes.startsWith("{") && parameterTypes.endsWith("}");
    }

    /**
     * isBaseType.
     *
     * @param paramType the parameter type.
     * @return whether the base type is.
     */
    private static boolean isBaseType(final String paramType) {
        try {
            //shenyu-sdk-2.4.2
            return isPrimitives(ClassUtils.getClass(paramType));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Verify the cls is Primitives (Maybe array).
     *
     * @param cls class
     * @return boolean
     */
    public static boolean isPrimitives(final Class<?> cls) {
        if (cls.isArray()) {
            return isPrimitive(cls.getComponentType());
        }
        return isPrimitive(cls);
    }

    /**
     * Verify the cls is Primitive.
     *
     * @param cls class
     * @return boolean
     */
    public static boolean isPrimitive(final Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class
                || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }

}
