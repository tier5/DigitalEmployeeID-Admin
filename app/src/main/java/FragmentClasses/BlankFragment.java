package FragmentClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import HelperClasses.Api;
import HelperClasses.AsyncResponse;
import HelperClasses.UserConstants;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.BeaconDetails;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.LogActivity;
import us.tier5.digitalemployeeidadmin.digitalemployeeidadmin.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment implements View.OnClickListener, AsyncResponse.Response{

    //view variables
    TextView tvuuid;
    TextView tvmajor;
    TextView tvminor;
    TextView tvdistance;
    TextView tvBeaconName;
    Button btnAddBeacon;
    Button btnVerify;
    Button btnBeaconLog;
    ImageView ivEditBeacon;

    LinearLayout llBeaconDetails;

    //server variables
    Api api = new Api("POST");
    String routeVerifyApi = "api/v1/beacon/status";
    HashMap<String,String> dataOfVerifyApi = new HashMap<>();

    //beacon variables
    private BeaconManager beaconManager;
    private Region region;

    //loading variables
    ProgressDialog loading;

    //Page varables
    boolean verified = false;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
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
        View fragView = inflater.inflate(R.layout.fragment_blank, container, false);

        api.delegate=this;

        SharedPreferences prefs = getActivity().getSharedPreferences("Digital-Employee-Admin", Context.MODE_PRIVATE);
        final int company_id = prefs.getInt("ID",0);

        tvuuid = (TextView) fragView.findViewById(R.id.tvuuid);
        tvmajor = (TextView) fragView.findViewById(R.id.tvmajor);
        tvminor = (TextView) fragView.findViewById(R.id.tvminor);
        tvdistance = (TextView) fragView.findViewById(R.id.tvdistance);
        tvBeaconName = (TextView) fragView.findViewById(R.id.tvBeaconName);

        llBeaconDetails = (LinearLayout) fragView.findViewById(R.id.llBeaconDetails);

        btnVerify = (Button) fragView.findViewById(R.id.btnVerify);
        btnAddBeacon = (Button) fragView.findViewById(R.id.btnAddBeacon);
        btnBeaconLog = (Button) fragView.findViewById(R.id.btnBeaconLog);

        ivEditBeacon = (ImageView) fragView.findViewById(R.id.ivEditBeacon);

        btnAddBeacon.setOnClickListener(this);
        btnAddBeacon.setVisibility(View.GONE);


        beaconManager = new BeaconManager(getContext());
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    Double beaconDistance = Utils.computeAccuracy(nearestBeacon);
                    //Log.i("kingsukmajumder",beaconDistance.toString());

                    tvdistance.setText("Nearest Beacon's Distance: "+beaconDistance.toString());

                    if(beaconDistance<0.50)
                    {
                        tvuuid.setText(nearestBeacon.getProximityUUID().toString());
                        tvmajor.setText(String.valueOf(nearestBeacon.getMajor()));
                        tvminor.setText(String.valueOf(nearestBeacon.getMinor()));
                        btnAddBeacon.setVisibility(View.VISIBLE);
                        btnVerify.setVisibility(View.VISIBLE);
                        if(verified)
                        {
                            llBeaconDetails.setVisibility(View.VISIBLE);
                            btnAddBeacon.setVisibility(View.GONE);
                        }

                    }
                    else
                    {
                        tvuuid.setText("");
                        tvmajor.setText("");
                        tvminor.setText("");
                        btnAddBeacon.setVisibility(View.GONE);
                        btnVerify.setVisibility(View.GONE);
                        if(verified)
                        {
                            llBeaconDetails.setVisibility(View.GONE);
                        }
                        verified=false;

                    }

                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataOfVerifyApi.put("company_id",String.valueOf(company_id));
                dataOfVerifyApi.put("uuid",tvuuid.getText().toString());
                dataOfVerifyApi.put("major",tvmajor.getText().toString());
                dataOfVerifyApi.put("minor",tvminor.getText().toString());

                loading = ProgressDialog.show(getContext(), "","Please wait", true, false);

                api.register(dataOfVerifyApi,routeVerifyApi);
            }
        });

        btnBeaconLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String beaconId = view.findViewById(R.id.btnBeaconLog).getTag().toString();
                //Toast.makeText(getContext(), ""+beaconId, Toast.LENGTH_SHORT).show();
                UserConstants.beaconToEdit=beaconId;
                Intent intent = new Intent(getContext(), LogActivity.class);
                startActivity(intent);
            }
        });

        ivEditBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String beaconId = view.findViewById(R.id.ivEditBeacon).getTag().toString();
                //Toast.makeText(getContext(), ""+beaconId, Toast.LENGTH_SHORT).show();
                UserConstants.beaconEditPage=true;
                UserConstants.beaconToEdit=beaconId;
                Intent intent = new Intent(getContext(), BeaconDetails.class);
                startActivity(intent);
            }
        });

        return fragView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(getActivity());

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
               /* scanId = beaconManager.startNearableDiscovery();
                scanId = beaconManager.startTelemetryDiscovery();*/
            }
        });
    }

    @Override
    public void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
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
    public void onClick(View view) {
        Toast.makeText(getContext(), "adding this beacon", Toast.LENGTH_SHORT).show();
        UserConstants.ConstantData.put("uuid",tvuuid.getText().toString());
        UserConstants.ConstantData.put("major",tvmajor.getText().toString());
        UserConstants.ConstantData.put("minor",tvminor.getText().toString());

        UserConstants.beaconEditPage=false;
        Intent intent = new Intent(getContext(), BeaconDetails.class);
        startActivity(intent);

    }

    @Override
    public void processFinish(String output) {
        loading.dismiss();
        //Toast.makeText(getContext(), ""+output, Toast.LENGTH_SHORT).show();
        Log.i("kingsukmajumder","response of verification "+output);

        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                JSONObject beacon = new JSONObject(jsonObject.getString("Beacon"));
                llBeaconDetails.setVisibility(View.VISIBLE);
                btnAddBeacon.setVisibility(View.GONE);
                verified=true;
                tvBeaconName.setText(beacon.getString("name"));

                btnBeaconLog.setTag(String.valueOf(beacon.getInt("id")));
                ivEditBeacon.setTag(String.valueOf(beacon.getInt("id")));
            }
            else
            {
                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","Error in beacon varification "+e.toString());
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
