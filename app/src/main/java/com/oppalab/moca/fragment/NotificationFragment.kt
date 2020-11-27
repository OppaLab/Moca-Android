package com.oppalab.moca.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca.R
import com.oppalab.moca.adapter.NotificationAdapter
import com.oppalab.moca.adapter.PostAdapterRetro
import com.oppalab.moca.dto.FeedsAtHome
import com.oppalab.moca.dto.GetFeedsAtHomeDTO
import com.oppalab.moca.dto.GetNotificationsDTO
import com.oppalab.moca.dto.NotificationsDTO
import com.oppalab.moca.util.PreferenceManager
import com.oppalab.moca.util.RetrofitConnection
import kotlinx.android.synthetic.main.activity_add_post.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment : Fragment() {

    private var notificationList : MutableList<NotificationsDTO>? = null
    private var notificationAdapter: NotificationAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        var recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.notification_recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)


        notificationList = ArrayList()


        notificationAdapter = context?.let { NotificationAdapter(it, notificationList as ArrayList<NotificationsDTO>) }
        recyclerView.adapter = notificationAdapter


        readNotifications()
        return view
    }

    private fun readNotifications() {

        RetrofitConnection.server.getNotifications(
            userId = PreferenceManager.getLong(
                requireContext(),
                "userId"
            ), page = 0
        ).enqueue(
            object : Callback<GetNotificationsDTO> {
                override fun onResponse(
                    call: Call<GetNotificationsDTO>,
                    response: Response<GetNotificationsDTO>
                ) {
                    Log.d("Notifications", response.body().toString())

                    notificationList?.clear()

                    for (notification in response.body()!!.content) {
                        val curNotification = notification
                        notificationList!!.add(curNotification)
                        notificationAdapter!!.notifyDataSetChanged()
                    }

                    if (response.body()!!.last == true) {
                        Toast.makeText(context, "마지막 페이지 입니다.", Toast.LENGTH_LONG)
                    }

                }

                override fun onFailure(call: Call<GetNotificationsDTO>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }
            })
    }
}
