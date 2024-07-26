package com.testing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class FragDiary extends Fragment {

    public FragDiary() {
        // Required empty public constructor
    }


    Button btnAn, btnUong;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {View view = inflater.inflate(R.layout.fragment_frag_diary, container, false);

        btnAn = (Button) view.findViewById(R.id.btnAn);
        btnUong = (Button) view.findViewById(R.id.btnUong);

        btnAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TrackEatingActivity.class);
                startActivity(intent);
            }
        });

        btnUong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TrackDrinkingActivity.class);
                startActivity(intent);
            }
        });

       /* btnTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TrackEatingActivity.class);
                startActivity(intent);
            }
        });*/

        return view;
    }
}