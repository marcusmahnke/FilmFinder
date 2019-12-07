package com.marcusm.filmfinder;

import android.database.Cursor;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class MovieListActivity extends Fragment {
    private GridView gridView;
    //private GridViewAdapter gridAdapter;
    protected Cursor mCursor;
    protected ImageAdapter mAdapter;
    MyDB db;

    public static MovieListActivity newInstance() {
        MovieListActivity fragment = new MovieListActivity();
        Bundle args = new Bundle();
        //args.putBoolean(RECOMMENDED, isRecommended);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_movie_list);

        //gridView = (GridView) findViewById(R.id.gridView);
        //gridAdapter = new GridViewAdapter(this, R.layout.movie_grid_item, getData());
        //gridView.setAdapter(gridAdapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_movie_list, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);

        db = new MyDB(this.getContext());
        mCursor = db.selectMovieRecords(false, true, "title");
        mAdapter = new ImageAdapter(this.getActivity(), mCursor);
        gridView.setAdapter(mAdapter);

        return view;
    }
}
