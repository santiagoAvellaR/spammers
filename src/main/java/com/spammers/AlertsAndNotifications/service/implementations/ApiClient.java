package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiClient {

    private final RestClient restClient;
    @Value("${API_GATEWAY_URL}")
    private String APIGATEWAY_URL;
    @Value("${API_AUTH_URL}")
    private String APIAUTHURL;
    @Value("${API_USERNAME}")
    private String USERNAME;
    @Value("${API_PASSWORD}")
    private String PASSWORD;
    private final TokenHolder tokenHolder;


    public UserInfo getUserInfoById(String userId){
        String token = tokenHolder.getToken();
        ResponseEntity<String> userInfo = restClient.get()
                .uri(APIGATEWAY_URL + "/find/student/{id}", userId)
                //.header("AUTHORIZATION","Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
        if(userInfo.getStatusCode().is4xxClientError()){
            throw new SpammersPrivateExceptions(SpammersPrivateExceptions.USER_NOT_FOUND,404);
        } else if (userInfo.getStatusCode().is5xxServerError()) {
            throw new SpammersPrivateExceptions("User Service error", 500);
        }
        String name = userInfo.getBody().split("\"studentName\":\"")[1].split("\"")[0];
        String guardianName = userInfo.getBody().split("\"responsibleName\":\"")[1].split("\"")[0];
        String guardianEmail = userInfo.getBody().split("\"responsibleEmail\":\"")[1].split("\"")[0];
        System.out.println("guardianEmail = " + guardianEmail);
        System.out.println("guardianName = " + guardianName);
        System.out.println("name = " + name);
        UserInfo userInfoEst = new UserInfo();
        userInfoEst.setName(name);
        userInfoEst.setGuardianName(guardianName);
        userInfoEst.setGuardianEmail(guardianEmail);
        return userInfoEst;

    }
    public boolean validateToken(String token){
        ResponseEntity<?> validateClient = restClient.get()
                .uri(APIAUTHURL + "/auth/session")
                .header("AUTHORIZATION","Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Object.class);
        if(validateClient.getStatusCode().is2xxSuccessful()){
            return true;
        }
        return false;
    }

    public String getToken() {
        Map<String, String> body = new HashMap<>();
        body.put("username", USERNAME);
        body.put("password", PASSWORD);
        ResponseEntity<String> token = restClient.post()
                .uri(APIAUTHURL + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(String.class);
        if (token.getStatusCode().is2xxSuccessful()) {
            System.out.println("token = " + token);
            String[] parts = token.getBody().split("\"data\":\"");
            return parts[1].split("\"")[0];
        } else {
            throw new SpammersPrivateExceptions("User not found", 404);
        }

    }
}
