package com.etl.sfdc.user.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;


@Getter
public class UserAccount extends User {
    private Member member;
    public UserAccount(Member member) {
        super(member.getEmail(), member.getPassword(), member.getAuthority());
        this.member = member;
    }
}