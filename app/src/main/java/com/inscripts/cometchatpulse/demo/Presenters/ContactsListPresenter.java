package com.inscripts.cometchatpulse.demo.Presenters;

import com.inscripts.cometchatpulse.demo.Base.Presenter;
import com.inscripts.cometchatpulse.demo.Contracts.ContactsContract;
import com.inscripts.cometchatpulse.demo.Utils.Logger;
import com.cometchat.cometchatpulse.core.CometChat;
import com.cometchat.cometchatpulse.core.UsersRequest;
import com.cometchat.cometchatpulse.exceptions.CometChatException;
import com.cometchat.cometchatpulse.models.User;
import java.util.List;


public class ContactsListPresenter extends Presenter<ContactsContract.ContactView>
        implements ContactsContract.ContactPresenter{

    private UsersRequest usersRequest ;


    @Override
    public void fecthUsers() {


        if (usersRequest==null) {
            usersRequest  = new UsersRequest.UsersRequestBuilder().setLimit(30).build();
            usersRequest.fetchNext(new UsersRequest.UsersReceivedListener() {
                @Override
                public void onUsersReceived(List<com.cometchat.cometchatpulse.models.User> list, CometChatException e) {
                    if (e==null) {
                        Logger.error("new user list resquest obj");
                        Logger.error("userList"," "+list.size());
                             if (isViewAttached())
                            getBaseView().setContactAdapter(list);

                    }
                }
            });
        }
        else {
            usersRequest.fetchNext(new UsersRequest.UsersReceivedListener() {
                @Override
                public void onUsersReceived(List<com.cometchat.cometchatpulse.models.User> list, CometChatException e) {
                      if (e==null) {
                          if (list != null && list.size() != 0) {
                              Logger.error("old user list resquest obj");
                              Logger.error("userList"," "+list.size());
                              if (isViewAttached())
                              getBaseView().setContactAdapter(list);
                          }
                      }

                }
            });
        }

    }

    @Override
    public void addPresenceListener(String presenceListener) {
        CometChat.addUserPresenceListener(presenceListener, new CometChat.UserPresenceListener() {
            @Override
            public void onUserOnline(User user) {
                   getBaseView().updatePresence(user);
            }

            @Override
            public void onUserOffline(User user) {
                  getBaseView().updatePresence(user);
            }
        });
    }

    @Override
    public void removePresenceListener(String presenceListener) {

        CometChat.removePresenceListener(presenceListener);
    }

    @Override
    public void getLoggedInUser() {
        User user=CometChat.getLoggedInUser();
        if (isViewAttached())
        getBaseView().setLoggedInUser(user);
    }


}
