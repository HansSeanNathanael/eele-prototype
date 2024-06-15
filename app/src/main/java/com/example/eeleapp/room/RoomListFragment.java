package com.example.eeleapp.room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

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

public class RoomListFragment extends Fragment {

    private Button buttonAddRoom;
    private RecyclerView recyclerViewRoom;
    private RoomRecyclerViewAdapter roomRecyclerViewAdapter;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.buttonAddRoom) {
                RoomListFragment.this.tampilInput();
            }
        }
    };

    private View.OnClickListener roomRecyclerViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.buttonActionEdit && RoomListFragment.this.isAdded()) {
                String roomId = (String) v.getTag();
                Intent intent = new Intent(RoomListFragment.this.requireActivity(), EditRoomActivity.class);

                intent.putExtra("id", roomId);
                startActivity(intent);

            }
            else if (id == R.id.buttonActionActivation) {
                String roomId = (String) v.getTag();

                Log.d("TES", roomId);
                if (((Button)v).getText().toString().compareTo(getResources().getString(R.string.turn_on)) == 0) {
                    RoomListFragment.this.setTurn(roomId, true);
                }
                else {
                    RoomListFragment.this.setTurn(roomId, false);
                }

            }
        }
    };

    private CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String roomId = (String) buttonView.getTag();
            RoomListFragment.this.setAutomatic(roomId, isChecked);
        }
    };

    public RoomListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_room_list, container, false);

        this.buttonAddRoom = view.findViewById(R.id.buttonAddRoom);
        this.recyclerViewRoom = view.findViewById(R.id.recyclerViewRoom);

        this.buttonAddRoom.setOnClickListener(this.listener);

        this.roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(this.roomRecyclerViewListener, this.switchListener);
        this.recyclerViewRoom.setAdapter(this.roomRecyclerViewAdapter);
        this.recyclerViewRoom.setLayoutManager(new LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false));

        this.connectToServer();

        return view;
    }

    private void tampilInput() {
        if (this.isAdded()) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.requireActivity());
            View viewDialog = layoutInflater.inflate(R.layout.dialog_add_new_room, null);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this.requireActivity());
            dialog.setView(viewDialog);

            EditText editTextNewRoomName = (EditText) viewDialog.findViewById(R.id.editTextNewRoomName);

            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String roomName = editTextNewRoomName.getText().toString();
                    if (which == -1 && !roomName.isEmpty()) {
                        RoomListFragment.this.addNewRoom(roomName);
                    }
                }
            };

            dialog
                    .setCancelable(false)
                    .setPositiveButton(R.string.save, dialogListener)
                    .setNegativeButton(R.string.cancel, dialogListener);

            dialog.show();
        }
    }

    public void addNewRoom(String newRoomName) {
        new Thread(() -> {

            HttpURLConnection connection;
            BufferedReader reader = null;

            try {
                URL url = new URL(Server.SERVER_URL + "/addnewroom");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String requestBody = String.format("{\"name\": \"%s\"}", newRoomName);

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

    private void setAutomatic(String roomId, boolean automatic) {
        new Thread(() -> {

            HttpURLConnection connection;
            BufferedReader reader = null;

            try {
                URL url = new URL(Server.SERVER_URL + "/setautomatic");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String requestBody = String.format("{\"room_id\": \"%s\", \"automatic\": %b}", roomId, automatic);

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

    private void setTurn(String roomId, boolean turnOn) {
        new Thread(() -> {

            HttpURLConnection connection;
            BufferedReader reader = null;

            try {
                URL url = new URL(Server.SERVER_URL + "/turnroom");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String requestBody = String.format("{\"room_id\": \"%s\", \"turn_on\": %b}", roomId, turnOn);

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

    private void connectToServer() {
        new Thread(() -> {
            try {
                while (true) {
                    HttpURLConnection connection;
                    BufferedReader reader = null;
                    StringBuilder response = new StringBuilder();

                    if (RoomListFragment.this.isVisible() && RoomListFragment.this.isResumed()) {
                        try {
                            URL url = new URL(Server.SERVER_URL + "/roomlist");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");

                            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                if (RoomListFragment.this.isAdded()) {
                                    RoomListFragment.this.requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(RoomListFragment.this.requireContext(), "Failed fetching data from server", Toast.LENGTH_SHORT).show();
                                    });
                                }
                                throw new Exception();
                            }

                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                            final ArrayList<RoomRecyclerViewAdapter.RoomData> roomDataArrayList = new ArrayList<>();

                            JSONObject responseObject = new JSONObject(response.toString());
                            JSONArray room = responseObject.getJSONArray("room");
                            for (int i = 0; i < room.length(); i++) {
                                JSONObject roomObject = room.getJSONObject(i);
                                String id = roomObject.getString("id");
                                String name = roomObject.getString("name");
                                boolean automatic = roomObject.getBoolean("automatic");
                                String status = roomObject.getString("status");
                                boolean humanDetected = roomObject.getBoolean("human_detected");

                                roomDataArrayList.add(new RoomRecyclerViewAdapter.RoomData(id, name, status, humanDetected, automatic));
                            }


                            if (RoomListFragment.this.isAdded()) {
                                RoomListFragment.this.requireActivity().runOnUiThread(() -> {
                                    RoomListFragment.this.roomRecyclerViewAdapter.setRoomDataArrayList(roomDataArrayList);
                                });
                            }
                        }
                        catch (Exception e) {
                            if (RoomListFragment.this.isAdded()) {
                                RoomListFragment.this.requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(RoomListFragment.this.requireContext(), "Failed fetching data from server", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                        finally {
                            try {
                                Objects.requireNonNull(reader).close();
                            }
                            catch (Exception ignored) {}
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}