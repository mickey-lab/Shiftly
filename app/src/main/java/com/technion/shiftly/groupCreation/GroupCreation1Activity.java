package com.technion.shiftly.groupCreation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.technion.shiftly.R;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

// The first activity of the group creation process.
// In this activity the future admin sets the group name.

public class GroupCreation1Activity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
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
        setContentView(R.layout.activity_group_creation_1);
        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar_1);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final EditText group_name_edittext = findViewById(R.id.group_name_edittext);
        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(GroupCreation1Activity.this, R.id.group_name_edittext, "^[a-zA-Z0-9\u0590-\u05fe][a-zA-Z0-9\u0590-\u05fe\\s]*$", R.string.err_groupname);

        Button createButton = findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    Intent group_creation_2_intent = new Intent(getApplicationContext(), GroupCreation2Activity.class);
                    String group_name = group_name_edittext.getText().toString();
                    group_creation_2_intent.putExtra("GROUP_NAME", group_name);
                    startActivity(group_creation_2_intent);
                }
            }
        });
    }
}