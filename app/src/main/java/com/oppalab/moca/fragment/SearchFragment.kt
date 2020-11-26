package com.oppalab.moca.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R
import com.oppalab.moca.adapter.MyThumbnailAdapter
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.dto.MyPostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {
    private var currentUser: Long = 0L
    private var postList: MutableList<MyPostDTO>? = null
    private var myThumbnailAdapter: MyThumbnailAdapter? = null
    private var sort: String = ""
    private var curCategory: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        currentUser = PreferenceManager.getLong(requireContext(), "userId")

        var recyclerview_search_thumbnails: RecyclerView
        recyclerview_search_thumbnails = view.findViewById(R.id.search_recycler_view)
        recyclerview_search_thumbnails.setHasFixedSize(true)
        postList = ArrayList()
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerview_search_thumbnails.layoutManager = linearLayoutManager

        myThumbnailAdapter = context?.let { MyThumbnailAdapter(it, postList as ArrayList<MyPostDTO>) }
        recyclerview_search_thumbnails.adapter = myThumbnailAdapter


        val spinnerItems = resources.getStringArray(R.array.sort_array)
        view.search_sort_spinner.setSelection(0)
        view.search_sort_spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerItems)
        view.search_sort_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        sort = ""
                        RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "DEFAULT", category = curCategory, sort=sort).enqueue(object:
                            Callback<GetMyPostDTO> {
                            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                                Log.d("retrofit", response.body().toString())
                                postList!!.clear()
                                for (post in response.body()!!.content) {
                                    postList!!.add(post)
                                }
                                Collections.reverse(postList)
                                myThumbnailAdapter!!.notifyDataSetChanged()
                            }

                            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                                Log.d("retrofit", t.message.toString())
                            }

                        })

                    }

                    1 -> {
                        sort = "likeCount"
                        RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "DEFAULT", category = curCategory, sort=sort).enqueue(object:
                            Callback<GetMyPostDTO> {
                            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                                Log.d("retrofit", response.body().toString())
                                postList!!.clear()
                                for (post in response.body()!!.content) {
                                    postList!!.add(post)
                                }
                                Collections.reverse(postList)
                                myThumbnailAdapter!!.notifyDataSetChanged()
                            }

                            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                                Log.d("retrofit", t.message.toString())
                            }

                        })
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                sort = ""
            }

        }

        RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "DEFAULT", category = curCategory, sort=sort).enqueue(object:
            Callback<GetMyPostDTO> {
            override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                Log.d("retrofit", response.body().toString())
                postList!!.clear()
                for (post in response.body()!!.content) {
                    postList!!.add(post)
                }
                Collections.reverse(postList)
                myThumbnailAdapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })

        view.search_fragment_icon.setOnClickListener {
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = search_edit_text.text.toString(), category = "", sort=sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }

        view.search_category_couple_btn.setOnClickListener {
            curCategory = "Couple"
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "", category = curCategory, sort=sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }
        view.search_category_family_btn.setOnClickListener {
            curCategory = "Family"
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "", category = curCategory, sort = sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }
        view.search_category_friend_btn.setOnClickListener {
            curCategory = "Friend"
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "", category = curCategory, sort = sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }
        view.search_category_money_btn.setOnClickListener {
            curCategory = "Money"
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "", category = curCategory, sort = sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }
        view.search_category_parent_btn.setOnClickListener {
            curCategory = "Parent"
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "", category = curCategory, sort = sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }
        view.search_category_school_btn.setOnClickListener {
            curCategory = "School"
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "", category = curCategory, sort = sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }
        view.search_category_sex_btn.setOnClickListener {
            curCategory = "Sex"
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "", category = curCategory, sort = sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }
        view.search_category_study_btn.setOnClickListener {
            curCategory = "Study"
            RetrofitConnection.server.getPosts(userId = currentUser, page = 0, search = "", category = curCategory, sort = sort).enqueue(object:
                Callback<GetMyPostDTO> {
                override fun onResponse(call: Call<GetMyPostDTO>, response: Response<GetMyPostDTO>) {
                    Log.d("retrofit", response.body().toString())
                    postList!!.clear()
                    for (post in response.body()!!.content) {
                        postList!!.add(post)
                    }
                    Collections.reverse(postList)
                    myThumbnailAdapter!!.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<GetMyPostDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
        }

        return view
    }
}