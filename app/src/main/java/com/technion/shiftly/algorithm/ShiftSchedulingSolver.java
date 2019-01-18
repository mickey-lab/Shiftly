package com.technion.shiftly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ShiftSchedulingSolver {

    private LinkedHashMap<String, String> options;
    private List<String> final_schedule;
    private int total_shifts_num;
    private int workers_in_shift;

    public ShiftSchedulingSolver(LinkedHashMap<String, String> options, int workers_in_shift) {
        this.options = options;
        this.workers_in_shift = workers_in_shift;
        this.final_schedule = new ArrayList<>();
        this.total_shifts_num = options.entrySet().iterator().next().getValue().length();
    }

    public List<String> getFinal_schedule() {
        return final_schedule;
    }

    // If returns true, then the final_schedule can be accessed with the method getFinalSchedule
    // on the ShiftSchedulingSolver object
    Boolean solve() {
        if (!minimize_sched(options, true))
            return false;
        else
//            for (LinkedHashMap.Entry<String, String> entry : options.entrySet()) {
//                System.out.println(entry.getValue());
//            }
                return solve_aux(0, this.options);
    }

    // Given an options map, removing all the redundant options
    Boolean minimize_sched(LinkedHashMap<String, String> options, boolean changed) {
        if (!changed) {
            return true;
        }
        Boolean has_changed = false;
        for (int i=0 ; i < total_shifts_num ; i++) {
            int num_of_employees_can_work_this_shift = 0;
            LinkedHashMap.Entry<String, String> only_worker_available = null;
            for (LinkedHashMap.Entry<String, String> entry : options.entrySet()) {
                if (entry.getValue().charAt(i) == '1') {
                    num_of_employees_can_work_this_shift++;
                    only_worker_available = entry;
                }
            }
            // The i'th shift cannot be assigned. Schedule is unsolvable.
            if (num_of_employees_can_work_this_shift == 0) {
                return false;
            }
            if (num_of_employees_can_work_this_shift == 1) {
                if (i>0) {
                    if (only_worker_available.getValue().charAt(i-1) == '1') {
                        has_changed = true;
                    }
                    // removing the shift before
                    String updated_options = only_worker_available.getValue().substring(0,i-1)
                            +'0'+only_worker_available.getValue().substring(i);
                    only_worker_available.setValue(updated_options);
                }
                if (i<total_shifts_num-1) {
                    if (only_worker_available.getValue().charAt(i+1) == '1') {
                        has_changed = true;
                    }
                    // removing the shift after
                    String updated_options = only_worker_available.getValue().substring(0,i+1)
                            +'0';
                    if (i < total_shifts_num-2)
                        updated_options += only_worker_available.getValue().substring(i+2);
                    only_worker_available.setValue(updated_options);
                }
            }
        }
        return minimize_sched(options, has_changed);
    }

    Boolean solve_aux(int starting_sched_from_shift, LinkedHashMap<String, String> options_aux) {

        LinkedHashMap<String, String> shuffled_options = shuffle_options(options_aux);

        // Base case of the recursion: if the starting_sched_from_shift == total_shift_num - we are done
        if (starting_sched_from_shift == this.total_shifts_num) return true;

        for (LinkedHashMap.Entry<String, String> entry : shuffled_options.entrySet()) {

            Boolean employee_can_work_this_shift = (entry.getValue().charAt(starting_sched_from_shift) == '1');
            Boolean is_first_shift = (starting_sched_from_shift/workers_in_shift == 0);
//            Boolean employee_wasnt_scheduled_for_previous_shift = (starting_sched_from_shift == 0) ||
//                    !(this.final_schedule.get(starting_sched_from_shift - workers_in_shift).equals(entry.getKey()));

            int employees_already_assigned_for_shift = final_schedule.size() % workers_in_shift;

            // Validate that the employee was not assigned already to the previous shift
            Boolean employee_wasnt_scheduled_for_previous_shift = true;
            if (!is_first_shift) {
                for (int i = 0 ; i < workers_in_shift ; i++) {
                    employee_wasnt_scheduled_for_previous_shift = employee_wasnt_scheduled_for_previous_shift &&
                            !(this.final_schedule.get(starting_sched_from_shift - employees_already_assigned_for_shift - i - 1).equals(entry.getKey()));
                }
            }

            employee_wasnt_scheduled_for_previous_shift = employee_wasnt_scheduled_for_previous_shift || is_first_shift;

            // Validate that the employee was not assigned already to the current shift
            Boolean employee_is_not_scheduled_for_current_shift = true;
            for (int i = 0 ; i < employees_already_assigned_for_shift ; i++) {
                employee_is_not_scheduled_for_current_shift = employee_is_not_scheduled_for_current_shift &&
                        !(this.final_schedule.get(starting_sched_from_shift - i - 1).equals(entry.getKey()));
            }

            // If the employee can work this shift and she wasn't scheduled for the previous one
            if (employee_can_work_this_shift && employee_wasnt_scheduled_for_previous_shift
                && employee_is_not_scheduled_for_current_shift) {

                // Schedule her to work this shift
                this.final_schedule.add(entry.getKey());


                // --------- THE RECURSIVE CALL -----------
                Boolean result = solve_aux(starting_sched_from_shift + 1, shuffled_options);

                // A viable solution has been found
                if (result) return true; // The solution is already written in the final_schedule field
            }
        }
        return false;

    }

    LinkedHashMap<String, String> shuffle_options(LinkedHashMap<String, String> options_to_shuffle) {
        // --------- ADDS RANDOMIZATION -----------
        // Shuffle the options and send a new options map before performing the recursive call
        // Shuffle the options map so that the shifts are evenly distributed and each solving
        // can produce a different result
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        List<String> options_keys = new ArrayList<>(options_to_shuffle.keySet());
        Collections.shuffle(options_keys);

        List<String> options_values = new ArrayList<>();
        for (int i = 0; i < options_keys.size(); i++) {
            options_values.add(options_to_shuffle.get(options_keys.get(i)));
        }
        for (int i = 0; i < options_keys.size(); i++) {
            result.put(options_keys.get(i), options_values.get(i));
        }
        return result;
    }

    @Override
    public String toString() {
        String sched = "";
        int i = 1;
        for (String employee : final_schedule) {
            sched += "Shift #" + i + ":\n";
            for (int j=0 ; j < workers_in_shift ; j++) {
                sched += employee + ",";
            }
            i++;
            sched += "\n";
        }
        return "----ShiftSolver----\n" +
                "schedule:\n" + sched;
    }
}
