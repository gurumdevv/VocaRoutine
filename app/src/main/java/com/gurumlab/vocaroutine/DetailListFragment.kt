package com.gurumlab.vocaroutine

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.databinding.FragmentDetailListBinding
import java.util.GregorianCalendar
import java.util.Calendar
import java.util.Locale

class DetailListFragment : BaseFragment<FragmentDetailListBinding>() {

    private lateinit var alarmManger: AlarmManager
    private lateinit var mCalender: GregorianCalendar
    private lateinit var notificationManager: NotificationManager
    private lateinit var alarmFunctions: AlarmFunctions
    private var isNotificationSet = false

    private val args: DetailListFragmentArgs by navArgs()
    private lateinit var list: MyList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = args.list
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailListBinding {
        return FragmentDetailListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation(false)

        alarmFunctions = AlarmFunctions(requireContext())
        notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        alarmManger = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mCalender = GregorianCalendar()

        binding!!.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding!!.tvCurrentTitle.text = list.name

        val detailListAdapter = DetailListAdapter()
        binding!!.rvDetailList.adapter = detailListAdapter
        detailListAdapter.submitList(list.vocabularies)

        val alarmCode = 231103001 //해당 값은 서버에서 받아올 수 있도록 수정해야함(업로드 구현시 데이터 구조와 함께 수정)

        binding!!.ivSetNotification.setOnClickListener {
            if (!isNotificationSet) {
                val date = getDate()
                val content = list.name

                isNotificationSet = setAlarm(alarmCode, content, date)
                if (isNotificationSet) {
                    binding!!.ivSetNotification.setImageResource(R.drawable.ic_bell_enabled)
                    Snackbar.make(
                        requireView(),
                        getString(R.string.set_review_notification_success), Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.set_review_notification_fail), Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                cancelAlarm(alarmCode)
            }
        }
    }

    private fun getDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.getDefault(), "%d-%02d-%02d 18:00:00", year, month, day)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }

    private fun setAlarm(alarmCode: Int, content: String, date: String): Boolean {
        return alarmFunctions.callAlarm(date, alarmCode, content)
    }

    private fun cancelAlarm(alarmCode: Int) {
        alarmFunctions.cancelAlarm(alarmCode)
        binding!!.ivSetNotification.setImageResource(R.drawable.ic_bell_disabled)
        Snackbar.make(
            requireView(),
            getString(R.string.cancel_review_notification_success), Snackbar.LENGTH_SHORT
        ).show()
    }
}