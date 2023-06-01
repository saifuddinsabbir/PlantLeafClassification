package com.example.plantleafclassification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Navbar extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    ExploreFragment exploreFragment = new ExploreFragment();
    CommunityFragment communityFragment = new CommunityFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);

        bottomNavigationView = findViewById(R.id.bottomNavbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeId:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
                        return true;

                    case R.id.communityId:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, communityFragment).commit();
                        return true;

                    case R.id.exploreId:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, exploreFragment).commit();
                        return true;

                    case R.id.profileId:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}