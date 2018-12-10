package com.inscripts.cometchatpulse.demo.Presenters;

import android.content.Context;
import android.widget.Toast;

import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.CreateGroupActivityContract;
import com.inscripts.cometchatpulse.demo.Utils.CommonUtils;
import com.cometchat.cometchatpulse.constants.CometChatConstants;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.models.Group;


public class CreateGroupActivityPresenter extends Presenter<CreateGroupActivityContract.CreateGroupView>
        implements CreateGroupActivityContract.CreateGroupPresenter {


    @Override
    public void createGroup(final Context context, Group group) {

        switch (group.getGroupType().toLowerCase()) {
            case CometChatConstants.GROUP_TYPE_PUBLIC:
                CometChat.createGroup(group, new CometChat.GroupCreateListener() {
                    @Override
                    public void onGroupCreated(Group group, CometChatException e) {

                        if (e == null) {
                            CommonUtils.startActivityIntent(group, context, true,null);
                            Toast.makeText(context, "Public Group Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case CometChatConstants.GROUP_TYPE_PASSWORD_PROTECTED:

                CometChat.createGroup(group, new CometChat.GroupCreateListener() {
                    @Override
                    public void onGroupCreated(Group group, CometChatException e) {

                        if (e == null) {
                            CommonUtils.startActivityIntent(group, context, true,null);
                            Toast.makeText(context, "Protected Group Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case CometChatConstants.GROUP_TYPE_PRIVATE:

                CometChat.createGroup(group, new CometChat.GroupCreateListener() {
                    @Override
                    public void onGroupCreated(Group group, CometChatException e) {

                        if (e == null) {
                            CommonUtils.startActivityIntent(group, context, true,null);
                            Toast.makeText(context, "Private Group Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;


        }


    }


}
