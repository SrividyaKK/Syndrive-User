package universe.sk.syndriveapp;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, View.OnClickListener {

    GoogleMap map;
    GoogleApiClient googleApiClient;//for current location
    Boolean firsttime = false;
    public ContactUsFragment() {
        // Required empty public constructor
    }

    int PROXIMITY_RADIUS = 10000;//for nearby hospitals
    double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);

        //nearby hospitals

        //Button btnHospitals = (Button) v.findViewById(R.id.btnHospitals);
        FloatingActionButton btnHospitals = v.findViewById(R.id.btnHospitals);
        btnHospitals.setOnClickListener(this);
        //nearby hospitals

        FloatingActionButton fabDonateBlood = v.findViewById(R.id.fabDonateBlood);
        fabDonateBlood.setOnClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
        //zoom
        Location loc = map.getMyLocation();
        if (loc != null){
            LatLng point = new LatLng(loc.getLatitude() , loc.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(point,18));
        }
        buildClient();
//
//        LatLng pp = new LatLng(10.028576,76.328727);
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(pp).title("Model Engineering College");
//        map.addMarker(markerOptions);
//        map.moveCamera(CameraUpdateFactory.newLatLng(pp));
    }

    private void buildClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //zoom
       if (!firsttime)
       {
           LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
           latitude=location.getLatitude();
           longitude=location.getLongitude();
           map.animateCamera(CameraUpdateFactory.newLatLngZoom(point,18));
           firsttime = true;
       }

    }

    //nearby hospitals
    public void onClick(View v)
    {
        if (v.getId() == R.id.btnHospitals)
        {
            map.clear();
            String hospital= "hospital";
            String url = getUrl(latitude, longitude, hospital);
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = map;
            dataTransfer[1]= url;

            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(getActivity(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.fabDonateBlood)
        {
            startActivity(new Intent(getActivity(), BloodDonationActivity.class));
        }

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyAwXCzIE9533_qHW8PdfWRamdVTqi6vrJg");

        return  googlePlaceUrl.toString();
    }

}
