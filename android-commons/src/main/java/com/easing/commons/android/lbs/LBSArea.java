package com.easing.commons.android.lbs;

import lombok.Data;

import java.util.ArrayList;

@Data
public class LBSArea {

    public ArrayList<Province> provinces;

    public ArrayList<Province> getAllProvinces() {
        return provinces;
    }

    public ArrayList<ArrayList<City>> getAllCities() {
        ArrayList<ArrayList<City>> l1 = new ArrayList();
        for (Province province : provinces)
            l1.add(province.cities);
        return l1;
    }

    public ArrayList<ArrayList<ArrayList<County>>> getAllCounties() {
        ArrayList<ArrayList<ArrayList<County>>> l1 = new ArrayList();
        for (Province province : provinces) {
            ArrayList<ArrayList<County>> l2 = new ArrayList();
            for (City city : province.cities)
                l2.add(city.counties);
            l1.add(l2);
        }
        return l1;
    }

    @Data
    public static class Province {
        public int provinceId;
        public String provinceName;
        public ArrayList<City> cities;

        @Override
        public String toString() {
            return provinceName;
        }
    }

    @Data
    public static class City {
        public int cityId;
        public String cityName;
        public ArrayList<County> counties;

        @Override
        public String toString() {
            return cityName;
        }
    }

    @Data
    public static class County {
        public int countyId;
        public String countyName;

        @Override
        public String toString() {
            return countyName;
        }
    }


}
