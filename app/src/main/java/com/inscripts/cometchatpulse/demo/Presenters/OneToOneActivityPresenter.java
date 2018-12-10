package com.inscripts.cometchatpulse.demo.Presenters;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.cometchatpulse.constants.CometChatConstants;
import com.cometchat.cometchatpulse.core.Call;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.core.UserMessagesRequest;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.models.Action;
import com.cometchat.cometchatpulse.models.BaseMessage;
import com.cometchat.cometchatpulse.models.MediaMessage;
import com.cometchat.cometchatpulse.models.TextMessage;
import com.cometchat.cometchatpulse.models.User;
import com.inscripts.cometchatpulse.demo.R;
import com.inscripts.cometchatpulse.demo.Activity.OneToOneChatActivity;
import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.OneToOneActivityContract;
import com.inscripts.cometchatpulse.demo.Contracts.StringContract;
import com.inscripts.cometchatpulse.demo.CustomView.CircleImageView;
import com.inscripts.cometchatpulse.demo.Utils.Logger;
import com.inscripts.cometchatpulse.demo.Utils.MediaUtils;

import java.io.File;
import java.util.List;

public class OneToOneActivityPresenter extends Presenter<OneToOneActivityContract.OneToOneView>
        implements OneToOneActivityContract.OneToOnePresenter {

    private Context context;
    private UserMessagesRequest userMessagesRequest;

    @Override
    public void sendMessage(String message, String uId) {

        TextMessage textMessage = new TextMessage(uId, message, CometChatConstants.MESSAGE_TYPE_TEXT,
                CometChatConstants.RECEIVER_TYPE_USER);
        textMessage.setSentAt(System.currentTimeMillis());

        CometChat.sendMessage(textMessage, new CometChat.SendMessageListener() {
            @Override
            public void onMessageSent(BaseMessage baseMessage, CometChatException e) {
                if (e == null) {
                    if (isViewAttached()) {
                        MediaUtils.playSendSound(context, R.raw.send);
                        getBaseView().addSendMessage(baseMessage);
                    }

                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public void handleIntent(Intent intent) {

        if (intent.hasExtra(StringContract.IntentStrings.USER_ID)) {
            String uid = intent.getStringExtra(StringContract.IntentStrings.USER_ID);
            if (isViewAttached()) {
                getBaseView().setContactUid(uid);
            }

            CometChat.getUser(uid, new CometChat.UserFetchedListener() {
                @Override
                public void onResult(User user, CometChatException e) {
                    if (isViewAttached()) {
                        getBaseView().setPresence(user);
                    }
                }
            });
        }
        if (intent.hasExtra(StringContract.IntentStrings.USER_AVATAR)) {
            if (isViewAttached())
                getBaseView().setAvatar(intent.getStringExtra(StringContract.IntentStrings.USER_AVATAR));
        }
        if (intent.hasExtra(StringContract.IntentStrings.USER_NAME)) {
            if (isViewAttached())
                getBaseView().setTitle(intent.getStringExtra(StringContract.IntentStrings.USER_NAME));
        }
    }

    @Override
    public void addMessageReceiveListener(final String contactUid) {

        CometChat.addMessageEventListener(context.getString(R.string.message_listener), new CometChat.MessageEventListener() {
            @Override
            public void onMessageReceived(BaseMessage baseMessage) {
                try {
                    if (contactUid != null && contactUid.equals(baseMessage.getSender().getUid())) {
                        if (isViewAttached()) {
                            MediaUtils.playSendSound(context, R.raw.receive);
                            getBaseView().addMessage(baseMessage);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAction(Action action) {

            }

            @Override
            public void onCall(Call call) {

            }
        });


    }

    @Override
    public void sendMediaMessage(File filepath, String receiverUid, String type) {

        final MediaMessage mediaMessage = new MediaMessage(receiverUid, filepath, type,
                CometChatConstants.RECEIVER_TYPE_USER);
        mediaMessage.setSentAt(System.currentTimeMillis());

        CometChat.sendMediaMessage(mediaMessage, new CometChat.SendMessageListener() {
            @Override
            public void onMessageSent(BaseMessage baseMessage, CometChatException e) {
                if (e == null) {
                    if (isViewAttached()) {
                        MediaUtils.playSendSound(context, R.raw.send);
                        getBaseView().addMessage(baseMessage);
                    }

                    Toast.makeText(context, "Media Message Sent", Toast.LENGTH_SHORT).show();
                } else {
                    showToast(e.getMessage());
                }
            }
        });
    }

    @Override
    public void fetchPreviousMessage(String contactUid, int limit) {

        if (userMessagesRequest == null) {
            userMessagesRequest = new UserMessagesRequest.UserMessagesRequestBuilder(contactUid, System.currentTimeMillis()).setLimit(limit).build();
            userMessagesRequest.fetchPrevious(new CometChat.MessagesFetchListener() {
                @Override
                public void onMessagesReceived(List<BaseMessage> list, CometChatException e) {
                    if (e == null) {
                        for (BaseMessage baseMessage : list) {

                            Logger.error(" Message Id : " + baseMessage.getId() + " timestamp : " + baseMessage.getSentAt() + " list size :" + list.size());
                        }
                        Logger.error("new message request Obj");
                        if (isViewAttached())
                            getBaseView().setAdapter(list);
                    } else {
                        showToast(e.getMessage());
                    }
                }
            });
        } else {

            userMessagesRequest.fetchPrevious(new CometChat.MessagesFetchListener() {
                @Override
                public void onMessagesReceived(List<BaseMessage> list, CometChatException e) {
                    if (e == null) {
                        for (BaseMessage baseMessage : list) {

                            Logger.error(" Message Id : " + baseMessage.getId());
                        }
                        Logger.error("old message request obj");
                        if (list.size() != 0) {
                            if (isViewAttached())
                                getBaseView().setAdapter(list);
                        }
                    } else {
                        showToast(e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void getOwnerDetail() {
        User user = CometChat.getLoggedInUser();
        if (user != null) {
            if (isViewAttached())
                getBaseView().setOwnerDetail(user);

        }
    }

    @Override
    public void addPresenceListener(String presenceListener) {
        CometChat.addUserPresenceListener(presenceListener, new CometChat.UserPresenceListener() {
            @Override
            public void onUserOnline(User user) {
                if (isViewAttached())
                    getBaseView().setPresence(user);
            }

            @Override
            public void onUserOffline(User user) {
                if (isViewAttached())
                getBaseView().setPresence(user);
            }
        });
    }


    @Override
    public void removeMessageLisenter() {
        CometChat.removeMessageEventListener(context.getString(R.string.message_listener));
    }

    @Override
    public void setContactPic(OneToOneChatActivity oneToOneChatActivity, String avatar, CircleImageView circleImageView) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(oneToOneChatActivity.getResources().getDrawable(R.drawable.ic_broken_image));
        Glide.with(oneToOneChatActivity).load(avatar).apply(requestOptions).into(circleImageView);
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
