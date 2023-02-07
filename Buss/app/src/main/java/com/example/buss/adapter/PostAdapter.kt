package com.example.buss.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.buss.Model.Post
import com.example.buss.Model.User
import com.example.buss.PostUserFragment
import com.example.buss.R
import com.example.buss.ui.profile.ProfileFragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter
    (private val mContext: Context,
    private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{

private var firebaseUser: FirebaseUser? = null
inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
{
    var date: TextView
    var profileImage: CircleImageView
    var postImage : ImageView
    var likeButton: ImageView
    var userName : TextView
    var likes : TextView
    var description : TextView
    var helper : TextView



    init {

        date = itemView.findViewById(R.id.date)
        profileImage = itemView.findViewById(R.id.user_profile_image_search)
        postImage = itemView.findViewById(R.id.all)
        likeButton = itemView.findViewById(R.id.post_image_like_btn)
        userName = itemView.findViewById(R.id.user_name_search)
        likes = itemView.findViewById(R.id.likes)
helper = itemView.findViewById(R.id.helper)
        description = itemView.findViewById(R.id.description)
    }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
val view = LayoutInflater.from(mContext).inflate(R.layout.each_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPost[position]

        holder.postImage.setOnClickListener(){

            val pref = mContext.getSharedPreferences("PREFSPOST", Context.MODE_PRIVATE ).edit()
            pref.putString("postId", post.getPostid())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.item_view2, PostUserFragment()).commit()
        }


        firebaseUser = FirebaseAuth.getInstance().currentUser




        Picasso.get().load(post.getPostimage()).into(holder.postImage)
        holder.description.text = post.getDescription()
      getLikesCount(post.getPostid(),holder.likes)
        holder.date.text = post.getDate()

        publisherInfo(holder.profileImage, holder.userName, post.getPublisher())
        checkLikes(post.getPostid(), holder.likeButton, holder.helper)


        holder.likeButton.setOnClickListener(){

            likes(post.getPostid(), holder.likeButton, holder.helper )


        }
    }


    override fun getItemCount(): Int {
return mPost.size
    }


    private fun checkLikes(postId: String, likesButton: ImageView,helper: TextView){

        val followingRef = firebaseUser!!.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Likes").child(postId)

        }
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()) {
                  likesButton.setImageResource(R.drawable.heartl)
                   helper.text = "1"
                } else {
                    likesButton.setImageResource(R.drawable.heart)
                    helper.text = "2"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


private fun likes(postId: String, likesButton: ImageView, helper: TextView) {
if (helper.text == "2") {
    firebaseUser?.uid.let { it1 ->
        FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId).child(it1.toString()).setValue(true)
    }
    firebaseUser?.uid.let { it1 ->
        FirebaseDatabase.getInstance().reference
            .child("Users").child(it1.toString()).child("Likes").child(postId).setValue(true)
    }
} else if (helper.text == "1") {
    firebaseUser?.uid.let { it1 ->
        FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId).child(it1.toString()).removeValue()
    }
    firebaseUser?.uid.let { it1 ->
        FirebaseDatabase.getInstance().reference
            .child("Users").child(it1.toString()).child("Likes").child(postId).removeValue()
    }
}



}

    private  fun getLikesCount(postId: String, likes: TextView) {

        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Likes")
            .child(postId)



        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                        val p = snapshot.childrenCount - 1       // because childrenCount when its = 0, its = 1
                        likes.text = p.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun publisherInfo(profileImage: ImageView, userName: TextView, publisherId: String)
    {
val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)

        userRef.addValueEventListener(object : ValueEventListener
        {

            override fun onDataChange(snapshot: DataSnapshot) {
if (snapshot.exists())
{
    val user = snapshot.getValue<User>(User::class.java)

    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.user).into(profileImage)
    userName.text = user.getName()


}
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}