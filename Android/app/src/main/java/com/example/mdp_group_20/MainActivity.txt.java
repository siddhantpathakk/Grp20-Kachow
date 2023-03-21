package com.example.mdp_group_20;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.mdp_group_20.Fragments.ControlFragment;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.UUID;

import com.example.mdp_group_20.Fragments.CommunicationFragment;
import com.example.mdp_group_20.Fragments.MapFragment;
import com.example.mdp_group_20.Fragments.ReconfigureFragment;
import com.example.mdp_group_20.Settings.BluetoothActivity;
import com.example.mdp_group_20.Settings.BluetoothServices;
import com.example.mdp_group_20.Settings.SectionsPagerAdapter;
import com.example.mdp_group_20.Ui.GridMap;
import com.example.mdp_group_20.Ui.MapInformation;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    // Declaration Variables
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;

    private static GridMap gridMap;
    static TextView xAxisTextView, yAxisTextView, directionAxisTextView;
    static TextView robotStatusTextView;
    static Button f1, f2;
    Button reconfigure;
    ReconfigureFragment reconfigureFragment = new ReconfigureFragment();

    BluetoothServices mBluetoothConnection;
    BluetoothDevice mBTDevice;
    private static UUID myUUID;
    ProgressDialog myDialog;

    private static final String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(9999);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("incomingMessage"));

//        BigInteger hexBigIntegerExplored = new BigInteger("fffe", 16);
//        String exploredString = hexBigIntegerExplored.toString(2);
//        showLog("MainActivity : exploredString :"  + expl oredString);


        // Set up sharedPreferences
        MainActivity.context = getApplicationContext();
        this.sharedPreferences();
        editor.putString("message", "");
        editor.putString("direction","None");
        editor.putString("connStatus", "Disconnected");
        editor.commit();


        Button printMDFStringButton = (Button) findViewById(R.id.printMDFString);
        printMDFStringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Explored : " + GridMap.getPublicMDFExploration();
                editor = sharedPreferences.edit();
                editor.putString("message", CommunicationFragment.getMessageReceivedTV().getText() + "\n" + message);
                editor.commit();
                refreshMessageReceived();
                message = "Obstacle : " + GridMap.getPublicMDFObstacle() + "0";
                editor.putString("message", CommunicationFragment.getMessageReceivedTV().getText() + "\n" + message);
                editor.commit();
                refreshMessageReceived();
            }
        });

        // Toolbar
        Button bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent popup = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(popup);
            }
        });
        Button mapInformationButton = (Button) findViewById(R.id.mapInfoButton);
        mapInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("mapJsonObject", String.valueOf(gridMap.getCreateJsonObject()));
                editor.commit();
                Intent popup = new Intent(MainActivity.this, MapInformation.class);
                startActivity(popup);
            }
        });


        // Map
        gridMap = new GridMap(this);
        gridMap = findViewById(R.id.mapView);
        xAxisTextView = findViewById(R.id.xAxisTextView);
        yAxisTextView = findViewById(R.id.yAxisTextView);
        directionAxisTextView = findViewById(R.id.directionAxisTextView);
        // Robot Status
        robotStatusTextView = findViewById(R.id.robotStatusTextView);

        myDialog = new ProgressDialog(MainActivity.this);
        myDialog.setMessage("Waiting for other device to reconnect...");
        myDialog.setCancelable(false);
        myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        f1 = (Button) findViewById(R.id.f1ActionButton);
        f2 = (Button) findViewById(R.id.f2ActionButton);
        reconfigure = (Button) findViewById(R.id.configureButton);

        if (sharedPreferences.contains("F1")) {
            f1.setContentDescription(sharedPreferences.getString("F1", ""));
            showLog("setText for f1Btn: " + f1.getContentDescription().toString());
        }
        if (sharedPreferences.contains("F2")) {
            f2.setContentDescription(sharedPreferences.getString("F2", ""));
            showLog("setText for f2Btn: " + f2.getContentDescription().toString());
        }

        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked f1Btn");
                if (!f1.getContentDescription().toString().equals("empty"))
                    MainActivity.printMessage(f1.getContentDescription().toString());
                showLog("f1Btn value: " + f1.getContentDescription().toString());
                showLog("Exiting f1Btn");
            }
        });

        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLog("Clicked f2Btn");
                if (!f2.getContentDescription().toString().equals("empty"))
                    MainActivity.printMessage(f2.getContentDescription().toString());
                showLog("f2Btn value: " + f2.getContentDescription().toString());
                showLog("Exiting f2Btn");
            }
        });

        reconfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Clicked reconfigureBtn");
                reconfigureFragment.show(getFragmentManager(), "Reconfigure Fragment");
                showLog("Exiting reconfigureBtn");
            }
        });


