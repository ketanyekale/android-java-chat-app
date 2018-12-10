package com.inscripts.cometchatpulse.demo.Presenters;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.inscripts.cometchatpulse.demo.Adapter.GroupListAdapter;
import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.GroupListContract;
import com.cometchat.cometchatpulse.constants.CometChatConstants;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.core.GroupsRequest;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.helpers.Logger;
import com.cometchat.cometchatpulse.models.Group;

import java.util.List;

public class GroupListPresenter extends Presenter<GroupListContract.GroupView> implements
        GroupListContract.GroupPresenter  {

    private GroupsRequest groupsRequest;


    @Override
    public void initGroupView() {

        if (groupsRequest == null) {

            groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(50).build();

            groupsRequest.fetchNext(new GroupsRequest.GroupsReceivedListener() {
                @Override
                public void onGroupsReceived(List<Group> list, CometChatException e) {
                    Logger.error("Groups List Received : " + list);
                    if (e == null && list != null) {
                        if (isViewAttached())
                        getBaseView().setGroupAdapter(list);
                    }

                }
            });
        } else {
            groupsRequest.fetchNext(new GroupsRequest.GroupsReceivedListener() {
                @Override
                public void onGroupsReceived(List<Group> list, CometChatException e) {
                    Logger.error("Groups List Received : " + list);

                    if (e == null && list.size() != 0) {
                         if (isViewAttached())
                        getBaseView().setGroupAdapter(list);
                    }

                }
            });
        }
    }


    @Override
    public void joinGroup(final Context context, final Group group, final ProgressDialog progressDialog,
                          final GroupListAdapter groupListAdapter) {
        switch (group.getGroupType()) {

            case CometChatConstants.GROUP_TYPE_PUBLIC:

                CometChat.joinGroup(group.getGuid(), group.getGroupType(), null, new CometChat.GroupJoinedListener() {
                    @Override
                    public void onGroupJoined(CometChatException e) {
                        if (e == null) {
                            showToast(context, "yes");
                            progressDialog.dismiss();
                            group.setHasJoined(true);
                            groupListAdapter.notifyDataSetChanged();
                            if (isViewAttached())
                            getBaseView().groupjoinCallback(group);
                        } else {
                            progressDialog.dismiss();
                            showToast(context, "no");
                        }
                    }


                });
                break;

            case CometChatConstants.GROUP_TYPE_PRIVATE:
                CometChat.joinGroup(group.getGuid(), group.getGroupType(), null, new CometChat.GroupJoinedListener() {
                    @Override
                    public void onGroupJoined(CometChatException e) {
                        if (e == null) {
                            showToast(context, "yes");
                            progressDialog.dismiss();
                            group.setHasJoined(true);
                            groupListAdapter.notifyDataSetChanged();
                            if (isViewAttached())
                            getBaseView().groupjoinCallback(group);
                        } else {
                            progressDialog.dismiss();
                            showToast(context, "no");
                        }
                    }
                });

                break;

            case CometChatConstants.GROUP_TYPE_PASSWORD_PROTECTED:

                CometChat.joinGroup(group.getGuid(), group.getGroupType(), group.getPassword(), new CometChat.GroupJoinedListener() {
                    @Override
                    public void onGroupJoined(CometChatException e) {
                        if (e == null) {
                            showToast(context, "yes");
                            progressDialog.dismiss();
                            group.setHasJoined(true);
                            groupListAdapter.notifyDataSetChanged();
                            if (isViewAttached())
                            getBaseView().groupjoinCallback(group);
                        } else {
                            progressDialog.dismiss();
                            showToast(context, "no");
                        }
                    }
                });
                break;


        }
    }

    @Override
    public void refresh() {
           groupsRequest=null;
           initGroupView();
    }


    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
