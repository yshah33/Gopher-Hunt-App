package com.example.project4;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {

     private Context mContext;
    private int mGopherX, mGopherY;
    private int x, y, count;
    private List<int[]> mMoves;

    public GridAdapter(Context context, int gopherX, int gopherY, List<int[]> move) {
        mContext = context;
        mGopherX = gopherX;
        mGopherY = gopherY;
        mMoves = move;
    }

    // Total number of grid cells (10x10 grid)
    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Method to create and return the grid view for each grid cell
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            // Inflate the grid square layout if not already inflated
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_sqaure, parent, false);
        }

        // Calculate the row and column position of the grid cell
        int row = position / 10;
        int col = position % 10;

        // Set background color of the grid cell based on gopher position
        if (row == mGopherY && col == mGopherX) {
            view.setBackgroundColor(Color.BLACK);
        }
        else {
            view.setBackgroundColor(Color.parseColor("#8B4513"));
        }

        // Set text view in the grid cell to display the move count
        TextView textView = view.findViewById(R.id.gridText);
        for (int[] move : mMoves) {
            if (move[0] == col && move[1] == row) {
                textView.setText(String.valueOf(move[2]));
                break;
            }
        }

        return view;
    }
}
