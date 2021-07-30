package com.example.android.busbooking.daos;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.android.busbooking.modals.*;
public class UserDao {
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    public void addUser(User user)
    {
        db.collection("user").document(user.getUid()).set(user);
    }
}
