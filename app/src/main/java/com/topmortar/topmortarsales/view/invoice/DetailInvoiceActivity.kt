package com.topmortar.topmortarsales.view.invoice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topmortar.topmortarsales.R
import com.topmortar.topmortarsales.adapter.InvoiceOrderRecyclerViewAdapter
import com.topmortar.topmortarsales.commons.CONST_INVOICE_ID
import com.topmortar.topmortarsales.commons.RESPONSE_STATUS_EMPTY
import com.topmortar.topmortarsales.commons.RESPONSE_STATUS_OK
import com.topmortar.topmortarsales.commons.TAG_RESPONSE_CONTACT
import com.topmortar.topmortarsales.commons.utils.SessionManager
import com.topmortar.topmortarsales.commons.utils.convertDpToPx
import com.topmortar.topmortarsales.commons.utils.handleMessage
import com.topmortar.topmortarsales.data.ApiService
import com.topmortar.topmortarsales.data.HttpClient
import com.topmortar.topmortarsales.model.DetailInvoiceModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailInvoiceActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    private lateinit var icBack: ImageView
    private lateinit var icSyncNow: ImageView
    private lateinit var tvTitleBar: TextView

    private lateinit var container: RelativeLayout

    private lateinit var previewInvoiceLayout: ScrollView
    private lateinit var tvReferenceNumber: TextView
    private lateinit var tvDeliveryDate: TextView
    private lateinit var tvShipToName: TextView
    private lateinit var tvShipToAddress: TextView
    private lateinit var tvShipToPhone: TextView
    private lateinit var tvDeliveryOrderDate: TextView
    private lateinit var tvDeliveryOrderNumber: TextView
    private lateinit var rvOrderList: RecyclerView
    private lateinit var tvCourier: TextView
    private lateinit var tvVehicle: TextView
    private lateinit var tvVehicleNumber: TextView
    private lateinit var tvReceivedBy: TextView
    private lateinit var tvReceivedDate: TextView

    private lateinit var btnPrint: Button
    private lateinit var btnClosing: Button
    private lateinit var lnrClosing: LinearLayout
//    private lateinit var imgPreview: ImageView
    private lateinit var txtLoading: TextView

    private var invoiceId: String? = null

    private var cameraPermissionLauncher: ActivityResultLauncher<String>? = null
    private var imagePicker: ActivityResultLauncher<Intent>? = null
    private var selectedUri: Uri? = null
    private var currentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        sessionManager = SessionManager(this)

        setContentView(R.layout.activity_detail_invoice)

        initVariable()
        initClickHandler()
        dataActivityValidation()

    }

    private fun initVariable() {

        icBack = findViewById(R.id.ic_back)
        icSyncNow = findViewById(R.id.ic_sync_now)
        tvTitleBar = findViewById(R.id.tv_title_bar)

        container = findViewById(R.id.container)

        previewInvoiceLayout = findViewById(R.id.preview_invoice_layout)
        tvReferenceNumber = previewInvoiceLayout.findViewById(R.id.tv_reference_number)
        tvDeliveryDate = previewInvoiceLayout.findViewById(R.id.tv_delivery_date)
        tvShipToName = previewInvoiceLayout.findViewById(R.id.tv_ship_to_name)
        tvShipToAddress = previewInvoiceLayout.findViewById(R.id.tv_ship_to_address)
        tvShipToPhone = previewInvoiceLayout.findViewById(R.id.tv_ship_to_phone)
        tvDeliveryOrderDate = previewInvoiceLayout.findViewById(R.id.tv_delivery_order_date)
        tvDeliveryOrderNumber = previewInvoiceLayout.findViewById(R.id.tv_delivery_order_number)
        rvOrderList = previewInvoiceLayout.findViewById(R.id.rv_order_list)
        tvCourier = previewInvoiceLayout.findViewById(R.id.tv_courier)
        tvVehicle = previewInvoiceLayout.findViewById(R.id.tv_vehicle)
        tvVehicleNumber = previewInvoiceLayout.findViewById(R.id.tv_vehicle_number)
        tvReceivedBy = previewInvoiceLayout.findViewById(R.id.tv_received_by)
        tvReceivedDate = previewInvoiceLayout.findViewById(R.id.tv_received_date)

        btnPrint = findViewById(R.id.btn_print_invoice)
        btnClosing = findViewById(R.id.btn_closing)
        lnrClosing = findViewById(R.id.lnr_closing)
//        imgPreview = findViewById(R.id.img_preview)
        txtLoading = findViewById(R.id.txt_loading)

        // Setup Title Bar
        icSyncNow.visibility = View.VISIBLE
        tvTitleBar.text = "Detail Surat Jalan"
        tvTitleBar.setPadding(0, 0, convertDpToPx(16, this), 0)

        // Setup Image Picker
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                chooseFile()
            } else {
                handleMessage(this@DetailInvoiceActivity, "CAMERA ACCESS DENIED", "Permission camera denied")
            }
        }
        imagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data == null || data.data == null) {
                    selectedUri = currentPhotoUri
//                    handleMessage(this@DetailInvoiceActivity, "IMAGE CONTENT", "$selectedUri")
//                    Handler().postDelayed({
//                        currentPhotoUri?.let {
//                            val contentResolver = contentResolver
//                            contentResolver.delete(it, null, null)
//                        }
//                    }, 2000)
                } else {
                    selectedUri = data.data
//                    handleMessage(this@DetailInvoiceActivity, "IMAGE CONTENT", "$selectedUri")
                }
