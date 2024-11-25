package com.spammers.AlertsAndNotifications.service.implementations;

import com.spammers.AlertsAndNotifications.model.UserInfo;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

class ApiClientTestTest {
    private final ApiClientTest test = new ApiClientTest(RestClient.builder().build());
    @BeforeEach
    void setUp() {
        test.deleteAllUserInfo();
        saveSomeUsers();
    }

    private void saveSomeUsers() {
        UserInfo userInfo1 = new UserInfo("user1","user Guardian 1","guardian1@gmail.com");
        UserInfo userInfo2 = new UserInfo("user2","user Guardian 2","guardian2@gmail.com");
        UserInfo userInfo3 = new UserInfo("user3","user Guardian 3","guardian3@gmail.com");
        UserInfo userInfo4 = new UserInfo("user4","user Guardian 4","guardian4@gmail.com");
        UserInfo userInfo5 = new UserInfo("user5","user Guardian 5","guardian5@gmail.com");
        test.saveUserInfo("user1", userInfo1);
        test.saveUserInfo("user2", userInfo2);
        test.saveUserInfo("user3", userInfo3);
        test.saveUserInfo("user4", userInfo4);
        test.saveUserInfo("user5", userInfo5);
    }

    @Test
    void getUserInfoById() {
        UserInfo userInfo1 = test.getUserInfoById("user1");
        assertEquals("user1",userInfo1.getName());
        assertEquals("user Guardian 1", userInfo1.getGuardianName());
        assertEquals("guardian1@gmail.com",userInfo1.getGuardianEmail());
    }

    @Test
    void saveUserInfo() {
        test.saveUserInfo("saveUser-test", new UserInfo("saveUser","saveUser-test Guardian ","guardian-test@gmail.com"));
        UserInfo userInfo = test.getUserInfoById("saveUser-test");
        assertEquals("saveUser",userInfo.getName());
        assertEquals("saveUser-test Guardian ",userInfo.getGuardianName());
        assertEquals("guardian-test@gmail.com",userInfo.getGuardianEmail());
    }

    @Test
    void deleteUserInfo() {
        test.deleteUserInfo("user1");
        UserInfo userInfo = test.getUserInfoById("user1");
        assertNull(userInfo);
    }

    @Test
    void deleteAllUserInfo() {
        test.deleteAllUserInfo();
        assertTrue(test.getUserRepository().isEmpty());
    }
}