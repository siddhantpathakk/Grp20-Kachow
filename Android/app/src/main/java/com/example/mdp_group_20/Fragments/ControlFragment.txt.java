package com.example.mdp_group_20.Fragments;

import static android.content.Context.SENSOR_SERVICE;
import android.os.CountDownTimer;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdp_group_20.MainActivity;
import com.example.mdp_group_20.Models.PageViewModel;
import com.example.mdp_group_20.R;
import com.example.mdp_group_20.Ui.GridMap;

import java.util.ArrayList;

public class ControlFragment extends Fragment implements SensorEventListener {
    // Init
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "ControlFragment";
    private PageViewModel pageViewModel;

    // Declaration Variable
    // Shared Preferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // Control Button
    ImageButton moveForwardImageBtn, turnForwardRightImageBtn, turnBackwardRightImageBtn, moveBackImageBtn, turnForwardLeftImageBtn, turnBackwardLeftImageBtn, exploreResetButton, fastestResetButton;
    private static long exploreTimer, fastestTimer;
    ToggleButton exploreButton, fastestButton;
    TextView exploreTimeTextView, fastestTimeTextView, robotStatusTextView;
    Switch phoneTiltSwitch;
    static Button calibrateButton;
    private static GridMap gridMap;
//    static Button StopRobot;
//    static Button StraightenWheel;

    public static String frontOrBackDirection = "none";
    ToggleButton task1;
    Button task2;

    private Sensor mSensor;
    private SensorManager mSensorManager;


    // Timer
    public Handler timerHandler = new Handler();


    public Runnable timerRunnableExplore = new Runnable() {
        @Override
        public void run() {
            long millisExplore = System.currentTimeMillis() - exploreTimer;
            int secondsExplore = (int) (millisExplore / 1000);
            int minutesExplore = secondsExplore / 60;
            secondsExplore = secondsExplore % 60;

            exploreTimeTextView.setText(String.format("%02d:%02d", minutesExplore, secondsExplore));
            int obstacleLatestCountDown = gridMap.updateobstacleCountDown();

            showLog("inside timerRUnnableExplore " + obstacleLatestCountDown);
            timerHandler.postDelayed(this, 500);

            if(obstacleLatestCountDown == 0)
            {

                timerHandler.removeCallbacks(timerRunnableExplore);
                task1.toggle();
            }
        }
    };

    Runnable timerRunnableFastest = new Runnable() {
        @Override
        public void run() {
            long millisFastest = System.currentTimeMillis() - fastestTimer;
            int secondsFastest = (int) (millisFastest / 1000);
            int minutesFastest = secondsFastest / 60;
            secondsFastest = secondsFastest % 60;

            fastestTimeTextView.setText(String.format("%02d:%02d", minutesFastest, secondsFastest));

            timerHandler.postDelayed(this, 500);
        }
    };

    // Fragment Constructor
    public static ControlFragment newInstance(int index) {
        ControlFragment fragment = new ControlFragment();
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
        // inflate
        View root = inflater.inflate(R.layout.fragment_control, container, false);

        // get shared preferences
        sharedPreferences = getActivity().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);


        // variable initialization
//        StopRobot = root.findViewById(R.id.stopRobot);
//        StraightenWheel = root.findViewById(R.id.straightenWheel);
        moveForwardImageBtn = root.findViewById(R.id.forwardImageBtn);
        turnForwardRightImageBtn = root.findViewById(R.id.rightforwardImageBtn);
        turnBackwardRightImageBtn = root.findViewById(R.id.rightbackwardImageBtn);
        moveBackImageBtn = root.findViewById(R.id.backImageBtn);
        turnForwardLeftImageBtn = root.findViewById(R.id.leftforwardImageBtn);
        turnBackwardLeftImageBtn = root.findViewById(R.id.leftbackwardImageBtn);
        exploreTimeTextView = root.findViewById(R.id.exploreTimeTextView);
        fastestTimeTextView = root.findViewById(R.id.fastestTimeTextView);
        exploreButton = root.findViewById(R.id.exploreToggleBtn);
        fastestButton = root.findViewById(R.id.fastestToggleBtn);
        exploreResetButton = root.findViewById(R.id.exploreResetImageBtn);
        fastestResetButton = root.findViewById(R.id.fastestResetImageBtn);
        phoneTiltSwitch = root.findViewById(R.id.phoneTiltSwitch);
        calibrateButton = root.findViewById(R.id.calibrateButton);
        task1 = root.findViewById(R.id.task1);
        task2 = root.findViewById(R.id.task2);

