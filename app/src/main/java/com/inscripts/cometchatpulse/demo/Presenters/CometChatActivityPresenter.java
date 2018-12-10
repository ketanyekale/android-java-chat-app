package com.inscripts.cometchatpulse.demo.Presenters;

import android.content.Context;

import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.CometChatActivityContract;
import com.inscripts.cometchatpulse.demo.Utils.CommonUtils;
import com.cometchat.cometchatpulse.constants.CometChatConstants;
import com.cometchat.cometchatpulse.core.Call;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.models.Action;
import com.cometchat.cometchatpulse.models.BaseMessage;

public class CometChatActivityPresenter extends Presenter<CometChatActivityContract.CometChatActivityView>
implements CometChatActivityContract.CometChatActivityPresenter {

    @Override
    public void addMessageListener(final Context context, String listnerId) {

        CometChat.addMessageEventListener(listnerId, new CometChat.MessageEventListener() {
            @Override
            public void onMessageReceived(BaseMessage baseMessage) {

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
    public void removeMessageListener(String listenerId) {

    }
}