//                imgPreview.setImageURI(selectedUri)
//                imgPreview.setOnClickListener { handleMessage(this@DetailInvoiceActivity, "IMAGE CONTENT", "$selectedUri") }
            }
        }

    }

    private fun initClickHandler() {
        icBack.setOnClickListener { backHandler() }
        icSyncNow.setOnClickListener { getDetail() }
        btnPrint.setOnClickListener { printNow() }
        btnClosing.setOnClickListener { chooseFile() }
        lnrClosing.setOnClickListener { chooseFile() }
    }

    private fun dataActivityValidation() {

        val iInvoiceId = intent.getStringExtra(CONST_INVOICE_ID)

        if (!iInvoiceId.isNullOrEmpty() ) {
            invoiceId = iInvoiceId
        }

        getDetail()

    }

    private fun getDetail() {

        loadingState(true)

        lifecycleScope.launch {
            try {

                val apiService: ApiService = HttpClient.create()
                val response = apiService.getInvoicesDetail(processNumber = "3", invoiceId = invoiceId!!)

                when (response.status) {
                    RESPONSE_STATUS_OK -> {

                        val data = response.results[0]

                        tvReferenceNumber.text = "${ data.no_surat_jalan }"
                        tvDeliveryDate.text = "${ data.dalivery_date }"
                        tvShipToName.text = "${ data.ship_to_name }"
                        tvShipToAddress.text = "${ data.ship_to_address }"
                        tvShipToPhone.text = "${ data.ship_to_phone }"
                        tvDeliveryOrderDate.text = "Delivery Date: ${ data.dalivery_date }"
                        tvDeliveryOrderNumber.text = "Order Number: ${ data.order_number }"
                        tvCourier.text = "Kurir: ${ data.courier_name }"
                        tvVehicle.text = "Kendaraan: ${ data.nama_kendaraan }"
                        tvVehicleNumber.text = "No. Polisi: ${ data.nopol_kendaraan }"
//                        if (isClosing) {
//                            tvReceivedBy.visibility = View.VISIBLE
//                            tvReceivedDate.visibility = View.VISIBLE
//                            tvReceivedBy.text = "${ data.ship_to_name }"
//                            tvReceivedDate.text = "${ data.dalivery_date }"
//                        }

                        setRecyclerView(response.results[0].details)
                        loadingState(false)
//                        loadingState(true, "Success get data!")

                    }
                    RESPONSE_STATUS_EMPTY -> {

                        loadingState(true, "Detail surat jalan kosong!")

                    }
                    else -> {

                        handleMessage(this@DetailInvoiceActivity, TAG_RESPONSE_CONTACT, "Failed get data")
                        loadingState(true, getString(R.string.failed_request))

                    }
                }

            } catch (e: Exception) {

                handleMessage(this@DetailInvoiceActivity, TAG_RESPONSE_CONTACT, "Failed run service. Exception " + e.message)
                loadingState(true, getString(R.string.failed_request))

            }

        }

    }


    private fun setRecyclerView(listItem: ArrayList<DetailInvoiceModel>) {
        val rvAdapter = InvoiceOrderRecyclerViewAdapter()
        rvAdapter.setListItem(listItem)

        rvOrderList.apply {
            layoutManager = LinearLayoutManager(this@DetailInvoiceActivity)
            adapter = rvAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                private var lastScrollPosition = 0

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy < 0) {
                        // Scrolled up
                        val firstVisibleItemPosition =
                            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        if (lastScrollPosition != firstVisibleItemPosition) {
                            recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition)?.itemView?.startAnimation(
                                AnimationUtils.loadAnimation(
                                    recyclerView.context,
                                    R.anim.rv_item_fade_slide_down
                                )
                            )
                            lastScrollPosition = firstVisibleItemPosition
                        }
                    } else lastScrollPosition = -1
                }
            })
        }
    }

    private fun loadingState(state: Boolean, message: String = getString(R.string.txt_loading)) {

        txtLoading.text = message

        if (state) {

            txtLoading.visibility = View.VISIBLE
            container.visibility = View.GONE

        } else {

            txtLoading.visibility = View.GONE
            container.visibility = View.VISIBLE

        }

    }

    private fun printNow() {}

    private fun chooseFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Create a file to store the captured image
            val photoFile: File? = createImageFile()

            if (photoFile != null) {
                val photoUri: Uri = FileProvider.getUriForFile(this, "com.topmortar.topmortarsales.fileprovider", photoFile)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                currentPhotoUri = photoUri
            }

            val chooserIntent = Intent.createChooser(galleryIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

            imagePicker!!.launch(chooserIntent)
        } else {
            cameraPermissionLauncher!!.launch(Manifest.permission.CAMERA)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir("Invoices")
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun backHandler() {
        finish()
    }

    override fun onBackPressed() {
        backHandler()
    }
}