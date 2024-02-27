package com.etl.sfdc.config.model.service;

import com.etl.sfdc.config.model.dto.User;
import com.etl.sfdc.config.model.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService{

    private final ConfigRepository configRepository;

    public User getUserDes(String name) {

        User user = configRepository.getUserDes(name);

        user = user == null ? new User() : user;

        return user;
    }
}
















