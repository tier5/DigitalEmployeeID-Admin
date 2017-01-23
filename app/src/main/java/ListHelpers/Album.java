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
    private String idcard;
    private String beaconName;

    public Album() {
    }

    public Album(String employee_id, String time, String imageUrl, String action, String name, String idcard, String beaconName) {
        this.employee_id = employee_id;
        this.time = time;
        this.imageUrl = imageUrl;
        this.action = action;
        this.name = name;
        this.idcard=idcard;
        this.beaconName=beaconName;
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

    public String getIdcard(){
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard=idcard;
    }

    public String getBeaconName()
    {
        return beaconName;
    }

    public void setBeaconName(String beaconName)
    {
        this.beaconName=beaconName;
    }
}
