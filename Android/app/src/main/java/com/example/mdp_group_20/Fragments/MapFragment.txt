package com.example.mdp_group_20.Fragments;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mdp_group_20.MainActivity;
import com.example.mdp_group_20.Models.PageViewModel;
import com.example.mdp_group_20.R;
import com.example.mdp_group_20.Ui.GridMap;

import java.util.ArrayList;
import java.util.Arrays;

public class MapFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "MapFragment";

    private PageViewModel pageViewModel;

    Button resetMapBtn, updateButton, directionChangeImageBtn, sendObstacleDetailBtn;

    Button prefixObstacle, saveObstacle, loadObstacle;
    ToggleButton exploredImageBtn, obstacleImageBtn, clearImageBtn,obstacleFaceTopBtn, obstacleFaceBtmBtn, obstacleFaceLeftBtn, obstacleFaceRightBtn;
//    ImageButton obstacleFaceTopBtn, obstacleFaceBtmBtn, obstacleFaceLeftBtn, obstacleFaceRightBtn;
    ToggleButton setStartPointToggleBtn, setWaypointToggleBtn;
    Switch manualAutoToggleBtn;
    GridMap gridMap;
    private static boolean autoUpdate = false;
    public static boolean manualUpdateRequest = false;

    public static MapFragment newInstance(int index) {
        MapFragment fragment = new MapFragment();
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
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        gridMap = MainActivity.getGridMap();
        final DirectionFragment directionFragment = new DirectionFragment();

        resetMapBtn = root.findViewById(R.id.resetMapBtn);
        setStartPointToggleBtn = root.findViewById(R.id.setStartPointToggleBtn);
        setWaypointToggleBtn = root.findViewById(R.id.setWaypointToggleBtn);
        directionChangeImageBtn = root.findViewById(R.id.directionChangeImageBtn);
        exploredImageBtn = root.findViewById(R.id.exploredImageBtn);
        obstacleImageBtn = root.findViewById(R.id.obstacleImageBtn);
        sendObstacleDetailBtn = root.findViewById(R.id.sendObstacleBtn);
        prefixObstacle = root.findViewById(R.id.prefixObstacle);
        saveObstacle = root.findViewById(R.id.savePrefixObstacle);
        loadObstacle = root.findViewById(R.id.loadObstacle);

        // Obstacle Face @ MapFragment.java onCreateView (Start)
        obstacleFaceTopBtn = root.findViewById(R.id.obstacleFaceTopBtn);
        obstacleFaceBtmBtn = root.findViewById(R.id.obstacleFaceBtmBtn);
        obstacleFaceLeftBtn = root.findViewById(R.id.obstacleFaceLeftBtn);
        obstacleFaceRightBtn = root.findViewById(R.id.obstacleFaceRightBtn);
        // End
        clearImageBtn = root.findViewById(R.id.clearImageBtn);
//        manualAutoToggleBtn = root.findViewById(R.id.manualAutoToggleBtn);
//        updateButton = root.findViewById(R.id.updateButton);
        resetMapBtn.setOnClickListener(view -> {
            showLog("Clicked resetMapBtn");
//            showToast("Reseting map...");
            gridMap.resetMap();
        });
        setStartPointToggleBtn.setOnClickListener(view -> {



            showLog("Clicked setStartPointToggleBtn");
            if (setStartPointToggleBtn.getText().equals("STARTING POINT"))
            {

            }
//                showToast("Cancelled selecting starting point");
            else if (setStartPointToggleBtn.getText().equals("CANCEL") && !gridMap.getAutoUpdate()) {
                //showToast("Please select starting point");
                gridMap.setStartCoordStatus(true);
                gridMap.toggleCheckedBtn("setStartPointToggleBtn");
            } else
            {

            }
//                showToast("Please select manual mode");
            showLog("Exiting setStartPointToggleBtn");
        });

        setWaypointToggleBtn.setOnClickListener(view -> {
            showLog("Clicked setWaypointToggleBtn");
            if (setWaypointToggleBtn.getText().equals("WAYPOINT"))
                showToast("Cancelled selecting waypoint");
            else if (setWaypointToggleBtn.getText().equals("CANCEL")) {
                showToast("Please select waypoint");
                gridMap.setWaypointStatus(true);
                gridMap.toggleCheckedBtn("setWaypointToggleBtn");
            } else
                showToast("Please select manual mode");
            showLog("Exiting setWaypointToggleBtn");
        });

        directionChangeImageBtn.setOnClickListener(view -> {


            showLog("Clicked directionChangeImageBtn");
            directionFragment.show(getActivity().getFragmentManager(), "Direction Fragment");
            showLog("Exiting directionChangeImageBtn");
        });

        exploredImageBtn.setOnClickListener(view -> {
            showLog("Clicked exploredImageBtn");
            if (!gridMap.getExploredStatus()) {
//                showToast("Please check cell");
                gridMap.setExploredStatus(true);
                gridMap.toggleCheckedBtn("exploredImageBtn");
            } else if (gridMap.getExploredStatus()) {
                gridMap.setExploredStatus(false);
            }

            showLog("Exiting exploredImageBtn");
        });

        obstacleImageBtn.setOnClickListener(view -> {
            showLog("Clicked obstacleImageBtn");
            if (!gridMap.getSetObstacleStatus()) {
//                showToast("Plot your obstacles");
                gridMap.setSetObstacleStatus(true);
                gridMap.toggleCheckedBtn("obstacleImageBtn");
            } else if (gridMap.getSetObstacleStatus())
                gridMap.setSetObstacleStatus(false);
            showLog("Exiting obstacleImageBtn");
        });
        // Obstacle Face @ GridMap.java start
        obstacleFaceTopBtn.setOnClickListener(view -> {
            showLog("Clicked obstacleFaceTopBtn");
            if (!gridMap.getObstacleFaceTopStatus()) {
//                showToast("Please set obstacle face Top");
                gridMap.setObstacleFaceTopStatus(true);
                gridMap.toggleCheckedBtn("obstacleFaceTopBtn");
            } else if (gridMap.getObstacleFaceTopStatus()) {
                gridMap.setObstacleFaceTopStatus(false);
                showToast("Exiting obstacleFaceTopBtn");
            }
            showLog("Exiting obstacleFaceTopBtn");
        });
        obstacleFaceBtmBtn.setOnClickListener(view -> {
            showLog("Clicked obstacleFaceBtmBtn");
            if (!gridMap.getObstacleFaceBtmStatus()) {
//                showToast("Please set obstacle face Bottom");
                gridMap.setObstacleFaceBtmStatus(true);
                gridMap.toggleCheckedBtn("obstacleFaceBtmBtn");
            } else if (gridMap.getObstacleFaceBtmStatus()) {
                gridMap.setObstacleFaceBtmStatus(false);
                showToast("Exiting obstacleFaceBtmBtn");
            }
            showLog("Exiting obstacleFaceBtmBtn");
        });
        obstacleFaceLeftBtn.setOnClickListener(view -> {
            showLog("Clicked obstacleFaceLeftBtn");
            if (!gridMap.getObstacleFaceLeftStatus()) {
//                showToast("Please set obstacle face Left");
                gridMap.setObstacleFaceLeftStatus(true);
                gridMap.toggleCheckedBtn("obstacleFaceLeftBtn");
            } else if (gridMap.getObstacleFaceLeftStatus()) {
                gridMap.setObstacleFaceLeftStatus(false);
                showToast("Exiting obstacleFaceLeftBtn");
            }
            showLog("Exiting obstacleFaceLeftBtn");
        });
        obstacleFaceRightBtn.setOnClickListener(view -> {
            showLog("Clicked obstacleFaceRightBtn");
            if (!gridMap.getObstacleFaceRightStatus()) {
                gridMap.setObstacleFaceRightStatus(true);
                gridMap.toggleCheckedBtn("obstacleFaceRightBtn");
//                showToast("Please set obstacle face Right");
            } else if (gridMap.getObstacleFaceRightStatus()) {
                gridMap.setObstacleFaceRightStatus(false);
                showToast("Exiting obstacleFaceRightBtn");
            }
            showLog("Exiting obstacleFaceRightBtn");
        });
        // End
        clearImageBtn.setOnClickListener(view -> {
            showLog("Clicked clearImageBtn");
            if (!gridMap.getUnSetCellStatus()) {
//                showToast("Please remove cells");
                gridMap.setUnSetCellStatus(true);
                gridMap.toggleCheckedBtn("clearImageBtn");
            } else if (gridMap.getUnSetCellStatus())
                gridMap.setUnSetCellStatus(false);
            showLog("Exiting clearImageBtn");
        });

        saveObstacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.sharedPreferences();
                ArrayList<int[]> obstacleCoord = gridMap.getObstacleCoord();

                if(obstacleCoord.size() == 0)
                {
                    showLog("Please set obstacle in the map");
                    return;
                }
                JSONArray jsonArray =  new JSONArray();

                for (int i = 0; i < obstacleCoord.size(); i++) {
                    int x = obstacleCoord.get(i)[0];
                    int y = obstacleCoord.get(i)[1];
                    showLog("save obstacle btn : x , y : " + "[" + x + "]" + "[" + y + "]");
                    String direction = GridMap.returnObstacleFacing(x, (20-y));
                    String message =  (obstacleCoord.get(i)[0]-1) + "," + (obstacleCoord.get(i)[1]-1) + "," + direction + "";
                    jsonArray.put(message);
                }
                SharedPreferences sharedPreferences = MainActivity.getSharedPreferences();
                sharedPreferences.edit().remove("obstacle").apply();
                MainActivity.getEditor().putString("obstacle", jsonArray.toString());
                showLog("Print JSON : ");
                showLog(jsonArray.toString());
                MainActivity.getEditor().commit();
            }
        });

        loadObstacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONArray jsonArray2 = new JSONArray(MainActivity.getSharedPreferences().getString("obstacle", "[]"));
                    for (int i = 0; i < jsonArray2.length(); i++) {

                        String[] obs = jsonArray2.getString(i).split(",");
                        showLog("SHOW OBS ARRAY " + Arrays.toString(obs));
                        int x = Integer.parseInt(obs[0]);
                        int y = Integer.parseInt(obs[1]);
                        String direction = obs[2].toString().trim();
                        showLog("length " + direction.toString().length());
                        gridMap.setObs(x,y,direction);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showLog("printing e " + e.getLocalizedMessage());
                }

                gridMap.invalidate();
            }
        });

