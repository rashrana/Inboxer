package com.rash.inboxer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rash.inboxer.Fragments.AccountFragment;
import com.rash.inboxer.Fragments.ChatFragment;
import com.rash.inboxer.Fragments.ContactsFragment;
import com.rash.inboxer.Model.UserAccount;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    //android:background="?android:attr/windowBackground"
    private Toolbar chatToolbar, contactToolbar, profileToolbar;
    private BottomNavigationView bottomNavigationView;
    private ImageView cimage, timage, coimage;

    private String email, curprofileurl, curUsername;
    private UserAccount current;
    private Button searchnew,logout,addchat,addtask;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            Log.d("nived","1");
            email = firebaseAuth.getCurrentUser().getEmail();

        }

        Log.d("nived","1");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference(email);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot selected : snapshot.getChildren()) {
                    Log.d("nived","3");
                    try{
                        UserAccount user = selected.getValue(UserAccount.class);
                        user.setKey(selected.getKey());
                        if (user.getEmail().equals(email)) {
                            Log.d("nived","4");
                            current=user;
                            curUsername = user.getUsername();
                            curprofileurl = user.getImageurl();
                            Picasso.with(getApplicationContext()).load(curprofileurl).placeholder(R.drawable.profiledefault).into(cimage);
                            Picasso.with(getApplicationContext()).load(curprofileurl).placeholder(R.drawable.profiledefault).into(coimage);
                            break;
                        }
                    }catch (Exception e){
                        Log.d("nived",e.getMessage());
                        e.printStackTrace();
                    }



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("nived","5");
        chatToolbar = findViewById(R.id.chatToolBar);
        contactToolbar = findViewById(R.id.contactsToolBar);
        profileToolbar = findViewById(R.id.profileToolBar);
        setSupportActionBar(chatToolbar);
        Log.d("nived","6");
        contactToolbar.setVisibility(View.INVISIBLE);
        profileToolbar.setVisibility(View.INVISIBLE);

        Log.d("nived","7");
        cimage = chatToolbar.findViewById(R.id.cimage);
        coimage = contactToolbar.findViewById(R.id.coimage);
        Log.d("nived","8");
        searchnew = contactToolbar.findViewById(R.id.search_newpeople);
        logout= profileToolbar.findViewById(R.id.logout);
        addchat= chatToolbar.findViewById(R.id.addchat);
        searchnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddFriendActivity.class);
                i.putExtra("curUsername", curUsername);
                startActivity(i);
            }
        });

        Log.d("nived","9");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status("false");
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });


        Log.d("nived","11");
        bottomNavigationView = findViewById(R.id.bottomnavbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment(curUsername)).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("nived","12");
                Fragment selected_frag = null;
                switch (item.getItemId()) {
                    case R.id.chat:
                        chatToolbar.setVisibility(View.VISIBLE);
                        contactToolbar.setVisibility(View.INVISIBLE);
                        profileToolbar.setVisibility(View.INVISIBLE);
                        setSupportActionBar(chatToolbar);
                        selected_frag = new ChatFragment(curUsername);
                        break;
                    case R.id.contact:
                        contactToolbar.setVisibility(View.VISIBLE);
                        chatToolbar.setVisibility(View.INVISIBLE);
                        profileToolbar.setVisibility(View.INVISIBLE);
                        setSupportActionBar(contactToolbar);
                        selected_frag = new ContactsFragment(curUsername);
                        break;
                    case R.id.account:
                        profileToolbar.setVisibility(View.VISIBLE);
                        chatToolbar.setVisibility(View.INVISIBLE);
                        contactToolbar.setVisibility(View.INVISIBLE);
                        setSupportActionBar(profileToolbar);
                        selected_frag = new AccountFragment(current);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected_frag).commit();
                return true;
            }
        });


    }

    public void status(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        databaseReference.updateChildren(map);
    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            status("true");
//        }
//
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            status("false");
//        }
//    }
}