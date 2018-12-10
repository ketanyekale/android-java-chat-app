package com.inscripts.cometchatpulse.demo.Presenters;

import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.LoginActivityContract;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.models.User;
import com.inscripts.cometchatpulse.demo.Contracts.StringContract;

public class LoginAcitivityPresenter extends Presenter<LoginActivityContract.LoginActivityView> implements
LoginActivityContract.LoginActivityPresenter{


    @Override
    public void Login(String uid) {

        CometChat.login(uid, StringContract.AppDetails.API_KEY, new CometChat.LoginListener() {
            @Override
            public void onLogin(User user, CometChatException e) {
                if (e==null)
                {     if (isViewAttached())
                    getBaseView().startCometChatActivity();
                }
                else
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void loginCheck() {

        try {
            if (CometChat.getLoggedInUser()!=null)
            {    if (isViewAttached())
               getBaseView().startCometChatActivity();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
