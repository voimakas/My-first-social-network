package com.example.buss.settings

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider

import com.example.buss.MAIN
import com.example.buss.MainActivity
import com.example.buss.Model.User
import com.example.buss.R
import com.example.buss.SignInActivity
import com.example.buss.databinding.FragmentSettingsBinding
import com.example.buss.ui.profile.ProfileFragment
import com.example.buss.ui.profile.ProfileViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class SettingsFragment : Fragment() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    private lateinit var storageRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ViewModelProvider(this).get(ProfileViewModel::class.java)


        val b: ImageView? = _binding?.profilePicture
        userInfo()
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE )?.edit()
        pref?.clear()

        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MAIN.navView.visibility = View.GONE
        binding.backSetings.setOnClickListener() {
            MAIN.navController.navigate(R.id.action_settingsFragment2_to_navigation_profile)
        }


        binding.logOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.setPicture.setOnClickListener {
            checker = "clicked"
            resultLauncher.launch("image/*")
        }
        binding.saveBtn.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
                updateUserInfoOnly()
            } else {
                updateUserInfoOnly()
            }
        }


        binding.nickNameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

        imageUri = it
        binding.profilePicture.setImageURI(it)
    }

    private fun updateUserInfoOnly() {

        if (binding.nickNameEdit.text.toString() == "") {
            Toast.makeText(context, "Write full name firs", Toast.LENGTH_SHORT).show()
        } else if (binding.bioEdit.text.toString() == "") {
            Toast.makeText(context, "Write bio first", Toast.LENGTH_SHORT).show()
        } else {
            val userRef = FirebaseDatabase.getInstance().reference.child("Users")

            val userMap = HashMap<String, Any>()
            userMap["name"] = binding.nickNameEdit.text.toString().lowercase()
            userMap["bio"] = binding.bioEdit.text.toString().lowercase()

            Toast.makeText(context, "Successfully", Toast.LENGTH_SHORT).show()
            userRef.child(firebaseUser.uid).updateChildren(userMap)


        }
    }


    private fun userInfo() {


        val userRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
if (snapshot.exists()) {

    val user = snapshot.getValue<User>(User::class.java)

    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.user).into(binding.profilePicture)
    binding.nickNameEdit.setText(user.getName())
    binding.bioEdit.setText(user.getBio())
    Log.i(ContentValues.TAG, "userInfoTag2")
}
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(ContentValues.TAG, "userInfoTag3")
            }
        })
    }


    private fun uploadImageAndUpdateInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")

        storageRef = FirebaseStorage.getInstance().reference.child("ImagesProfile")
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    storageRef.downloadUrl.addOnSuccessListener { uri ->


                        val profilePictureMap = HashMap<String, Any>()

                        profilePictureMap["image"] = uri.toString()

                        Toast.makeText(context, "Successfully", Toast.LENGTH_SHORT).show()

                        userRef.child(firebaseUser.uid).updateChildren(profilePictureMap)

                    }
                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}