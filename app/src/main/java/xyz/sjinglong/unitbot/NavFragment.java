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
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.Locale;

import xyz.sjinglong.unitbot.utils.TTS;

public class NavFragment extends Fragment {

    private QMUIGroupListView mGroupListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav, container, false);

        mGroupListView = view.findViewById(R.id.groupListView);

        QMUICommonListItemView itemGame = mGroupListView.createItemView(getResources().getText(R.string.robot_string_game_text));
        itemGame.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemGame.setTag(1);

        QMUICommonListItemView itemMaintain = mGroupListView.createItemView(getResources().getText(R.string.robot_string_maintain_text));
        itemMaintain.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemMaintain.setTag(2);

        QMUICommonListItemView itemLanguage = mGroupListView.createItemView("中文/English");
        itemLanguage.setOrientation(QMUICommonListItemView.VERTICAL);
        itemLanguage.setTag(3);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    int tag = (int)((QMUICommonListItemView)v).getTag();
                    if (tag == 1) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        // transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
                        transaction.replace(R.id.functional_fragment_layout, new GameFragment());
 //                       transaction.addToBackStack(null);
                        transaction.commit();
                        MainActivity mainActivity = (MainActivity)getActivity();
                        mainActivity.closeDrawer();
                    } else if (tag == 2) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
                        // transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        transaction.replace(R.id.functional_fragment_layout, new MaintainFragment());
 //                       transaction.addToBackStack(null);
                        transaction.commit();
                        MainActivity mainActivity = (MainActivity)getActivity();
                        mainActivity.closeDrawer();
                    } else if (tag == 3) {
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
                }
            }
        };

        QMUIGroupListView.newSection(getContext())
                .setTitle("")
                .setDescription("")
                .addItemView(itemGame, onClickListener)
                .addItemView(itemMaintain, onClickListener)
                .addItemView(itemLanguage, onClickListener)
                .addTo(mGroupListView);

        return view;
    }
}
