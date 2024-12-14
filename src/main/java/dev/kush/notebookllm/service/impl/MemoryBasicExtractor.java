package dev.kush.notebookllm.service.impl;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.stereotype.Service;

@Service
public class MemoryBasicExtractor {

    public String extractResponseContent(AdvisedResponse advisedResponse) {
        return advisedResponse.response().getResult().getOutput().getContent();
    }

    public String extractRequestContent(AdvisedRequest advisedRequest) {
        return advisedRequest.userText();
    }


}