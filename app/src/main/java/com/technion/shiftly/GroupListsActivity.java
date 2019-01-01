package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GroupListsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInAccount mGoogleSignInAccount;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (currentUser == null) {
            if (mGoogleSignInAccount == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lists);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.group_list_toolbar);
        final DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mViewPager = (ViewPager) findViewById(R.id.groups);
        setupViewPager(mViewPager);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons(mPagerAdapter);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.drawer_about_button: {
                                gotoAbout();
                                break;
                            }
                            case R.id.drawer_signout_button: {
                                mAuth.signOut(); // Firebase Sign-out
                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(GroupListsActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                gotoLogin();
                                            }
                                        });
                                gotoLogin();
                                break;
                            }
                        }
                        // close drawer when item is tapped
                        // mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });
        int intentFragment = getIntent().getExtras().getInt("FRAGMENT_TO_LOAD");
        if (intentFragment == Constants.GROUPS_I_MANAGE_FRAGMENT) {
            mViewPager.setCurrentItem(Constants.GROUPS_I_MANAGE_FRAGMENT);
        }
    }

    public void gotoAbout() {
        Intent intent = new Intent(this, AboutActiviy.class);
        startActivity(intent);
    }

    public void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void setupTabIcons(ViewPagerAdapter pagerAdapter) {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        mPagerAdapter.addFragment(new GroupsIBelongFragment(), getString(R.string.groups_i_belong));
        mPagerAdapter.addFragment(new GroupsIManageFragment(), getString(R.string.groups_i_manage));
        viewPager.setAdapter(mPagerAdapter);
    }
}