package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.dto.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
@Component
public class ApiClientLocal extends ApiClient {
    HashMap<String, UserInfo> users = new HashMap<>(Map.of(
            "miguel-123", new UserInfo("Miguel","Miguel Guardian","miguelangelmu2016@gmail.com"),
            "santi-123",new UserInfo("Santi","santi Guardian", "santiago.avellaneda@mail.escuelaing.edu.co"),
            "jorge-123",new UserInfo("Jorge","Jorge Guardian", "jorge.gamboa-s@mail.escuelaing.edu.co"),
            "daniel-123", new UserInfo("Daniel", "daniel Guardian", "aldandaniel535@gmail.com") ));

    public ApiClientLocal(RestClient restClient) {
        super(restClient);
    }
    @Override
    public UserInfo getUserInfoById(String userId){
        return users.get(userId);
    }
}