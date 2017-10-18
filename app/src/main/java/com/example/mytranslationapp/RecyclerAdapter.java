package com.example.mytranslationapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yvonne40335 on 2017/10/16.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Vocabulary> mVocabularies;

    // Constructor
    public RecyclerAdapter(List<Vocabulary> vocabularies){
        mVocabularies = vocabularies;
    }

    // add ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView wordTextView;
        public RecyclerViewHolderClick rvListener;
        private final Context context;

        public ViewHolder(View itemview, RecyclerViewHolderClick listener){
            super(itemview);
            context = itemview.getContext();
            rvListener = listener;
            wordTextView = (TextView) itemview.findViewById(R.id.tv_word);
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            rvListener.clickOnView(view,getLayoutPosition());
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
        View vocabualryView = LayoutInflater.from(context).inflate(R.layout.item_vocabulary, viewGroup, false);

        ViewGroup.LayoutParams params = vocabualryView.getLayoutParams();
        params.height=100;
        vocabualryView.setLayoutParams(params);

        ViewHolder viewHolder = new ViewHolder(vocabualryView, new ViewHolder.RecyclerViewHolderClick(){
            @Override
            public void clickOnView(View view, int position){
                final Vocabulary vocabulary = mVocabularies.get(position);
                new Thread(){
                    public void run(){
                        try{
                            Intent intent = new Intent(context, Main2Activity.class);
                            intent.putExtra("LOOKUP", vocabulary.getWord());
                            context.startActivity(intent);
                            sleep(2000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                        }
                    }
                }.start();
            }

            /*@Override
            public void onItemLongClick(View view,int position){
                //final Vocabulary vocabulary = mVocabularies.get(position);
                //removeData(position);
            }*/

        });

        return viewHolder;
    }

    // let Vocabulary show in the view
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Vocabulary vocabulary = mVocabularies.get(position);
        TextView wordTextview = viewHolder.wordTextView;
        wordTextview.setText(vocabulary.getWord());
    }

    @Override
    public int getItemCount() {
        return mVocabularies.size();
    }

    /*public void removeData(int position)
    {
        mVocabularies.remove(position);
        notifyItemRemoved(position);
    }*/

}
