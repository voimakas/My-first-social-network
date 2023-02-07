package com.example.buss.ui.upload

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.buss.databinding.FragmentUploadBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private var myUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this).get(UploadViewModel::class.java)

        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVars()
registerClickEvents()
binding.uploadBtn.text = "${getDate()}"


    }


    private fun registerClickEvents() {
        binding.uploadBtn.setOnClickListener {
            uploadPost()
        }

        binding.imageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

        imageUri = it
        binding.imageView.setImageURI(it)
    }


    private fun initVars() {

        storageRef = FirebaseStorage.getInstance().reference.child("Post Pictures")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    private fun uploadPost() {

        // сделать цикл на проверку выбопа фото и текста

        binding.progressBar.visibility = View.VISIBLE
        val fileRef = storageRef.child(System.currentTimeMillis().toString() + "jpg")

        var uploadTask: StorageTask<*>
        uploadTask = fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask<Uri?>(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation fileRef.downloadUrl
        })
            .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    myUrl = downloadUrl.toString()
val ref2 = FirebaseDatabase.getInstance().reference.child("Likes")
                    val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                    val postId = ref.push().key

                    val postMap = HashMap<String, Any>()
                    postMap["postid"] = postId!!
                    postMap["date"] = getDate()
                    postMap["description"] = binding.textForYourIdea.text.toString().lowercase()
                    postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                    postMap["postimage"] = myUrl

                    val postMap2 = HashMap<String, Any>()
                    postMap2["const"] = 1

                    ref2.child(postId).updateChildren(postMap2)
                    ref.child(postId).updateChildren(postMap)
                    Toast.makeText(context,"Post uploaded", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            })

    }

    private fun getDate() : String{

       var sdf =  SimpleDateFormat("dd.MM.yyyy");
        var c = Calendar.getInstance();
       var date  = sdf.format(c.getTime());


return  date
    }
}


