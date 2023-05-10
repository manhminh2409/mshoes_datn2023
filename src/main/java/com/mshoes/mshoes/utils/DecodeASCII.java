package com.mshoes.mshoes.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DecodeASCII {
    public static void main(String[] args) {
        String encodedString = "Hà Nội";
        String decodedString = "";
        decodedString = URLEncoder.encode(encodedString, StandardCharsets.UTF_8);
        System.out.println(decodedString);
    }
}
