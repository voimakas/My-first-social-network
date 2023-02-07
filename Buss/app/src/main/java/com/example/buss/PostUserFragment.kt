package com.example.buss

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.buss.Model.Post
import com.example.buss.Model.User
import com.example.buss.databinding.FragmentPostUserBinding
import com.example.buss.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class PostUserFragment : Fragment() {

    private var _binding: FragmentPostUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MAIN.navView.visibility = View.VISIBLE

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFSPOST", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("postId", "none")!!
        }
         postInfo()

        _binding = FragmentPostUserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun postInfo()
    {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(profileId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<Post>(Post::class.java)

                    Picasso.get().load(user!!.getPostimage()).placeholder(R.drawable.user).into(binding?.imageView2)
                    binding.description.text = user.getDescription()
                    Log.i(ContentValues.TAG, "userInfoTag2")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.i(ContentValues.TAG,"userInfoTag3")
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
    firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFSPOST", Context.MODE_PRIVATE )?.edit()
        pref?.putString("postId", firebaseUser.uid)
        pref?.apply()
        //mb problem
    }
}