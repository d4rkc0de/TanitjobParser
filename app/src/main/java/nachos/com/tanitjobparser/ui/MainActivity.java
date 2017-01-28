package nachos.com.tanitjobparser.ui;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nachos.com.tanitjobparser.R;
import nachos.com.tanitjobparser.adapter.OfferAdapter;
import nachos.com.tanitjobparser.model.Offer;
import nachos.com.tanitjobparser.network.TanitjobsParser;
import nachos.com.tanitjobparser.utils.RecyclerViewItemTouchHelper;
import static nachos.com.tanitjobparser.utils.Utils.isOnline;

public class MainActivity extends AppCompatActivity implements OfferAdapter.ItemClickCallback,TanitjobsParser.AsyncTaskCallback {

    private RecyclerView recyclerView;
    private OfferAdapter adapter;
    private List<Offer> mResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int counter = 0;
    private boolean isFirst = true,online = false;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("offer");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database.removeValue();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mResults = new ArrayList<>();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        setSwipeRefreshListener();
        online = isOnline(this);
        loadNextPage();
    }

    public void setSwipeRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                recyclerView.post( new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    public void onDone(List<Offer> results) {
        if(results != null) {
            if(isFirst)
                setUpList(results);
            else
                addToList(results);
        } else
            Toast.makeText(getApplicationContext(),"Didn't find any offers!",Toast.LENGTH_SHORT).show();
    }

    private void addToList(List<Offer> results) {
        //mResults.addAll(results);
        mResults.addAll(listByType(results,"java"));
        adapter.notifyDataSetChanged();
    }

    private void setUpList(List<Offer> results) {
        //mResults = results;
        mResults = listByType(results,"java");
        adapter = new OfferAdapter(mResults,MainActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickCallback(MainActivity.this);
        RecyclerViewItemTouchHelper helper = new RecyclerViewItemTouchHelper(adapter,mResults);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(helper.createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
        isFirst = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemClick(int pos,View v) {
        Offer offer = mResults.get(pos);
        EventBus.getDefault().postSticky(offer);
        Pair<View, String> p1 = Pair.create(v.findViewById(R.id.image), "offerImage");
        Pair<View, String> p2 = Pair.create(v.findViewById(R.id.title), "offerTitle");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, p1,p2);
        startActivity(new Intent(MainActivity.this,DetailledOffer.class), options.toBundle());
    }

    @Override
    public void onCompanyImageClick(int pos) {
        Toast.makeText(getApplicationContext(),"position " + String.valueOf(pos),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadNextPage() {
        if(online)
            loadMore(counter++);
        else
            Toast.makeText(getApplicationContext(),"No internet Connection!",Toast.LENGTH_SHORT).show();
    }

    private void loadMore(int counter) {
        TanitjobsParser tanitjobsParser = new TanitjobsParser();
        tanitjobsParser.setAsyncTaskCallback(MainActivity.this);
        tanitjobsParser.execute("http://tanitjobs.com/search-results-jobs/?searchId=1483197031.949&action=search&page="
                + String.valueOf(counter) + "&view=list");
    }

    private List<Offer> listByType(List<Offer> list,String type) {
        Map<String,Object> map = new HashMap<>();
        List<Offer> result = new ArrayList<>();
        for(Offer offer:list) {
            String offerId = database.push().getKey();
            database.child(offerId).setValue(offer);
            map.put("url",offer.getUrl());
            map.put("title",offer.getTitle());
            map.put("comanyName",offer.getComanyName());
            map.put("imgUrl",offer.getImgUrl());
            map.put("place",offer.getPlace());
            map.put("date",offer.getDate());
            if(offer.getTitle().toLowerCase().contains(type))
                result.add(offer);
        }


        return result;
    }

}
