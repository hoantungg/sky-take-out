package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        String openid =
                getOpenid(userLoginDTO.getCode());
        if (openid == null || openid.isEmpty()){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.getByOpenId(openid);
        if (user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insertUser(user);
        }
        return user;
    }
    private String getOpenid(String code) {

        Map<String, String> paramMap =
                new HashMap<>();
        paramMap.put(
                "appid",
                weChatProperties.getAppid()
        );
        paramMap.put(
                "secret",
                weChatProperties.getSecret()
        );
        paramMap.put(
                "js_code",
                code
        );
        paramMap.put(
                "grant_type",
                "authorization_code"
        );
        String result = HttpClientUtil.doGet(
                WX_LOGIN_URL,
                paramMap
        );
        JSONObject jsonObject =
                JSON.parseObject(result);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.getString("openid");
    }
}


