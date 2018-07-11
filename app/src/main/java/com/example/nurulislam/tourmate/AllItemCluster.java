package com.example.nurulislam.tourmate;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class AllItemCluster implements ClusterItem {
    private LatLng latLng;
    private String title;
    private String Snippet;
    private String placeId;


    public AllItemCluster(LatLng latLng, String title, String placeId, String snippet) {
        this.latLng = latLng;
        this.title = title;
        Snippet = snippet;
    }

    public AllItemCluster(LatLng latLng, String title) {
        this.latLng = latLng;
        this.title = title;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return Snippet;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }
    public String getPlaceId() {
        return placeId;
    }
}
