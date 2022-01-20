package com.example.omegleclone

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import android.content.SharedPreferences
import com.google.firebase.database.DatabaseError



import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates
import android.content.Intent
import android.telecom.Call
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    var database=FirebaseDatabase.getInstance()
    lateinit var uuid:String
    lateinit var roomid:String
    private var isokay=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
         uuid = sh.getString("uuid", "").toString()
        if (uuid ==""){
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
            uuid=UUID.randomUUID().toString()
        myEdit.putString("uuid", uuid)
        myEdit.commit();}
        database!!.reference.child("rooms").
        orderByChild("status").equalTo(true).
        limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount>0){
                    isokay=true
                    for (childSnap:DataSnapshot in snapshot.children)
                    {
                        database!!.reference
                            .child("rooms")
                            .child(childSnap.key.toString())
                            .child("incoming")
                            .setValue(uuid);
                        database!!.reference
                            .child("rooms")
                            .child(childSnap.key.toString())
                            .child("incoming")
                            .setValue(uuid);
                        database!!.reference
                            .child("rooms")
                            .child(childSnap.key.toString())
                            .child("isAvailable")
                            .setValue(false);
                        database!!.reference
                            .child("rooms")
                            .child(childSnap.key.toString())
                            .child("status")
                            .setValue(false);
                        val intent = Intent(
                            this@MainActivity,
                            CallActivity::class.java
                        )
                        val incoming = childSnap.child("incoming").getValue(
                            String::class.java
                        )
                        val createdBy = childSnap.child("createdBy").getValue(
                            String::class.java
                        )
                        val isAvailable = childSnap.child("isAvailable").getValue(
                            Boolean::class.java
                        )!!
                        intent.putExtra("username", uuid)
                        intent.putExtra("incoming", incoming)
                        intent.putExtra("createdBy", createdBy)
                        intent.putExtra("isAvailable", isAvailable)
                        roomid= childSnap.key.toString()
                        intent.putExtra("roomid",roomid)
                        startActivity(intent)
                        finish()

                    }
                }
                else{
                    statustxt.text="created a new room for you"
                    val room=HashMap<String,Any>()
                    room["incoming"] = uuid;
                    room["createdBy"] = uuid;
                    room["isAvailable"] = true;
                    room["status"] = true
                    roomid=UUID.randomUUID().toString()
                    database!!.reference
                        .child("rooms")
                        .child(roomid)
                        .setValue(room).addOnSuccessListener(object : OnSuccessListener<Void> {
                            override fun onSuccess(p0: Void?) {
                                database!!.reference.child("rooms").child(roomid).addValueEventListener(
                                    object : ValueEventListener {
                                        /**
                                         * This method will be called with a snapshot of the data at this location. It will also be called
                                         * each time that data changes.
                                         *
                                         * @param snapshot The current data at the location
                                         */
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.child("status").value==false){
                                                    statustxt.text="someone joined your room"
                                                    if (isokay){
                                                        return
                                                    }
                                                    Log.d("TAG", "onDataChange:status changed ")
                                                    isokay = true;
                                                    val intent = Intent(this@MainActivity, CallActivity::class.java);
                                                    val incoming = snapshot.child("incoming").getValue(String::class.java)
                                                    val createdBy = snapshot.child("createdBy").getValue(String::class.java)
                                                    val isAvailable = snapshot.child("isAvailable").getValue(Boolean::class.java)
                                                    intent.putExtra("username", uuid);
                                                    intent.putExtra("incoming", incoming);
                                                    intent.putExtra("createdBy", createdBy);
                                                    intent.putExtra("roomid",roomid)
                                                    intent.putExtra("isAvailable", isAvailable);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                            }

                        })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }
}