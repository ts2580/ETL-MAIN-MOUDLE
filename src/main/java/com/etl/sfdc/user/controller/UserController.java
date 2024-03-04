package com.etl.sfdc.user.controller;

import com.etl.sfdc.user.model.dto.Member;
import com.etl.sfdc.user.model.dto.UserCreateForm;
import com.etl.sfdc.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @ResponseBody
    @PostMapping("/login")
    public Member login(@RequestBody Map<String, Object> map) {

        // RestController가 아니므로 @ResponseBody 붙여줘야함

        Member member = new Member();

        try {

             String username = String.valueOf(map.get("userName"));
             String password = String.valueOf(map.get("password"));
             member = userService.getUserDes(username);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return member;
    }

    @GetMapping("/signup")
    public String signup(Model model) {

        // 폼에 바인딩할 빈 객체를 모델에 추가
        model.addAttribute("UserCreateForm", new UserCreateForm());

        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {

        /*if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }*/

        userService.create(userCreateForm.getUsername(),
                userCreateForm.getEmail(), userCreateForm.getPassword1());

        return "redirect:/";
    }

}
