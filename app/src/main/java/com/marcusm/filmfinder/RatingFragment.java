package com.marcusm.filmfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.mtp.MtpConstants;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RatingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int NUM_SIMILAR_MOVIES = 5;
    private static final String API_KEY = "apikey=api_key_here";
    private static final String TMDB_API_KEY = "api_key=api_key_here";
    private static final String TMDB_SEARCH_REQ = "https://api.themoviedb.org/3/search/movie?query=";
    private static final String TMDB_RESULTS_ARRAY = "results";
    private static final String RT_RESULTS_ARRAY = "movies";
    private static final String PAGE = "page=";
    private static final String TMDB_POPULAR_MOVIES = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc";
    private static final String SEARCH_REQ = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?q=";
    private static final String SIMILAR_REQ1 = "http://api.rottentomatoes.com/api/public/v1.0/movies/";
    private static final String SIMILAR_REQ2 = "/similar.json?";

    public static String CRITICS_SCORE = "critics_score";
    public static String CRITICS_CONSENSUS = "critics_consensus";
    public static String AUDIENCE_SCORE = "audience_score";
    public static String RATINGS = "ratings";
    public static String CAST = "abridged_cast";
    public static String ACTOR_NAME = "name";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    MyDB db;
    ImageView movieImageView;
    TextView titleView, filterView;
    ArrayList<Movie> movieList;
    ArrayList<String> loggedMovies;
    Movie currentMovie;
    int popularMoviesPage = 1;
    Mode mode = Mode.ALL;

    private OnFragmentInteractionListener mListener;

    enum Mode {
        ALL, RECOMMENDED
    }

    public RatingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RatingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingFragment newInstance(String param1, String param2) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        movieList = new ArrayList<Movie>();
        loggedMovies = new ArrayList<String>();
        db = new MyDB(this.getActivity());
        loadSeenMovies();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);
        movieImageView = (ImageView) rootView.findViewById(R.id.movie_image);
        movieImageView.setOnClickListener(this);
        titleView = (TextView) rootView.findViewById(R.id.movie_title);
        filterView = (TextView) rootView.findViewById(R.id.filter);

        if (mode == Mode.ALL) {
            filterView.setText("Showing: All Movies by Popularity");
        } else {
            filterView.setText("Showing: Movies Recommended for You");
        }

        Button likeButton = (Button) rootView.findViewById(R.id.like_button);
        likeButton.setOnClickListener(this);
        Button dislikeButton = (Button) rootView.findViewById(R.id.dislike_button);
        dislikeButton.setOnClickListener(this);
        Button skipButton = (Button) rootView.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(this);
        Button saveButton = (Button) rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);

        if (findPopularMovies()) {
            changeMovie();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                findMovie(query);
                System.out.println(currentMovie.getImageURL());
                System.out.println(currentMovie.getThumbURL());
                changeMovie();
                //movieImageView.setImageBitmap(loadImage(currentMovie.getImageURL()));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //System.out.println("TEST");
                showFilterPopup(titleView);
                return false;
            }
        });
        //MenuItemCompat.getActionView(filterItem);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.movie_image) {
            Intent myIntent = new Intent(getActivity(), MovieActivity.class);
            myIntent.putExtra("movie", currentMovie);
            getActivity().startActivity(myIntent);
        } else if (id == R.id.radio_all || id == R.id.radio_recommended) {
            //System.out.println("TEST");
            // RadioButton rb = (RadioButton) v;
            //  System.out.println(rb.isChecked());
            // onRadioButtonClicked(v);
        } else {
            if (movieList.size() > 0) {
                movieList.remove(0);
            }
            //update db
            switch (id) {
                case R.id.like_button:
                    findSimilarMovies();
                    db.updateMovieRecord(currentMovie.getId(), 0, 1, 1);
                    break;
                case R.id.dislike_button:
                    db.updateMovieRecord(currentMovie.getId(), 0, 1, 0);
                    break;
                case R.id.save_button:
                    db.updateMovieRecord(currentMovie.getId(), 0, 0, 0);
                    break;
                case R.id.skip_button:
                    db.updateMovieRecord(currentMovie.getId(), 0, 2, 0);
                    break;
            }

            //add to seenmovielist
            loggedMovies.add(currentMovie.getId());
            changeMovie();
        }
    }

    boolean findMovie(String searchQuery) {
        JSONObject TMDBObject = WebRequest.APICall(TMDB_SEARCH_REQ + Uri.encode(searchQuery) + "&"
                + TMDB_API_KEY);

        try {
            JSONArray TMDBArray = TMDBObject.getJSONArray("results");
            TMDBObject = TMDBArray.getJSONObject(0);

            currentMovie = new Movie(TMDBObject);
            if (currentMovie == null) {
                System.out.println(currentMovie.toString());
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
        movieList.add(0, currentMovie);
        db.createMovieRecord(currentMovie.getId(), currentMovie.getTitle(), currentMovie.getYear(),
                currentMovie.getImageURL(), currentMovie.getThumbURL(), 0, 0, 0);
        return true;
    }

    boolean findPopularMovies() {
        JSONObject obj = WebRequest.APICall(TMDB_POPULAR_MOVIES + "&" + PAGE + popularMoviesPage + "&" + TMDB_API_KEY);
        try {
            JSONArray arr = obj.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                Movie movie = new Movie(obj);
                if ((!loggedMovies.contains(movie.getId())) && (!movieList.contains(movie)) && movie != null) {
                    movieList.add(movie);
                    db.createMovieRecord(movie.getId(), movie.getTitle(), movie.getYear(),
                            movie.getImageURL(), movie.getThumbURL(), 0, 0, 0);
                } else if (movie == null) {
                    Log.i("ERROR", "MOVIE NULL");
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    boolean findSimilarMovies() {
        JSONObject obj = WebRequest.APICall("https://api.themoviedb.org/3/movie/" + currentMovie.getId() + "/similar?" + TMDB_API_KEY);
        try {
            JSONArray arr = obj.getJSONArray("results");
            int numMovies = arr.length();
            if (numMovies > NUM_SIMILAR_MOVIES) {
                numMovies = NUM_SIMILAR_MOVIES;
            }
            for (int i = 0; i < numMovies; i++) {
                obj = arr.getJSONObject(i);
                Movie movie = new Movie(obj);
                if ((!loggedMovies.contains(movie.getId())) && movie != null) {
                    db.createMovieRecord(movie.getId(), movie.getTitle(), movie.getYear(),
                            movie.getImageURL(), movie.getThumbURL(), 1, 0, 0);
                    if (mode == Mode.RECOMMENDED && (!movieList.contains(movie))) {
                        movieList.add(movie);
                    }
                } else if (movie == null) {
                    Log.i("ERROR", "MOVIE NULL");
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    void changeMovie() {
        if (movieList.size() > 0) {
            currentMovie = movieList.get(0);
            Bitmap image = loadImage(currentMovie.getImageURL());
            titleView.setText(currentMovie.getTitle() + " (" + currentMovie.getYear() + ")");
            movieImageView.setImageBitmap(image);
        } else if (mode == Mode.ALL) {
            popularMoviesPage++;
            if (findPopularMovies()) {
                changeMovie();
            } else {
                movieImageView.setImageBitmap(null);
            }
        }
    }

    void changeMode(Mode mode) {
        if (mode == Mode.ALL) {
            filterView.setText("Showing: All Movies by Popularity");
        } else {
            filterView.setText("Showing: Movies Recommended for You");
        }

        this.mode = mode;
    }

    void loadSeenMovies() {
        Cursor mCursor = db.selectMovieRecords(false, true, null);
        for (int i = 0; i < mCursor.getCount(); i++) {
            loggedMovies.add(mCursor.getString(0));
            mCursor.moveToNext();
        }
        mCursor.close();
    }

    void loadSimilarMovies() {
        movieList.clear();
        Cursor mCursor = db.selectMovieRecords(true, false, null);
        for (int i = 0; i < mCursor.getCount(); i++) {
            movieList.add(getMovieFromCursor(mCursor));
            mCursor.moveToNext();
        }
        mCursor.close();
    }

    public void showFilterPopup(View anchorView) {

        View popupView = getActivity().getLayoutInflater().inflate(R.layout.filter_popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Example: If you have a TextView inside `popup_layout.xml`
        //TextView tv = (TextView) popupView.findViewById(R.id.tv);

        final RadioButton radioAll = (RadioButton) popupView.findViewById(R.id.radio_all);
        radioAll.setOnClickListener(this);
        RadioButton radioRecommended = (RadioButton) popupView.findViewById(R.id.radio_recommended);
        radioRecommended.setOnClickListener(this);

        final Mode previousMode = mode;
        if (mode == Mode.ALL) {
            radioAll.setChecked(true);
        } else {
            radioRecommended.setChecked(true);
        }

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        //popupWindow.setBackgroundDrawable(new ColorDrawable());

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (radioAll.isChecked()) {
                    mode = Mode.ALL;
                } else {
                    mode = Mode.RECOMMENDED;
                }

                if (previousMode != mode) {
                    onFilterOptionsChanged();
                }
            }
        });

    }

    void onFilterOptionsChanged() {
        System.out.println("TESTING");
        if (mode == Mode.ALL) {
            changeMode(Mode.ALL);
            movieList.clear();
            findPopularMovies();
            changeMovie();
        } else {
            changeMode(Mode.RECOMMENDED);
            loadSimilarMovies();
            changeMovie();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_all:
                if (checked) {
                    changeMode(Mode.ALL);
                    movieList.clear();
                    findPopularMovies();
                    changeMovie();
                }
                break;
            case R.id.radio_recommended:
                if (checked) {
                    changeMode(Mode.RECOMMENDED);
                    loadSimilarMovies();
                    changeMovie();
                }
                break;
        }
    }

    void changeMovieList() {
        loadSimilarMovies();
    }

    Movie getMovieFromCursor(Cursor c) {
        String cast = c.getString(12);
        String[] castArray = null;
        if (cast != null) {
            castArray = cast.split(",");
        }

        return new Movie(c.getString(0), c.getString(1), c.getString(2), c.getString(3),
                c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getInt(8),
                c.getInt(9), c.getString(10), c.getInt(11), castArray, c.getString(13));
    }

    Bitmap loadImage(String imageurl) {
        Bitmap image = null;
        ImageDownloader id = new ImageDownloader(imageurl);
        try {
            image = id.execute().get();
        } catch (Exception e) {
        }

        return image;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        System.out.println(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