//        // Prefix Obstacle Coord
//        gridMap.setObs(15,10,"BOTTOM");
//        gridMap.setObs(8,9,"TOP");
//        gridMap.setObs(1,18,"RIGHT");
//        gridMap.setObs(15,10,"LEFT");
    }

    public static Button getF1() { return f1; }

    public static Button getF2() { return f2; }

    public static GridMap getGridMap() {
        return gridMap;
    }

    public static TextView getRobotStatusTextView() {  return robotStatusTextView; }

    public static void sharedPreferences() {
        sharedPreferences = MainActivity.getSharedPreferences(MainActivity.context);
        editor = sharedPreferences.edit();
    }

    // Send message to bluetooth
    public static void printMessage(String message) {
        showLog("Entering printMessage");
        editor = sharedPreferences.edit();

        if (BluetoothServices.BluetoothConnectionStatus == true) {
            byte[] bytes = message.getBytes(Charset.defaultCharset());
            BluetoothServices.write(bytes);
        }
        else {
        }
        showLog(message);
        editor.putString("message", CommunicationFragment.getMessageReceivedTV().getText() + "\n" + message);
        editor.commit();
        refreshMessageReceived();
        showLog("Exiting printMessage");
    }

    public static void printMessage(String name, int x, int y) throws JSONException {
        showLog("Entering printMessage");
        sharedPreferences();

        JSONObject jsonObject = new JSONObject();
        String message;

        switch(name) {
//            case "starting":
            case "waypoint":
                jsonObject.put(name, name);
                jsonObject.put("x", x);
                jsonObject.put("y", y);
                message = name + " (" + x + "," + y + ")";
                break;
            default:
                message = "Unexpected default for printMessage: " + name;
                break;
        }
        editor.putString("message", CommunicationFragment.getMessageReceivedTV().getText() + "\n" + message);
        editor.commit();
        if (BluetoothServices.BluetoothConnectionStatus == true) {
            byte[] bytes = message.getBytes(Charset.defaultCharset());
            BluetoothServices.write(bytes);
        }
        showLog("Exiting printMessage");
    }

    public static void refreshMessageReceived() {
        CommunicationFragment.getMessageReceivedTV().setText(sharedPreferences.getString("message", ""));
    }


    public void refreshDirection(String direction) {
        gridMap.setRobotDirection(direction);
        directionAxisTextView.setText(sharedPreferences.getString("direction",""));
        printMessage("Direction is set to " + direction);
    }

    public static void refreshLabel() {
        xAxisTextView.setText(String.valueOf(gridMap.getCurCoord()[0]-1));
        yAxisTextView.setText(String.valueOf(gridMap.getCurCoord()[1]-1));
        directionAxisTextView.setText(sharedPreferences.getString("direction",""));
    }

    public static void receiveMessage(String message) {
        showLog("Entering receiveMessage");
        sharedPreferences();
        editor.putString("message", sharedPreferences.getString("message", "") + "\n" + message);
        editor.commit();
        showLog("Exiting receiveMessage");
    }

    private static void showLog(String message) {
        Log.d(TAG, message);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
    }

    private BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice mDevice = intent.getParcelableExtra("Device");
            String status = intent.getStringExtra("Status");
            boolean isDisconnectClick = intent.getBooleanExtra("IsDisconnectButtonClicked",false);

            sharedPreferences();

            if(status.equals("connected")){
//                try {
//                    myDialog.dismiss();
//                } catch(NullPointerException e){
//                    e.printStackTrace();
//                }

                Log.d(TAG, "mBroadcastReceiver5: Device now connected to "+mDevice.getName());
                Toast.makeText(MainActivity.this, "Device now connected to "+mDevice.getName(), Toast.LENGTH_SHORT).show();
                editor.putString("connStatus", "Connected to " + mDevice.getName());
//                TextView connStatusTextView = findViewById(R.id.connStatusTextView);
//                connStatusTextView.setText("Connected to " + mDevice.getName());
            }
            else if(status.equals("disconnected") ){
                Log.d(TAG, "mBroadcastReceiver5: Disconnected from "+mDevice.getName());
                Toast.makeText(MainActivity.this, "Disconnected from "+mDevice.getName(), Toast.LENGTH_SHORT).show();
//                mBluetoothConnection = new BluetoothServices(MainActivity.this);
//                mBluetoothConnection.startAcceptThread();

                editor.putString("connStatus", "Disconnected");
//                TextView connStatusTextView = findViewById(R.id.connStatusTextView);
//                connStatusTextView.setText("Disconnected");
//                if(!isDisconnectClick)
//                {
//                    myDialog.show();
//                }
            }
            editor.commit();
        }
    };

    BroadcastReceiver messageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //change based on the noOfRetries set
            int noOfRetries = 4;
            int obstacleCountDown = gridMap.updateobstacleCountDown();
            int maxPossibleTries = obstacleCountDown * noOfRetries;
            int obstacleLatestCountDown = 0;

            String message = intent.getStringExtra("receivedMessage");
            showLog("receivedMessage: message --- " + message);

            if (message.equals("r,W")){
                robotStatusTextView.setText("Robot Moving Forward");
            }
            else if (message.equals("r,S")){
                robotStatusTextView.setText("Robot Reversing");
            }
            else if (message.equals("r,A")){
                robotStatusTextView.setText("Robot Turning Left");
            }
            else if (message.equals("r,D")){
                robotStatusTextView.setText("Robot Turning Right");
            }
            else if (message.equals("r,RR")){
                robotStatusTextView.setText("Robot Ready To Start");
            }
            else if (message.equals("ALGO,LT")){
                robotStatusTextView.setText("Robot Looking For Target");
            }

            // Try getting update image
            // First Case

            if(message.contains("OBS")){
                String[] messageComponent = message.split("-");

                if(messageComponent.length < 3)
                    return;

                int obstacleNo = Integer.parseInt(messageComponent[1].toString());
                int targetId = Integer.parseInt(messageComponent[2].toString());

                if(obstacleNo < 1 || obstacleNo > 8)
                {
                    Toast.makeText(MainActivity.this,"Invalid Obstacle No", Toast.LENGTH_SHORT).show();
                }
                else if (targetId<11 || targetId > 40)
                {

                    Toast.makeText(MainActivity.this,"Invalid Target ID", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Obstacle No " + obstacleNo + " detected as " + targetId, Toast.LENGTH_SHORT).show();
                    gridMap.updateImageNumberCell(obstacleNo,String.valueOf(targetId));
                    obstacleLatestCountDown = gridMap.updateLatestobstacleCountDown();
                    showLog("obstacleLatestCountDown : " + obstacleCountDown);

                    if(obstacleLatestCountDown == 0)
                    {
                        showLog("stop timer : ");
                    }
                }

            }

            if(message.contains("TARGET")){ // example String: “TARGET, <Obstacle Number>, <Target ID>”
                int startingIndex = message.indexOf("<");
                int endingIndex = message.indexOf(">");
                String obstacleNo = message.substring(startingIndex + 1, endingIndex);

                startingIndex = message.indexOf("<", endingIndex+1);
                endingIndex = message.indexOf(">", endingIndex+1);
                String targetID = message.substring(startingIndex+1, endingIndex);

                // to count the number of <
                char check = '<';
                int count = 0;

                for (int i = 0; i < message.length(); i++) {
                    if (message.charAt(i) == check) {
                        count++;
                    }
                }

                // if count is equal to 3 == second case
                int integerObstacleNo = Integer.parseInt(obstacleNo);
                int integerTargetID = Integer.parseInt(targetID);
                if(count == 3){
                    startingIndex = message.indexOf("<", endingIndex+1);
                    endingIndex = message.indexOf(">", endingIndex+1);
                    String obstacleFacing = message.substring(startingIndex+1, endingIndex);
                    if (integerObstacleNo<1 || integerObstacleNo>8){
                        Toast.makeText(MainActivity.this,  "Invalid Obstacle No", Toast.LENGTH_SHORT).show();
//                        printMessage("ALG|Invalid Obstacle No,Obstacle No must be between 1 & 8");
                    }
                    else if ((integerTargetID<11 || integerTargetID>40)){ // && integerTargetID != 100
                        Toast.makeText(MainActivity.this,  "Invalid Target ID", Toast.LENGTH_SHORT).show();
//                        printMessage("ALG|Invalid Target ID,Target ID must be between 11 & 40");
                    }
                    else {
//                        if (integerTargetID == 100){
//                            printMessage("Total number of obstacles currently are: " + obstacleCountDown);
//                            maxPossibleTries = maxPossibleTries - 1;
//                            printMessage("Total number of tries currently are: " + maxPossibleTries);
//                            if (maxPossibleTries == 0){
//                                printMessage("RPI|END");
//                            }
//                        }
//                        else{
                        Toast.makeText(MainActivity.this, "Obstacle No " + obstacleNo + " detected as " + targetID + " on direction " + obstacleFacing, Toast.LENGTH_SHORT).show();
                        gridMap.updateImageNumberCell(Integer.parseInt(obstacleNo), targetID, obstacleFacing);
                        obstacleLatestCountDown = gridMap.updateLatestobstacleCountDown();
//                        printMessage("Total number of obstacles currently are: " + obstacleLatestCountDown);
                        if (obstacleLatestCountDown == 0){
                            printMessage("RPI|END");
                        }
                        else{
                            maxPossibleTries = obstacleLatestCountDown * noOfRetries;
//                            printMessage("Total number of tries currently are: " + maxPossibleTries);
                        }
//                        }
                    }
                // if count is not equal 3 == first case
                } else {
                    if (integerObstacleNo<0 || integerObstacleNo>8){
                        Toast.makeText(MainActivity.this,  "Invalid Obstacle No", Toast.LENGTH_SHORT).show();
//                        printMessage("ALG|Invalid Obstacle No,Obstacle No must be between 1 & 8");
                    }
                    else if ((integerTargetID<11 || integerTargetID>40)){ //&& integerTargetID != 100
                        Toast.makeText(MainActivity.this,  "Invalid Target ID", Toast.LENGTH_SHORT).show();
//                        printMessage("ALG|Invalid Target ID,Target ID must be between 11 & 40");
                    }
                    else {
//                        if (integerTargetID == 100){
//                            printMessage("Total number of obstacles currently are: " + obstacleCountDown);
//                            maxPossibleTries = maxPossibleTries - 1;
//                            printMessage("Total number of tries currently are: " + maxPossibleTries);
//                            if (maxPossibleTries == 0){
//                                printMessage("RPI|END");
//                            }
//                        }
//                        else{
                        Toast.makeText(MainActivity.this, "Obstacle No " + obstacleNo + " detected as " + targetID, Toast.LENGTH_SHORT).show();
                        gridMap.updateImageNumberCell(Integer.parseInt(obstacleNo), targetID);
                        obstacleLatestCountDown = gridMap.updateLatestobstacleCountDown();
//                        printMessage("Total number of obstacles currently are: " + obstacleLatestCountDown);
                        if (obstacleLatestCountDown == 0){
                            printMessage("RPI|END");
                        }
                        else{
                            maxPossibleTries = obstacleLatestCountDown * noOfRetries;
//                            printMessage("Total number of tries currently are: " + maxPossibleTries);
                        }
//                        }
                    }
                }
            }
            // Case C.10
            if(message.contains("ROBOT")){
                int startingIndex = message.indexOf("<");
                int endingIndex = message.indexOf(">");
                String xCoord = message.substring(startingIndex + 1, endingIndex);
                int xCoordInt = Integer.parseInt(xCoord);

                startingIndex = message.indexOf("<", endingIndex+1);
                endingIndex = message.indexOf(">", endingIndex+1);
                String yCoord = message.substring(startingIndex+1, endingIndex);
                int yCoordInt = Integer.parseInt(yCoord);

                startingIndex = message.indexOf("<", endingIndex+1);
                endingIndex = message.indexOf(">", endingIndex+1);
                String direction = message.substring(startingIndex+1, endingIndex);

                // set directions from N S E W to up down left right
                if(direction.contains("N")){
                    direction="up";
                } else if(direction.contains("S")){
                    direction="down";
                } else if(direction.contains("E")){
                    direction="right";
                } else if(direction.contains("W")){
                    direction="left";
                } else{
                    direction="up";
                }

                if (xCoordInt > 20 && xCoordInt < 41){
                    xCoordInt = xCoordInt/2;
                    xCoord = String.valueOf(xCoordInt);
                }
                if (yCoordInt > 20 && yCoordInt < 41){
                    yCoordInt = yCoordInt/2;
                    yCoord = String.valueOf(yCoordInt);
                }

                if (GridMap.lookingForTarget != null){
                    for (int i=0; i<GridMap.lookingForTarget.size(); i+=2){
//                        if ((GridMap.lookingForTarget.get(i) == xCoordInt || GridMap.lookingForTarget.get(i) == xCoordInt - 1 || GridMap.lookingForTarget.get(i) == xCoordInt + 1 || GridMap.lookingForTarget.get(i) == xCoordInt - 2 || GridMap.lookingForTarget.get(i) == xCoordInt + 2) && (GridMap.lookingForTarget.get(i+1) == yCoordInt || GridMap.lookingForTarget.get(i+1) == yCoordInt - 1 || GridMap.lookingForTarget.get(i+1) == yCoordInt + 1 || GridMap.lookingForTarget.get(i+1) == yCoordInt - 2 || GridMap.lookingForTarget.get(i+1) == yCoordInt + 2)){
//                            printMessage("Robot crashing into obstacle");
//                            if(direction=="up"){
//                                yCoordInt = yCoordInt - 3;
//                            } else if(direction=="down"){
//                                yCoordInt = yCoordInt + 3;
//                            } else if(direction=="right"){
//                                xCoordInt = xCoordInt - 3;
//                            } else if(direction=="left"){
//                                xCoordInt = xCoordInt + 3;
//                            }
//                            yCoord = String.valueOf(yCoordInt);
//                            xCoord = String.valueOf(xCoordInt);
//                        }

                        if (GridMap.lookingForTarget.get(i) == xCoordInt && GridMap.lookingForTarget.get(i+1) == yCoordInt) { //checked
//                            printMessage("Case 1: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 2;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 2;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 2;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 2;
                            }
                        }
                        else if (GridMap.lookingForTarget.get(i) == xCoordInt-1 && GridMap.lookingForTarget.get(i+1) == yCoordInt){ //checked
//                            printMessage("Case 2: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 2;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 2;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 3;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 1;
                            }
                        }
                        else if (GridMap.lookingForTarget.get(i) == xCoordInt+1 && GridMap.lookingForTarget.get(i+1) == yCoordInt){ //checked
//                            printMessage("Case 3: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 2;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 2;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 1;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 3;
                            }
                        }

                        else if (GridMap.lookingForTarget.get(i) == xCoordInt && GridMap.lookingForTarget.get(i+1) == yCoordInt-1){ //checked
//                            printMessage("Case 4: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 3;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 1;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 2;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 2;
                            }
                        }
                        else if (GridMap.lookingForTarget.get(i) == xCoordInt-1 && GridMap.lookingForTarget.get(i+1) == yCoordInt-1){ //checked
//                            printMessage("Case 5: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 3;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 1;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 3;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 1;
                            }
                        }
                        else if (GridMap.lookingForTarget.get(i) == xCoordInt+1 && GridMap.lookingForTarget.get(i+1) == yCoordInt-1){//checked
//                            printMessage("Case 6: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 3;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 1;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 1;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 3;
                            }
                        }
                        else if (GridMap.lookingForTarget.get(i) == xCoordInt && GridMap.lookingForTarget.get(i+1) == yCoordInt+1){ //checked
//                            printMessage("Case 7: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 1;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 3;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 2;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 2;
                            }
                        }
                        else if (GridMap.lookingForTarget.get(i) == xCoordInt-1 && GridMap.lookingForTarget.get(i+1) == yCoordInt+1){//checked
//                            printMessage("Case 8: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 1;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 3;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 3;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 1;
                            }
                        }
                        else if (GridMap.lookingForTarget.get(i) == xCoordInt+1 && GridMap.lookingForTarget.get(i+1) == yCoordInt+1){ //checked
//                            printMessage("Case 9: Robot is Crashing into Obstacle: (" + xCoordInt + "," + yCoordInt + ")");
                            if(direction=="up"){
                                yCoordInt = yCoordInt - 1;
                            } else if(direction=="down"){
                                yCoordInt = yCoordInt + 3;
                            } else if(direction=="right"){
                                xCoordInt = xCoordInt - 1;
                            } else if(direction=="left"){
                                xCoordInt = xCoordInt + 3;
                            }
                        }

                        if (xCoordInt < 0 || xCoordInt > 18 || yCoordInt < 0 || yCoordInt > 19){
                                xCoordInt = 2;
                                yCoordInt = 2;
                        }

