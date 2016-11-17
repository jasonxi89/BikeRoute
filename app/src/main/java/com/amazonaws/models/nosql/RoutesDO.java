package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "bikerroute-mobilehub-1014875679-routes")

public class RoutesDO {
    private String _userId;
    private String _routeId;
    private String _category;
    private String _endLatitude;
    private String _endLongitude;
    private Double _numberTraveled;
    private String _routeName;
    private String _startLatitude;
    private String _startLongitude;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBIndexHashKey(attributeName = "userId", globalSecondaryIndexName = "Popular")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "routeId")
    @DynamoDBAttribute(attributeName = "routeId")
    public String getRouteId() {
        return _routeId;
    }

    public void setRouteId(final String _routeId) {
        this._routeId = _routeId;
    }
    @DynamoDBIndexHashKey(attributeName = "category", globalSecondaryIndexName = "Categories")
    public String getCategory() {
        return _category;
    }

    public void setCategory(final String _category) {
        this._category = _category;
    }
    @DynamoDBAttribute(attributeName = "end_latitude")
    public String getEndLatitude() {
        return _endLatitude;
    }

    public void setEndLatitude(final String _endLatitude) {
        this._endLatitude = _endLatitude;
    }
    @DynamoDBAttribute(attributeName = "end_longitude")
    public String getEndLongitude() {
        return _endLongitude;
    }

    public void setEndLongitude(final String _endLongitude) {
        this._endLongitude = _endLongitude;
    }
    @DynamoDBIndexRangeKey(attributeName = "number_traveled", globalSecondaryIndexName = "Popular")
    public Double getNumberTraveled() {
        return _numberTraveled;
    }

    public void setNumberTraveled(final Double _numberTraveled) {
        this._numberTraveled = _numberTraveled;
    }
    @DynamoDBIndexHashKey(attributeName = "routeName", globalSecondaryIndexName = "Names")
    public String getRouteName() {
        return _routeName;
    }

    public void setRouteName(final String _routeName) {
        this._routeName = _routeName;
    }
    @DynamoDBAttribute(attributeName = "start_latitude")
    public String getStartLatitude() {
        return _startLatitude;
    }

    public void setStartLatitude(final String _startLatitude) {
        this._startLatitude = _startLatitude;
    }
    @DynamoDBAttribute(attributeName = "start_longitude")
    public String getStartLongitude() {
        return _startLongitude;
    }

    public void setStartLongitude(final String _startLongitude) {
        this._startLongitude = _startLongitude;
    }

}
