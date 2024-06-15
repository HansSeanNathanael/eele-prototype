package com.example.eeleapp.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eeleapp.R;

import java.util.ArrayList;

public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.RoomViewHolder> {

    private final ArrayList<RoomData> roomDataArrayList = new ArrayList<>();
    private View.OnClickListener listener;
    private CompoundButton.OnCheckedChangeListener switchListener;

    public RoomRecyclerViewAdapter(View.OnClickListener listener, CompoundButton.OnCheckedChangeListener switchListener) {
        this.listener = listener;
        this.switchListener = switchListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RoomViewHolder roomViewHolder = new RoomViewHolder(layoutInflater.inflate(R.layout.room_list_item, parent, false), this.listener, this.switchListener);
        return roomViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        synchronized (this.roomDataArrayList) {
            if (position < this.roomDataArrayList.size()) {
                holder.bind(this.roomDataArrayList.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.roomDataArrayList.size();
    }

    public void setRoomDataArrayList(ArrayList<RoomData> roomDataArrayList) {
        synchronized (this.roomDataArrayList) {
            this.roomDataArrayList.clear();
            this.roomDataArrayList.addAll(roomDataArrayList);
            this.notifyDataSetChanged();
        }
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewRoomName;
        private TextView textViewRoomInfo;

        private Button buttonActionActivation;
        private Button buttonActionEdit;
        private SwitchCompat switchAutomatic;

        public RoomViewHolder(@NonNull View itemView, View.OnClickListener listener, CompoundButton.OnCheckedChangeListener switchListener) {
            super(itemView);

            this.textViewRoomName = itemView.findViewById(R.id.textViewRoomName);
            this.textViewRoomInfo = itemView.findViewById(R.id.textViewRoomInfo);
            this.buttonActionActivation = itemView.findViewById(R.id.buttonActionActivation);
            this.buttonActionEdit = itemView.findViewById(R.id.buttonActionEdit);
            this.switchAutomatic = itemView.findViewById(R.id.switchAutomatic);

            this.buttonActionActivation.setOnClickListener(listener);
            this.buttonActionEdit.setOnClickListener(listener);
            this.switchAutomatic.setOnCheckedChangeListener(switchListener);
        }

        public void bind(RoomData roomData) {
            this.textViewRoomName.setText(roomData.name);
            this.textViewRoomInfo.setText(
                    String.format("Status: %s\nHuman detection: %sDetected\n", roomData.status, roomData.humanDetected ? "" : "Not ")
            );

            this.buttonActionActivation.setText(roomData.status.compareTo("all off") == 0 ? R.string.turn_on : R.string.turn_off);
            this.switchAutomatic.setChecked(roomData.automatic);

            this.buttonActionActivation.setTag(roomData.roomId);
            this.buttonActionEdit.setTag(roomData.roomId);
            this.switchAutomatic.setTag(roomData.roomId);
        }
    }

    public static class RoomData {
        private String roomId;
        private String name;
        private String status;
        private boolean humanDetected;
        private boolean automatic;

        public RoomData(String roomId, String name, String status, boolean humanDetected, boolean automatic) {
            this.roomId = roomId;
            this.name = name;
            this.status = status;
            this.humanDetected = humanDetected;
            this.automatic = automatic;
        }
    }
}
