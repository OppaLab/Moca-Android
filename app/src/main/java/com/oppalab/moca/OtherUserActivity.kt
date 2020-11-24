package com.oppalab.moca

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.oppalab.moca.adapter.MyThumbnailAdapter
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.dto.GetProfileDTO
import com.oppalab.moca.dto.MyPostDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OtherUserActivity : AppCompatActivity() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private var currentUser: Long = 0L
    private var postList: MutableList<MyPostDTO>? = null
    private var myThumbnailAdapter: MyThumbnailAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user)

        Toast.makeText(this, "OtherUserActivity", Toast.LENGTH_LONG)
            .show()


    }
}