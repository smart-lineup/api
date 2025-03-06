package com.jun.smartlineup.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey; // for register id such as google.
    private String name;
    private String email;
    private String picture;

    public static OAuthAttributes of(String registerId, String attributeName, Map<String, Object> attributes) {
//        if ("google".equals(registerId)) {
//            return ofGoogle(attributeName, attributes);
//        }
        return ofGoogle(attributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String attributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(attributeName)
                .build();
    }


}
