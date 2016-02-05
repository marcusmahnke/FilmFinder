package com.marcusm.filmfinder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RatingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final int NUM_SIMILAR_MOVIES = 5;

    private static final String TMDB_ROOT_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "api_key=1b482df935405b1a9df063071c476c4f";
    private static final String SEARCH_REQ = "search/movie?query=";
    private static final String RESULTS_ARRAY = "results";
    private static final String MOVIE_LOOKUP = "movie/";
    private static final String SIMILAR_LOOKUP = "/similar?";
    private static final String GENRE_FILTER = "with_genres=";
    private static final String YEAR_FILTER = "primary_release_year=";
    private static final String PAGE = "page=";
    private static final String POPULAR_MOVIES_REQ = "discover/movie?sort_by=popularity.desc";
    private static final String AMPERSAND = "&";
    private static final int GENRE_IDS[] = {28, 16, 35, 99, 18, 10751, 27, 10749, 53};

    private String[] genreLabels;
    MyDB db;
    ImageView movieImageView;
    TextView titleView, filterView;
    ArrayList<Movie> movieList;
    ArrayList<String> loggedMovies;
    Movie currentMovie;
    int popularMoviesPage = 1;
    Mode mode = Mode.ALL;
    int genreFilter = 0;
    int genreIndex = 0;
    String genreLabel;
    int yearFilter;
    boolean filterByGenre, filterByYear = false;

    private OnFragmentInteractionListener mListener;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        genreFilter = GENRE_IDS[position];
        genreLabel = genreLabels[position];
        genreIndex = position;
        if (genreFilter > 0) {
            System.out.println("Genre");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
           // mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        movieList = new ArrayList<Movie>();
        loggedMovies = new ArrayList<String>();
        db = new MyDB(this.getActivity());
        loadSeenMovies();
        genreLabels = getActivity().getResources().getStringArray(R.array.genre_array);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);
        movieImageView = (ImageView) rootView.findViewById(R.id.movie_image);
        movieImageView.setOnClickListener(this);
        titleView = (TextView) rootView.findViewById(R.id.movie_title);
        filterView = (TextView) rootView.findViewById(R.id.filter);
        filterView.setOnClickListener(this);

        Button likeButton = (Button) rootView.findViewById(R.id.like_button);
        likeButton.setOnClickListener(this);
        Button dislikeButton = (Button) rootView.findViewById(R.id.dislike_button);
        dislikeButton.setOnClickListener(this);
        Button skipButton = (Button) rootView.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(this);
        Button saveButton = (Button) rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);

        if (findMovies()) {
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
                findMovies(query);
                System.out.println(currentMovie.getImageURL());
                System.out.println(currentMovie.getThumbURL());
                changeMovie();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
        if (id == R.id.movie_image && currentMovie != null) {
            Intent myIntent = new Intent(getActivity(), MovieActivity.class);
            myIntent.putExtra("movie", currentMovie);
            getActivity().startActivity(myIntent);
        } else if (id == R.id.filter) {
            showFilterPopup(filterView);
        } else {
            if (currentMovie != null) {
                if (movieList.size() > 0) {
                    movieList.remove(0);
                }

                //update db
                int liked = 0;
                int seen = 0;
                switch (id) {
                    case R.id.like_button:
                        findSimilarMovies();
                        liked = 1;
                        seen = 1;
                        break;
                    case R.id.dislike_button:
                        seen = 1;
                        break;
                    case R.id.save_button:
                        break;
                    case R.id.skip_button:
                        seen = 2;
                        break;
                }

                db.createMovieRecord(currentMovie.getId(), currentMovie.getTitle(), currentMovie.getYear(),
                        currentMovie.getImageURL(), currentMovie.getThumbURL(), 0, seen, liked);

                //add to seenmovielist
                loggedMovies.add(currentMovie.getId());
                changeMovie();
            } else {
                Toast toast = Toast.makeText(this.getActivity().getApplicationContext(), "No movie to rate!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    boolean findMovies(String searchQuery) {
        JSONObject TMDBObject = WebRequest.APICall(TMDB_ROOT_URL + SEARCH_REQ + Uri.encode(searchQuery) + AMPERSAND
                + API_KEY);

        try {
            JSONArray TMDBArray = TMDBObject.getJSONArray(RESULTS_ARRAY);
            TMDBObject = TMDBArray.getJSONObject(0);
            currentMovie = new Movie(TMDBObject);
        } catch (JSONException e) {
            return false;
        }
        movieList.add(0, currentMovie);

        return true;
    }

    boolean findMovies() {
        String call = TMDB_ROOT_URL + POPULAR_MOVIES_REQ + AMPERSAND + PAGE + popularMoviesPage;

        String genreText = "All";
        if (filterByGenre) {
            System.out.println("Calling API with genre");
            call += AMPERSAND + GENRE_FILTER + genreFilter;
            genreText = genreLabel;
        }

        String yearText = "";
        if(filterByYear){
            System.out.println("Calling API with year");
            call += AMPERSAND + YEAR_FILTER + yearFilter;
            yearText = "From " + yearFilter;
        }

        filterView.setText("Showing: " + genreText + " Movies " + yearText);

        call += AMPERSAND + API_KEY;

        System.out.println(call);
        JSONObject obj = WebRequest.APICall(call);
        try {
            JSONArray arr = obj.getJSONArray(RESULTS_ARRAY);
            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);
                Movie movie = new Movie(obj);
                if ((!loggedMovies.contains(movie.getId())) && (!movieList.contains(movie))) {
                    movieList.add(movie);
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    boolean findSimilarMovies() {
        JSONObject obj = WebRequest.APICall(TMDB_ROOT_URL + MOVIE_LOOKUP + currentMovie.getId() + SIMILAR_LOOKUP + API_KEY);
        try {
            JSONArray arr = obj.getJSONArray(RESULTS_ARRAY);
            int numMovies = arr.length();
            if (numMovies > NUM_SIMILAR_MOVIES) {
                numMovies = NUM_SIMILAR_MOVIES;
            }
            for (int i = 0; i < numMovies; i++) {
                obj = arr.getJSONObject(i);
                Movie movie = new Movie(obj);
                if ((!loggedMovies.contains(movie.getId()))) {
                    db.createMovieRecord(movie.getId(), movie.getTitle(), movie.getYear(),
                            movie.getImageURL(), movie.getThumbURL(), 1, 0, 0);
                    if (mode == Mode.RECOMMENDED && (!movieList.contains(movie))) {
                        movieList.add(movie);
                    }
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    void changeMovie() {
        if (movieList.size() > 0) {
            System.out.println("changing movie normally.");
            currentMovie = movieList.get(0);
            Bitmap image = ImageDownloader.loadImage(currentMovie.getImageURL());
            String s = currentMovie.getTitle() + " (" + currentMovie.getYear() + ")";
            titleView.setText(s);
            movieImageView.setImageBitmap(image);
        } else if (mode == Mode.ALL) {
            popularMoviesPage++;
            if (findMovies()) {
                changeMovie();
            } else {
                clearMovie("Error. No Movies Found!");
            }
        } else {
            clearMovie("No Movies Found! Rate more movies for more recommendations!");
        }
    }

    void clearMovie(String s){
        currentMovie = null;
        movieImageView.setImageBitmap(null);
        titleView.setText(s);
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
        final View popupView = getActivity().getLayoutInflater().inflate(R.layout.filter_popup_layout, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button applyButton = (Button) popupView.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        final RadioButton radioAll = (RadioButton) popupView.findViewById(R.id.radio_all);
        RadioButton radioRecommended = (RadioButton) popupView.findViewById(R.id.radio_recommended);

        Spinner genreSpinner = (Spinner) popupView.findViewById(R.id.genre_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.genre_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);
        genreSpinner.setOnItemSelectedListener(this);
        genreSpinner.setSelection(genreIndex);

        final NumberPicker yearPicker = (NumberPicker) popupView.findViewById(R.id.year_picker);
        yearPicker.setMinValue(1920);
        yearPicker.setMaxValue(2016);
        yearPicker.setValue(yearFilter);

        final CheckBox genreCheckBox = (CheckBox) popupView.findViewById(R.id.genre_checkbox);
        final CheckBox yearCheckBox = (CheckBox) popupView.findViewById(R.id.year_checkbox);

        genreCheckBox.setChecked(filterByGenre);
        yearCheckBox.setChecked(filterByYear);

        if (mode == Mode.ALL) {
            radioAll.setChecked(true);
        } else {
            radioRecommended.setChecked(true);
        }

        final int previousYearFilter = yearFilter;
        final int previousGenreFilter = genreFilter;
        final boolean previousFilterByGenre = filterByGenre;
        final boolean previousFilterByYear = filterByYear;
        final Mode previousMode = mode;

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

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

                filterByGenre = genreCheckBox.isChecked();
                filterByYear = yearCheckBox.isChecked();
                yearFilter = yearPicker.getValue();

                boolean genreOrYearChanged = (previousFilterByYear != yearCheckBox.isChecked() || previousFilterByGenre != genreCheckBox.isChecked());
                genreOrYearChanged = genreOrYearChanged ||
                        (filterByGenre && previousGenreFilter != genreFilter) ||
                        (filterByYear && previousYearFilter != yearFilter);
                if (previousMode != mode || genreOrYearChanged) {
                    System.out.println("Filter Options Changed.");
                    onFilterOptionsChanged();
                }
            }
        });

    }

    void onFilterOptionsChanged() {
        System.out.println(mode);
        if (mode == Mode.ALL) {
            movieList.clear();
            findMovies();
            changeMovie();
        } else {
            loadSimilarMovies();
            filterView.setText("Showing: Movies Recommended for You");
            changeMovie();
        }
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
