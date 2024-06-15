package com.example.eeleapp.room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eeleapp.R;
import com.example.eeleapp.Server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class EditRoomActivity extends AppCompatActivity {

    private String roomId;

    private TextView textViewRoomName;
    private Button buttonAddPlug;
    private Button buttonAddCamera;

    private RecyclerView recyclerViewElectronics;
    private ElectronicRecyclerViewAdapter electronicRecyclerViewAdapter;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.buttonAddCamera) {
                EditRoomActivity.this.tampilInputTambahCamera();
            }
        }
    };

    private View.OnClickListener electronicListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.buttonActionElectronic) {
                ElectronicRecyclerViewAdapter.ElectronicData electronicData = (ElectronicRecyclerViewAdapter.ElectronicData) v.getTag();
                if (electronicData.getType().compareTo("PLUG") == 0) {
                    if (((Button)v).getText().toString().compareTo(getResources().getString(R.string.turn_on)) == 0) {
                        EditRoomActivity.this.turnPlug(electronicData.getId(), true);
                    }
                    else {
                        EditRoomActivity.this.turnPlug(electronicData.getId(), false);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extra = this.getIntent().getExtras();
        assert extra != null;

        this.roomId = extra.getString("id");

        this.textViewRoomName = this.findViewById(R.id.textViewRoomName);
        this.buttonAddPlug = this.findViewById(R.id.buttonAddPlug);
        this.buttonAddCamera = this.findViewById(R.id.buttonAddCamera);
        this.recyclerViewElectronics = this.findViewById(R.id.recyclerViewElectronics);

        this.buttonAddPlug.setOnClickListener(this.listener);
        this.buttonAddCamera.setOnClickListener(this.listener);

        this.electronicRecyclerViewAdapter = new ElectronicRecyclerViewAdapter(this.electronicListListener);
        this.recyclerViewElectronics.setAdapter(this.electronicRecyclerViewAdapter);
        this.recyclerViewElectronics.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        this.connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                while (true) {
                    HttpURLConnection connection;
                    BufferedReader reader = null;
                    StringBuilder response = new StringBuilder();

                    try {
                        URL url = new URL(Server.SERVER_URL + "/getroominfo");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.setDoOutput(true);

                        String requestBody = String.format("{\"id\": \"%s\"}", EditRoomActivity.this.roomId);

                        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                            outputStream.writeBytes(requestBody);
                            outputStream.flush();
                        }

                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            this.runOnUiThread(() -> {
                                Toast.makeText(this, "Failed fetching data from server", Toast.LENGTH_SHORT).show();
                            });
                            throw new Exception();
                        }

                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        final ArrayList<ElectronicRecyclerViewAdapter.ElectronicData> electronicDataArrayList = new ArrayList<>();

                        JSONObject responseObject = new JSONObject(response.toString());
                        String name = responseObject.getString("name");
                        JSONArray camera = responseObject.getJSONArray("camera");

                        for (int i = 0; i < camera.length(); i++) {
                            JSONObject cameraObject = camera.getJSONObject(i);
                            String cameraName = cameraObject.getString("name");
                            String info = cameraObject.getString("info");

                            electronicDataArrayList.add(new ElectronicRecyclerViewAdapter.ElectronicData(cameraName, "CAMERA", info));
                        }

                        JSONArray plug = responseObject.getJSONArray("plug");
                        for (int i = 0; i < plug.length(); i++) {
                            JSONObject plugObject = plug.getJSONObject(i);
                            String cameraName = plugObject.getString("name");
                            String info = plugObject.getString("info");

                            electronicDataArrayList.add(new ElectronicRecyclerViewAdapter.ElectronicData(cameraName, "PLUG", info));
                        }

                        this.runOnUiThread(() -> {
                            EditRoomActivity.this.textViewRoomName.setText(name);
                            EditRoomActivity.this.electronicRecyclerViewAdapter.setElectronicDataArrayList(electronicDataArrayList);
                        });
                    }
                    catch (Exception e) {
                        this.runOnUiThread(() -> {
                            Toast.makeText(this, "Failed fetching data from server", Toast.LENGTH_SHORT).show();
                        });
                    }
                    finally {
                        try {
                            Objects.requireNonNull(reader).close();
                        }
                        catch (Exception ignored) {}
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void tampilInputTambahCamera() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View viewDialog = layoutInflater.inflate(R.layout.dialog_add_new_camera, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(viewDialog);

        EditText editTextCameraAddress = (EditText) viewDialog.findViewById(R.id.editTextCameraAddress);

        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cameraAddress = editTextCameraAddress.getText().toString();
                if (which == -1 && !cameraAddress.isEmpty()) {
                    EditRoomActivity.this.addNewCamera(cameraAddress);
                }
            }
        };

        dialog
                .setCancelable(false)
                .setPositiveButton(R.string.save, dialogListener)
                .setNegativeButton(R.string.cancel, dialogListener);

        dialog.show();
    }

    private void addNewCamera(String address) {
        new Thread(() -> {

            HttpURLConnection connection;
            BufferedReader reader = null;

            try {
                URL url = new URL(Server.SERVER_URL + "/addnewcamera");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String requestBody = String.format("{\"room_id\": \"%s\", \"camera_address\": \"%s\"}", this.roomId, address);

                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes(requestBody);
                    outputStream.flush();
                }

                connection.getResponseCode();
            }
            catch (Exception ignored) {}
            finally {
                try {
                    reader.close();
                }catch (Exception ignored) {}
            }
        }).start();
    }

    private void turnPlug(String deviceId, boolean turnOn) {
        new Thread(() -> {

            HttpURLConnection connection;
            BufferedReader reader = null;

            try {
                URL url = new URL(Server.SERVER_URL + "/turnplug");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String requestBody = String.format("{\"plug_id\": \"%s\", \"turn_on\": %b}", deviceId, turnOn);

                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.writeBytes(requestBody);
                    outputStream.flush();
                }

                connection.getResponseCode();
            }
            catch (Exception ignored) {}
            finally {
                try {
                    reader.close();
                }catch (Exception ignored) {}
            }
        }).start();
    }
}