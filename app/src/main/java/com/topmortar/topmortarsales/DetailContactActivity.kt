package com.topmortar.topmortarsales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.topmortar.topmortarsales.commons.CONST_BIRTHDAY
import com.topmortar.topmortarsales.commons.CONST_CONTACT_ID
import com.topmortar.topmortarsales.commons.CONST_NAME
import com.topmortar.topmortarsales.commons.CONST_OWNER
import com.topmortar.topmortarsales.commons.CONST_PHONE
import com.topmortar.topmortarsales.commons.LOGGED_OUT
import com.topmortar.topmortarsales.commons.MAIN_ACTIVITY_REQUEST_CODE
import com.topmortar.topmortarsales.commons.RESPONSE_STATUS_OK
import com.topmortar.topmortarsales.commons.SYNC_NOW
import com.topmortar.topmortarsales.commons.TAG_RESPONSE_MESSAGE
import com.topmortar.topmortarsales.commons.utils.DateFormat
import com.topmortar.topmortarsales.commons.utils.createPartFromString
import com.topmortar.topmortarsales.commons.utils.handleMessage
import com.topmortar.topmortarsales.data.ApiService
import com.topmortar.topmortarsales.data.HttpClient
import kotlinx.coroutines.launch
import java.util.Calendar

@Suppress("DEPRECATION")
class DetailContactActivity : AppCompatActivity() {

    private lateinit var tvPhoneContainer: LinearLayout
    private lateinit var tvBirthdayContainer: LinearLayout
    private lateinit var etPhoneContainer: LinearLayout
    private lateinit var etBirthdayContainer: LinearLayout
    private lateinit var tvOwnerContainer: LinearLayout
    private lateinit var etOwnerContainer: LinearLayout
    private lateinit var icBack: ImageView
    private lateinit var icEdit: ImageView
    private lateinit var tooltipOwner: ImageView
    private lateinit var tvTitleBar: TextView
    private lateinit var tvCancelEdit: TextView
    private lateinit var tvName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvBirthday: TextView
    private lateinit var tvEditBirthday: TextView
    private lateinit var etName: EditText
    private lateinit var tvOwner: TextView
    private lateinit var etOwner: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSendMessage: Button
    private lateinit var btnSaveEdit: Button