//        manualAutoToggleBtn.setOnClickListener(view -> {
//            showLog("Clicked manualAutoToggleBtn");
//            if (manualAutoToggleBtn.getText().equals("MANUAL")) {
//                try {
//                    gridMap.setAutoUpdate(true);
//                    autoUpdate = true;
//                    gridMap.toggleCheckedBtn("None");
//                    updateButton.setClickable(false);
//                    updateButton.setTextColor(Color.GRAY);
//                    ControlFragment.getCalibrateButton().setClickable(false);
//                    ControlFragment.getCalibrateButton().setTextColor(Color.GRAY);
//                    manualAutoToggleBtn.setText("AUTO");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                showToast("AUTO mode");
//            } else if (manualAutoToggleBtn.getText().equals("AUTO")) {
//                try {
//                    gridMap.setAutoUpdate(false);
//                    autoUpdate = false;
//                    gridMap.toggleCheckedBtn("None");
//                    updateButton.setClickable(true);
//                    updateButton.setTextColor(Color.WHITE);
//                    ControlFragment.getCalibrateButton().setClickable(true);
//                    ControlFragment.getCalibrateButton().setTextColor(Color.BLACK);
//                    manualAutoToggleBtn.setText("MANUAL");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                showToast("MANUAL mode");
//            }
//            showLog("Exiting manualAutoToggleBtn");
//        });

