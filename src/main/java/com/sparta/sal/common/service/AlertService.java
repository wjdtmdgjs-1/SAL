package com.sparta.sal.common.service;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsCreateRequest;
import com.slack.api.methods.request.conversations.ConversationsInviteRequest;
import com.slack.api.methods.request.users.UsersLookupByEmailRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsCreateResponse;
import com.slack.api.methods.response.conversations.ConversationsInviteResponse;
import com.slack.api.methods.response.users.UsersLookupByEmailResponse;
import com.sparta.sal.common.exception.InputOutputException;
import com.sparta.sal.common.exception.SlackException;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertService {

    @Value("${slack.bot.token}")
    private String slackToken;

    public void sendMessage(String slackChannel, String message) {
        Slack slack = Slack.getInstance();

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(slackChannel)
                .text(message)
                .build();

        ChatPostMessageResponse response;

        try {
            response = slack.methods(slackToken).chatPostMessage(request);
        } catch (IOException e) {
            throw new InputOutputException(e.getMessage());
        } catch (SlackApiException e) {
            throw new SlackException(e.getMessage());
        }

        if (!response.isOk()) {
            throw new RuntimeException("Error sending message to Slack: " + response.getError());
        }
    }

    public void createSlackChannel(WorkSpace workSpace) {
        Slack slack = Slack.getInstance();

        ConversationsCreateRequest request = ConversationsCreateRequest.builder()
                .name(workSpace.getWorkSpaceTitle())
                .build();

        ConversationsCreateResponse response;

        try {
            response = slack.methods(slackToken).conversationsCreate(request);
        } catch (IOException e) {
            throw new InputOutputException(e.getMessage());
        } catch (SlackApiException e) {
            throw new SlackException(e.getMessage());
        }

        workSpace.updateSlackChannel(response.getChannel().getId());

        if (!response.isOk()) {
            throw new RuntimeException("Error sending message to Slack: " + response.getError());
        }
    }

    public String findSlackIdByEmail(String email) {
        Slack slack = Slack.getInstance();

        UsersLookupByEmailRequest request = UsersLookupByEmailRequest.builder()
                .email(email)
                .build();

        UsersLookupByEmailResponse response;

        try {
            response = slack.methods(slackToken).usersLookupByEmail(request);
        } catch (IOException e) {
            throw new InputOutputException(e.getMessage());
        } catch (SlackApiException e) {
            throw new SlackException(e.getMessage());
        }

        if (!response.isOk()) {
            throw new RuntimeException("Error sending message to Slack: " + response.getError());
        }

        return response.getUser().getId();
    }

    public void inviteSlackChannel(String channelId, String slackId) {
        Slack slack = Slack.getInstance();

        List<String> slackIdList = new ArrayList<>();
        slackIdList.add(slackId);

        ConversationsInviteRequest request = ConversationsInviteRequest.builder()
                .channel(channelId)
                .users(slackIdList)
                .build();

        ConversationsInviteResponse response;

        try {
            response = slack.methods(slackToken).conversationsInvite(request);
        } catch (IOException e) {
            throw new InputOutputException(e.getMessage());
        } catch (SlackApiException e) {
            throw new SlackException(e.getMessage());
        }

        if (!response.isOk()) {
            throw new RuntimeException("Error sending message to Slack: " + response.getError());
        }
    }
}