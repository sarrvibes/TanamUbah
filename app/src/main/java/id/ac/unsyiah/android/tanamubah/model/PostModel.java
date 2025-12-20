package id.ac.unsyiah.android.tanamubah.model;


public class PostModel {

    private String postId;
    private String userId;
    private String userName;
    private String content;
    private String imageUrl;
    private long timestamp;

    // WAJIB constructor kosong untuk Firestore
    public PostModel() {}

    public PostModel(String postId, String userId, String userName,
                     String content, String imageUrl, long timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

