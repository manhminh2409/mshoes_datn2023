package com.mshoes.mshoes.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mshoes.mshoes.models.request.ColorRequest;
import com.mshoes.mshoes.models.request.SizeRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ConvertJSON {
    public static void main(String[] args) throws JsonProcessingException {
        String jsonStr = "{\"colors\": [{\"value\": \"Đỏ\",\"sizes\": [{\"value\": \"39\",\"total\": 10},{\"value\": \"40\",\"total\": 5},{\"value\": \"42\",\"total\": 15}]},{\"value\": \"Trắng\",\"sizes\": [{\"value\": \"40\",\"total\": 5},{\"value\": \"41\",\"total\": 10}]}]}";

        JSONObject jsonObj = new JSONObject(jsonStr);

        JSONArray colorsArr = jsonObj.getJSONArray("colors");
        List<ColorRequest> colorRequests = new ArrayList<>();

        for (int i = 0; i < colorsArr.length(); i++) {
            ColorRequest colorRequest = new ColorRequest();

            JSONObject colorObj = colorsArr.getJSONObject(i);

            String value = colorObj.getString("value");
            colorRequest.setValue(value);
            JSONArray sizesArr = colorObj.getJSONArray("sizes");
            List<SizeRequest> sizeRequests = new ArrayList<>();
            for (int j = 0; j < sizesArr.length(); j++) {
                JSONObject sizeObj = sizesArr.getJSONObject(j);
                SizeRequest sizeRequest =new SizeRequest();

                String sizeValue = sizeObj.getString("value");
                int total = sizeObj.getInt("total");
                sizeRequest.setValue(sizeValue);
                sizeRequest.setTotal(total);
                sizeRequests.add(sizeRequest);
            }
            colorRequest.setSizes(sizeRequests);
            colorRequests.add(colorRequest);
        }
    }
}
