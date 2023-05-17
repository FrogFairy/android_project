package com.example.project;

import java.util.List;

public class GeocoderResponse {
    public class Geocoder {
        public Response response;
    }

    public class Response {
        public MyGeoObjectCollection GeoObjectCollection;
    }

    public class MyGeoObjectCollection {
        public MetaDataProperty metaDataProperty;
        public List<FeatureMember> featureMember;
    }

    public class MetaDataProperty {
        public MyGeocoderResponseMetaData GeocoderResponseMetaData;
    }

    public class MyGeocoderResponseMetaData {
        public String request;
        public int found;
        public int results;
    }

    public class FeatureMember {
        public MyGeoObject GeoObject;
    }

    public class MyGeoObject {
        public MyMetaDataProperty metaDataProperty;
        public String description;
        public String name;
        public BoundedBy boundedBy;
        public MyPoint Point;
    }

    public class MyPoint {
        public String pos;
    }

    public class BoundedBy {
        public MyEnvelope Envelope;
    }

    public class MyEnvelope {
        public String lowerCorner;
        public String upperCorner;
    }

    public class MyMetaDataProperty {
        public MyGeocoderMetaData GeocoderMetaData;
    }

    public class MyGeocoderMetaData {
        public String kind;
        public String text;
        public String precision;
        public MyAddress Address;
        public MyAddressDetails AddressDetails;
    }

    public class MyAddress {
        public String country_code;
        public int postal_code;
        public String formatted;
        public List<MyComponents> Components;
    }

    public class MyComponents {
        public String kind;
        public String name;
    }

    public class MyAddressDetails {
        public MyCountry Country;
    }

    public class MyCountry {
        public String AddressLine;
        public String CountryNameCode;
        public String CountryName;
        public MyAdministrativeArea AdministrativeArea;
    }

    public class MyAdministrativeArea {
        public String AdministrativeAreaName;
        public MyLocality Locality;
    }

    public class MyLocality {
        public String LocalityName;
        public MyThoroughfare Thoroughfare;
    }

    public class MyThoroughfare {
        public String ThoroughfareName;
        public MyPremise Premise;
    }

    public class MyPremise {
        public String PremiseNumber;
        public MyPostalCode PostalCode;
    }

    public class MyPostalCode {
        public int PostalCodeNumber;
    }
}
