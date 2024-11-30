package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
@Component
public class ApiClientLocal extends ApiClient {
    HashMap<String, UserInfo> users = new HashMap<>(Map.of("miguel-123", new UserInfo("miguel","Miguel Guardian","miguelangelmu2016@gmail.com"),
            "raul-123",new UserInfo("raul","raul Guardian", "raul@gmail.com"),
            "samuel-123",new UserInfo("samuel","samuel Guardian", "samuel@gmail.com") ));
    public ApiClientLocal(RestClient restClient) {
        super(restClient);
    }
    @Override
    public UserInfo getUserInfoById(String userId){
        return users.get(userId);
    }
}
