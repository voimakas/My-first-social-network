package com.example.buss.ui.profile

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.buss.MAIN
import com.example.buss.Model.User
import com.example.buss.R
import com.example.buss.adapter.VPAdapter
import com.example.buss.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileId: String
    lateinit var postsRef1 : DatabaseReference
    lateinit var postsRef2 : DatabaseReference
    lateinit var postsRef3 : DatabaseReference
    lateinit var listener1 : ValueEventListener
    lateinit var listener2 : ValueEventListener
    lateinit var listener3 : ValueEventListener
    private lateinit var firebaseUser: FirebaseUser
    private var tabTitle = arrayOf("Posts","Likes")

//    override fun onAttach(context: Context) {
//       var myContext = activity as FragmentActivity
//        super.onAttach(context)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        MAIN.navView.visibility = View.VISIBLE
        MAIN.supportActionBar?.hide()
        ViewModelProvider(this)[ProfileViewModel::class.java]


        Log.i(TAG,"onViewCreate")

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }





        fun checkFollowAndFollowing() {


            postsRef1 = firebaseUser!!.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileId).child("Followers")
            }
          listener1 =  postsRef1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(firebaseUser!!.uid).exists()) {

binding.buttonEditSettings.text = "Following"
                    } else {
                        binding.buttonEditSettings.text = "Follow"
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })



        }

//
        if (profileId == firebaseUser.uid)
        {
            _binding?.buttonEditSettings?.text = "Edit Profile"

        } else if (profileId != firebaseUser.uid)
        {
            checkFollowAndFollowing()
        }
//
         fun userInfo() {


            postsRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

          listener2 = postsRef2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
if (snapshot.exists()) {
    val user = snapshot.getValue<User>(User::class.java)

    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.user).into(binding?.profilePicture)
    binding.profineNickName.text = user.getName()
    binding.bioFragmentText.text = user.getBio()
    Log.i(TAG, "userInfoTag2")
}
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG,"userInfoTag3")
                }
            })
        }

        userInfo()
        getFollowers()
        getFollowings()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG,"onViewCreated")



        var pager = _binding!!.viewPager2
        var tabLayout = _binding!!.tabLayout
pager.adapter = VPAdapter((activity as AppCompatActivity).supportFragmentManager, lifecycle)


        TabLayoutMediator(tabLayout, pager){
                tab, position ->
            tab.text = tabTitle[position]
        }.attach()









        binding.buttonEditSettings.setOnClickListener {
            val getButtonText = binding.buttonEditSettings.text.toString()


            when
            {
                getButtonText == "Edit Profile" -> MAIN.navController.navigate(R.id.action_navigation_profile_to_settingsFragment2)

                getButtonText == "Follow" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).setValue(true)
                    }

                }

                getButtonText == "Following" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).removeValue()
                    }
                }
            }

        }


    }






  private  fun getFollowers() {

        postsRef3 = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(profileId)
            .child("Followers")


      listener3 =  postsRef3.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val p = snapshot.childrenCount - 1
                    _binding?.followersCount?.text = p.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

 private   fun getFollowings() {

        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.i(TAG, "bindi")
                    val p = snapshot.childrenCount - 1
                    _binding?.followingCount?.text = p.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "bindi2")
            }
        })


    }


    override fun onStop() {
        super.onStop()

    Log.i(ContentValues.TAG, "onStop")



    }


    override fun onResume() {
        super.onResume()


    }


    override fun onDestroy() {
        super.onDestroy()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE )?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()

    }



}