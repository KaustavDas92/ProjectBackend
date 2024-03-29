package com.example.springbootlibrary.utils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ExtractJWTToken {

    public static String payloadExtract (String token,String extraction) {
        token.replace("Bearer ", "");

        String[] parts=token.split("\\.");
        Base64.Decoder decoder=Base64.getUrlDecoder();
        String payload= new String ( decoder.decode(parts[1]));

        String[] entries=payload.split(",");
        Map<String,String> map=new HashMap<>();
        for(String ent:entries) {
            String[] keyValue = ent.split(":");

            if (keyValue[0].equals(extraction)) {
                int remove = 1;

                if (keyValue[1].endsWith("}")) {
                    remove = 2;
                }
            keyValue[1]=keyValue[1].substring(0,remove);
            keyValue[1]=keyValue[1].substring(1);

            map.put(keyValue[0],keyValue[1]);
            }

        }

        if (map.containsKey(extraction)){
            return map.get(extraction);
        }

        return null;
    }
}
