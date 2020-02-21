package com.leyou.auth.utils;

import com.leyou.auth.entity.UserInfo;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "D:\\item\\rsa.pub";

    private static final String priKeyPath = "D:\\item\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // Éú³Étoken
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU4MjA5Nzk3NH0.KXcCsIEF2fiSug0XfMRahQ9pRCPIuwqJ8pq_f0JW0t2vI9tjkI7MNGERVjYkQ_B0RyuRoSLR9fCXtkAU_5sFqO7KGabEym1KN6A-_T1eNcRWWuMgwr5FXJRFQCyrVLr9betNVwoU5a7kX0QtZvDUjzH-nWanGcr_H1XsXsncsPQ";
        // ½âÎötoken
        UserInfo user = JwtUtils.getUserInfo(publicKey, token);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}