package com.marcusm.filmfinder;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.widget.ListViewCompat;

import com.marcusm.filmfinder.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieListFragment extends ListFragment {

    private static final String HAS_SEEN = "has_seen";

    OnListFragmentInteractionListener mListener;

    MyDB db;
    CustomCursorAdapter adapter;
    boolean hasSeen;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MovieListFragment newInstance(boolean hasSeen) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putBoolean(HAS_SEEN, hasSeen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            hasSeen = getArguments().getBoolean(HAS_SEEN);
        }

        db = new MyDB(this.getActivity());
        Cursor mCursor = db.selectMovieRecords(false, hasSeen, "title");
        adapter = new CustomCursorAdapter(this.getActivity(), mCursor, 0, false);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        Cursor mCursor = db.selectMovieRecords(false, hasSeen, "title");
        adapter.changeCursor(mCursor);

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Intent myIntent = new Intent(getActivity(), MovieActivity.class);
        Cursor c = adapter.getCursor();
        c.moveToPosition(position);
        Movie movie = new Movie(c.getString(0), c.getString(1), c.getString(2), c.getString(3),
                c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getInt(8),
                c.getInt(9), c.getString(10), c.getInt(11), c.getString(12), c.getString(13), c.getString(14));
        myIntent.putExtra("movie", movie);
        startActivity(myIntent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