        robotStatusTextView = MainActivity.getRobotStatusTextView();
        fastestTimer = 0;
        exploreTimer = 0;

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gridMap = MainActivity.getGridMap();

//        StopRobot.setOnClickListener(view -> MainActivity.printMessage("stop"));
//        StraightenWheel.setOnClickListener(view -> MainActivity.printMessage("straighten"));

        task1.setOnClickListener(view -> {
//            MainActivity.printMessage(gridMap.sendAlgoObstacleCoords());
//            updateStatus("Initiating Task 1");

            if (task1.getText().equals("Task 1: Image Recognition")) {
                showLog("Exploration timer stop!");
                robotStatusTextView.setText("Exploration Stopped");
                timerHandler.removeCallbacks(timerRunnableExplore);
            } else if (task1.getText().equals("Exploring")) {
                showLog("Exploration timer start!");
                sendObstacleDetail();
                robotStatusTextView.setText("Exploration Started");
                exploreTimer = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnableExplore, 0);
            } else {
                showLog("Else statement: " + task1.getText());
            }
            showLog("Exiting exploreToggleBtn");
        });

        task2.setOnClickListener(view -> {
            MainActivity.printMessage("START");
            updateStatus("Initiating Task 2");
        });

        // Button Listener
        moveForwardImageBtn.setOnClickListener(view -> {
            showLog("Clicked moveForwardImageBtn");
            if (gridMap.getAutoUpdate())
                updateStatus("Please press 'MANUAL'");
            else if (gridMap.getCanDrawRobot() && !gridMap.getAutoUpdate()) {
                gridMap.moveRobot("forward");
                MainActivity.refreshLabel();
                if (gridMap.getValidPosition())
                    updateStatus("moving forward");
                else
                    updateStatus("Unable to move forward");
                MainActivity.printMessage("STM|FW010");
            } else
                updateStatus("Please press 'STARTING POINT'");
            showLog("Exiting moveForwardImageBtn");
        });

        turnForwardRightImageBtn.setOnClickListener(view -> {
            showLog("Clicked turnForwardRightImageBtn");
            if (gridMap.getAutoUpdate())
                updateStatus("Please press 'MANUAL'");
            else if (gridMap.getCanDrawRobot() && !gridMap.getAutoUpdate()) {
                frontOrBackDirection = "forward";
                gridMap.moveRobot("right");
                MainActivity.refreshLabel();
//                updateStatus("turning forward right");
                if (gridMap.getValidPosition())
                    updateStatus("Turning forward right");
                else
                    updateStatus("Unable to turn forward right");
                MainActivity.printMessage("STM|FR090"); //In ASCII - eZ
            } else
                updateStatus("Please press 'STARTING POINT'");
            showLog("Exiting turnBackwardRightImageBtn");
        });

        turnBackwardRightImageBtn.setOnClickListener(view -> {
            showLog("Clicked turnBackwardRightImageBtn");
            if (gridMap.getAutoUpdate())
                updateStatus("Please press 'MANUAL'");
            else if (gridMap.getCanDrawRobot() && !gridMap.getAutoUpdate()) {
                frontOrBackDirection = "backward";
                gridMap.moveRobot("right");
                MainActivity.refreshLabel();
//                updateStatus("turning backward right");
                if (gridMap.getValidPosition())
                    updateStatus("Turning backward right");
                else
                    updateStatus("Unable to turn backward right");
                MainActivity.printMessage("STM|BR090"); //In ASCII - dZ
            } else
                updateStatus("Please press 'STARTING POINT'");
            showLog("Exiting turnBackwardRightImageBtn");
        });

        moveBackImageBtn.setOnClickListener(view -> {
            showLog("Clicked moveBackwardImageBtn");
            if (gridMap.getAutoUpdate())
                updateStatus("Please press 'MANUAL'");
            else if (gridMap.getCanDrawRobot() && !gridMap.getAutoUpdate()) {
                gridMap.moveRobot("back");
                MainActivity.refreshLabel();
                if (gridMap.getValidPosition())
                    updateStatus("moving backward");
                else
                    updateStatus("Unable to move backward");
                MainActivity.printMessage("STM|BW010");
            } else
                updateStatus("Please press 'STARTING POINT'");
            showLog("Exiting moveBackwardImageBtn");
        });

        turnForwardLeftImageBtn.setOnClickListener(view -> {
            showLog("Clicked turnForwardLeftImageBtn");
            if (gridMap.getAutoUpdate())
                updateStatus("Please press 'MANUAL'");
            else if (gridMap.getCanDrawRobot() && !gridMap.getAutoUpdate()) {
                frontOrBackDirection = "forward";
                gridMap.moveRobot("left");
                MainActivity.refreshLabel();
//                updateStatus("turning forward left");
                if (gridMap.getValidPosition())
                    updateStatus("Turning forward left");
                else
                    updateStatus("Unable to turn forward left");
                MainActivity.printMessage("STM|FL090"); //In ASCII - qZ
            } else
                updateStatus("Please press 'STARTING POINT'");
            showLog("Exiting turnForwardLeftImageBtn");
        });

        turnBackwardLeftImageBtn.setOnClickListener(view -> {
            showLog("Clicked turnBackwardLeftImageBtn");
            if (gridMap.getAutoUpdate())
                updateStatus("Please press 'MANUAL'");
            else if (gridMap.getCanDrawRobot() && !gridMap.getAutoUpdate()) {
                frontOrBackDirection = "backward";
                gridMap.moveRobot("left");
                MainActivity.refreshLabel();
//                updateStatus("turning left");
                if (gridMap.getValidPosition())
                    updateStatus("Turning backward left");
                else
                    updateStatus("Unable to turn backwards left");
                MainActivity.printMessage("STM|BL090"); //In ASCII - aZ
            } else
                updateStatus("Please press 'STARTING POINT'");
            showLog("Exiting turnBackwardLeftImageBtn");
        });

        exploreButton.setOnClickListener(v -> {
            showLog("Clicked exploreToggleBtn");
            ToggleButton exploreToggleBtn = (ToggleButton) v;
            if (exploreToggleBtn.getText().equals("EXPLORE")) {
                showToast("Exploration timer stop!");
                robotStatusTextView.setText("Exploration Stopped");
                timerHandler.removeCallbacks(timerRunnableExplore);
            } else if (exploreToggleBtn.getText().equals("STOP")) {
                showToast("Exploration timer start!");
                MainActivity.printMessage("ES|");
                robotStatusTextView.setText("Exploration Started");
                exploreTimer = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnableExplore, 0);
            } else {
                showToast("Else statement: " + exploreToggleBtn.getText());
            }
            showLog("Exiting exploreToggleBtn");
        });

        fastestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked fastestToggleBtn");
                ToggleButton fastestToggleBtn = (ToggleButton) v;
                if (fastestToggleBtn.getText().equals("FASTEST")) {
                    showToast("Fastest timer stop!");
                    robotStatusTextView.setText("Fastest Path Stopped");
                    timerHandler.removeCallbacks(timerRunnableFastest);
                } else if (fastestToggleBtn.getText().equals("STOP")) {
                    showToast("Fastest timer start!");
                    MainActivity.printMessage("FS|");
                    robotStatusTextView.setText("Fastest Path Started");
                    fastestTimer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnableFastest, 0);
                } else
                    showToast(fastestToggleBtn.getText().toString());
                showLog("Exiting fastestToggleBtn");
            }
        });

        exploreResetButton.setOnClickListener(v -> {
            showLog("Clicked exploreResetImageBtn");
            showToast("Reseting exploration time...");
            exploreTimeTextView.setText("00:00");
            robotStatusTextView.setText("Not Available");
            if (exploreButton.isChecked())
                exploreButton.toggle();
            timerHandler.removeCallbacks(timerRunnableExplore);
            showLog("Exiting exploreResetImageBtn");
        });

        fastestResetButton.setOnClickListener(v -> {
            showLog("Clicked fastestResetImageBtn");
            showToast("Reseting fastest time...");
            fastestTimeTextView.setText("00:00");
            robotStatusTextView.setText("Not Available");
            if (fastestButton.isChecked())
                fastestButton.toggle();
            timerHandler.removeCallbacks(timerRunnableFastest);
            showLog("Exiting fastestResetImageBtn");
        });

        phoneTiltSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (gridMap.getAutoUpdate()) {
                updateStatus("Please press 'MANUAL'");
                phoneTiltSwitch.setChecked(false);
            } else if (gridMap.getCanDrawRobot() && !gridMap.getAutoUpdate()) {
                if (phoneTiltSwitch.isChecked()) {
                    showToast("Tilt motion control: ON");
                    phoneTiltSwitch.setPressed(true);

                    mSensorManager.registerListener(ControlFragment.this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);
                    sensorHandler.post(sensorDelay);
                } else {
                    showToast("Tilt motion control: OFF");
                    showLog("unregistering Sensor Listener");
                    try {
                        mSensorManager.unregisterListener(ControlFragment.this);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    sensorHandler.removeCallbacks(sensorDelay);
                }
            } else {
                updateStatus("Please press 'STARTING POINT'");
                phoneTiltSwitch.setChecked(false);
            }
            if (phoneTiltSwitch.isChecked()) {
                compoundButton.setText("TILT ON");
            } else {
                compoundButton.setText("TILT OFF");
            }
        });

        calibrateButton.setOnClickListener(v -> {
            showLog("Clicked Calibrate Button");
            MainActivity.printMessage("SS|");
            MapFragment.manualUpdateRequest = true;
            showLog("Exiting Calibrate Button");
        });

        return root;
    }
    private static void sendObstacleDetail()
    {
        showLog("clicked Send Obstacle Button");

        boolean success = false;
        String message = "ALGO|[";
        ArrayList<int[]> obstacleCoord = gridMap.getObstacleCoord();
        //obstacleCoord.get() [x][y]  /  [col][row]

        if (obstacleCoord.size() == 0) {
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
    }
    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    Handler sensorHandler = new Handler();
    boolean sensorFlag = false;

    private final Runnable sensorDelay = new Runnable() {
        @Override
        public void run() {
            sensorFlag = true;
            sensorHandler.postDelayed(this, 1000);
        }
    };
    public ToggleButton getTaskOneButton()
    {
        return task1;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        showLog("SensorChanged X: " + x);
        showLog("SensorChanged Y: " + y);
        showLog("SensorChanged Z: " + z);

        if (sensorFlag) {
            if (y < -2) {
                showLog("Sensor Move Forward Detected");
                gridMap.moveRobot("forward");
                MainActivity.refreshLabel();
                MainActivity.printMessage("W1|");
            } else if (y > 2) {
                showLog("Sensor Move Backward Detected");
                gridMap.moveRobot("back");
                MainActivity.refreshLabel();
                MainActivity.printMessage("S1|");
            } else if (x > 2) {
                showLog("Sensor Move Left Detected");
                gridMap.moveRobot("left");
                MainActivity.refreshLabel();
                MainActivity.printMessage("A|");
            } else if (x < -2) {
                showLog("Sensor Move Right Detected");
                gridMap.moveRobot("right");
                MainActivity.refreshLabel();
                MainActivity.printMessage("D|");
            }
        }
        sensorFlag = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mSensorManager.unregisterListener(ControlFragment.this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void stopTimer()
    {
        timerHandler.removeCallbacks(timerRunnableExplore);
    }

    private void updateStatus(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    public static Button getCalibrateButton() {
        return calibrateButton;
    }
}
