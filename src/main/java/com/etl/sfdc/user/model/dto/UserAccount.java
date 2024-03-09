package com.etl.sfdc.home.model.dto;

import com.etl.sfdc.user.model.dto.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;


public class UserAccount extends User {
    private Member member;
    public UserAccount(Member member) {
        super(member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.member = member;
    }
}
