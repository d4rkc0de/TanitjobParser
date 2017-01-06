package nachos.com.tanitjobparser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import nachos.com.tanitjobparser.R;
import nachos.com.tanitjobparser.model.Offer;

/**
 * Created by lichiheb on 28/12/16.
 */

public class OfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Offer> listData;
    private LayoutInflater inflater;
    private Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final int VISIBLE_ITEMS = 6;

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int pos);
        void onCompanyImageClick(int pos);
        void loadNextPage();
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public OfferAdapter(List<Offer> listData, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.listData = listData;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == listData.size() - VISIBLE_ITEMS)
            itemClickCallback.loadNextPage();
        return (position == listData.size()) ? VIEW_PROG : VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            vh = new OfferHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OfferHolder) {
            Offer offer = listData.get(position);
            ((OfferHolder)holder).title.setText(offer.getTitle());
            ((OfferHolder)holder).comanyName.setText(offer.getComanyName());
            ((OfferHolder)holder).location.setText(offer.getPlace());
            Picasso.with(context).load(offer.getImgUrl()).into(((OfferHolder)holder).image);
        }  else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size() + 1;
    }

    class OfferHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title,comanyName,location,date;
        private ImageView image;
        private View container;

        public OfferHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            comanyName = (TextView) itemView.findViewById(R.id.comanyName);
            location = (TextView) itemView.findViewById(R.id.location);
            date = (TextView) itemView.findViewById(R.id.date);
            image = (ImageView) itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);

            image.setOnClickListener(this);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.container)
                itemClickCallback.onItemClick(getAdapterPosition());
            else if(v.getId() == R.id.image)
                itemClickCallback.onCompanyImageClick(getAdapterPosition());
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }
}
