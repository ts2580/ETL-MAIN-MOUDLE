package com.etl.sfdc.etl.controller;

import com.etl.sfdc.common.UserSession;
import com.etl.sfdc.etl.dto.ObjectDefinition;
import com.etl.sfdc.etl.service.ETLService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("etl")
public class ETLController {

    private final UserSession userSession;

    private final ETLService etlService;

    @GetMapping("/objects")
    public String getObjects(Model model) throws Exception {

        List<ObjectDefinition> objectDefinitions = etlService.getObjects();

        if(userSession.getUserAccount() != null){
            model.addAttribute(userSession.getUserAccount().getMember());
            model.addAttribute("objectDefinitions", objectDefinitions);
        }

        return "object_select_form";

    }

    @PostMapping("/ddl")
    public String setObjects(@RequestParam("selectedObject") String selectedObject, Model model, RedirectAttributes redirectAttributes) throws Exception {


        if(userSession.getUserAccount() != null){
            model.addAttribute(userSession.getUserAccount().getMember());
        }

        etlService.setObjects(selectedObject);

        return "home_form";
    }
}
