package com.inscripts.cometchatpulse.demo.Presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.cometchatpulse.core.GroupMessagesRequest;
import com.inscripts.cometchatpulse.demo.Activity.CometChatActivity;
import com.inscripts.cometchatpulse.demo.Activity.GroupChatActivity;
import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.GroupChatActivityContract;
import com.inscripts.cometchatpulse.demo.Contracts.StringContract;
import com.inscripts.cometchatpulse.demo.CustomView.CircleImageView;
import com.inscripts.cometchatpulse.demo.R;
import com.inscripts.cometchatpulse.demo.Utils.Logger;
import com.inscripts.cometchatpulse.demo.Utils.MediaUtils;
import com.cometchat.cometchatpulse.constants.CometChatConstants;
import com.cometchat.cometchatpulse.core.Call;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.core.GroupMembersRequest;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.models.Action;
import com.cometchat.cometchatpulse.models.BaseMessage;
import com.cometchat.cometchatpulse.models.Group;
import com.cometchat.cometchatpulse.models.GroupMember;
import com.cometchat.cometchatpulse.models.MediaMessage;
import com.cometchat.cometchatpulse.models.TextMessage;


import java.io.File;
import java.util.List;

public class GroupChatPresenter extends Presenter<GroupChatActivityContract.GroupChatView>
        implements GroupChatActivityContract.GroupChatPresenter {

    private Context context;

    private GroupMessagesRequest groupMessagesRequest;



    @Override
    public void getContext(Context context) {

        this.context = context;
    }

    @Override
    public void handleIntent(Intent intent) {

        if (intent.hasExtra(StringContract.IntentStrings.INTENT_GROUP_ID)) {
            String id = CometChat.getLoggedInUser().getUid();
            getBaseView().setOwnerUid(id);
            CometChat.getGroup(intent.getStringExtra(StringContract.IntentStrings.INTENT_GROUP_ID), new CometChat.GroupFetchedListener() {
                @Override
                public void onResult(Group group, CometChatException e) {
                    if (isViewAttached())
                    getBaseView().setGroup(group);
                }
            });
            getBaseView().setGroupId(intent.getStringExtra(StringContract.IntentStrings.INTENT_GROUP_ID));
        }
        if (intent.hasExtra(StringContract.IntentStrings.INTENT_GROUP_NAME)) {
            if (isViewAttached())
            getBaseView().setTitle(intent.getStringExtra(StringContract.IntentStrings.INTENT_GROUP_NAME));
        }

    }

    @Override
    public void addMessageReceiveListener(String listenerId, final String groupId, final String ownerId) {

        CometChat.addMessageEventListener(listenerId, new CometChat.MessageEventListener() {
            @Override
            public void onMessageReceived(BaseMessage baseMessage) {

                if (groupId != null && groupId.equals(baseMessage.getReceiverUid()) &&
                        !baseMessage.getSender().getUid().equals(ownerId)) {
                    MediaUtils.playSendSound(context,R.raw.receive);
                    if (isViewAttached())
                    getBaseView().addReceivedMessage(baseMessage);
                }
            }

            @Override
            public void onAction(Action action) {

                if (groupId != null && groupId.equals(action.getReceiverUid())){
                     if (isViewAttached())
                    getBaseView().addSentMessage(action);
                }
            }

            @Override
            public void onCall(Call call) {

            }
        });
    }

    @Override
    public void removeMessageReceiveListener(String ListenerId) {
        try {
            CometChat.removeMessageEventListener(ListenerId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendTextMessage(final String message, String groupId) {

        TextMessage textMessage = new TextMessage(groupId, message,
                CometChatConstants.MESSAGE_TYPE_TEXT, CometChatConstants.RECEIVER_TYPE_GROUP);

        CometChat.sendMessage(textMessage, new CometChat.SendMessageListener() {
            @Override
            public void onMessageSent(BaseMessage baseMessage, CometChatException e) {

                if (e == null) {
                    MediaUtils.playSendSound(context,R.raw.send);
                    if (isViewAttached())
                    getBaseView().addSentMessage(baseMessage);
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void addActionMessageListener() {

    }

    @Override
    public void fetchPreviousMessage(String groupId, int limit) {

        if (groupMessagesRequest == null) {
            groupMessagesRequest = new GroupMessagesRequest.GroupMessagesRequestBuilder(groupId, System.currentTimeMillis()).setLimit(limit).build();
            groupMessagesRequest.fetchPrevious(new CometChat.MessagesFetchListener() {
                @Override
                public void onMessagesReceived(List<BaseMessage> list, CometChatException e) {
                    if (e == null) {
                        for (BaseMessage baseMessage : list) {
                            Logger.error("groupMessage" + baseMessage.getId() + " timestamp : " + baseMessage.getSentAt() + " list size :" + list.size());

                        }
                        if (isViewAttached()) {
                            getBaseView().setAdapter(list);
                        }
                    }
                }
            });
        } else {

//               userMessagesRequest=new UserMessagesRequest.UserMessagesRequestBuilder(friendUid,id).setLimit(limit).build();
            groupMessagesRequest.fetchPrevious(new CometChat.MessagesFetchListener() {
                @Override
                public void onMessagesReceived(List<BaseMessage> list, CometChatException e) {
                    if (e == null) {
                        for (BaseMessage baseMessage : list) {
                            Logger.error("groupMessage" + baseMessage.getId());

                        }

                        if (list.size() != 0) {
                            if (isViewAttached())
                            getBaseView().setAdapter(list);
                        }
                    }
                }
            });
        }

    }

    @Override
    public void fetchGroupMembers(String groupId) {


        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(groupId).setLimit(5).build();

        groupMembersRequest.fetchNext(new GroupMembersRequest.GroupMembersReceivedListener() {
            @Override
            public void onGroupMembersReceived(List<GroupMember> list, CometChatException e) {

                if (e == null) {
                    String s[] = new String[0];
                    if (list != null && list.size() != 0) {
                         s= new String[list.size()];
                        for (int j = 0; j < list.size(); j++) {

                            s[j] = list.get(j).getUser().getName();
                        }

                    }
                    if (isViewAttached())
                    getBaseView().setSubTitle(s);
                }
            }
        });

    }

    @Override
    public void setGroupIcon(GroupChatActivity groupChatActivity, String icon, CircleImageView groupIcon) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(groupChatActivity.getResources().getDrawable(R.drawable.ic_broken_image));
        Glide.with(groupChatActivity).load(icon).apply(requestOptions).into(groupIcon);

    }

    @Override
    public void sendMediaMessage(File file, String groupId, String messageType) {
        MediaMessage mediaMessage = new MediaMessage(groupId, file, messageType,
                CometChatConstants.RECEIVER_TYPE_GROUP);
        mediaMessage.setSentAt(System.currentTimeMillis());

        CometChat.sendMediaMessage(mediaMessage, new CometChat.SendMessageListener() {
            @Override
            public void onMessageSent(BaseMessage baseMessage, CometChatException e) {
                if (e == null) {
                    if (isViewAttached()) {
                        getBaseView().addSentMessage(baseMessage);
                    }
                    MediaUtils.playSendSound(context,R.raw.send);
                }
            }
        });
    }

    @Override
    public void leaveGroup(final Group group, final Context context) {

        CometChat.leaveGroup(group.getGuid(), new CometChat.GroupLeftListener() {
            @Override
            public void onGroupLeft(CometChatException e) {
                if (e == null) {
                    group.setHasJoined(false);
                    Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context,CometChatActivity.class);
                    ((Activity)context).startActivityForResult(intent,StringContract.RequestCode.LEFT);
                    ((GroupChatActivity)context).finish();
                }
            }
        });
    }


}
