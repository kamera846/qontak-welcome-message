package com.topmortar.topmortarsales.view.reports

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.topmortar.topmortarsales.R
import com.topmortar.topmortarsales.adapter.recyclerview.ReportsRecyclerViewAdapter
import com.topmortar.topmortarsales.commons.CONST_CONTACT_ID
import com.topmortar.topmortarsales.commons.CONST_NAME
import com.topmortar.topmortarsales.commons.RESPONSE_STATUS_EMPTY
import com.topmortar.topmortarsales.commons.RESPONSE_STATUS_FAIL
import com.topmortar.topmortarsales.commons.RESPONSE_STATUS_FAILED
import com.topmortar.topmortarsales.commons.RESPONSE_STATUS_OK
import com.topmortar.topmortarsales.commons.TAG_RESPONSE_MESSAGE
import com.topmortar.topmortarsales.commons.utils.SessionManager
import com.topmortar.topmortarsales.commons.utils.handleMessage
import com.topmortar.topmortarsales.data.ApiService
import com.topmortar.topmortarsales.data.HttpClient
import com.topmortar.topmortarsales.databinding.ActivityReportsBinding
import com.topmortar.topmortarsales.modal.DetailReportModal
import com.topmortar.topmortarsales.model.ReportVisitModel
import kotlinx.coroutines.launch

class ReportsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivityReportsBinding

    private val userID get() = sessionManager.userID().toString()
    private var contactID = ""
    private var contactName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.hide()
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        contactID = intent.getStringExtra(CONST_CONTACT_ID).toString()
        contactName = intent.getStringExtra(CONST_NAME).toString()

        binding.titleBarDark.tvTitleBar.text = contactName
        binding.titleBarDark.tvTitleBarDescription.visibility = View.VISIBLE
        binding.titleBarDark.tvTitleBarDescription.text = "Daftar laporan saya pada toko ini"

        getList()
        initClickHandler()

    }

    private fun getList() {
        loadingState(true)

        lifecycleScope.launch {
            try {
                val apiService: ApiService = HttpClient.create()
                val response = apiService.listReport(idUser = userID, idContact = contactID)

                if (response.isSuccessful) {

                    val responseBody = response.body()!!

                    when (responseBody.status) {
                        RESPONSE_STATUS_OK -> {

                            setRecyclerView(responseBody.results)
                            loadingState(false)

                        }
                        RESPONSE_STATUS_EMPTY -> {

                            loadingState(true, "Belum ada laporan")

                        }
                        RESPONSE_STATUS_FAIL, RESPONSE_STATUS_FAILED -> {

                            val message = "Gagal memuat laporan! Message: ${ responseBody.message }"
                            handleMessage(this@ReportsActivity, TAG_RESPONSE_MESSAGE, message)
                            loadingState(true, message)

                        }
                        else -> {

                            val message = "Gagal memuat laporan!: ${ responseBody.message }"
                            handleMessage(this@ReportsActivity, TAG_RESPONSE_MESSAGE, message)
                            loadingState(true, message)

                        }
                    }

                } else {

                    val message = "Gagal memuat laporan! Error: " + response.message()
                    handleMessage(this@ReportsActivity, TAG_RESPONSE_MESSAGE, message)
                    loadingState(true, message)

                }

            } catch (e: Exception) {

                val message = "Failed run service. Exception " + e.message
                handleMessage(this@ReportsActivity, TAG_RESPONSE_MESSAGE, message)
                loadingState(true, message)

            }

        }

    }

    private fun setRecyclerView(items: ArrayList<ReportVisitModel>) {

        val mAdapter = ReportsRecyclerViewAdapter()
        mAdapter.setList(items)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@ReportsActivity, 2)
            adapter = mAdapter

            mAdapter.setOnItemClickListener(object : ReportsRecyclerViewAdapter.OnItemClickListener{
                override fun onItemClick(item: ReportVisitModel) {
                    val modalDetail = DetailReportModal(this@ReportsActivity)
                    modalDetail.setData(item)
                    modalDetail.show()
                }

            })
        }
    }

    private fun loadingState(state: Boolean, message: String = getString(R.string.txt_loading)) {

        binding.txtLoading.text = message

        if (state) {

            binding.txtLoading.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE

        } else {

            binding.txtLoading.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

        }

    }

    private fun initClickHandler() {

        binding.titleBarDark.icBack.setOnClickListener { finish() }

    }
}