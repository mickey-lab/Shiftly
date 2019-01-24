package com.technion.shiftly.scheduleView;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.technion.shiftly.R;

import java.util.Calendar;

public class WeeklyViewFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get a reference for the week view in the layout.
        View v = inflater.inflate(R.layout.fragment_weekly_view, container, false);
        Resources res = getResources();

        int[] mColors = res.getIntArray(R.array.calendar_colors);
        String groupId = getActivity().getIntent().getExtras().getString("GROUP_ID");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.weekly_label));
        WeekView mWeekView = (WeekView) v.findViewById(R.id.weekView);
        Scheduler weekly_schedule = new Scheduler(mWeekView,groupId,mColors,7,"");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.DATE, -2);
        mWeekView.goToDate(calendar);
        return v;
    }
}