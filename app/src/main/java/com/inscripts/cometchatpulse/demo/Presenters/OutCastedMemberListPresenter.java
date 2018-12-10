package com.inscripts.cometchatpulse.demo.Presenters;

import android.content.Context;
import android.widget.Toast;

import com.inscripts.cometchatpulse.demo.Adapter.GroupMemberListAdapter;
import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.OutCastedMemberListContract;
import com.inscripts.cometchatpulse.demo.Utils.Logger;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.core.OutcastMembersRequest;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.models.GroupMember;

import java.util.List;

public class OutCastedMemberListPresenter extends Presenter<OutCastedMemberListContract.OutCastedMemberListView>
        implements OutCastedMemberListContract.OutCastedMemberListPresenter {

    private OutcastMembersRequest outcastMembersRequest;
    private Context context;

    @Override
    public void initMemberList(String groupId, int limit, Context context) {

        this.context = context;

        if (outcastMembersRequest == null) {
            outcastMembersRequest = new OutcastMembersRequest.OutcastMembersRequestBuilder(groupId).setLimit(limit).build();

            outcastMembersRequest.fetchNext(new OutcastMembersRequest.OutcastMembersReceivedListener() {
                @Override
                public void onResult(List<GroupMember> list, CometChatException e) {
                    if (e == null) {
                        if (list != null && list.size() != 0) {
                            Logger.error("OutcastMembersRequest", " " + list.size());
                             if (isViewAttached())
                            getBaseView().setAdapter(list);
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            outcastMembersRequest.fetchNext(new OutcastMembersRequest.OutcastMembersReceivedListener() {
                @Override
                public void onResult(List<GroupMember> list, CometChatException e) {
                    if (e == null) {
                        if (list != null && list.size() != 0) {
                            Logger.error("OutcastMembersRequest", " " + list.size());
                            if (isViewAttached())
                            getBaseView().setAdapter(list);
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    public void reinstateUser(final String uid, String groupId, final GroupMemberListAdapter groupMemberListAdapter) {
        CometChat.reinstateUser(groupId, uid, new CometChat.UserReinstatedListener() {
            @Override
            public void onResult(CometChatException e) {
                if (e == null) {

                    groupMemberListAdapter.removeMember(uid);
                    Logger.error("UserReinstatedListener", "Success");
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
