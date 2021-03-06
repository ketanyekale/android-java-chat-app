package com.inscripts.cometchatpulse.demo.Presenters;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.inscripts.cometchatpulse.demo.R;
import com.inscripts.cometchatpulse.demo.Activity.GroupDetailActivity;
import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.GroupDetailActivityContract;
import com.inscripts.cometchatpulse.demo.Contracts.StringContract;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.models.Group;

public class GroupDeatailActivityPresenter extends Presenter<GroupDetailActivityContract.GroupDetailView>
        implements GroupDetailActivityContract.GroupDetailPresenter {


    private Context context;

    @Override
    public void handleIntent(Intent data, Context context) {
        this.context = context;

        if (data.hasExtra(StringContract.IntentStrings.INTENT_GROUP_ID)) {
            String groupId = data.getStringExtra(StringContract.IntentStrings.INTENT_GROUP_ID);

            CometChat.getGroup(groupId, new CometChat.GroupFetchedListener() {
                @Override
                public void onResult(Group group, CometChatException e) {
                    if (isViewAttached()) {
                        getBaseView().setGroupId(group.getGuid());
                        getBaseView().setGroupName(group.getName());
                        getBaseView().setGroupOwnerName(group.getOwner());
                        getBaseView().setGroupIcon(group.getIcon());
                        getBaseView().setGroupDescription(group.getDescription());
                    }
                }
            });

        }

        if (CometChat.getLoggedInUser() != null) {
            if (isViewAttached())
            getBaseView().setOwnerDetail(CometChat.getLoggedInUser());
        }
    }

    @Override
    public void leaveGroup(String gUid) {

        CometChat.leaveGroup(gUid, new CometChat.GroupLeftListener() {
            @Override
            public void onGroupLeft(CometChatException e) {
                Toast.makeText(context, "You left the group", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void clearConversation(String gUid) {

    }

    @Override
    public void setIcon(GroupDetailActivity groupDetailActivity, String icon, ImageView groupImage) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(groupDetailActivity.getResources().getDrawable(R.drawable.ic_broken_image));
        Glide.with(groupDetailActivity).load(icon).apply(requestOptions).into(groupImage);
    }

}
