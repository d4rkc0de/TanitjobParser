package nachos.com.tanitjobparser.ui;


import android.content.Intent;
import android.os.AsyncTask;
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("onScrollStateChanged",String.valueOf(newState));
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("onScrolledx",String.valueOf(dx));
                Log.d("onScrolledy",String.valueOf(dy));
            }
        });

        mResults = new ArrayList<>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMore(counter++);
                adapter.notifyDataSetChanged();
                recyclerView.post( new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        loadMore(counter++);

    }

    @Override
    public void onDone(List<Offer> results) {
        mResults.addAll(results);
        //mResults = results;
        if(results != null) {
            adapter = new OfferAdapter(mResults,MainActivity.this);
            recyclerView.setAdapter(adapter);
            adapter.setItemClickCallback(MainActivity.this);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
            itemTouchHelper.attachToRecyclerView(recyclerView);
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
    }

    @Override
    public void onCompanyImageClick(int pos) {
        Toast.makeText(getApplicationContext(),"position " + String.valueOf(pos),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void moveItemUp(int pos) {

    }

    private void loadMore(int counter) {
        TanitjobsParser tanitjobsParser = new TanitjobsParser();
        tanitjobsParser.setAsyncTaskCallback(MainActivity.this);
        tanitjobsParser.execute("http://tanitjobs.com/search-results-jobs/?searchId=1483197031.949&action=search&page="
                + String.valueOf(counter) + "&view=list");
    }

}
