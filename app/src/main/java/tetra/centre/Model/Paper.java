package tetra.centre.Model;

public class Paper {
    private String PaperId;
    private String UserId;
    private String Name;
    private String Photo;
    private String Title;
    private String Description;
    private String Url;

    public Paper(String PaperId, String UserId, String Name, String Photo, String Title, String Description, String Url) {
        this.PaperId     = PaperId;
        this.UserId      = UserId;
        this.Name        = Name;
        this.Photo       = Photo;
        this.Title       = Title;
        this.Description = Description;
        this.Url         = Url;
    }

    public String getPaperId() {
        return PaperId;
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