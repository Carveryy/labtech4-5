package labtech5.model;

public class Letter {
    private final int id;
    private final User sender;
    private final User receiver;
    private final String subject;
    private final String body;
    private final String sendDate;

    public Letter(int id, User sender, User receiver, String subject, String body, String sendDate) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.sendDate = sendDate;
    }

    public int getId() { return id; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getSendDate() { return sendDate; }
}