    private var contactId: String? = null
    private var isEdit: Boolean = false
    private var selectedDate: Calendar = Calendar.getInstance()
    private var hasEdited: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_detail_contact)

        initVariable()
        initClickHandler()
        dataActivityValidation()

    }

    private fun initVariable() {

        tvPhoneContainer = findViewById(R.id.tv_phone_container)
        tvBirthdayContainer = findViewById(R.id.tv_birthday_container)
        etPhoneContainer = findViewById(R.id.et_phone_container)
        etBirthdayContainer = findViewById(R.id.et_birthday_container)
        tvOwnerContainer = findViewById(R.id.tv_owner_container)
        etOwnerContainer = findViewById(R.id.et_owner_container)
        tvOwner = findViewById(R.id.tv_owner)
        tvName = findViewById(R.id.tv_name)
        tvPhone = findViewById(R.id.tv_phone)
        icBack = findViewById(R.id.ic_back)
        icEdit = findViewById(R.id.ic_edit)
        tooltipOwner = findViewById(R.id.tooltip_owner)
        tvTitleBar = findViewById(R.id.tv_title_bar)
        tvCancelEdit = findViewById(R.id.tv_cancel_edit)
        tvName = findViewById(R.id.tv_name)
        tvDescription = findViewById(R.id.tv_description)
        tvPhone = findViewById(R.id.tv_phone)
        tvBirthday = findViewById(R.id.tv_birthday)
        etName = findViewById(R.id.et_name)
        etOwner = findViewById(R.id.et_owner)
        etPhone = findViewById(R.id.et_phone)
        tvEditBirthday = findViewById(R.id.tv_edit_birthday)
        btnSendMessage = findViewById(R.id.btn_send_message)
        btnSaveEdit = findViewById(R.id.btn_save_edit)

    }

    private fun initClickHandler() {

        icBack.setOnClickListener { backHandler() }
        icEdit.setOnClickListener { toggleEdit(true) }
        tvCancelEdit.setOnClickListener { toggleEdit(false) }
        btnSendMessage.setOnClickListener { navigateAddNewRoom() }
        btnSaveEdit.setOnClickListener { editConfirmation() }
        etBirthdayContainer.setOnClickListener { showDatePickerDialog() }
        tvEditBirthday.setOnClickListener { showDatePickerDialog() }
        tooltipOwner.setOnClickListener {
            val tooltipText = "Store owner name"
            TooltipCompat.setTooltipText(tooltipOwner, tooltipText)
        }

    }

    private fun dataActivityValidation() {

        val iContactId = intent.getStringExtra(CONST_CONTACT_ID)
        val iOwner = intent.getStringExtra(CONST_OWNER)
        val iPhone = intent.getStringExtra(CONST_PHONE)
        val iName = intent.getStringExtra(CONST_NAME)
        val iBirthday = intent.getStringExtra(CONST_BIRTHDAY)

        if (!iContactId.isNullOrEmpty() ) {
            contactId = iContactId
        }
        if (!iOwner.isNullOrEmpty() ) {
            tvOwner.text = "+$iOwner"
            etOwner.setText(iOwner)
        }
        if (!iPhone.isNullOrEmpty() ) {
            tvPhone.text = "+$iPhone"
            etPhone.setText(iPhone)
        }
        if (!iName.isNullOrEmpty() ) {
            tvName.text = iName
            etName.setText(iName)
        }
        if (!iBirthday.isNullOrEmpty() ) {
            tvBirthday.text = DateFormat.format(iBirthday)
            tvEditBirthday.text = DateFormat.format(iBirthday)
        }

    }

    private fun toggleEdit(value: Boolean? = null) {

        isEdit = if (value!!) value else !isEdit

        if (isEdit) {

            tvBirthdayContainer.visibility = View.GONE
            tvOwnerContainer.visibility = View.GONE
            tvName.visibility = View.GONE
            tvDescription.visibility = View.GONE
            icEdit.visibility = View.GONE
            btnSendMessage.visibility = View.GONE

            etBirthdayContainer.visibility = View.VISIBLE
            etOwnerContainer.visibility = View.VISIBLE
            tvCancelEdit.visibility = View.VISIBLE
            etName.visibility = View.VISIBLE
            btnSaveEdit.visibility = View.VISIBLE

            tvTitleBar.text = "Edit Contact"
            etName.requestFocus()
            etName.setSelection(etName.text.length)

        } else {

            tvBirthdayContainer.visibility = View.VISIBLE
            tvOwnerContainer.visibility = View.VISIBLE
            tvName.visibility = View.VISIBLE
            tvDescription.visibility = View.VISIBLE
            icEdit.visibility = View.VISIBLE
            btnSendMessage.visibility = View.VISIBLE

            etBirthdayContainer.visibility = View.GONE
            etOwnerContainer.visibility = View.GONE
            tvCancelEdit.visibility = View.GONE
            etName.visibility = View.GONE
            btnSaveEdit.visibility = View.GONE

            tvTitleBar.text = "Detail Contact"
            etName.clearFocus()

        }

    }

    private fun editConfirmation() {

        if (!formValidation("${ etName.text }", "${ tvEditBirthday.text }")) return

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Confirmation")
            .setMessage("Are you sure you want to save changes?")
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                saveEdit()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveEdit() {

        val pName = "${ etName.text }"
        val pBirthday = DateFormat.format("${ tvEditBirthday.text }", "dd MMMM yyyy", "yyyy-MM-dd")

        loadingState(true)

        lifecycleScope.launch {
            try {

                val rbId = createPartFromString(contactId!!)
                val rbName = createPartFromString(pName)
                val rbBirthday = createPartFromString(pBirthday)

                val apiService: ApiService = HttpClient.create()
                val response = apiService.editContact(rbId, rbName, rbBirthday)

                if (response.isSuccessful) {

                    val responseBody = response.body()!!

                    if (responseBody.status == RESPONSE_STATUS_OK) {

                        tvName.text = "${ etName.text }"
                        tvBirthday.text = "${ tvEditBirthday.text }"
                        hasEdited = true

                        handleMessage(this@DetailContactActivity, TAG_RESPONSE_MESSAGE, "Successfully edit data!")
                        loadingState(false)
                        toggleEdit(false)

                    } else {

                        handleMessage(this@DetailContactActivity, TAG_RESPONSE_MESSAGE, "Failed to edit data!")
                        loadingState(false)
                        toggleEdit(false)

                    }

                } else {

                    handleMessage(this@DetailContactActivity, TAG_RESPONSE_MESSAGE, "Failed to edit data! Error: " + response.message())
                    loadingState(false)
                    toggleEdit(false)

                }


            } catch (e: Exception) {

                handleMessage(this@DetailContactActivity, TAG_RESPONSE_MESSAGE, "Failed run service. Exception " + e.message)
                loadingState(false)
                toggleEdit(false)

            }

        }

    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, day)

                // Do something with the selected date
                val formattedDate = DateFormat.format(selectedDate)
                tvEditBirthday.text = formattedDate
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun loadingState(state: Boolean) {

        btnSaveEdit.setTextColor(ContextCompat.getColor(this, R.color.white))
        btnSaveEdit.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_200))

        if (state) {

            btnSaveEdit.isEnabled = false
            btnSaveEdit.text = "LOADING..."

        } else {

            btnSaveEdit.isEnabled = true
            btnSaveEdit.text = "SAVE"
            btnSaveEdit.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))

        }

    }

    private fun formValidation(name: String, birthday: String): Boolean {
        return if (name.isEmpty()) {
            etName.error = "Name cannot be empty!"
            false
        } else if (birthday.isEmpty()) {
            etName.error = null
            handleMessage(this@DetailContactActivity, "ERROR EDIT CONTACT", "Choose a birthday")
            false
        } else {
            etName.error = null
            true
        }
    }

    private fun backHandler() {

        if (isEdit) toggleEdit(false)
        else {

            if (hasEdited) {

                val resultIntent = Intent()
                resultIntent.putExtra("$MAIN_ACTIVITY_REQUEST_CODE", SYNC_NOW)
                setResult(RESULT_OK, resultIntent)
                finish()

            } else finish()

        }

    }

    private fun navigateAddNewRoom() {

        val intent = Intent(this@DetailContactActivity, NewRoomChatFormActivity::class.java)

        intent.putExtra(CONST_NAME, tvName.text)
        // Remove "+" on text phone
        val trimmedInput = tvPhone.text.trim()
        if (trimmedInput.startsWith("+")) intent.putExtra(CONST_PHONE, trimmedInput.substring(1))

        startActivity(intent)

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == MAIN_ACTIVITY_REQUEST_CODE) {
//
//            val resultData = data?.getStringExtra("$MAIN_ACTIVITY_REQUEST_CODE")
//
//            if (resultData == SYNC_NOW) {
//
//                finish()
//
//            }
//
//        }
//    }

    override fun onBackPressed() {
//      return super.onBackPressed()
        backHandler()
    }

}