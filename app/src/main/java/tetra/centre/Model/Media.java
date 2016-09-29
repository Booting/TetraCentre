package tetra.centre.Model;

public class Media {
    private String MediaId;
    private String UserId;
    private String Name;
    private String Photo;
    private String Title;
    private String Description;
    private String Url;

    public Media(String MediaId, String UserId, String Name, String Photo, String Title, String Description, String Url) {
        this.MediaId     = MediaId;
        this.UserId      = UserId;
        this.Name        = Name;
        this.Photo       = Photo;
        this.Title       = Title;
        this.Description = Description;
        this.Url         = Url;
    }

    public String getMediaId() {
        return MediaId;
    }
    public String getUserId() {
        return UserId;
    }
    public String getName() {
        return Name;
    }
    public String getPhoto() {
        return Photo;
    }
    public String getTitle() {
        return Title;
    }
    public String getDescription() {
        return Description;
    }
    public String getUrl() {
        return Url;
    }
}