package com.woody.cat.holic.presentation.main.notification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogNotificationListBinding
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import dagger.android.support.DaggerDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationListDialog : DaggerDialogFragment() {

    companion object {
        fun newInstance(fragmentManager: FragmentManager) {
            if (fragmentManager.findFragmentByTag(NotificationListDialog::class.java.name) == null) {
                NotificationListDialog().show(fragmentManager, NotificationListDialog::class.java.name)
            }
        }
    }

    private lateinit var binding: DialogNotificationListBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var notificationListViewModel: NotificationListViewModel

    private lateinit var notificationListAdapter: NotificationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<DialogNotificationListBinding>(inflater, R.layout.dialog_notification_list, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationListViewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(NotificationListViewModel::class.java).apply {
            notificationListAdapter = NotificationListAdapter(viewLifecycleOwner, this)

            eventHandleDeepLink.observeEvent(this@NotificationListDialog, { deepLink ->
                context?.let { context ->
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        `package` = context.packageName
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        data = Uri.parse(deepLink)
                        startActivity(this)
                        dismiss()
                    }
                }
            })

            viewLifecycleOwner.lifecycleScope.launch {
                getNotificationFlow().collectLatest { pagingData ->
                    notificationListAdapter.submitData(pagingData)
                }
            }
        }
        binding.rvNotification.adapter = notificationListAdapter
    }
}