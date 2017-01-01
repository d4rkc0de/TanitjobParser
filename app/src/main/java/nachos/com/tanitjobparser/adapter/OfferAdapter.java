package nachos.com.tanitjobparser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import nachos.com.tanitjobparser.R;
import nachos.com.tanitjobparser.model.Offer;

/**
 * Created by lichiheb on 28/12/16.
 */

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder>  {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Offer> listData;
    private LayoutInflater inflater;
    private Context context;

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int pos);
        void onCompanyImageClick(int pos);
        void moveItemUp(int pos);
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
        return listData.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public OfferHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM)
            Log.d("buttom","buttom");
        else
            Log.d("buttom","Not buttom");
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new OfferHolder(view);
    }

    @Override
    public void onBindViewHolder(OfferHolder holder, int position) {
        Offer offer = listData.get(position);
        holder.title.setText(offer.getTitle());
        holder.comanyName.setText(offer.getComanyName());
        holder.location.setText(offer.getPlace());
        Picasso.with(context).load(offer.getImgUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return listData.size();
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
}
