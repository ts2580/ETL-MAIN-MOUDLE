package com.etl.sfdc.temp.controller;
import com.etl.sfdc.common.UserSession;
import com.etl.sfdc.temp.model.service.TempService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("cometd")
public class TempBayeuxController {

    private final UserSession userSession;
    private final TempService tempService;

    @GetMapping("hand-shake")
    public String getSubscriptionForm(Model model) {

        if(userSession.getUserAccount() != null){
            model.addAttribute(userSession.getUserAccount().getMember());
        }

        return "cometd_form";
    }

    @PostMapping("hand-shake")
    public String patchEventBus(Model model, @RequestParam("TopicNm") String topicNm) {

        tempService.patchCometD(topicNm);

        model.addAttribute(userSession.getUserAccount().getMember());
        model.addAttribute("topicNm",topicNm);

        return "cometd_form";
    }
}
