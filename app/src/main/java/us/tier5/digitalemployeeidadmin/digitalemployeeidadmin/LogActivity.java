package us.tier5.digitalemployeeidadmin.digitalemployeeidadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import HelperClasses.Api;
import HelperClasses.AsyncResponse;
import HelperClasses.UserConstants;
import ListHelpers.Album;
import ListHelpers.AlbumsAdapter;


public class LogActivity extends AppCompatActivity implements AsyncResponse.Response{

    //recycle view variables
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<Album> albumList;


    //server variables
    Api api = new Api("POST");
    HashMap<String,String> data = new HashMap<>();
    String routeGetAllLogs = "api/v1/beacon/view/logs";
    String routeUserSpecificLogs = "api/v1/user/logs";
    String routeUserBeaconSpecificLogs = "api/v1/user/beacon/logs";

    //view variables
    LinearLayout parentLLList;

    //loading variable
    ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        api.delegate=this;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        adapter = new AlbumsAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


       // prepareAlbums();

        parentLLList = (LinearLayout) findViewById(R.id.parentLLList);

        SharedPreferences prefs = getSharedPreferences("Digital-Employee-Admin", Context.MODE_PRIVATE);
        int Id = prefs.getInt("ID",0);

        UserConstants.comapnyIdRunTime = String.valueOf(Id);

        data.put("company_id",String.valueOf(Id));
        data.put("beacon_id",UserConstants.beaconToEdit);


        loading = ProgressDialog.show(this, "","Please wait", true, false);
        if(UserConstants.userSpecificLog)
        {
            data.put("card_id",UserConstants.currentEmployeeId);
            api.register(data,routeUserSpecificLogs);
            //UserConstants.userSpecificLog=false;
        }
        else if(UserConstants.userBeaconSpecificLog)
        {
            data.put("card_id",UserConstants.currentEmployeeId);
            api.register(data,routeUserBeaconSpecificLogs);
            //UserConstants.userBeaconSpecificLog=false;
        }
        else
        {
            api.register(data,routeGetAllLogs);
        }





    }



    @Override
    public void processFinish(String output) {
        //Toast.makeText(this, ""+output, Toast.LENGTH_SHORT).show();
        Log.i("kingsukmajumder",output);
        loading.dismiss();
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                JSONArray jsonArray = new JSONArray(jsonObject.getString("logs"));

                Album a;
                for(int i=0; i<jsonArray.length();i++)
                {
                    //Log.i("kingsukmajumder",jsonArray.getJSONObject(i).toString());
                    JSONObject currentObject = jsonArray.getJSONObject(i);

                    if(i==0)
                    {
                        setTitle(currentObject.getString("beacon_name"));
                        if(UserConstants.userSpecificLog)
                        {
                            setTitle(currentObject.getString("employee_id"));
                        }
                    }

                    String actionString = "";
                    String name = "";

                    if(currentObject.has("interaction"))
                    {
                        actionString = currentObject.getString("interaction");

                        /*if(currentObject.getString("interaction").equals("1"))
                        {
                            actionString = "Yes";
                        }
                        else if(currentObject.getString("interaction").equals("0"))
                        {
                            actionString = "No";
                        }
                        else
                        {
                            actionString = currentObject.getString("interaction");
                        }*/
                    }
                    else
                    {
                        actionString = "";
                    }

                    if(currentObject.has("user_name"))
                    {

                        name = currentObject.getString("user_name");
                    }
                    else
                    {
                        name = "";
                    }

                    if(UserConstants.userSpecificLog)
                    {
                        a = new Album(currentObject.getString("employee_id"), currentObject.getString("time"), currentObject.getString("picture"), actionString, name, currentObject.getString("idcard"),currentObject.getString("beacon_name"));

                    }
                    else
                    {
                        a = new Album(currentObject.getString("employee_id"), currentObject.getString("time"), currentObject.getString("picture"), actionString, name, currentObject.getString("idcard"),"");

                    }

                    albumList.add(a);
                }

                adapter.notifyDataSetChanged();

            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in log of beacon "+e.toString());
        }
        finally {
            if(UserConstants.userSpecificLog)
            {
                UserConstants.userSpecificLog=false;
            }
            if(UserConstants.userBeaconSpecificLog)
            {
                UserConstants.userBeaconSpecificLog=false;
            }
        }
    }




    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



}
