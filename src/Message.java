public class Message {

    int msg_uid;
    boolean isAnnouncement;

    public Message(int msg_uid, boolean isAnnouncement) {
        this.msg_uid = msg_uid;
        this.isAnnouncement = isAnnouncement;
    }
}