//        updateButton.setOnClickListener(v -> {
//            showLog("Clicked updateButton");
////            MainActivity.printMessage("sendArena");
//            manualUpdateRequest = true;
//            showLog("Exiting updateButton");
//            try {
//                String message = "{\"map\":[{\"explored\": \"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0000000000000000000000\",\"length\":400,\"obstacle\":\"706180400080010001e000400000000000000000000000000000000008000000000000000000000000000000000000000000\"}]}";
//
//                gridMap.setReceivedJsonObject(new JSONObject(message));
//                gridMap.updateMapInformation();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });

        sendObstacleDetailBtn.setOnClickListener(view -> {
            showLog("clicked Send Obstacle Button");

            boolean success = false;
            String message = "ALGO|[";
            ArrayList<int[]> obstacleCoord = gridMap.getObstacleCoord();
            //obstacleCoord.get() [x][y]  /  [col][row]

            if(obstacleCoord.size() == 0)
            {
                showLog("Please set obstacle in the map");
                return;
            }


            for (int i = 0; i < obstacleCoord.size(); i++) {
                message += "[";
                int x = obstacleCoord.get(i)[0];
                int y = obstacleCoord.get(i)[1];
                int obstacleNo = gridMap.getObsObstacleNo(x,(20-y));
                showLog("send obstacle btn : x , y : " + "[" + x + "]" + "[" + y + "]");
                String direction = GridMap.returnObstacleFacing(x, (20-y));
                showLog("send obstacle btn : direction : " + direction);
                char d = 'X';
                switch (direction) {
                    case "LEFT":
                        d = 'W';
                        break;
                    case "RIGHT":
                        d = 'E';
                        break;
                    case "TOP":
                        d = 'N';
                        break;
                    case "BOTTOM":
                        d = 'S';
                        break;

                }
                message += "" + (obstacleCoord.get(i)[0]-1) + "," + (obstacleCoord.get(i)[1]-1) + ",'" + d + "'," + obstacleNo +"]";
                if (i != obstacleCoord.size() -1) {
                    message += ",";
                }
                else if (i == obstacleCoord.size() - 1){
                    success = true;
                }
            }
            message+= "]";
            showLog("Send obstacle message : " +success);
            Log.d(TAG,"printing message : " + message);
            if(success)
                MainActivity.printMessage(message);
        });

        prefixObstacle.setOnClickListener(new View.OnClickListener() {
            ArrayList<int[]> obstacleCoord = gridMap.getObstacleCoord();

            @Override
            public void onClick(View view) {
                gridMap.setObs(1,6,"RIGHT");
                gridMap.setObs(9,1,"LEFT");
                gridMap.setObs(13,2,"LEFT");
                gridMap.setObs(17,8,"BOTTOM");
                gridMap.setObs(12,10,"LEFT");
                gridMap.setObs(6,12,"BOTTOM");
            }
        });

        return root;
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
