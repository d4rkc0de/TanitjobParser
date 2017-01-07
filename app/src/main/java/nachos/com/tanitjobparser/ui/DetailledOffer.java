package nachos.com.tanitjobparser.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import nachos.com.tanitjobparser.R;
import nachos.com.tanitjobparser.adapter.OfferAdapter;
import nachos.com.tanitjobparser.model.Offer;

public class DetailledOffer extends AppCompatActivity {

    Offer offer;
    ImageView image;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailled_offer);
        offer = EventBus.getDefault().removeStickyEvent(Offer.class);

        image = (ImageView) findViewById(R.id.image);
        Picasso.with(this).load(offer.getImgUrl()).into(image);

        title = (TextView) findViewById(R.id.title);
        title.setText(offer.getTitle());

        Log.d("Offertitle",offer.getTitle());
        Log.d("OfferUrl",offer.getUrl());
        Log.d("Offertitle",offer.getImgUrl());
        Log.d("OfferComanyName",offer.getComanyName());
        Log.d("OfferPlace",offer.getPlace());
    }
}
