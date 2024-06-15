package com.example.eeleapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private TextView textViewPlugInfo;
    private TextView textViewCameraInfo;
    private boolean processPaused = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        this.textViewPlugInfo = view.findViewById(R.id.textViewPlugInfo);
        this.textViewCameraInfo = view.findViewById(R.id.textViewCameraInfo);

        this.connectToServer();

        return view;
    }

    public void connectToServer() {
        new Thread(() -> {
            try {
                while (true) {
                    HttpURLConnection connection;
                    BufferedReader reader = null;
                    StringBuilder response = new StringBuilder();

                    if (HomeFragment.this.isVisible() && HomeFragment.this.isResumed()) {
                        try {
                            URL url = new URL(Server.SERVER_URL + "/home");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");

                            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                if (HomeFragment.this.isAdded()) {
                                    HomeFragment.this.requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(HomeFragment.this.requireContext(), "Failed fetching data from server", Toast.LENGTH_SHORT).show();
                                    });
                                }
                                throw new Exception();
                            }

                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                            JSONObject responseObject = new JSONObject(response.toString());
                            JSONObject plug = responseObject.getJSONObject("plug");
                            JSONObject camera = responseObject.getJSONObject("camera");

                            int totalPlug = plug.getInt("total");
                            int connectedPlug = plug.getInt("connected");
                            int activePlug = plug.getInt("active");

                            int totalCamera = camera.getInt("total");
                            int connectedCamera = camera.getInt("connected");

                            if (HomeFragment.this.isAdded()) {
                                HomeFragment.this.requireActivity().runOnUiThread(() -> {
                                    this.textViewPlugInfo.setText(String.format("Connected %d/%d\nActive %d/%d", connectedPlug, totalPlug, activePlug, totalPlug));
                                    this.textViewCameraInfo.setText(String.format("Connected %d/%d", connectedCamera, totalCamera));
                                });
                            }
                        }
                        catch (Exception e) {
                            if (HomeFragment.this.isAdded()) {
                                HomeFragment.this.requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(HomeFragment.this.requireContext(), "Failed fetching data from server", Toast.LENGTH_SHORT).show();
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