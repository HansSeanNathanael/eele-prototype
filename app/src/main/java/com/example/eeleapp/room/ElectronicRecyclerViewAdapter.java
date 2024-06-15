package com.example.eeleapp.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eeleapp.R;

import java.util.ArrayList;

public class ElectronicRecyclerViewAdapter extends RecyclerView.Adapter<ElectronicRecyclerViewAdapter.ElectronicViewHolder> {

    private final ArrayList<ElectronicData> electronicDataArrayList = new ArrayList<>();
    private View.OnClickListener listener;

    public ElectronicRecyclerViewAdapter(View.OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ElectronicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ElectronicViewHolder electronicViewHolder = new ElectronicViewHolder(layoutInflater.inflate(R.layout.electronic_list_item, parent, false), this.listener);
        return electronicViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ElectronicViewHolder holder, int position) {
        synchronized (this.electronicDataArrayList) {
            if (position < this.electronicDataArrayList.size()) {
                holder.bind(this.electronicDataArrayList.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.electronicDataArrayList.size();
    }

    public void setElectronicDataArrayList(ArrayList<ElectronicData> electronicDataArrayList) {
        synchronized (this.electronicDataArrayList) {
            this.electronicDataArrayList.clear();
            this.electronicDataArrayList.addAll(electronicDataArrayList);
            this.notifyDataSetChanged();
        }
    }

    public class ElectronicViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewElectronicType;
        private TextView textViewElectronicName;

        private Button buttonActionElectronic;
        private Button buttonActionDelete;

        public ElectronicViewHolder(@NonNull View itemView, View.OnClickListener listener) {
            super(itemView);

            this.textViewElectronicType = itemView.findViewById(R.id.textViewElectronicType);
            this.textViewElectronicName = itemView.findViewById(R.id.textViewElectronicName);
            this.buttonActionElectronic = itemView.findViewById(R.id.buttonActionElectronic);
            this.buttonActionDelete = itemView.findViewById(R.id.buttonActionDelete);

            this.buttonActionElectronic.setOnClickListener(listener);
        }

        public void bind(ElectronicData electronicData) {
            this.textViewElectronicType.setText(electronicData.type);
            this.textViewElectronicName.setText(
                    String.format("Name: %s\nInfo: %s\n", electronicData.id, electronicData.info)
            );

            if (electronicData.type.compareTo("CAMERA") == 0) {
                this.buttonActionElectronic.setText(R.string.view);
            }
            else if (electronicData.type.compareTo("PLUG") == 0) {
                if (electronicData.info.compareTo("Status On") == 0) {
                    this.buttonActionElectronic.setText(R.string.turn_off);
                }
                else {
                    this.buttonActionElectronic.setText(R.string.turn_on);
                }
            }

            this.buttonActionElectronic.setTag(electronicData);
            this.buttonActionDelete.setTag(electronicData.id);
        }
    }

    public static class ElectronicData {
        private String id;
        private String type;
        private String info;

        public ElectronicData(String id, String type, String info) {
            this.id = id;
            this.type = type;
            this.info = info;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getInfo() {
            return info;
        }
    }
}
