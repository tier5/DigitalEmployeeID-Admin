package us.tier5.digitalemployeeidadmin.digitalemployeeidadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import HelperClasses.Api;
import HelperClasses.AsyncResponse;

public class LoginActivity extends AppCompatActivity implements AsyncResponse.Response,View.OnClickListener{

    //server variables
    Api api = new Api("POST");
    HashMap<String,String> data = new HashMap<>();
    String route = "api/v1/company/login";

    //page variables
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;

    //loading variables
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        /*View inflatedLayout= getLayoutInflater().inflate(R.layout.custom_action_bar, null, false);*/

        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        setContentView(R.layout.activity_login);

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);

        api.delegate = this;



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
                JSONObject company = new JSONObject(jsonObject.getString("company"));
                int id = company.getInt("id");
                SharedPreferences.Editor editor = getSharedPreferences("Digital-Employee-Admin", MODE_PRIVATE).edit();
                editor.putInt("ID",id);
                if(editor.commit())
                {
                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in login response "+e.toString());
        }
    }

    @Override
    public void onClick(View view) {

        if(!etEmail.getText().toString().equals("") && !etPassword.getText().toString().equals(""))
        {
            data.put("email",etEmail.getText().toString());
            data.put("password",etPassword.getText().toString());
            loading = ProgressDialog.show(this, "","Please wait", true, false);
            api.register(data,route);
        }
        else
        {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        }

    }
}
