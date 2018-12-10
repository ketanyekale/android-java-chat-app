package com.inscripts.cometchatpulse.demo.Contracts;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.inscripts.cometchatpulse.demo.Base.BasePresenter;
import com.cometchat.cometchatpulse.models.User;

public interface UserProfileViewActivityContract {

    interface UserProfileActivityView{

        void setTitle(String name);

        void setStatus(String status,long lastActiveAt);

        void setUserImage(String avatar);

        void setUserId(String uid);
    }

    interface UserProfileActivityPresenter extends BasePresenter<UserProfileActivityView> {

        void handleIntent(Intent data);

        void setContactAvatar(Context context, String avatar, ImageView userAvatar);

        void addUserPresenceListener(String presenceListener);

        void removeUserPresenceListener(String presenceListener);


    }
}
