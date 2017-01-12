package us.tier5.digitalemployeeidadmin.digitalemployeeidadmin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import HelperClasses.Api;
import HelperClasses.Api2;
import HelperClasses.Api3;
import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse2;
import HelperClasses.AsyncResponse3;
import HelperClasses.UserConstants;

public class BeaconDetails extends AppCompatActivity implements View.OnClickListener, AsyncResponse.Response, AsyncResponse2.Response2, AsyncResponse3.Response3{

    //page variables
    RadioGroup rg;
    LinearLayout parentLL;
    EditText etBeaconName;
    EditText etTimeOut;
    EditText etBeaconTask;
    EditText etEndPointYes;
    EditText etEndPointNo;
    Button btnRegister;
    EditText etRange;
    Button btnUpdate;

    RadioButton rbLog;
    RadioButton rbTask;

    //dialog popup view
    Dialog myDialog;


    //user variables
    String type = "";
    String name="";
    String range="";
    String timeout="";
    String text= "";
    String endpoint_yes = "";
    String endpoint_no = "";


    //server variables
    Api api = new Api("POST");
    String route = "api/v1/beacons/register";
    HashMap<String,String> dataOfBeacOnDetails = new HashMap<>();

    //beacon details api call
    Api2 api2 = new Api2("POST");
    String routeBeaconsDetails = "api/v1/beacons/getById";

    //beacon update api call
    Api3 api3 = new Api3("POST");
    String routeBeaconsUpdate = "api/v1/beacons/update";
    HashMap<String,String> dataOfBeaconUpdate = new HashMap<>();

