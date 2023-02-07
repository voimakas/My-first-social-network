package com.example.buss
//
//import android.annotation.SuppressLint
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.View
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.buss.adapter.PostAdapter
//import com.example.buss.databinding.ActivityStartBinding
//import com.google.firebase.firestore.FirebaseFirestore
//
//class StartActivity : AppCompatActivity() {
//    private lateinit var binding : ActivityStartBinding
//    private lateinit var firebaseFirestore: FirebaseFirestore
//    private var imageUri: Uri? = null
//    private var mList = mutableListOf<String>()
//    private lateinit var adapter: PostAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_start)
//        binding = ActivityStartBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        initVars()
//        getImages()
//    }
//    private fun initVars() {
//        firebaseFirestore = FirebaseFirestore.getInstance()
//        binding.recyclerView.setHasFixedSize(true)
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = PostAdapter(mList)
//        binding.recyclerView.adapter = adapter
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun getImages(){
//        binding.progressBar.visibility = View.VISIBLE
//        firebaseFirestore.collection("images")
//            .get().addOnSuccessListener {
//                for(i in it){
//                    mList.add(i.data["pic"].toString())
//                }
//                adapter.notifyDataSetChanged()
//                binding.progressBar.visibility = View.GONE
//            }
//    }
//}