package com.technion.shiftly.scheduleView;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.technion.shiftly.R;

import java.util.Calendar;

public class AgendaViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get a reference for the week view in the layout.
        View v = inflater.inflate(R.layout.fragment_agenda_view, container, false);
        Resources res = getResources();

        int[] mColors = res.getIntArray(R.array.calendar_colors);
        String groupId = getActivity().getIntent().getExtras().getString("GROUP_ID");
        String employeeId = getActivity().getIntent().getExtras().getString("EMPLOYEE_ID");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.agenda_label));
        WeekView mAgendaView = (WeekView) v.findViewById(R.id.agendaView);
        Scheduler agenda_schedule = new Scheduler(mAgendaView,groupId,mColors,7,employeeId);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.DATE, -2);
        mAgendaView.goToDate(calendar);
        return v;
    }
}