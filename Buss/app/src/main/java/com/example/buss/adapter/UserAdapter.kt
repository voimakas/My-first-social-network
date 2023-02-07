package com.example.buss.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.buss.MAIN
import com.example.buss.Model.User
import com.example.buss.ProfileSearchUserFragment
import com.example.buss.R
import com.example.buss.settings.SettingsFragment
import com.example.buss.ui.messages.MessagesFragment
import com.example.buss.ui.profile.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.FieldPosition
import kotlin.contracts.contract

class UserAdapter (private var mContext: Context,
                   private var mUser:List<User>,
                   private var isFragment: Boolean = false) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
return mUser.size
    }


    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
val user = mUser[position]
        holder.userNameTextView.text = user.getName()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.user).into(holder.userProfileImage)

        checkFollowingStatus(user.getUID(), holder.followButton)

        holder.followButton.setOnClickListener{
            if (holder.followButton.text.toString() == "Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener {
                            if (it.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener {
                                            if (it.isSuccessful) {

                                            }
                                        }
                                }


                            }
                        }
                }
            }
            else
            {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener {
                            if (it.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener {
                                            if (it.isSuccessful) {

                                            }
                                        }
                                }


                            }
                        }
                }
            }
        }

holder.userProfileImage.setOnClickListener(View.OnClickListener {



    val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE ).edit()
    pref.putString("profileId", user.getUID())
    pref.apply()



    (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
        .replace(R.id.item_view,ProfileFragment()).commit()
})



    }



    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTextView: TextView = itemView.findViewById(R.id.user_name_search)
     var userProfileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
     var messageButton: Button = itemView.findViewById(R.id.message_btn)
     var followButton : Button = itemView.findViewById(R.id.follow_btn)
    }

    private fun checkFollowingStatus(uid: String, followButton: Button)
    {
       val followingRef = firebaseUser?.uid.let { it1 ->
           FirebaseDatabase.getInstance().reference
               .child("Follow").child(it1.toString())
               .child("Following")
       }
        followingRef.addValueEventListener(object : ValueEventListener
        {
        override fun onDataChange(dataSnapshot: DataSnapshot)
        {
if (dataSnapshot.child(uid).exists())
{
    followButton.text = "Following"
}
else
{
    followButton.text = "Follow"
}
        }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

