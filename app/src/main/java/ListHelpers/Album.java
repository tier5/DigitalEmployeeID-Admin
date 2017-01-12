package ListHelpers;

/**
 * Created by Lincoln on 18/05/16.
 */
public class Album {
    private String employee_id;
    private String time;
    private String imageUrl;
    private String action;
    private String name;

    public Album() {
    }

    public Album(String employee_id, String time, String imageUrl, String action, String name) {
        this.employee_id = employee_id;
        this.time = time;
        this.imageUrl = imageUrl;
        this.action = action;
        this.name = name;
    }

    public String getEmploeeId() {
        return employee_id;
    }

    public void setEmploeeId(String name) {
        this.employee_id = employee_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName(){
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
