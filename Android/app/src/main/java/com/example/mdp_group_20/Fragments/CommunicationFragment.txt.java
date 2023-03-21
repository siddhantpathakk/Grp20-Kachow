package com.example.mdp_group_20.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdp_group_20.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.nio.charset.Charset;

import com.example.mdp_group_20.Models.PageViewModel;
import com.example.mdp_group_20.R;
import com.example.mdp_group_20.Settings.BluetoothServices;

/**
 * A placeholder fragment containing a simple view.
 */
public class CommunicationFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "CommunicationFragment";

    private PageViewModel pageViewModel;

    // Declaration Variable
    // Shared Preferences
    SharedPreferences sharedPreferences;

    FloatingActionButton send,clear;
    private static TextView MessageReceivedTV1;
    private EditText typeBoxEditText;

    TextView robotStatusTextView;



    public static CommunicationFragment newInstance(int index) {
        CommunicationFragment fragment = new CommunicationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        pageViewModel = ViewModelProvider.of(this).get(PageViewModel.class);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);

        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_communication, container, false);
        robotStatusTextView = MainActivity.getRobotStatusTextView();
        send = (FloatingActionButton) root.findViewById(R.id.messageButton);
        clear = (FloatingActionButton) root.findViewById(R.id.clearMessageButton);
        // Message Box
        MessageReceivedTV1 = (TextView) root.findViewById(R.id.messageReceivedTextView);
        MessageReceivedTV1.setMovementMethod(new ScrollingMovementMethod());
        typeBoxEditText = (EditText) root.findViewById(R.id.typeBoxEditText);

        // get shared preferences
        sharedPreferences = getActivity().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        MessageReceivedTV1.setText(sharedPreferences.getString("message", ""));


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked sendTextBtn");
                String sentText = "" + typeBoxEditText.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("message", sharedPreferences.getString("message", "") + '\n' + sentText);
                editor.commit();
                MessageReceivedTV1.setText(sharedPreferences.getString("message", ""));
                typeBoxEditText.setText("");

                if (BluetoothServices.BluetoothConnectionStatus == true) {
                    byte[] bytes = sentText.getBytes(Charset.defaultCharset());
                    robotStatusTextView.setText("Connected");
                    BluetoothServices.write(bytes);
                }
                else{
                    robotStatusTextView.setText("Not Connected");
                }
                showLog("Exiting sendTextBtn");
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("message", "");
                editor.commit();
                MessageReceivedTV1.setText(sharedPreferences.getString("message", ""));
            }
        });

        return root;
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    public static TextView getMessageReceivedTV() {
        return MessageReceivedTV1;
    }
}