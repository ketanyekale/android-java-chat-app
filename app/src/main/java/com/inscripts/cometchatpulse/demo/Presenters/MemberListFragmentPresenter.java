package com.inscripts.cometchatpulse.demo.Presenters;

import android.content.Context;
import android.widget.Toast;

import com.inscripts.cometchatpulse.demo.Adapter.GroupMemberListAdapter;
import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.MemberListFragmentContract;
import com.inscripts.cometchatpulse.demo.Utils.Logger;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.core.GroupMembersRequest;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.models.GroupMember;

import java.util.List;

public class MemberListFragmentPresenter extends Presenter<MemberListFragmentContract.MemberListFragmentView>
    implements MemberListFragmentContract.MemberListFragmentPresenter {

    private GroupMembersRequest groupMembersRequest;

    private Context context;

    @Override
    public void initMemberList(String guid, int LIMIT,Context context) {

        this.context=context;


        if (groupMembersRequest==null)
        {
            groupMembersRequest=new GroupMembersRequest.GroupMembersRequestBuilder(guid).setLimit(LIMIT).build();

            groupMembersRequest.fetchNext(new GroupMembersRequest.GroupMembersReceivedListener() {
                @Override
                public void onGroupMembersReceived(List<GroupMember> list, CometChatException e) {

                    if (e==null)
                    {
                        if (list!=null&&list.size()!=0)
                        {
                            Logger.error("groupMembersRequest"," "+list.size());
                            if (isViewAttached())
                            getBaseView().setAdapter(list);
                        }
                    }
                    else {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {

            groupMembersRequest.fetchNext(new GroupMembersRequest.GroupMembersReceivedListener() {
                @Override
                public void onGroupMembersReceived(List<GroupMember> list, CometChatException e) {
                    if (e==null)
                    {
                        if (list!=null&&list.size()!=0)
                        {
                            Logger.error("groupMembersRequest"," "+list.size());
                            if (isViewAttached())
                            getBaseView().setAdapter(list);
                        }
                    }
                    else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void outCastUser(final String uid, String groupGuid, final GroupMemberListAdapter groupMemberListAdapter) {

        CometChat.outcastUser(groupGuid, uid, new CometChat.UserOutcastedListener() {
            @Override
            public void onResult(CometChatException e) {
                if (e==null) {
                    groupMemberListAdapter.removeMember(uid);
                    Logger.error("UserOutcastedListener", "Success");
                }
                else {

                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void kickUser(final String uid, String groupId, final GroupMemberListAdapter groupMemberListAdapter) {

        CometChat.kickUser(uid, groupId, new CometChat.UserKickedListener() {
            @Override
            public void onResult(CometChatException e) {
                if (e==null)
                {
                    groupMemberListAdapter.removeMember(uid);
                    Logger.error("UserKickedListener","Success");
                }
                else{
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
