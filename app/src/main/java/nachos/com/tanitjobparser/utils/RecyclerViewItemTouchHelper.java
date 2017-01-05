package nachos.com.tanitjobparser.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

import nachos.com.tanitjobparser.model.Offer;

/**
 * Created by abdelnacer on 1/5/17.
 */

public class RecyclerViewItemTouchHelper {

    private static RecyclerView.Adapter adapter;
    private static List<Offer> mResults;

    public RecyclerViewItemTouchHelper(RecyclerView.Adapter adapter,List<Offer> mResults) {
        this.adapter = adapter;
        this.mResults = mResults;
    }
    public static ItemTouchHelper.Callback createHelperCallback() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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

    private static void addItemToList(Offer offer) {
        mResults.add(offer);
        adapter.notifyItemInserted(mResults.indexOf(offer));
    }

    private static void moveItem(int oldPos, int newPos) {

        Offer offer = mResults.get(oldPos);
        mResults.remove(oldPos);
        mResults.add(newPos, offer);
        adapter.notifyItemMoved(oldPos, newPos);
    }

    private static void deleteItem(final int position) {
        mResults.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