    //loading variables
    ProgressDialog loading;
    ProgressDialog loadingUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_details);

        api.delegate=this;
        api2.delegate=this;
        api3.delegate=this;

        /*myDialog = new Dialog(getApplicationContext());
        myDialog.getWindow();
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);*/

        rg = (RadioGroup) findViewById(R.id.rg);
        parentLL = (LinearLayout) findViewById(R.id.parentLL);
        etBeaconName = (EditText) findViewById(R.id.etBeaconName);
        etTimeOut = (EditText) findViewById(R.id.etTimeOut);
        etBeaconTask = (EditText) findViewById(R.id.etBeaconTask);
        etEndPointYes = (EditText) findViewById(R.id.etEndPointYes);
        etEndPointNo = (EditText) findViewById(R.id.etEndPointNo);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etRange = (EditText) findViewById(R.id.etRange);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        rbLog = (RadioButton) findViewById(R.id.rbLog);
        rbTask = (RadioButton) findViewById(R.id.rbTask);


        btnRegister.setOnClickListener(this);


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rbLog)
                {
                    //Toast.makeText(BeaconDetails.this, "Log", Toast.LENGTH_SHORT).show();
                    type = "log";
                    parentLL.setVisibility(View.GONE);

                }
                else if(i == R.id.rbTask)
                {
                    //Toast.makeText(BeaconDetails.this, "Task", Toast.LENGTH_SHORT).show();
                    type = "text";
                    popupActivity();
                }
            }
        });
        SharedPreferences prefs = getSharedPreferences("Digital-Employee-Admin", Context.MODE_PRIVATE);
        int Id = prefs.getInt("ID",0);
        UserConstants.ConstantData.put("company_id",String.valueOf(Id));
        Log.i("kingsukmajumder", UserConstants.ConstantData.toString());
        dataOfBeaconUpdate.put("company_id",String.valueOf(Id));
        dataOfBeaconUpdate.put("beacon_id",UserConstants.beaconToEdit);

        if(UserConstants.beaconEditPage)
        {
            btnUpdate.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.GONE);
            //Toast.makeText(this, UserConstants.beaconToEdit, Toast.LENGTH_SHORT).show();
            dataOfBeacOnDetails.put("company_id",String.valueOf(Id));
            dataOfBeacOnDetails.put("beacon_id",UserConstants.beaconToEdit);
            loading = ProgressDialog.show(this, "","Please wait", true, false);
            api2.register(dataOfBeacOnDetails,routeBeaconsDetails);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etBeaconName.getText().toString().equals("") && !etTimeOut.getText().toString().equals("") && !etRange.getText().toString().equals("") &&!type.equals(""))
                {
                    if(type.equals("text"))
                    {
                        if(!etBeaconTask.getText().toString().equals(""))
                        {
                            dataOfBeaconUpdate.put("timeout",etTimeOut.getText().toString());
                            dataOfBeaconUpdate.put("name",etBeaconName.getText().toString());
                            dataOfBeaconUpdate.put("range",etRange.getText().toString());
                            dataOfBeaconUpdate.put("type","task");
                            dataOfBeaconUpdate.put("text",etBeaconTask.getText().toString());
                            dataOfBeaconUpdate.put("endpoint_yes",etEndPointYes.getText().toString());
                            dataOfBeaconUpdate.put("endpoint_no",etEndPointNo.getText().toString());

                            loadingUpdate = ProgressDialog.show(BeaconDetails.this, "","Please wait", true, false);
                            Log.i("kingsukmajumder",dataOfBeaconUpdate.toString());
                            api3.register(dataOfBeaconUpdate,routeBeaconsUpdate);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Please Enter A Task!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        dataOfBeaconUpdate.put("timeout",etTimeOut.getText().toString());
                        dataOfBeaconUpdate.put("name",etBeaconName.getText().toString());
                        dataOfBeaconUpdate.put("range",etRange.getText().toString());
                        dataOfBeaconUpdate.put("type","log");
                        loadingUpdate = ProgressDialog.show(BeaconDetails.this, "","Please wait", true, false);
                        Log.i("kingsukmajumder",dataOfBeaconUpdate.toString());
                        api3.register(dataOfBeaconUpdate,routeBeaconsUpdate);

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext() , "Please enter all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void popupActivity()
    {
        parentLL.setVisibility(View.VISIBLE);

        /*View inflatedLayout= getLayoutInflater().inflate(R.layout.activity_task, null, false);


        parentLL.addView(inflatedLayout);*/

        /*myDialog.setContentView(inflatedLayout);

        myDialog.setCancelable(false);

        myDialog.show();*/
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
        if(!etBeaconName.getText().toString().equals("") && !etTimeOut.getText().toString().equals("") && !etRange.getText().toString().equals("") &&!type.equals(""))
        {
            if(type.equals("text"))
            {
                if(!etBeaconTask.getText().toString().equals(""))
                {
                    UserConstants.ConstantData.put("timeout",etTimeOut.getText().toString());
                    UserConstants.ConstantData.put("name",etBeaconName.getText().toString());
                    UserConstants.ConstantData.put("range",etRange.getText().toString());
                    UserConstants.ConstantData.put("type","task");
                    UserConstants.ConstantData.put("text",etBeaconTask.getText().toString());
                    UserConstants.ConstantData.put("endpoint_yes",etEndPointYes.getText().toString());
                    UserConstants.ConstantData.put("endpoint_no",etEndPointNo.getText().toString());

                    loading = ProgressDialog.show(this, "","Please wait", true, false);
                    Log.i("kingsukmajumder",UserConstants.ConstantData.toString());
                    api.register(UserConstants.ConstantData,route);
                }
                else
                {
                    Toast.makeText(this, "Please Enter A Task!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                UserConstants.ConstantData.put("timeout",etTimeOut.getText().toString());
                UserConstants.ConstantData.put("name",etBeaconName.getText().toString());
                UserConstants.ConstantData.put("range",etRange.getText().toString());
                UserConstants.ConstantData.put("type","log");
                loading = ProgressDialog.show(this, "","Please wait", true, false);
                Log.i("kingsukmajumder",UserConstants.ConstantData.toString());
                api.register(UserConstants.ConstantData,route);

            }
        }
        else
        {
            Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void processFinish(String output) {
        loading.dismiss();
        //Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        Log.i("kingsukmajumder",output);

        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder",e.toString());
        }
    }

    @Override
    public void processFinish2(String output) {
        loading.dismiss();
        //Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        Log.i("kingsukmajumder",output);

        try
        {
            JSONObject jsonObject = new JSONObject(output);

            if(jsonObject.getBoolean("status"))
            {
                JSONObject beacon = new JSONObject(jsonObject.getString("beacon"));
                String name = beacon.getString("name");
                //String uuid = beacon.getString("uuid");
                //String major = beacon.getString("major");
                //String minor = beacon.getString("minor");
                Double range = beacon.getDouble("range");
                String timeOut = beacon.getString("timeout");
                int beaconType = beacon.getInt("type");

                if(beaconType==1)
                {
                    rbTask.setChecked(true);
                    type = "text";
                    popupActivity();

                    String text = beacon.getString("text");
                    etBeaconTask.setText(text);
                    String endpointyes = beacon.getString("endpoint_yes");
                    String endpointno = beacon.getString("endpoint_no");

                    etEndPointYes.setText(endpointyes);
                    etEndPointNo.setText(endpointno);
                }
                else if(beaconType==0)
                {
                    rbLog.setChecked(true);
                    type = "log";
                    parentLL.setVisibility(View.GONE);
                }

                etBeaconName.setText(name);
                etRange.setText(range.toString());
                etTimeOut.setText(timeOut);

            }
            else
            {

            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in bracon details response "+e.toString());
        }
    }

    @Override
    public void processFinish3(String output) {
        loadingUpdate.dismiss();
        //Toast.makeText(this, ""+output, Toast.LENGTH_SHORT).show();
        try
        {
            JSONObject jsonObject = new JSONObject(output);

            if(jsonObject.getBoolean("status"))
            {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","Error in beacon update "+e.toString());
        }
    }
}
