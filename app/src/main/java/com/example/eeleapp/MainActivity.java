package com.example.eeleapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eeleapp.room.RoomListFragment;

public class MainActivity extends AppCompatActivity {

    private ImageButton imageButtonHome;
    private ImageButton imageButtonRoom;
    private ImageButton imageButtonEnergy;
    private ImageButton imageButtonSettings;

    private FragmentManager fragmentManager;

    private View.OnClickListener navigationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.imageButtonHome) {
                MainActivity.this.setDisplayedFragment(new HomeFragment());
            }
            else if (id == R.id.imageButtonRoom) {
                MainActivity.this.setDisplayedFragment(new RoomListFragment());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.fragmentManager = getSupportFragmentManager();

        this.imageButtonHome = this.findViewById(R.id.imageButtonHome);
        this.imageButtonRoom = this.findViewById(R.id.imageButtonRoom);
        this.imageButtonEnergy = this.findViewById(R.id.imageButtonEnergy);
        this.imageButtonSettings = this.findViewById(R.id.imageButtonSettings);

        this.imageButtonHome.setOnClickListener(this.navigationListener);
        this.imageButtonRoom.setOnClickListener(this.navigationListener);
        this.imageButtonEnergy.setOnClickListener(this.navigationListener);
        this.imageButtonSettings.setOnClickListener(this.navigationListener);

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentMain, homeFragment, homeFragment.getClass().getName())
                .commit();
    }

    private void setDisplayedFragment(Fragment fragment) {
        String newFragmentName = fragment.getClass().getName();
        Fragment f = this.fragmentManager.findFragmentById(R.id.fragmentMain);

        if (f != null && f.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentMain, fragment, newFragmentName)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }
}