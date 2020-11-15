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
import com.oppalab.moca.R
import com.oppalab.moca.adapter.PostAdapterRetro
import com.oppalab.moca.dto.FeedsAtHome
import com.oppalab.moca.dto.GetFeedsAtHomeDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragmentRetro : Fragment() {

    private var postAdapterRetro: PostAdapterRetro? = null
    private var postList: MutableList<FeedsAtHome>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.home_recycler_view)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapterRetro = context?.let { PostAdapterRetro(it, postList as ArrayList<FeedsAtHome>) }
        recyclerView.adapter = postAdapterRetro

        retrievePosts1()

        return view
    }

    private fun retrievePosts1() {
        //
        RetrofitConnection.server.getFeedsAtHome(
            userId = PreferenceManager.getLong(
                    requireContext(),
            "userId"
        ), page = 0
        ).enqueue(
            object : Callback<GetFeedsAtHomeDTO> {
                override fun onResponse(
                    call: Call<GetFeedsAtHomeDTO>,
                    response: Response<GetFeedsAtHomeDTO>
                ) {
                    Log.d("POSTS", response.body().toString())

                    postList?.clear()

                    for (post in response.body()!!.content) {
                        val curPost = post
                        postList!!.add(curPost)
                        postAdapterRetro!!.notifyDataSetChanged()
                    }

                    if (response.body()!!.last == true) {
                        Toast.makeText(context, "마지막 페이지 입니다.", Toast.LENGTH_LONG)
                    }

                }

                override fun onFailure(call: Call<GetFeedsAtHomeDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }
            })
    }
}