package com.etl.sfdc.temp.model.service;

public interface TempService {

    long tryHttpUrlConnection(String number);

    long tryWebClient(String number);

    void patchCometD(String topicNm);
}
