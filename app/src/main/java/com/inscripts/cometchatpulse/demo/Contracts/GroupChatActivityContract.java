package com.inscripts.cometchatpulse.demo.Contracts;

import android.content.Context;
import android.content.Intent;

import com.inscripts.cometchatpulse.demo.Activity.GroupChatActivity;
import com.inscripts.cometchatpulse.demo.Base.BasePresenter;
import com.inscripts.cometchatpulse.demo.Base.BaseView;
import com.inscripts.cometchatpulse.demo.CustomView.CircleImageView;
import com.cometchat.cometchatpulse.models.BaseMessage;
import com.cometchat.cometchatpulse.models.Group;
import com.cometchat.cometchatpulse.models.User;

import java.io.File;
import java.util.List;

public interface GroupChatActivityContract {


    interface GroupChatView extends BaseView {

        void setGroupId(String stringExtra);

        void setAdapter(List<BaseMessage> messageList);

        void addSentMessage(BaseMessage baseMessage);

        void setGroup(Group group);


        void setSubTitle(String[] users);

        void setOwnerUid(String id);

        void addReceivedMessage(BaseMessage baseMessage);
    }

    interface GroupChatPresenter extends BasePresenter<GroupChatView> {

        void getContext(Context context);

        void handleIntent(Intent intent);

        void addMessageReceiveListener(String ListenerId,String groupId,String ownerId);

        void removeMessageReceiveListener(String ListenerId);

        void sendTextMessage(String message, String groupId);

        void fetchPreviousMessage(String groupId, int limit);

        void setGroupIcon(GroupChatActivity groupChatActivity, String icon, CircleImageView groupIcon);

        void sendMediaMessage(File imagefile, String groupId, String messageType);

        void leaveGroup(Group group,Context context);

        void fetchGroupMembers(String groupId);

        void addActionMessageListener();
    }

}
