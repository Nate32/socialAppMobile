package com.example.ntusocials;

public class MessagePojo {
    private Boolean current_messenger;
    private String message_display;

    public MessagePojo( String message_display, Boolean current_messenger){

        this.message_display = message_display;
        this.current_messenger = current_messenger;
    }

    public void setMessages (String message_display){
        this.message_display = message_display;
    }
    public String getMessages(){
        return message_display;
    }

    public void setCurrent_messenger (Boolean current_messenger){
        this.current_messenger = current_messenger;
    }
    public Boolean getCurrent_messenger(){
        return current_messenger;
    }

}
