package com.example.buss

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.buss.Model.User
import com.example.buss.databinding.FragmentProfileBinding
import com.example.buss.databinding.FragmentUserSearchProfileBinding
import com.example.buss.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class ProfileSearchUserFragment : Fragment() {
    private var _binding: FragmentUserSearchProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MAIN.navView.visibility = View.VISIBLE
        MAIN.supportActionBar?.hide()
        ViewModelProvider(this)[ProfileViewModel::class.java]



        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }


        _binding = FragmentUserSearchProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFollowers()
        getFollowings()
        userInfo()


        if (profileId == firebaseUser.uid) {
            binding.buttonEditSettings.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowing()
        }








    }

private fun checkFollowAndFollowing() {

    val followingRef = firebaseUser?.uid.let { it1 ->
        FirebaseDatabase.getInstance().reference
            .child("Follow").child(it1.toString())
            .child("Following")
    }
    if (followingRef != null) {
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(profileId).exists()) {
                    binding.buttonEditSettings.text = "Following"
                } else {
                    binding.buttonEditSettings.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}


private fun getFollowers() {

    val followersRef = FirebaseDatabase.getInstance().reference.child("Follow")
        .child(profileId)
        .child("Followers")


    followersRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {

                binding.followersCount.text = snapshot.childrenCount.toString()
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })
}


private fun getFollowings() {

    val followersRef = FirebaseDatabase.getInstance().reference
        .child("Follow").child(profileId)
        .child("Following")

    followersRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                binding.followingCount.text = snapshot.childrenCount.toString()
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

}

private fun userInfo() {

    val userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

    userRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
//                    if (context != null) {
//                        return
//                    }
            if (snapshot.exists()) {
                val user = snapshot.getValue<User>(User::class.java)

                Picasso.get().load(user!!.getImage()).placeholder(R.drawable.user).into(binding.profilePicture)
                binding.profineNickName.text = user!!.getName()
                binding.bioFragmentText.text = user!!.getBio()
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })
}
override fun onDestroyView() {
    super.onDestroyView()
    _binding = null

    val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE )?.edit()
    pref?.putString("profileId", firebaseUser.uid)
    pref?.apply()
}

override fun onStop() {
    super.onStop()

    val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE )?.edit()
    pref?.putString("profileId", firebaseUser.uid)
    pref?.apply()
}

override fun onPause() {
    super.onPause()

    val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE )?.edit()
    pref?.putString("profileId", firebaseUser.uid)
    pref?.apply()
}

override fun onDestroy() {
    super.onDestroy()

    val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE )?.edit()
    pref?.putString("profileId", firebaseUser.uid)
    pref?.apply()
}


}



