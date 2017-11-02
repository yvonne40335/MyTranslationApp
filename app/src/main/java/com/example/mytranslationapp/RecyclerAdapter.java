package com.example.mytranslationapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by yvonne40335 on 2017/10/16.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements RecycleItemTouchHelper.ItemTouchHelperCallback{

    private List<Vocabulary> mVocabularies;
    // Constructor
    public RecyclerAdapter(List<Vocabulary> vocabularies) {
        mVocabularies = vocabularies;
    }

    // add ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView wordTextView,alphabetTextView;
        public RecyclerViewHolderClick rvListener;
        private final Context context;

        public ViewHolder(View itemview, RecyclerViewHolderClick listener) {
            super(itemview);
            context = itemview.getContext();
            rvListener = listener;
            wordTextView = (TextView) itemview.findViewById(R.id.tv_word);
            alphabetTextView = (TextView) itemview.findViewById(R.id.tv_alphabet);
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            rvListener.clickOnView(view, getLayoutPosition());
            //rvListener.onItemLongClick(view,getLayoutPosition());
        }

        public interface RecyclerViewHolderClick {
            void clickOnView(View v, int position);
            //void onItemLongClick(View v , int position);
        }
    }

    // build the view and turn the view into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final Context context = viewGroup.getContext();
        View vocabularyView = null;
        View alphabetView = null;
        ViewHolder viewHolder = null;

        if (viewType == 2) {
            alphabetView = LayoutInflater.from(context).inflate(R.layout.item_alphabet, viewGroup, false);
            ViewGroup.LayoutParams params = alphabetView.getLayoutParams();
            params.height = 50;
            alphabetView.setLayoutParams(params);

            viewHolder = new ViewHolder(alphabetView, new ViewHolder.RecyclerViewHolderClick() {
                @Override
                public void clickOnView(View view, int position) {}});
        } else if (viewType == 1) {
            vocabularyView = LayoutInflater.from(context).inflate(R.layout.item_vocabulary, viewGroup, false);
            ViewGroup.LayoutParams params = vocabularyView.getLayoutParams();
            params.height = 100;
            vocabularyView.setLayoutParams(params);

            viewHolder = new ViewHolder(vocabularyView, new ViewHolder.RecyclerViewHolderClick() {
                @Override
                public void clickOnView(View view, int position) {
                    final Vocabulary vocabulary = mVocabularies.get(position);
                    new Thread() {
                        public void run() {
                            try {
                                Intent intent = new Intent(context, Main2Activity.class);
                                intent.putExtra("LOOKUP", "1"+vocabulary.getWord());
                                context.startActivity(intent);
                                //((Activity)context).startActivityForResult(new Intent(context, Main2Activity.class), 1);
                                //sleep(2000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    }.start();
                }
            });
        }
        return viewHolder;
    }

    // let Vocabulary show in the view
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Vocabulary vocabulary = mVocabularies.get(position);
        TextView wordTextview = viewHolder.wordTextView;
        TextView alphabetTextview = viewHolder.alphabetTextView;
        switch (viewHolder.getItemViewType()){
            case 1:
                Log.v("word",vocabulary.getWord());
                wordTextview.setText(vocabulary.getWord());
                break;
            case 2:
                Log.v("alphabet",vocabulary.getWord());
                alphabetTextview.setText(vocabulary.getWord());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mVocabularies.size();
    }

    public int getLetterPosition(String header) {

        for (int i = 0; i < mVocabularies.size(); i++) {

            if (mVocabularies.get(i).getWord().equals(header)) {
                return i;
            }

        }
        return -1;

    }

    @Override
    public int getItemViewType(int position) {
        if (mVocabularies.get(position).type == 2) {
            return 2; //alphabet
        } else if (mVocabularies.get(position).type == 1) {
            return 1; //word
        }
        return 0;
    }

    public void notifyRecycler(List<Vocabulary> data){
        mVocabularies = data;
        notifyDataSetChanged();
    }

    @Override
    public void onItemDelete(int positon) {
        String word = mVocabularies.get(positon).getWord();
        mVocabularies.remove(positon);
        notifyItemRemoved(positon);
        History.remove(word);
    }

    /*public void onItemDelete2(int positon) {
        String word = mVocabularies.get(positon).getWord();
        mVocabularies.remove(positon);
        notifyItemRemoved(positon);
        FavCollection.remove(word);
    }*/

    @Override
    public void onMove(int fromPosition, int toPosition) {
    }

}
