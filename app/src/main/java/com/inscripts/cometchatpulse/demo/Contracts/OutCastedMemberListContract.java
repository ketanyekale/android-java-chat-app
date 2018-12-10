package com.inscripts.cometchatpulse.demo.Contracts;

import android.content.Context;

import com.inscripts.cometchatpulse.demo.Adapter.GroupMemberListAdapter;
import com.inscripts.cometchatpulse.demo.Base.BasePresenter;
import com.cometchat.cometchatpulse.models.GroupMember;
import com.cometchat.cometchatpulse.models.GroupMember;

import java.util.List;

public interface OutCastedMemberListContract {

    interface OutCastedMemberListView{

        void setAdapter(List<GroupMember> list);
    }

    interface OutCastedMemberListPresenter extends BasePresenter<OutCastedMemberListView>
    {

        void initMemberList(String groupId, int limit, Context context);

        void reinstateUser(String uid, String groupId, GroupMemberListAdapter groupMemberListAdapter);
    }


}
