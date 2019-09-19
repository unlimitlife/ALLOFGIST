package com.allofgist.dell.allofgistlite;

public class Site {
    private String msite_name;
    private String msite_url;
    private String msite_urlF;
    private String msite_urlY;
    private String msite_urlB;
    private String msite_urlW;
    private String msite_urlI;
    private int msite_imagesource;

    private String msite_explanation;

    public Site(String name, int imagesource){
        msite_name = name;
        msite_imagesource = imagesource;
    }
    public Site(String name, String url){
        msite_name = name;
        msite_url = url;
    }

    public Site(String name, String url, int imagesource){
        msite_name = name;
        msite_url = url;
        msite_imagesource = imagesource;
    }

    public Site(String name, int imagesource, String explanation){
        this.msite_name = name;
        this.msite_imagesource = imagesource;
        this.msite_explanation =  explanation;
    }

    public Site(String name, String urlf, String urly, int imagesource){
        msite_name = name;
        msite_urlF = urlf;
        msite_urlY = urly;
        msite_imagesource = imagesource;
    }

    public Site(String name, String urlw, String urlb, String urlf, String urli,int imagesource){
        msite_name = name;
        msite_urlW = urlw;
        msite_urlB = urlb;
        msite_urlF = urlf;
        msite_urlI = urli;
        msite_imagesource = imagesource;
    }



    public Site(String name, String url, int imagesource, String explanation){
        msite_name = name;
        msite_url = url;
        msite_imagesource = imagesource;
        msite_explanation = explanation;
    }

    public Site(String name, String urlf, String urly, int imagesource, String explanation){
        msite_name = name;
        msite_urlF = urlf;
        msite_urlY = urly;
        msite_imagesource = imagesource;
        msite_explanation = explanation;
    }

    public Site(String name, String urlw, String urlb, String urlf, String urli,int imagesource, String explanation){
        msite_name = name;
        msite_urlW = urlw;
        msite_urlB = urlb;
        msite_urlF = urlf;
        msite_urlI = urli;
        msite_imagesource = imagesource;
        msite_explanation = explanation;
    }









    public String getMsite_name() {
        return this.msite_name;
    }
    public String getMsite_url(){
        return this.msite_url;
    }
    public String getMsite_urlW(){
        return this.msite_urlW;
    }
    public String getMsite_urlB(){ return this.msite_urlB; }
    public String getMsite_urlF(){
        return this.msite_urlF;
    }
    public String getMsite_urlY(){
        return this.msite_urlY;
    }
    public String getMsite_urlI(){
        return this.msite_urlI;
    }
    public int getMsite_imagesource(){
        return this.msite_imagesource;
    }

    public String getMsite_explanation() {
        return msite_explanation;
    }
}
