package com.mywuwu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mywuwu.entity.Test;
import com.mywuwu.service.IWsService;
import com.mywuwu.socket.MyWebSocket;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

/**
 * @Auther: 梁乐乐
 * @Date: 2018/12/18 11:18
 * @Description:
 */
@RestController
@CrossOrigin
public class TestController {
    @Autowired
    private MyWebSocket webSocket;

    @Autowired
    private IWsService wsService;

    @GetMapping("/sendAllWebSocket")
    public String test() {
        try {
            webSocket.sendAllMessage("清晨起来打开窗，心情美美哒~");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "websocket群体发送！";
    }

    @RequestMapping("/sendOneWebSocket")
    public String sendOneWebSocket(String userId) {
//        wsService.selectAll();
        webSocket.sendOneMessage(userId, "只要你乖给你买条gai！");
        return "websocket单人发送";
    }

    @RequestMapping("/sendGroupWebSocket")
    public String sendGroupWebSocket(String groupId) {
        List<Test> lists = wsService.selectAll();
        webSocket.sendGroupMessage(groupId, "只要你乖给你买" + JSON.toJSONString(lists));
        return "websocket分组发送" + JSON.toJSONString(lists);
    }


    /**
     * 获取微信token
     *
     * @param code
     * @return
     */
    private String getAccessToken(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        URI uri = URI.create(url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(uri);

        HttpResponse response;
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();

                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                StringBuilder sb = new StringBuilder();

                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    sb.append(temp);
                }

                JSONObject object = JSON.parseObject(sb.toString().trim());
                accessToken = object.getString("access_token");
                openID = object.getString("openid");
                refreshToken = object.getString("refresh_token");
                expires_in = object.getLong("expires_in");
                return accessToken;
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private Long expires_in = 0l;
    private String accessToken = "";
    private String openID = "";
    private String refreshToken = "";

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        if (isAccessTokenIsInvalid() && System.currentTimeMillis() < expires_in) {
            String uri = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openID;
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(URI.create(uri));
            try {
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                        builder.append(temp);
                    }
                    JSONObject object = JSON.parseObject(builder.toString().trim());
                    String nikeName = object.getString("nickname");
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 验证token
     *
     * @return
     */
    private boolean isAccessTokenIsInvalid() {
        String url = "https://api.weixin.qq.com/sns/auth?access_token=" + accessToken + "&openid=" + openID;
        URI uri = URI.create(url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(uri);
        HttpResponse response;
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();

                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                StringBuilder sb = new StringBuilder();

                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    sb.append(temp);
                }
                JSONObject object = JSON.parseObject(sb.toString().trim());
                int errorCode = object.getIntValue("errcode");
                if (errorCode == 0) {
                    return true;
                }
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     *  刷新token
     */
    private String APP_ID = "";
    private void refreshAccessToken() {
        String uri = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + APP_ID + "&grant_type=refresh_token&refresh_token=" + refreshToken;
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(URI.create(uri));
        try {
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    builder.append(temp);
                }
                JSONObject object = JSON.parseObject(builder.toString().trim());
                accessToken = object.getString("access_token");
                refreshToken = object.getString("refresh_token");
                openID = object.getString("openid");
                expires_in = object.getLong("expires_in");
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
