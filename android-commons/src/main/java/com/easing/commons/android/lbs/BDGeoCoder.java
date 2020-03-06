package com.easing.commons.android.lbs;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.easing.commons.android.struct.Collections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BDGeoCoder {

    //地理编码，精确地址转坐标
    //如果地址格式不准，搜不到结果
    public static void geoCode(String city, String address, OnResult onResult) {
        GeoCoder coder = GeoCoder.newInstance();
        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR)
                    onResult.onResult(null);
                else {
                    LatLng latLng = result.getLocation();
                    LBSLocation location = LBSLocation.of(latLng.latitude, latLng.longitude);
                    onResult.onResult(location);
                }
                coder.destroy();
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            }
        });
        coder.geocode(new GeoCodeOption().city(city).address(address));
    }

    //逆地理编码，坐标转地址
    public static void reversedGeoCode(double latitude, double longitude, OnResult onResult) {
        GeoCoder coder = GeoCoder.newInstance();
        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR)
                    onResult.onResult(null);
                else {
                    LBSLocation location = LBSLocation.of(latitude, longitude);
                    location.address = result.getAddress();
                    onResult.onResult(location);
                }
                coder.destroy();
            }
        });
        coder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude, longitude)));
    }

    //通过关键字，搜索地点建议
    public static void getSuggestions(String city, String keyword, OnSuggestionResult onResult) {
        SuggestionSearch searcher = SuggestionSearch.newInstance();
        searcher.setOnGetSuggestionResultListener(result -> {
            if (result == null
                    || result.error != SearchResult.ERRORNO.NO_ERROR
                    || result.getAllSuggestions() == null
                    || result.getAllSuggestions().size() == 0)
                onResult.onResult(null);
            else {
                List<SuggestionResult.SuggestionInfo> suggestions = result.getAllSuggestions();
                if (city != null)
                    suggestions = Collections.filter(suggestions, suggestion -> {
                        return suggestion.pt != null && suggestion.city.equals(city);
                    });
                LBSLocation[] locations = new LBSLocation[suggestions.size()];
                for (int i = 0; i < suggestions.size(); i++) {
                    SuggestionResult.SuggestionInfo suggestion = suggestions.get(i);
                    LBSLocation location = LBSLocation.of(suggestion.pt.latitude, suggestion.pt.longitude);
                    location.address = suggestion.city + suggestion.district + suggestion.key;
                    location.city = suggestion.city;
                    locations[i] = location;
                }
                onResult.onResult(locations);
            }
            searcher.destroy();
        });
        SuggestionSearchOption option = new SuggestionSearchOption();
        if (city != null) {
            option.citylimit(true);
            option.city(city);
        }
        option.keyword(keyword);
        searcher.requestSuggestion(option);
    }

    public interface OnResult {
        void onResult(LBSLocation location);
    }

    public interface OnSuggestionResult {
        void onResult(LBSLocation[] locations);
    }
}
