package nachos.com.tanitjobparser.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import nachos.com.tanitjobparser.R;
import nachos.com.tanitjobparser.model.Offer;

public class DetailledOffer extends AppCompatActivity {

    Offer offer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailled_offer);
        offer = EventBus.getDefault().removeStickyEvent(Offer.class);

        Log.d("Offertitle",offer.getTitle());
        Log.d("OfferComanyName",offer.getComanyName());
        Log.d("OfferPlace",offer.getPlace());
    }
}
