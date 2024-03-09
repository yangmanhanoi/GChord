package tunermusic.metronome.chords;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import tunermusic.metronome.chords.fragments.InstrumentFragment;

public class InstrumentActivity extends AppCompatActivity {

    FragmentContainerView fragmentContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);

        fragmentContainerView = findViewById(R.id.fragment_container_view);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container_view,new InstrumentFragment()).commit();
    }
}