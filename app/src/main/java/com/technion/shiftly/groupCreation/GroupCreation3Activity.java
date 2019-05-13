package com.technion.shiftly.groupCreation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technion.shiftly.R;
import com.technion.shiftly.dataTypes.Group;
import com.technion.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftly.utility.Constants;
import com.technion.shiftly.utility.CustomSnackbar;

// The third activity of the group creation process.
// In this activity the future admin sets the group settings.

public class GroupCreation3Activity extends AppCompatActivity {

    private CustomSnackbar mSnackbar;
    private ConstraintLayout mLayout;
    private FirebaseStorage mStorage;
    private DatabaseReference mGroupsRef;
    private StorageReference mStorageRef;
    private UploadTask uploadTask;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_3);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        mLayout = (ConstraintLayout) findViewById(R.id.group_creation_3_layout);

        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar_3);
        setSupportActionBar(mainToolbar);

        final String group_action = getIntent().getExtras().getString("GROUP_ACTION");
        String action_bar_title = getIntent().getExtras().getString("TITLE");

        getSupportActionBar().setTitle(action_bar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Setting the spinners with their adapters
        final Spinner days_spinner = findViewById(R.id.days_num_spinner);
        ArrayAdapter<CharSequence> days_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.days_num, R.layout.custom_spinner_item);
        days_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days_spinner.setAdapter(days_spinner_adapter);

        final Spinner shifts_per_day_spinner = findViewById(R.id.shifts_per_day_num_spinner);
        ArrayAdapter<CharSequence> shifts_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.shifts_per_day, R.layout.custom_spinner_item);
        shifts_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shifts_per_day_spinner.setAdapter(shifts_spinner_adapter);

        final Spinner employees_per_shift_spinner = findViewById(R.id.employees_per_shift_num_spinner);
        ArrayAdapter<CharSequence> employees_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.employees_per_shift, R.layout.custom_spinner_item);
        employees_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employees_per_shift_spinner.setAdapter(employees_spinner_adapter);

        final Spinner starting_hour_spinner = findViewById(R.id.starting_hour_spinner);
        ArrayAdapter<CharSequence> starting_hour_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.starting_hour, R.layout.custom_spinner_item);
        starting_hour_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        starting_hour_spinner.setAdapter(starting_hour_spinner_adapter);

        final Spinner shift_len_spinner = findViewById(R.id.shift_len_spinner);
        ArrayAdapter<CharSequence> shift_len_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.shift_len, R.layout.custom_spinner_item);
        shift_len_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shift_len_spinner.setAdapter(shift_len_spinner_adapter);

        mStorage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mGroupsRef = database.getReference().child(("Groups"));

        Button apply_button = findViewById(R.id.continue_button);
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.setEnabled(false);

                Long days_num = Long.parseLong(days_spinner.getSelectedItem().toString());
                Long shifts_per_day = Long.parseLong(shifts_per_day_spinner.getSelectedItem().toString());
                Long employees_per_shift = Long.parseLong(employees_per_shift_spinner.getSelectedItem().toString());
                String starting_hour = starting_hour_spinner.getSelectedItem().toString();
                Long shift_len = Long.parseLong(shift_len_spinner.getSelectedItem().toString());
                String group_name = getIntent().getExtras().getString("GROUP_NAME");
                byte[] group_pic_array = getIntent().getExtras().getByteArray("GROUP_PICTURE");
                // Contains the group_id to edit only in case the user reached this activity via edit, else contains the empty string
                String group_id = getIntent().getExtras().getString("GROUP_ID");

                if (group_action.equals("CREATE")) {
                    Intent group_creation_4_intent = new Intent(getApplicationContext(), GroupCreation4Activity.class);

                    group_creation_4_intent.putExtra("GROUP_NAME", group_name);
                    group_creation_4_intent.putExtra("DAYS_NUM", days_num);
                    group_creation_4_intent.putExtra("SHIFTS_PER_DAY", shifts_per_day);
                    group_creation_4_intent.putExtra("EMPLOYEES_PER_SHIFT", employees_per_shift);
                    group_creation_4_intent.putExtra("STARTING_HOUR", starting_hour);
                    group_creation_4_intent.putExtra("SHIFT_LEN", shift_len);
                    group_creation_4_intent.putExtra("GROUP_ACTION", group_action);

                    if (group_pic_array!=null) {
                        group_creation_4_intent.putExtra("GROUP_PICTURE", group_pic_array);
                    }
                    startActivity(group_creation_4_intent);
                    finish();

                } else {
                    edit_group(group_id, days_num, shifts_per_day, employees_per_shift, starting_hour,
                            shift_len, group_name, group_pic_array);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSnackbar.show(GroupCreation3Activity.this, mLayout, getResources().getString(R.string.group_edit_success), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_LONG);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent groupsIManageFragment = new Intent(getApplicationContext(), GroupListsActivity.class);
                                    startActivity(groupsIManageFragment);
                                    finish();
                                }
                            }, Constants.REDIRECTION_DELAY);
                        }
                    }, Constants.REDIRECTION_DELAY);

                }

            }
        });

    }

    private void edit_group(final String group_id, Long days_num, Long shifts_per_day, Long employees_per_shift, String starting_time, Long shift_length, String group_name, byte[] group_pic_array) {
        // Group icon
        uploadPic(group_id, group_pic_array);

        // Group characteristics
        mGroupsRef.child(group_id).child("days_num").setValue(days_num);
        mGroupsRef.child(group_id).child("employees_per_shift").setValue(employees_per_shift);
        mGroupsRef.child(group_id).child("shift_length").setValue(shift_length);
        mGroupsRef.child(group_id).child("shifts_per_day").setValue(shifts_per_day);
        mGroupsRef.child(group_id).child("starting_time").setValue(starting_time);

        // Group name
        mGroupsRef.child(group_id).child("group_name").setValue(group_name);

    }

    private void uploadPic(final String group_id, byte[] group_pic_array) {
        mStorageRef = mStorage.getReference().child("group_pics/" + group_id + ".png");
        if (group_pic_array == null) {
            // No image upload - update image url to be "none"
            mGroupsRef.child(group_id).child("group_icon_url").setValue("none");

        } else {
            uploadTask = mStorageRef.putBytes(group_pic_array);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Image upload failed
                    mSnackbar.show(GroupCreation3Activity.this, mLayout, getResources().getString(R.string.edit_pic_error), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mGroupsRef.child(group_id).child("group_icon_url").setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }


}