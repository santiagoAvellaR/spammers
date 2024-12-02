package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.dto.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
@Component
public class ApiClientLocal extends ApiClient {
    HashMap<String, UserInfo> users = new HashMap<>(Map.of(
            "miguel-123", new UserInfo("miguel","Miguel Guardian","aldandaniel535@gmail.com"),
            "raul-123",new UserInfo("raul","raul Guardian", "santiago.avellaneda@mail.escuelaing.edu.co"),
            "samuel-123",new UserInfo("samuel","samuel Guardian", "jorge.gamboa-s@mail.escuelaing.edu.co"),
            "user123", new UserInfo("user123", "Test User", "guardian@email.com") ));

    public ApiClientLocal(RestClient restClient) {
        super(restClient);
    }
    @Override
    public UserInfo getUserInfoById(String userId){
        return users.get(userId);
    }
}
