package com.topmortar.topmortarsales.modal

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.topmortar.topmortarsales.R
import com.topmortar.topmortarsales.commons.utils.CustomUtility
import com.topmortar.topmortarsales.commons.utils.convertDpToPx
import com.topmortar.topmortarsales.databinding.ModalFilterTokoBinding
import com.topmortar.topmortarsales.model.CityModel

class FilterTokoModal(private val context: Context) : Dialog(context) {

    private lateinit var customUtility: CustomUtility
    private lateinit var binding: ModalFilterTokoBinding

    private var statuses: ArrayList<String> = arrayListOf("Data", "Passive", "Active", "Bid", "Blacklist", "Not Set")
    private var selectedStatusID: String = "-1"
    fun setStatuses(selected: String = "-1") {
        selectedStatusID = selected
    }

    private var visited: ArrayList<String> = arrayListOf("Unvisited", "Visited")
    private var selectedVisitedID: String = "-1"
    fun setVisited(selected: String = "-1") {
        selectedVisitedID = selected
    }

    private var cities: ArrayList<CityModel> = arrayListOf()
    private var selectedCitiesID: String = "-1"
    fun setCities(items: ArrayList<CityModel> = arrayListOf(), selected: String = "-1") {
        cities = items
        selectedCitiesID = selected
    }

    interface SendFilterListener {
        fun onSendFilter(selectedStatusID: String, selectedVisitedID: String, selectedCitiesID: String)
    }

    private var listener: SendFilterListener? = null
    fun setSendFilterListener(listener: SendFilterListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ModalFilterTokoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customUtility = CustomUtility(context)

        setLayout()
        initClickHandler()
        setupFilterStatuses()
        setupFilterVisited()
        setupFilterCities()
    }

    private fun setLayout() {
        val displayMetrics = DisplayMetrics()
        window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        val widthPercentage = 0.9f // Set the width percentage (e.g., 90%)

        val width = (screenWidth * widthPercentage).toInt()

        val layoutParams = window?.attributes
        layoutParams?.width = width
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT // Set height to wrap content
        window?.attributes = layoutParams as WindowManager.LayoutParams

        val titleBar = binding.titleBarLight
        titleBar.tvTitleBar.text = "Filter Toko"
        titleBar.tvTitleBar.setPadding(convertDpToPx(16, context),0,0,0)

    }

    private fun initClickHandler() {
        val titleBar = binding.titleBarLight
        titleBar.icBack.visibility = View.GONE
        titleBar.icClose.visibility = View.VISIBLE
        titleBar.icClose.setOnClickListener { this@FilterTokoModal.dismiss() }
        binding.btnFilter.setOnClickListener {
            listener!!.onSendFilter(selectedStatusID, selectedVisitedID, selectedCitiesID)
            this@FilterTokoModal.dismiss()
        }
    }

    private fun setupFilterStatuses() {
        binding.filterStatusesContainer.visibility = View.VISIBLE
        val flexBoxCities = binding.flexboxStatus
        val margin = convertDpToPx(2, context)
        val paddingVertical = convertDpToPx(6, context)
        val paddingHorizontal = convertDpToPx(10, context)

        for (item in statuses.listIterator()) {
            val textView = TextView(context)
            textView.text = item
            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            textView.gravity = Gravity.CENTER
            textView.setTextColor(context.getColor(
                if (item == selectedStatusID) R.color.white
                else {
                    if (customUtility.isDarkMode()) R.color.black_600
                    else R.color.black_200
                }
            ))
            textView.setBackgroundResource(
                if (item == selectedStatusID) R.drawable.bg_primary_round
                else R.drawable.bg_border_round
            )
            textView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            layoutParams.setMargins(margin, margin, margin, margin)
            textView.layoutParams = layoutParams

            textView.setOnClickListener{
                selectedStatusID = if (item == selectedStatusID) "-1"
                else item
                flexBoxCities.removeAllViews()
                setupFilterStatuses()
            }

            flexBoxCities.addView(textView)
        }
    }

    private fun setupFilterVisited() {
        binding.filterVisitedContainer.visibility = View.VISIBLE
        val flexBoxCities = binding.flexboxVisited
        val margin = convertDpToPx(2, context)
        val paddingVertical = convertDpToPx(6, context)
        val paddingHorizontal = convertDpToPx(10, context)

        for (item in visited.listIterator()) {
            val textView = TextView(context)
            textView.text = item
            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            textView.gravity = Gravity.CENTER
            textView.setTextColor(context.getColor(
                if (item == selectedVisitedID) R.color.white
                else {
                    if (customUtility.isDarkMode()) R.color.black_600
                    else R.color.black_200
                }
            ))
            textView.setBackgroundResource(
                if (item == selectedVisitedID) R.drawable.bg_primary_round
                else R.drawable.bg_border_round
            )
            textView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            layoutParams.setMargins(margin, margin, margin, margin)
            textView.layoutParams = layoutParams

            textView.setOnClickListener{
                selectedVisitedID = if (item == selectedVisitedID) "-1"
                else item
                flexBoxCities.removeAllViews()
                setupFilterVisited()
            }

            flexBoxCities.addView(textView)
        }
    }

    private fun setupFilterCities() {
        binding.filterCityContainer.visibility = View.VISIBLE
        val flexBoxCities = binding.flexboxCities
        val margin = convertDpToPx(2, context)
        val paddingVertical = convertDpToPx(6, context)
        val paddingHorizontal = convertDpToPx(10, context)

        for (item in cities.listIterator()) {
            val textView = TextView(context)
            textView.text = item.nama_city
            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            textView.gravity = Gravity.CENTER
            textView.setTextColor(context.getColor(
                if (item.id_city == selectedCitiesID) R.color.white
                else {
                    if (customUtility.isDarkMode()) R.color.black_600
                    else R.color.black_200
                }
            ))
            textView.setBackgroundResource(
                if (item.id_city == selectedCitiesID) R.drawable.bg_primary_round
                else R.drawable.bg_border_round
            )
            textView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            layoutParams.setMargins(margin, margin, margin, margin)
            textView.layoutParams = layoutParams

            textView.setOnClickListener{
                selectedCitiesID = if (item.id_city == selectedCitiesID) "-1"
                else item.id_city
                flexBoxCities.removeAllViews()
                setupFilterCities()
            }

            flexBoxCities.addView(textView)
        }
    }
}