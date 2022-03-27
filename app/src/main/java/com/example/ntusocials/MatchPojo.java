package com.example.ntusocials;

public class MatchPojo {
    private String userId;
    private String fullName;
    private String imageUrl;

    public MatchPojo(String userId, String fullName, String imageUrl){

        this.userId = userId;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getUserId(){
        return userId;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public String getFullName(){
        return fullName;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
    public String getImageUrl(){
        return imageUrl;
    }
}
