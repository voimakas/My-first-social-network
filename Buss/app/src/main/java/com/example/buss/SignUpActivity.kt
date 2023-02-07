package com.example.buss

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.example.buss.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var binding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java )
            startActivity(intent)
        }

        binding.button.setOnClickListener{
            val name = binding.NameEt.text.toString()
val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confrimPass = binding.confirmPassEt.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confrimPass.isNotEmpty()) {
                if (pass == confrimPass){
firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
    if (it.isSuccessful){
        saveUserInfo(name, email)

    }else{
        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
    }
}
                }else{
                    Toast.makeText(this, "Passwords are not matching", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserInfo(name: String, email: String) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val ref2 = FirebaseDatabase.getInstance().reference.child("Follow")


        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["name"] = name.lowercase()
        userMap["email"] = email
        userMap["bio"] = "hey i am"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/ourbusiness-53716.appspot.com/o/deafoult%20profile%20images%2Fprofile%20(1).png?alt=media&token=83484ec7-9f0f-49c8-b9c5-0ca23680b1eb"

        val postMap2 = HashMap<String, Any>()
        postMap2["const"] = 1

        ref2.child(currentUserID).child("Followers").updateChildren(postMap2)
        ref2.child(currentUserID).child("Following").updateChildren(postMap2)

        userRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener {task ->
                if(task.isSuccessful)
                {

                    Toast.makeText(this, "Account has been creates successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java )
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                }
            }
    }

}