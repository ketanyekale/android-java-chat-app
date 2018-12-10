package com.inscripts.cometchatpulse.demo.Presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.models.User;
import com.inscripts.cometchatpulse.demo.R;
import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.StringContract;
import com.inscripts.cometchatpulse.demo.Contracts.UserProfileViewActivityContract;


public class UserProfileViewPresenter extends Presenter<UserProfileViewActivityContract.UserProfileActivityView> implements
        UserProfileViewActivityContract.UserProfileActivityPresenter {


    @Override
    public void handleIntent(Intent data) {


        if (data.hasExtra(StringContract.IntentStrings.USER_ID)) {
            String uid = data.getStringExtra(StringContract.IntentStrings.USER_ID);
            getBaseView().setUserId(uid);

        }
        if (data.hasExtra(StringContract.IntentStrings.USER_NAME)) {
            String name = data.getStringExtra(StringContract.IntentStrings.USER_NAME);
            getBaseView().setTitle(name);
        }
        if (data.hasExtra(StringContract.IntentStrings.USER_AVATAR)) {
            getBaseView().setUserImage(data.getStringExtra(StringContract.IntentStrings.USER_AVATAR));
        }


    }

    @SuppressLint("CheckResult")
    @Override
    public void setContactAvatar(Context context, String avatar, ImageView userAvatar) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(context.getResources().getDrawable(R.drawable.ic_broken_image));
        Glide.with(context).load(avatar).apply(requestOptions).into(userAvatar);

    }


    @Override
    public void addUserPresenceListener(String presenceListener) {

        CometChat.addUserPresenceListener(presenceListener, new CometChat.UserPresenceListener() {
            @Override
            public void onUserOnline(User user) {
                if (isViewAttached())
                    getBaseView().setStatus(user.getStatus(), user.getLastActiveAt());
            }

            @Override
            public void onUserOffline(User user) {
                if (isViewAttached())
                    getBaseView().setStatus(user.getStatus(), user.getLastActiveAt());
            }
        });
    }

    @Override
    public void removeUserPresenceListener(String presenceListener) {

        CometChat.removePresenceListener(presenceListener);
    }
}
