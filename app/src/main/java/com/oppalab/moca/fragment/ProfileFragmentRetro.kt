package com.oppalab.moca.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.oppalab.moca.AccountSettingActivity
import com.oppalab.moca.R
import com.oppalab.moca.adapter.MyThumbnailAdapter
import com.oppalab.moca.dto.GetPostDTO
import com.oppalab.moca.dto.GetProfileDTO
import com.oppalab.moca.dto.MyPostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProfileFragmentRetro : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private var currentUser: Long = 0L
    private var postList: MutableList<MyPostDTO>? = null
    private var myProfile: MutableList<GetProfileDTO>? = null
    private var myThumbnailAdapter: MyThumbnailAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        currentUser = PreferenceManager.getLong(requireContext(), "userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid) {
            edit_account_setting_button.text = "프로필 수정"
        } else if (profileId == firebaseUser.uid) {
            edit_account_setting_button.text = "팔로우"
            checkFollowAndFollowing()
        }
        var recyclerview_mypost_thumbnail: RecyclerView
        recyclerview_mypost_thumbnail = view.findViewById(R.id.recyclerview_grid_view)
        recyclerview_mypost_thumbnail.setHasFixedSize(true)
        postList = ArrayList()
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerview_mypost_thumbnail.layoutManager = linearLayoutManager

        myThumbnailAdapter = context?.let { MyThumbnailAdapter(it, postList as ArrayList<MyPostDTO>) }
        recyclerview_mypost_thumbnail.adapter = myThumbnailAdapter

        var circleimageview_thumbmail: CircleImageView
        circleimageview_thumbmail = view.findViewById(R.id.profile_profile_image)


        RetrofitConnection.server.getProfile(userId = currentUser).enqueue(object :
            Callback<GetProfileDTO> {
            override fun onResponse(call: Call<GetProfileDTO>, response: Response<GetProfileDTO>) {
                Log.d("retrofit", response.body().toString())
                val profile: GetProfileDTO = myProfile!![1]
                Picasso.get().load(RetrofitConnection.URL+"image/profile/"+profile.profileImageFilePath).into(circleimageview_thumbmail)


            }

            override fun onFailure(call: Call<GetProfileDTO>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
            }

        })





        RetrofitConnection.server.getPosts(userId = currentUser, page = 0, category = "", search = "").enqueue(object:
            Callback<GetPostDTO> {
            override fun onResponse(call: Call<GetPostDTO>, response: Response<GetPostDTO>) {
                Log.d("retrofit", response.body().toString())
                postList!!.clear()
                for (post in response.body()!!.content) {
                    postList!!.add(post)
                }
                Collections.reverse(postList)
                myThumbnailAdapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<GetPostDTO>, t: Throwable) {
            }

        })

        view.edit_account_setting_button.setOnClickListener {
            startActivity(Intent(context, AccountSettingActivity::class.java))
        }

        return view
    }


    private fun checkFollowAndFollowing() {
        val follwingRef = firebaseUser?.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")

//            if ()
        }
    }
}