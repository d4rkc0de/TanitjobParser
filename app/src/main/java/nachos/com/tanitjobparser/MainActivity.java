package nachos.com.tanitjobparser;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.parse.ParseObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
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

                Document document = Jsoup.connect(urls[0]).header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0).timeout(600000).get();
                for(Element element:document.select("div.offre-emploi")) {
                    Element innerElement = element.select("div.detail").first();
                    String title = element.select("div.detail a[href]").first().ownText();
                    String comanyName = "";
                    String imgUrl = encodeUrl(element.select("div.image img").first().absUrl("src"));
                    String place = "";
                    Date date = null;
                    Log.d("documenttx",title);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Offre> result) {

        }
    }

    private String encodeUrl(String url) {
        return url.replaceAll(" ","%20");
    }

    public class Offre {
        String title = "";
        String comanyName = "";
        String imgUrl = "";
        String place = "";
        Date date = null;

        public Offre(String title,String comanyName,String imgUrl,String place,Date date) {
            this.title = title;
            this.comanyName = comanyName;
            this.imgUrl = imgUrl;
            this.place = place;
            this.date = date;
        }
    }

}
