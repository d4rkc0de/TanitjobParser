package nachos.com.tanitjobparser.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import nachos.com.tanitjobparser.R;
import nachos.com.tanitjobparser.adapter.OfferAdapter;
import nachos.com.tanitjobparser.model.Offer;
import nachos.com.tanitjobparser.network.TanitjobsParser;
import nachos.com.tanitjobparser.utils.RecyclerViewItemTouchHelper;

public class MainActivity extends AppCompatActivity implements OfferAdapter.ItemClickCallback,TanitjobsParser.AsyncTaskCallback {

    private RecyclerView recyclerView;
    private OfferAdapter adapter;
    private List<Offer> mResults;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mResults = new ArrayList<>();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        setSwipeRefreshListener();

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
        if(results != null)
             mResults.addAll(results);
        else
            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();

        if(results != null) {
            adapter = new OfferAdapter(mResults,MainActivity.this);
            recyclerView.setAdapter(adapter);
            adapter.setItemClickCallback(MainActivity.this);

            RecyclerViewItemTouchHelper helper = new RecyclerViewItemTouchHelper(adapter,mResults);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(helper.createHelperCallback());
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onItemClick(int pos) {
        Offer offer = mResults.get(pos);
        EventBus.getDefault().postSticky(offer);
        startActivity(new Intent(MainActivity.this,DetailledOffer.class));
    }

    @Override
    public void onCompanyImageClick(int pos) {
        Toast.makeText(getApplicationContext(),"position " + String.valueOf(pos),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void moveItemUp(int pos) {

    }

    @Override
    public void loadNextPage() {
        loadMore(counter++);
    }

    private void loadMore(int counter) {
        TanitjobsParser tanitjobsParser = new TanitjobsParser();
        tanitjobsParser.setAsyncTaskCallback(MainActivity.this);
        tanitjobsParser.execute("http://tanitjobs.com/search-results-jobs/?searchId=1483197031.949&action=search&page="
                + String.valueOf(counter) + "&view=list");
    }

}
