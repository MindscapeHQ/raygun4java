package com.mindscapehq.raygun4java.play2;

import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RaygunPlayRequestMessage extends RaygunRequestMessage {
    protected Map<String, String> flattenMap(Map<String, String[]> map) {
        Map<String, String> result = new HashMap<String, String>();

        for (String key : map.keySet()) {
            result.put(key, Arrays.toString(map.get(key)));
        }

        return result;
    }
}
