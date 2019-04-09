package xyz.sjinglong.unitbot;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Locale;

public class NavFragment extends Fragment {
    private Button gameButton;
    private Button maintainButton;
    private Button languageButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav, container, false);

        gameButton = view.findViewById(R.id.nav_fragment_game_button);
        maintainButton = view.findViewById(R.id.nav_fragment_maintain_button);
        languageButton = view.findViewById(R.id.nav_fragment_language_button);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.functional_fragment_layout, new GameFragment());
                transaction.commit();
            }
        });

        maintainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.functional_fragment_layout, new MaintainFragment());
                transaction.commit();
            }
        });

        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources resources = getActivity().getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration config = resources.getConfiguration();

                if (config.locale == Locale.ENGLISH) {
                    config.locale = Locale.SIMPLIFIED_CHINESE;
                } else {
                    config.locale = Locale.ENGLISH;
                }
                resources.updateConfiguration(config, dm);
                getActivity().finish();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
