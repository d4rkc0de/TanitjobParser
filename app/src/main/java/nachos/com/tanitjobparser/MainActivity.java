package nachos.com.tanitjobparser;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.parse.ParseObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (new Parser()).execute("http://tanitjobs.com/search-results-jobs/?action=search&listing_type[equal]=Job&keywords[all_words]=&JobCategory[multi_like][]=378");
    }

    private class Parser extends AsyncTask<String, Void, ArrayList<Offre>> {

        @Override
        protected ArrayList<Offre> doInBackground(String... urls) {
            try {

                ArrayList<Offre> results = new ArrayList<>();
                Document document = Jsoup.connect(urls[0]).header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0).timeout(600000).get();
                for(Element element:document.select("div.offre-emploi")) {
                    Element detail = element.select("div.detail").first();
                    String title = detail.select("div.detail a[href]").first().ownText();
                    String url = detail.select("a.title_offre").first().attr("abs:href");
                    String comanyName = detail.select("#companytitle").first().text();
                    String imgUrl = element.select("div.image img").first().absUrl("src");
                    Element descriptionjob = detail.select("div.descriptionjob").first();
                    String datePlace = "";
                    if(descriptionjob.select("p.infoplusoffer").first() != null)
                         datePlace = descriptionjob.select("p.infoplusoffer").first().text();
                    String place = "";
                    String dateString = "";
                    String[] splitedDatePlace = datePlace.split("\\|");
                    if(splitedDatePlace.length == 2) {
                        place = splitedDatePlace[0];
                        dateString = splitedDatePlace[1];
                    } else if(splitedDatePlace.length == 1 && !splitedDatePlace[0].equals("")) {
                        if(Character.isLetter(splitedDatePlace[0].charAt(0)))
                            place = splitedDatePlace[0];
                        else
                            dateString = splitedDatePlace[0];
                    }
                    Date date = null;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                    try {
                        date = simpleDateFormat.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Offre offre = new Offre(title,url,comanyName,imgUrl,place,date);
                    results.add(offre);
                }
                return results;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Offre> results) {
            Log.d("offreSize", String.valueOf(results.size()));
            for(Offre offre:results) {
                Log.d("offreurl",offre.url);
                Log.d("offretitle",offre.title);
                Log.d("offrecomanyName",offre.comanyName);
                Log.d("offreimgUrl",offre.imgUrl);
                Log.d("offreplace",offre.place);
                if(offre.date != null)
                    Log.d("offredate",offre.date.toString());
                Log.d("offre----","--------------");
            }
        }
    }

    private String encodeUrl(String url) {
        return url.replaceAll(" ","%20");
    }

    public class Offre {
        String url = "";
        String title = "";
        String comanyName = "";
        String imgUrl = "";
        String place = "";
        Date date = null;

        public Offre(String title,String url,String comanyName,String imgUrl,String place,Date date) {
            this.title = title;
            this.comanyName = comanyName;
            this.imgUrl = encodeUrl(imgUrl);
            this.place = place;
            this.date = date;
            this.url = encodeUrl(url);
        }
    }

}
