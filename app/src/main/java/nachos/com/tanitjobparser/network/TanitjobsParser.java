package nachos.com.tanitjobparser.network;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nachos.com.tanitjobparser.model.Offer;

/**
 * Created by lichiheb on 31/12/16.
 */

public class TanitjobsParser extends AsyncTask<String, Void, List<Offer>> {

    private AsyncTaskCallback callback;
    public interface AsyncTaskCallback {
        void onDone(List<Offer> results);
    }

    public void setAsyncTaskCallback(final AsyncTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected List<Offer> doInBackground(String... urls) {
        try {
            List<Offer> results = new ArrayList<>();
            Document document = Jsoup.connect(urls[0]).header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0).timeout(600000).get();
            for(Element element:document.select("div.offre-emploi")) {
                Element detail = element.select("div.detail").first();
                String title = detail.select("div.detail a[href]").first().ownText();
                String url = detail.select("a.title_offre").first().attr("abs:href");
                String companyName = detail.select("#companytitle").first().text();
                String imgUrl = element.select("div.image img").first().absUrl("src");
                Element descriptionJob = detail.select("div.descriptionjob").first();
                String datePlace = "";
                if(descriptionJob.select("p.infoplusoffer").first() != null)
                    datePlace = descriptionJob.select("p.infoplusoffer").first().text();
                String place = "";
                String date = "";
                String[] splitedDatePlace = datePlace.split("\\|");
                if(splitedDatePlace.length == 2) {
                    place = splitedDatePlace[0];
                    date = splitedDatePlace[1];
                } else if(splitedDatePlace.length == 1 && !splitedDatePlace[0].equals("")) {
                    if(Character.isLetter(splitedDatePlace[0].charAt(0)))
                        place = splitedDatePlace[0];
                    else
                        date = splitedDatePlace[0];
                }
    
                Offer offer = new Offer(title,url,companyName,imgUrl,place,date);
                results.add(offer);
            }
            return results;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Offer> results) {
        callback.onDone(results);
    }
}