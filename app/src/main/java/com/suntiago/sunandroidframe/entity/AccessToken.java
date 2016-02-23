package com.suntiago.sunandroidframe.entity;

import com.suntiago.sunandroidframe.login.AccessTokenSource;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.Set;

/**
 * Created by yu.zai on 2016/2/22.
 */
public class AccessToken {
    //@JsonProperty(value=(""))
    private  String userId;
    private  String token;
    private  Date expires;
    private  Set<String> permissions;
    private  Set<String> declinedPermissions;
    private AccessTokenSource source;
    private  Date lastRefresh;
    private  String applicationId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getDeclinedPermissions() {
        return declinedPermissions;
    }

    public void setDeclinedPermissions(Set<String> declinedPermissions) {
        this.declinedPermissions = declinedPermissions;
    }

    public AccessTokenSource getSource() {
        return source;
    }

    public void setSource(AccessTokenSource source) {
        this.source = source;
    }

    public Date getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(Date lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
