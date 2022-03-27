package com.example.ntusocials;

public class populateCards {
    private String usrId;
    private String name;
    private String imageProfile;

    public populateCards (String usrId, String name, String imageProfile){
        this.usrId = usrId;
        this.name = name;
        this.imageProfile = imageProfile;
    }

    public void setUsrId (String usrId){
        this.usrId = usrId;
    }
    public String getUsrId(){
        return usrId;
    }

    public void setName (String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setImageProfile (String imageProfile){
        this.imageProfile= imageProfile;
    }
    public String getImageProfile(){
        return imageProfile;
    }
}