//                        printMessage("New Robot Coordinates are: (" + xCoordInt + "," + yCoordInt + ")");

                        yCoord = String.valueOf(yCoordInt);
                        xCoord = String.valueOf(xCoordInt);
                    }
                }


//                if (GridMap.lookingForTarget != null){
//                    for (int i=0; i<GridMap.lookingForTarget.size(); i+=2){
//                        if ((GridMap.lookingForTarget.get(i) == xCoordInt - 2 || GridMap.lookingForTarget.get(i) == xCoordInt + 2) || (GridMap.lookingForTarget.get(i+1) == yCoordInt + 2 || GridMap.lookingForTarget.get(i+1) == yCoordInt - 2)){
//                            robotStatusTextView.setText("Robot Looking For Target");
//                        }
//                    }
//                }

                // remove current robot
                // get current coordinate
                int[] curCoord = gridMap.getCurCoord(); // robot current coordinate this.setOldRobotCoord(curCoord[0], curCoord[1]);

                // conditions
                if(curCoord[0] != -1 && curCoord[1] != -1){
                    // set old coordinate to type unexplored
                    gridMap.unsetOldRobotCoord(curCoord[0],curCoord[1]);
                    // set new robot direction
                    gridMap.setCurCoord(Integer.parseInt(xCoord)+1,Integer.parseInt(yCoord)+1, direction);
                } else{
                    // Show Error Message or Alternatively allow draw robot w/o selecting robot start direction
                    // ToDo: show error message or allows putting the robot w/o setting start point
                    Toast.makeText(MainActivity.this, "Please set start point of the robot first", Toast.LENGTH_SHORT).show();
                }
            }

            try {
                if (message.length() > 7 && message.substring(2,6).equals("grid")) {
                    String resultString = "";
                    String amdString = message.substring(11,message.length()-2);
                    showLog("amdString: " + amdString);
                    BigInteger hexBigIntegerExplored = new BigInteger(amdString, 16);
                    String exploredString = hexBigIntegerExplored.toString(2);

                    while (exploredString.length() < 400)
                        exploredString = "0" + exploredString;

                    for (int i=0; i<exploredString.length(); i=i+20) {
                        int j=0;
                        String subString = "";
                        while (j<20) {
                            subString = subString + exploredString.charAt(j+i);
                            j++;
                        }
                        resultString = subString + resultString;
                    }
                    hexBigIntegerExplored = new BigInteger(resultString, 2);
                    resultString = hexBigIntegerExplored.toString(16);

                    JSONObject amdObject = new JSONObject();
                    amdObject.put("explored", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                    amdObject.put("length", amdString.length()*4);
                    amdObject.put("obstacle", resultString);
                    JSONArray amdArray = new JSONArray();
                    amdArray.put(amdObject);
                    JSONObject amdMessage = new JSONObject();
                    amdMessage.put("map", amdArray);
                    message = String.valueOf(amdMessage);
                    showLog("Executed for AMD message, message: " + message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (message.length() > 8 && message.substring(2,7).equals("image")) {
                    JSONObject jsonObject = new JSONObject(message);
                    JSONArray jsonArray = jsonObject.getJSONArray("image");
                    gridMap.drawImageNumberCell(jsonArray.getInt(0),jsonArray.getInt(1),jsonArray.getInt(2));
                    showLog("Image Added for index: " + jsonArray.getInt(0) + "," +jsonArray.getInt(1));
                }
            } catch (JSONException e) {
                showLog("Adding Image Failed");
            }

            if (gridMap.getAutoUpdate() || MapFragment.manualUpdateRequest) {
                try {
                    gridMap.setReceivedJsonObject(new JSONObject(message));
                    gridMap.updateMapInformation();
                    MapFragment.manualUpdateRequest = false;
                    showLog("messageReceiver: try decode successful");
                } catch (JSONException e) {
                    showLog("messageReceiver: try decode unsuccessful");
                }
            }
            sharedPreferences();
            String receivedText = sharedPreferences.getString("message", "") + "\n" + message;
            editor.putString("message", receivedText);
            editor.commit();
            refreshMessageReceived();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    mBTDevice = (BluetoothDevice) data.getExtras().getParcelable("mBTDevice");
                    myUUID = (UUID) data.getSerializableExtra("myUUID");
                }
        }
    }

    public static SharedPreferences.Editor getEditor()
    {
        return editor;
    }

    public static SharedPreferences getSharedPreferences()
    {
        return sharedPreferences;
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver5);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        try{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver5);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        if (BluetoothServices.BluetoothConnectionStatus == true) {
            robotStatusTextView.setText("Connected");
        }
        else {
            robotStatusTextView.setText("Not Connected");
        }
    }

    @Override
    protected void onResume(){
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                if (BluetoothServices.BluetoothConnectionStatus == true) {
                    robotStatusTextView.setText("Connected");
                }
                else {
                    robotStatusTextView.setText("Not Connected");
                }
            }
        }, delay);
        super.onResume();
        try{
            IntentFilter filter2 = new IntentFilter("ConnectionStatus");
            LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver5, filter2);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        showLog("Entering onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putString(TAG, "onSaveInstanceState");
        showLog("Exiting onSaveInstanceState");
    }
}