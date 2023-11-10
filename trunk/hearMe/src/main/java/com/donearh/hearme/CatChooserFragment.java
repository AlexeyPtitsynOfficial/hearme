package com.donearh.hearme;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.donearh.hearme.library.MainDatabaseHandler;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CatChooserFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<CategoryData> mCategoryData;

    private MyCatsRecyclerViewAdapter mMyCatsRecyclerViewAdapter;

    private TextView mHeaderText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CatChooserFragment() {
        MainDatabaseHandler db = new MainDatabaseHandler(getActivity());
        mCategoryData = db.getCategoryData();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CatChooserFragment newInstance(int columnCount) {
        CatChooserFragment fragment = new CatChooserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cat_chooser_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        // Set the adapter
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        mHeaderText = (TextView)view.findViewById(R.id.header_text);
        FloatingActionButton btn_back = (FloatingActionButton)view.findViewById(R.id.btn_back);
        mMyCatsRecyclerViewAdapter = new MyCatsRecyclerViewAdapter(MyCatsRecyclerViewAdapter.CAT_TYPE, btn_back, mHeaderText, context, mCategoryData, ((CatChooserDialog)getParentFragment()));
        recyclerView.setAdapter(mMyCatsRecyclerViewAdapter);


        btn_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mMyCatsRecyclerViewAdapter.backPress();
            }
        });
        btn_back.setVisibility(View.GONE);



        return view;
    }

    public void setHeaderText(String text){
        mHeaderText.setText(text);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onListFragmentInteraction(CategoryData item);
    }
}