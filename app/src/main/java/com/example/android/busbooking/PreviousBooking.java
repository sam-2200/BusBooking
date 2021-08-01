package com.example.android.busbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class PreviousBooking extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mfirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseUser currentUser;
    private Button downloadButton;
    private ImageButton back;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_booking);

        back=(ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PreviousBooking.this,Schedule.class));
                finish();
            }
        });

        firebaseFirestore =FirebaseFirestore.getInstance();
        mfirestoreList=findViewById(R.id.rv_pBooking);
        // Query
        Query query=firebaseFirestore.collection("Buses").limit(5);
//        .orderBy("datetime", Query.Direction.ASCENDING);

        //Recycleroption
        FirestoreRecyclerOptions<BusDetails> options=new FirestoreRecyclerOptions.Builder<BusDetails>()
                .setQuery(query,BusDetails.class)
                .build();

        adapter= new FirestoreRecyclerAdapter<BusDetails,PreviousBookingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull PreviousBookingViewHolder holder, int position, @NonNull @NotNull BusDetails bus) {
                holder.start.setText(bus.getStart());
                holder.end.setText(bus.getEnd());
                holder.time.setText(bus.getTime());
                holder.busNo.setText(bus.getBusNo());
                holder.date.setText(bus.getDate());
                holder.day.setText(bus.getDay());
                holder.seatsBooked.setText(bus.getSeatsBooked());
            }

            @NonNull
            @NotNull
            @Override
            public PreviousBookingViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
//                return  new BusAdapter.BusViewHolder(v);
                return new PreviousBookingViewHolder(v);
            }
        };
        mfirestoreList.setHasFixedSize(true);
//        ;hasFixedSize(true);
        mfirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mfirestoreList.setAdapter(adapter);


    }



    @Override
    public void onClick(View view) {

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public class PreviousBookingViewHolder extends RecyclerView.ViewHolder {


        TextView start, end, day, date, busNo, time, seatsLeft, seatsBooked;

        public PreviousBookingViewHolder(@NotNull View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.startPoint);
            end = itemView.findViewById(R.id.endPoint);
            date = itemView.findViewById(R.id.date);
            day = itemView.findViewById(R.id.day);
            busNo = itemView.findViewById(R.id.BusNo);
            time = itemView.findViewById(R.id.time);
            seatsBooked = itemView.findViewById(R.id.noBooked);
        }


    }
}
