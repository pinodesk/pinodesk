package com.getkembang.kembangdesktop.utility;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSON {

    private static final ObjectMapper MAPPER;

    // Static initialization
    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    private JSON() {}
    
    public static <T> T parse(String json, Class<T> t) {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, t);
        } catch (IOException e) {
            log.warn("Unable to parse JSON");
            return null;
        }
    }

    public static <T> T parse(String json, TypeReference<T> t) {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, t);
        } catch (IOException e) {
            log.warn("Unable to parse JSON");
            return null;
        }
    }

    /**
     * Returns JSON string of the specified object in one line. Always mask sensitive data by default.
     *
     * @param object The object to print as JSON string.
     * @return JSON string of the specified object.
     */
    public static String stringify(Object object) {
        return stringify(object, false);
    }

    /**
     * Returns JSON string of the specified object. 
     * @param object The object to print as JSON string.
     * @param prettyPrint An option to print in formatted style.
     * @return JSON string of the specified object.
     */
    public static String stringify(Object object, boolean prettyPrint) {
        if (object == null) {
            return null;
        }
        try {
            String str = MAPPER.writeValueAsString(object);
            if (prettyPrint) {
                str = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
            return str;
        } catch (JsonProcessingException e) {
            log.warn("Unable to write object as JSON string");
        }
        return null;
    }

    public static boolean validate(String json) {
        try {
            MAPPER.readTree(json);
            return true;
        } catch (IOException e) {
            log.warn("Not a valid JSON");
            return false;
        }
    }
    
}
