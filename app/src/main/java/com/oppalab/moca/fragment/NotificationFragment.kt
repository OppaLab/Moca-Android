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
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment : Fragment() {

    private var notificationList : MutableList<NotificationsDTO>? = null
    private var notificationAdapter: NotificationAdapter? = null
    private var curPage: Long = 0L
    private var currentUser: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        currentUser= PreferenceManager.getLong(requireContext(),"userId")

        var recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.notification_recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)


        notificationList = ArrayList()


        notificationAdapter = context?.let { NotificationAdapter(it, notificationList as ArrayList<NotificationsDTO>) }
        recyclerView.adapter = notificationAdapter
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount -1
                if (lastVisibleItemPosition == itemTotalCount) {
                    curPage += 1
                    notification_progress_bar.visibility = View.VISIBLE
                    readNotificationsMore()
                }
            }
        })

        readNotifications()
        return view
    }

    private fun readNotifications() {

        RetrofitConnection.server.getNotifications(
            userId = currentUser,
            page = curPage
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
                        if(curNotification.userId.toString() != currentUser.toString()) {
                            notificationList!!.add(curNotification)
                        }
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

    private fun readNotificationsMore() {

        RetrofitConnection.server.getNotifications(
            userId = currentUser,
            page = curPage
        ).enqueue(
            object : Callback<GetNotificationsDTO> {
                override fun onResponse(
                    call: Call<GetNotificationsDTO>,
                    response: Response<GetNotificationsDTO>
                ) {
                    Log.d("Notifications", response.body().toString())

                    for (notification in response.body()!!.content) {
                        val curNotification = notification
                        if(curNotification.userId.toString() != currentUser.toString()) {
                            notificationList!!.add(curNotification)
                        }
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
