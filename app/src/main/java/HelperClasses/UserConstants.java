package HelperClasses;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by root on 15/7/16.
 */
public class UserConstants {
    public static final String BASE_URL = "https://www.digitalemployeeid.com/";
    public static final String IMAGE_FOLDER = "public/img/company/employee_id/";

    public static HashMap<String,String> ConstantData = new HashMap<>();

    public static boolean beaconEditPage = false;
    public static String beaconToEdit = "";

    public static String beaconName="";

    public static String currentIdToShow = "";

    public static String comapnyIdRunTime = "";

    public static String currentEmployeeId = "";

    public static boolean userSpecificLog = false;
    public static boolean userBeaconSpecificLog = false;
}

