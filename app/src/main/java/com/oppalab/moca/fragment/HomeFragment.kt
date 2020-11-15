package com.oppalab.moca.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.oppalab.moca.adapter.PostAdapter
import com.oppalab.moca.model.Post
import com.oppalab.moca.R
import com.oppalab.moca.dto.GetFeedsAtHomeDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.home_recycler_view)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd=true
        recyclerView.layoutManager = linearLayoutManager

        postList=ArrayList()
        postAdapter = context?.let{PostAdapter(it, postList as ArrayList<Post>)}
        recyclerView.adapter = postAdapter

//        checkFollowings()
        retrievePosts1()

        return view
    }

    private fun retrievePosts1() {
        //
        RetrofitConnection.server.getFeedsAtHome(userId = PreferenceManager.getLong(requireContext(),"userId"), page = 0).enqueue(
            object : Callback<GetFeedsAtHomeDTO> {
                override fun onResponse(
                    call: Call<GetFeedsAtHomeDTO>,
                    response: Response<GetFeedsAtHomeDTO>
                ) {
                    Log.d("POSTS", response.body().toString())
                    if (response.body()!!.last == true) {
                        Toast.makeText(context, "마지막 페이지 입니다.", Toast.LENGTH_LONG)
                    }

                }

                override fun onFailure(call: Call<GetFeedsAtHomeDTO>, t: Throwable) {

                }

            })


        //firebase
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")


        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()

                for (snapshot in p0.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    postList!!.add(post!!)
                    postAdapter!!.notifyDataSetChanged()

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

//        RetrofitConnection.server.getFeedsAtHome(PreferenceManager.getLong(requireContext(),"userId"),0).enqueue(object : Callback<GetFeedsAtHomeDTO>{
//            override fun onResponse(
//                call: Call<GetFeedsAtHomeDTO>,
//                response: Response<GetFeedsAtHomeDTO>
//            ) {
//                fun onDataChange(p0: DataSnapshot) {
//                    postList?.clear()
//
//                    for (snapshot in p0.children)
//                    {
//                        val post = snapshot.getValue(Post::class.java)
//
//                        postList!!.add(post!!)
//                        postAdapter!!.notifyDataSetChanged()
//
//                    }
//                }
//
//                if (response?.isSuccessful) {
//                    Toast.makeText(
//                        context,
//                        "Post list called successfully",
//                        Toast.LENGTH_LONG
//                    ).show();
//                }
//
//            }
//
//            override fun onFailure(call: Call<GetFeedsAtHomeDTO>, t: Throwable) {
//                Log.d("retrofit result", t.message.toString())
//                Toast.makeText(
//                    context,
//                    "Fail",
//                    Toast.LENGTH_LONG
//                ).show();
//            }
//        })




    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    (followingList as ArrayList<String>).clear()

                    for(snapshot in p0.children)
                    {
                        snapshot.key?.let {(followingList as ArrayList<String>).add(it)}
                    }

                    retrievePosts()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun retrievePosts()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()

                for (snapshot in p0.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    for (id in (followingList as ArrayList<String>))
                    {
                        if (post!!.getPublisher() == id)
                        {
                            postList!!.add(post)
                        }

                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}