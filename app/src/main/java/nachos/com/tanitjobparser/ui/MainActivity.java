package nachos.com.tanitjobparser.ui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nachos.com.tanitjobparser.R;
import nachos.com.tanitjobparser.adapter.OfferAdapter;
import nachos.com.tanitjobparser.model.Offer;

public class MainActivity extends AppCompatActivity implements OfferAdapter.ItemClickCallback {

    private RecyclerView recyclerView;
    private OfferAdapter adapter;
    private List<Offer> mResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        (new Parser()).execute("http://tanitjobs.com/search-results-jobs/?action=search&listing_type[equal]=Job&keywords[all_words]=&JobCategory[multi_like][]=378");
    }

    private class Parser extends AsyncTask<String, Void, List<Offer>> {

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

                    Offer offre = new Offer(title,url,comanyName,imgUrl,place,date);
                    results.add(offre);
                }
                return results;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(List<Offer> results) {
            mResults = results;
            if(results != null) {
                adapter = new OfferAdapter(results,MainActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.setItemClickCallback(MainActivity.this);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
                itemTouchHelper.attachToRecyclerView(recyclerView);
            }
        }
    }

    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        deleteItem(viewHolder.getAdapterPosition());
                    }
                };
        return simpleItemTouchCallback;
    }

    private void addItemToList(Offer offer) {
        mResults.add(offer);
        adapter.notifyItemInserted(mResults.indexOf(offer));
    }

    private void moveItem(int oldPos, int newPos) {

        Offer offer = mResults.get(oldPos);
        mResults.remove(oldPos);
        mResults.add(newPos, offer);
        adapter.notifyItemMoved(oldPos, newPos);
    }

    private void deleteItem(final int position) {
        mResults.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onItemClick(int pos) {
        Offer offer = mResults.get(pos);
        EventBus.getDefault().postSticky(offer);
        startActivity(new Intent(MainActivity.this,DetailledOffer.class));
        //Toast.makeText(getApplicationContext(),"item " + offer.getTitle(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompanyImageClick(int pos) {
        Toast.makeText(getApplicationContext(),"position " + String.valueOf(pos),Toast.LENGTH_SHORT).show();
    }


}
