package us.tier5.digitalemployeeidadmin.digitalemployeeidadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import HelperClasses.UserConstants;

public class ShowBadge extends AppCompatActivity {

    ImageView idcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_badge);
        //Toast.makeText(this, UserConstants.currentIdToShow, Toast.LENGTH_SHORT).show();

        idcard=(ImageView) findViewById(R.id.idcard);

        Glide.with(getApplicationContext()).load(UserConstants.currentIdToShow).into(idcard);
    }
}
