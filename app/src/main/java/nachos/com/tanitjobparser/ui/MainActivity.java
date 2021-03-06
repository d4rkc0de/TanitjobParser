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
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private List<Offer> mResults,mFilteredResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int counter = 0;
    private boolean isFirst = true,online = false;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("offer");
    private Spinner spinner;
    private String spinnerSelectedOption = null;
    private boolean isItemSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database.removeValue();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mResults = new ArrayList<>();
        mFilteredResults = new ArrayList<>();

        setSpinner();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        setSwipeRefreshListener();
        online = isOnline(this);
        loadNextPage();
    }

    private void setSpinner() {
        spinner = (Spinner) findViewById(R.id.optionss_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(isItemSelected)
                    filterResults(spinner.getSelectedItem().toString());
                isItemSelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    private void filterResults(String option) {
        spinnerSelectedOption = option;
        mFilteredResults.clear();
        mFilteredResults = listByType(mResults,option);
        adapter.notifyDataSetChanged();
    }

    private void addToList(List<Offer> results) {
        mResults.addAll(results);
        if(spinnerSelectedOption != null)
            mFilteredResults.addAll(listByType(results,spinnerSelectedOption));
        else
            mFilteredResults.addAll(results);
        adapter.notifyDataSetChanged();
    }

    private void setUpList(List<Offer> results) {
        mFilteredResults = mResults = results;
        Log.d("resultsSize",mResults.size()+"");
        adapter = new OfferAdapter(mFilteredResults,MainActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickCallback(MainActivity.this);
        RecyclerViewItemTouchHelper helper = new RecyclerViewItemTouchHelper(adapter,mFilteredResults);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(helper.createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
        isFirst = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemClick(int pos,View v) {
        Offer offer = mFilteredResults.get(pos);
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
            if(offer.getTitle().toLowerCase().contains(type.toLowerCase()))
                result.add(offer);
        }
        Log.d("resultSize",result.size()+"");
        for(Offer offer:result) Log.d("results",offer.getTitle());
        return result;
    }

}