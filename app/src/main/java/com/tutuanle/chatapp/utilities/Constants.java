package com.tutuanle.chatapp.utilities;

import com.tutuanle.chatapp.R;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NUMBER_PHONE = "numberPhone";
    public static final String KEY_PREFERENCE_NAME = "ChatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_FEELING = "feeling";
    public static final String KEY_IS_ACTIVE = "isActive";
    public static final String KEY_ON_CHAT = "onChat";
    public static final String KEY_IS_SEEN = "isSeen";
    public static final String KEY_COLLECTION_CUSTOM_CHAT = "customChat";
    public static final String KEY_USER_UID_1 = "userUid1";
    public static final String KEY_USER_UID_2 = "userUid2";
    public static final String KEY_THEME = "theme";
    public static final String KEY_GRADIENT = "gradient";
    public static final String KEY_TYPE_MESSAGE = "typeMessage";
    public static final String KEY_SEND_IMAGE = "image_bitmap";
    public static final String KEY_SEND_VIDEO = "url_video";
    public static final String KEY_SEND_RECORD = "url_record";
    public static final String KEY_SEND_LINK = "url_link";
    public static final String KEY_COLLECTION_STORIES = "stories";
    public static final String KEY_COLLECTION_STATUSES = "statuses";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_LAST_UPDATE = "lastUpdate";


    public static int[] REACTIONS = new int[]{
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_wow,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry
    };

    public static int[] THEMES = new int[]{
            0,
            R.drawable.bg_1,
            R.drawable.bg_2,
            R.drawable.bg_3,
            R.drawable.bg_4,
            R.drawable.bg_5,
            R.drawable.bg_6
    };

    public static int[] REACTIONS_ANIMATION = new int[]{
            R.raw.like,
            R.raw.heart,
            R.raw.laugh,
            R.raw.wow,
            R.raw.sad,
            R.raw.angry,
    };


    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_COLLECTION_FRIENDS = "friends";
    public static final String KEY_COLLECTION_USER_GROUP = "groups";
    public static final String KEY_COUNT_NUMBER_OF_MESSAGE_SEEN = "countMessageSeen";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";
    public static final String REMOTE_MSG_MEETING_ROM = "meetingRoom";
    public static final String KEY_STATUS = "status";


    private static final String KEY_REMOTE_MSG = "key=AAAASl2bJrA:APA91bFjjqi92_OnCyBkr-ddg1iOkp-8eex5bDCy2pPMWAe-pJmArxHhKoJjT87ruqSj8k0nNlYI6XJ5mhYVs4V6gWTBbdp_6JC573Wq8A9n-dC59Ra2SiKziER1tyl9Gb-XMLv-AlVr";
    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAASl2bJrA:APA91bFjjqi92_OnCyBkr-ddg1iOkp-8eex5bDCy2pPMWAe-pJmArxHhKoJjT87ruqSj8k0nNlYI6XJ5mhYVs4V6gWTBbdp_6JC573Wq8A9n-dC59Ra2SiKziER1tyl9Gb-XMLv-AlVr"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }

}
