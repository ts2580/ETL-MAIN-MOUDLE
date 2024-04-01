package com.etl.sfdc.temp.controller;
import com.etl.sfdc.common.UserSession;
import lombok.RequiredArgsConstructor;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Controller
@RequiredArgsConstructor
@RequestMapping("cometd")
public class TempBayeuxController {

    private final UserSession userSession;

    @GetMapping("hand-shake")
    public String getSubscriptionForm(Model model) {

        if(userSession.getUserAccount() != null){
            model.addAttribute(userSession.getUserAccount().getMember());
        }

        return "cometd_form";
    }

    @PostMapping("hand-shake")
    public String patchEventBus(Model model, @RequestParam("TopicNm") String topicNm) {

        try {
            String salesforceInstance = "https://ecologysyncmanagement-dev-ed.develop.my.salesforce.com";
            String apiVersion = "60.0"; // Salesforce API 버전
            String topic = "/topic/" + topicNm;
            String accessToken = "00DIR000001LCVR!AQsAQJy_Xr4C8o0CZ1_SGetxYG7QmtgHCWm0clYNaCpFZ.yEmvpg9ITg.4TqoKclpFkv06z9zGzFq2P1tk71.al6U5qKhseY";

            // HTTP 클라이언트 생성
            HttpClient httpClient = new HttpClient();
            httpClient.start();

            // CometD 클라이언트 생성. Salesforce에 인증위해 Access Token 넣어줌
            LongPollingTransport httpTransport = new LongPollingTransport(null, httpClient) {
                @Override
                protected void customize(Request request) {
                    request.header("Authorization", "Bearer " + accessToken);;
                }
            };

            BayeuxClient client = new BayeuxClient(salesforceInstance + "/cometd/" + apiVersion, httpTransport);

            // PushTopic 구독
            client.handshake(handshakeReply -> {
                if (handshakeReply.isSuccessful()) {
                    ClientSessionChannel channel = client.getChannel(topic);
                    channel.subscribe((channel1, message) -> {
                        System.out.println(message.getData());
                    });
                } else {
                    System.out.println("Handshake 실패: " + handshakeReply);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute(userSession.getUserAccount().getMember());
        model.addAttribute("topicNm",topicNm);

        return "cometd_form";
    }
}
