package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.UserInfo;
import lombok.Getter;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
@Getter
public class ApiClientTest extends ApiClient{
    private final HashMap<String, UserInfo> userRepository;
    public ApiClientTest(RestClient restClient) {
        super(restClient);
        userRepository = new HashMap<>();
    }

    @Override
    public UserInfo getUserInfoById(String userId){
        return userRepository.get(userId);
    }

    public void saveUserInfo(String userId, UserInfo userInfo){
        userRepository.put(userId, userInfo);
    }
    public void deleteUserInfo(String userId){
        userRepository.remove(userId);
    }
    public void deleteAllUserInfo(){
        userRepository.clear();
    }
}
