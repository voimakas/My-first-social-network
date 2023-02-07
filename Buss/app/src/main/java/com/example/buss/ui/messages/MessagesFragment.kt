
package com.example.buss.ui.messages;


import android.os.Bundle
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buss.Model.User
import com.example.buss.R
import com.example.buss.adapter.UserAdapter
import com.example.buss.databinding.FragmentMessagesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MessagesFragment : Fragment() {
    private var _binding:FragmentMessagesBinding? = null
    private val binding get() = _binding!!

   private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


    _binding = FragmentMessagesBinding.inflate(inflater, container, false)
    val root: View = binding.root
    return root
}



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerViewSearch
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<User>, true) }
        recyclerView?.adapter = userAdapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }



            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


if (binding.searchEditText.text.toString() == "")
{
    mUser?.clear()
}
else {
    recyclerView?.visibility = View.VISIBLE
    retrieveUser()
    searchUser(s.toString().lowercase())
}

            }
            override fun afterTextChanged(s: Editable?) {

            }
        })

    }



    private fun searchUser(input: String)
    {
        val query = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("name")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser?.clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null)
                        {
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


  private  fun retrieveUser(){
val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
      userRef.addValueEventListener(object : ValueEventListener{
          override fun onDataChange(dataSnapshot: DataSnapshot) {
              if (_binding?.searchEditText.toString() == "")
              {
mUser?.clear()
                  for (snapshot in dataSnapshot.children)
                  {
                      val user = snapshot.getValue(User::class.java)
                      if (user != null)
                      {
                          mUser?.add(user)
                      }
                  }
                  userAdapter?.notifyDataSetChanged()
              }
          }

          override fun onCancelled(error: DatabaseError) {
              TODO("Not yet implemented")
          }
      })

    }

    override fun onDestroy() {
        super.onDestroy()
        mUser?.clear()
    }
}
