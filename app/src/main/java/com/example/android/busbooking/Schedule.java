package com.example.android.busbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.example.android.busbooking.daos.UserDao;
import com.example.android.busbooking.modals.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestoreDataSource;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;
//import com.google.firebase.ktx.Firebase;

public class Schedule extends AppCompatActivity implements View.OnClickListener , Serializable {

    private RecyclerView mfirestoreList;
    private  FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseUser currentUser;
    private Button downloadButton;
    private GoogleApiClient mGoogleApiClient;
    private Button preBooking;
    private FloatingActionButton logout;
    private Button button;
    public static final String TAG = "ScheduleActivity";
//    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setUserInfo();

        preBooking=(Button)findViewById(R.id.previousBookings);
        preBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Schedule.this,PreviousBooking.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(),"To do Later",Toast.LENGTH_LONG).show();
            }
        });
        logout=(FloatingActionButton)findViewById(R.id.exitButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Schedule.this,SignIn.class));
                finish();
            }
        });

        downloadButton=(Button)findViewById(R.id.downloadPdf);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db=FirebaseFirestore.getInstance();

                DocumentReference docRef = db.collection("url").document("timeTable");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String pdfUri=(String)document.get("timetableurl");
//                                        db.collection("urls").document("timeTable").grt("timetableurl")
//                                File file = new File(pdfUri);
                            Intent pdfIntent= new Intent(Intent.ACTION_VIEW);
                            pdfIntent.setData(Uri.parse(pdfUri));
                                // FLAG_GRANT_READ_URI_PERMISSION is needed on API 24+ so the activity opening the file can read it
                                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(pdfIntent);
//                                if (pdfIntent.resolveActivity(getPackageManager()) != null) {
//
//
//                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
//                File file = new File(pdfUri);
//                Intent pdfIntent= new Intent(Int)
            }
        });
//        preBooking=findViewById(R.id.previousBookings);


        firebaseFirestore =FirebaseFirestore.getInstance();
        mfirestoreList=findViewById(R.id.rv_schedule);
        // Query
        Query query=firebaseFirestore.collection("Buses").limit(5);
//        .orderBy("datetime", Query.Direction.ASCENDING);

        //Recycleroption
        FirestoreRecyclerOptions<BusDetails> options=new FirestoreRecyclerOptions.Builder<BusDetails>()
                .setQuery(query,BusDetails.class)
                .build();

//        name.setText(currentUser.getDisplayName().toString());

        adapter= new FirestoreRecyclerAdapter<BusDetails, BusViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public BusViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);
//                return  new BusAdapter.BusViewHolder(v);
                return new BusViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull BusViewHolder holder, int position, @NonNull @NotNull BusDetails bus) {
                holder.start.setText(bus.getStart());
                holder.end.setText(bus.getEnd());
                holder.time.setText(bus.getTime());
                holder.busNo.setText(bus.getBusNo());
                holder.date.setText(bus.getDate());
                holder.day.setText(bus.getDay());
                holder.seatsLeft.setText(bus.getSeatsLeft());
//                holder.name.setText(user.getDisplayName());
                int seat=Integer.parseInt(bus.getSeatsLeft());
                if(seat<3)
                    holder.seatsLeft.setTextColor(Color.parseColor("#FF0000"));
                else if(seat<10)
                    holder.seatsLeft.setTextColor(Color.parseColor("#0000CD"));
                else
                {
                    holder.seatsLeft.setTextColor(Color.parseColor("#228B22"));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Schedule.this,SeatSelectActivity.class).putExtra("busCard",getSnapshots().getSnapshot(position).getId().toString());
                        startActivity(intent);
                    }
                });
            }
        };

        mfirestoreList.setHasFixedSize(true);
//        ;hasFixedSize(true);
        mfirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mfirestoreList.setAdapter(adapter);



    }
    private void setUserInfo()
    {
        TextView name=findViewById(R.id.name);
        currentUser= FirebaseAuth.getInstance().getCurrentUser() ;
//        Toast.makeText(this, "" + currentUser.getDisplayName().toString(), Toast.LENGTH_SHORT).show();
        name.setText(currentUser.getDisplayName().toString());
//        ImageButton img=(ImageButton) findViewById(R.id.userImage);
//        img.setImageResource();

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
    public class BusViewHolder extends RecyclerView.ViewHolder implements Serializable{


        TextView start,end,day,date,busNo,time,seatsLeft;

        public BusViewHolder(@NotNull View itemView)
        {
            super(itemView);
            start=itemView.findViewById(R.id.startPoint);
            end=itemView.findViewById(R.id.endPoint);
            date=itemView.findViewById(R.id.date);
            day=itemView.findViewById(R.id.day);
            busNo=itemView.findViewById(R.id.BusNo);
            time=itemView.findViewById(R.id.time);
            seatsLeft=itemView.findViewById(R.id.seatLeft);
        }
    }
    @Override
    public void onClick(View view){

    }
}