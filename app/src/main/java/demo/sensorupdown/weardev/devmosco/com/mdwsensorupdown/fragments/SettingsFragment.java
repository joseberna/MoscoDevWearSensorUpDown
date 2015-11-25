
package demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.R;
import demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.activity.MainActivity;


/**
 * Created by joseberna on 24/11/15.
 */
public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_layout, container, false);
        Button button = (Button) view.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).resetCounter();
            }
        });
        return view;
    }

}
