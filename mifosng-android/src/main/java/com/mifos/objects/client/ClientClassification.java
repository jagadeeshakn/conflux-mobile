package com.mifos.objects.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by conflux37 on 12/14/2015.
 */
public class ClientClassification {

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
