package FragmentClasses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import HelperClasses.Api;
import HelperClasses.AsyncResponse;
import HelperClasses.UserConstants;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.BeaconDetails;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.LogActivity;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BeaconListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BeaconListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BeaconListFragment extends Fragment implements AsyncResponse.Response{

    //server variables
    Api api = new Api("POST");
    String route = "api/v1/beacons/all";
    HashMap<String,String> data = new HashMap<>();

    //page variables
    Bundle mySavedInstance;
    int company_id=0;

    //view variables
    LinearLayout parentLLList;

    //loading variables
    ProgressDialog loading;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BeaconListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BeaconListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BeaconListFragment newInstance(String param1, String param2) {
        BeaconListFragment fragment = new BeaconListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_beacon_list, container, false);


        mySavedInstance=savedInstanceState;
        api.delegate=this;

        loading = new ProgressDialog(getContext());
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setProgressStyle(R.style.MyAlertDialogStyle);
        loading.setMessage("Please wait...");

        SharedPreferences prefs = getActivity().getSharedPreferences("Digital-Employee-Admin", Context.MODE_PRIVATE);
        company_id = prefs.getInt("ID",0);
        if(company_id!=0)
        {
            data.put("company_id",String.valueOf(company_id));

            loading.show();

            api.register(data,route);
        }
        else
        {
            Toast.makeText(getContext(), "Sorry cannot get Your company ID please Login again!", Toast.LENGTH_SHORT).show();
        }

        parentLLList = (LinearLayout) fragView.findViewById(R.id.parentLLList);

        return fragView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void processFinish(String output) {
        loading.dismiss();
        Log.i("kingsukmajumder","All beacon list response: "+output);
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                JSONArray jsonArray = new JSONArray(jsonObject.getString("beacons"));

                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject currentObj = jsonArray.getJSONObject(i);
                    String name = currentObj.getString("name");

                    int type = currentObj.getInt("type");

                    View inflatedLayout= getLayoutInflater(mySavedInstance).inflate(R.layout.listitem, null, false);
                    TextView tvBeaconName = (TextView) inflatedLayout.findViewById(R.id.tvBeaconName);
                    TextView tvBeaconType = (TextView) inflatedLayout.findViewById(R.id.tvBeaconType);
                    ImageView ivEdit = (ImageView) inflatedLayout.findViewById(R.id.ivEdit);
                    ImageView ivViewLog = (ImageView) inflatedLayout.findViewById(R.id.ivViewLog);
                    ivViewLog.setTag(String.valueOf(currentObj.getInt("id")));
                    ivEdit.setTag(String.valueOf(currentObj.getInt("id")));
                    ivEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Toast.makeText(getContext(), view.findViewById(R.id.ivEdit).getTag().toString(), Toast.LENGTH_SHORT).show();
                            UserConstants.beaconEditPage=true;
                            UserConstants.beaconToEdit=view.findViewById(R.id.ivEdit).getTag().toString();
                            Intent intent = new Intent(getContext(), BeaconDetails.class);
                            startActivity(intent);
                        }
                    });

                    ivViewLog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UserConstants.beaconToEdit=view.findViewById(R.id.ivViewLog).getTag().toString();
                            Intent intent = new Intent(getContext(), LogActivity.class);
                            startActivity(intent);
                        }
                    });

                    tvBeaconName.setText(name);
                    if(type==1)
                    {
                        tvBeaconType.setText("Task");
                    }
                    else
                    {
                        tvBeaconType.setText("Log");
                    }




                    parentLLList.addView(inflatedLayout);
                }
            }
            else
            {
                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in beacon list response: "+e.toString());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
