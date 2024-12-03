package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ApiClient {

    private final RestClient restClient;
    @Value("${API_GATEWAY_URL}")
    private String APIGATEWAY_URL;
    @Value("${API_AUTH_URL}")
    private String APIAUTHURL;


    public UserInfo getUserInfoById(String userId){
        ResponseEntity<UserInfo> userInfo = restClient.get()
                .uri(APIGATEWAY_URL + "/user/getUserInfoById?id=" + userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(UserInfo.class);
        if(userInfo.getStatusCode().is4xxClientError()){
            throw new RuntimeException("al parecer no existe el id");
        } else if (userInfo.getStatusCode().is5xxServerError()) {
            throw new RuntimeException("5000");
        }else{
            //return userInfo.getBody();
        }
        return null;
    }
    public boolean validateToken(String token){
        System.out.println("token = " + token);
        ResponseEntity<?> validateClient = restClient.get()
                .uri(APIAUTHURL + "/auth/session")
                .header("AUTHORIZATION","Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Object.class);
        System.out.println("validateClient = " + validateClient);
        if(validateClient.getStatusCode().is2xxSuccessful()){
            return true;
        }
        return false;
    }

}
