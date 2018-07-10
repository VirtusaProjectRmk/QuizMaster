package com.example.sony.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import rmk.virtusa.com.quizmaster.DashActivity;
import rmk.virtusa.com.quizmaster.QuizActivity;
import rmk.virtusa.com.quizmaster.R;

/**
 * Created by sony on 18-06-2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
int count = 0;
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mTest = new ArrayList<>();
    private ArrayList<String> mDate= new ArrayList<>();
    private ArrayList<String> mtime =new ArrayList<>();

    private Context mContext;
   private ArrayList<String> mButton=new ArrayList<>();

    public RecyclerViewAdapter(Context mContext,ArrayList<String> mTest, ArrayList<String> mDate, ArrayList<String> mtime,  ArrayList<String> mButton) {
        this.mTest = mTest;
        this.mDate = mDate;
        this.mtime = mtime;
        this.mContext = mContext;
        this.mButton = mButton;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
         ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.test.setText(mTest.get(position));
        holder.date.setText(mDate.get(position));
        holder.time.setText(mtime.get(position));

        holder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                     Intent intent= null ;

                   // switch (position) {
                       // case 0:


                        intent = new Intent(mContext, QuizActivity.class);
                         count ++;
                         mContext.startActivity(intent);
                         view.setEnabled(false);
                         mButton.clear();
                         holder.button.setVisibility(View.GONE);


                     /*holder.button.setOnClickListener(null);

                            // holder.button.setEnabled(false);
                            //break;
//
                        case 1:
                            intent = new Intent(mContext, MainActivity.class);
                            break;

                        case 2:
                            intent = new Intent(mContext, MainActivity.class);
                            break;

                        case 3:
                            intent = new Intent(mContext, MainActivity.class);
                            break;

                        case 4:
                            intent = new Intent(mContext, MainActivity.class);
                            break;


                    }*/
                    //holder.button.setVisibility(View.GONE);


                //Toast.makeText(mContext, "button disabled", Toast.LENGTH_LONG).show();
                // count++;
                // holder.button.setEnabled(false);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mTest.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView test;
        TextView date;
        TextView time;
        Button button;
        RelativeLayout parentLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            test=itemView.findViewById(R.id.test);
            date= itemView.findViewById(R.id.date);
            time=itemView.findViewById(R.id.time);
            button = itemView.findViewById(R.id.button);
            button.setClickable(false);
           /* button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);

                    button.setVisibility(View.GONE);
                }
            });
*/
           if(count == 1) {
               button.setVisibility(View.GONE);
           }
           //itemView.setClickable(true);
           // itemView.setOnClickListener(this);
            parentLayout=itemView.findViewById(R.id.parent_layout);

        }
    }
}
