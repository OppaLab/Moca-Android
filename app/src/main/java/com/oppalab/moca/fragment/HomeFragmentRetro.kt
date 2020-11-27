package com.oppalab.moca.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R
import com.oppalab.moca.adapter.PostAdapterRetro
import com.oppalab.moca.dto.FeedsAtHome
import com.oppalab.moca.dto.GetFeedsAtHomeDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragmentRetro : Fragment() {

    private var postAdapterRetro: PostAdapterRetro? = null
    private var postList: MutableList<FeedsAtHome>? = null
    private var curPage: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        var home_post_recyclerview: RecyclerView? = null
        home_post_recyclerview = view.findViewById(R.id.home_recycler_view)
        val linearLayoutManager = LinearLayoutManager(context)
//        linearLayoutManager.reverseLayout = true
//        linearLayoutManager.stackFromEnd = true
        home_post_recyclerview.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapterRetro = context?.let { PostAdapterRetro(it, postList as ArrayList<FeedsAtHome>) }
        home_post_recyclerview.adapter = postAdapterRetro
        home_post_recyclerview.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount -1
                if (lastVisibleItemPosition == itemTotalCount) {
                    curPage += 1
                    home_progress_bar.visibility = View.VISIBLE
                    retrievePostsMore()
                }
            }
        })

        retrievePosts1()

        return view
    }

    private fun retrievePostsMore() {
        //
        RetrofitConnection.server.getFeedsAtHome(
            userId = PreferenceManager.getLong(
                requireContext(),
                "userId"
            ), page = curPage
        ).enqueue(
            object : Callback<GetFeedsAtHomeDTO> {
                override fun onResponse(
                    call: Call<GetFeedsAtHomeDTO>,
                    response: Response<GetFeedsAtHomeDTO>
                ) {
                    Log.d("curUserId", PreferenceManager.getLong(context!!, "userId").toString())
                    Log.d("POSTSMORE", response.body().toString())

                    home_progress_bar.visibility = View.GONE

                    for (post in response.body()!!.content) {
                        val curPost = post
                        postList!!.add(curPost)
                        postAdapterRetro!!.notifyDataSetChanged()
                    }

                    if (response.body()!!.last) {
                        Toast.makeText(context, "마지막 페이지 입니다.", Toast.LENGTH_LONG)
                    }

                }

                override fun onFailure(call: Call<GetFeedsAtHomeDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }
            })
    }

    private fun retrievePosts1() {
        //
        RetrofitConnection.server.getFeedsAtHome(
            userId = PreferenceManager.getLong(
                    requireContext(),
            "userId"
        ), page = curPage
        ).enqueue(
            object : Callback<GetFeedsAtHomeDTO> {
                override fun onResponse(
                    call: Call<GetFeedsAtHomeDTO>,
                    response: Response<GetFeedsAtHomeDTO>
                ) {
                    Log.d("curUserId", PreferenceManager.getLong(context!!, "userId").toString())
                    Log.d("POSTS", response.body().toString())

                    postList?.clear()

                    for (post in response.body()!!.content) {
                        val curPost = post
                        postList!!.add(curPost)
                        postAdapterRetro!!.notifyDataSetChanged()
                    }

                    if (response.body()!!.last) {
                        Toast.makeText(context, "마지막 페이지 입니다.", Toast.LENGTH_LONG)
                    }

                }

                override fun onFailure(call: Call<GetFeedsAtHomeDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }
            })
    }
}