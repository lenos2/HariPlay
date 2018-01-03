package zw.co.hariplay.hariplay.Home;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;


import zw.co.hariplay.hariplay.DirectConnect.ConnectionsActivity;
import zw.co.hariplay.hariplay.DirectConnect.Constants;
import zw.co.hariplay.hariplay.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class P2pConnectionFragment extends Fragment {

    /**
     * This service id lets us find other nearby devices that are interested in the same thing. Our
     * sample does exactly one thing, so we hardcode the ID.
     */
    private static final String SERVICE_ID =
            "zw.co.hariplay.hariplay.SERVICE_ID";
    private static String mName;

    private ArrayList<String> mEndpoints = new ArrayList<>();

    private View v;
    private ListView lv_devices;
    private ArrayAdapter<String> adapter;
    public P2pConnectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_p2p_connection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        v = view;

        lv_devices = (ListView)v.findViewById(R.id.lv_devices);

        adapter = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_list_item_1,mEndpoints);

        lv_devices.setAdapter(adapter);

    }

    public void setDevicesFound(Set<ConnectionsActivity.Endpoint> endpoints){
        mEndpoints.clear();
        for (ConnectionsActivity.Endpoint endpoint : endpoints){
            mEndpoints.add(endpoint.getName());
        }
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }

    }
}
