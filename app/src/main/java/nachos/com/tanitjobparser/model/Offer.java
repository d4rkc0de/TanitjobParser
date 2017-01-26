package nachos.com.tanitjobparser.model;

import java.util.Date;

import static nachos.com.tanitjobparser.utils.Utils.encodeUrl;

/**
 * Created by lichiheb on 28/12/16.
 */

public class Offer {
    String url = "";
    String title = "";
    String comanyName = "";
    String imgUrl = "";
    String place = "";
    String date = null;

    public Offer(String title,String url,String comanyName,String imgUrl,String place,String date) {
        this.title = title;
        this.comanyName = comanyName;
        this.imgUrl = encodeUrl(imgUrl);
        this.place = place;
        this.date = date;
        this.url = encodeUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComanyName() {
        return comanyName;
    }

    public void setComanyName(String comanyName) {
        this.comanyName = comanyName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
