package com.topmortar.topmortarsales.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.topmortar.topmortarsales.R
import com.topmortar.topmortarsales.commons.utils.DateFormat
import com.topmortar.topmortarsales.model.InvoiceModel

class InvoiceRecyclerViewAdapter(private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<InvoiceRecyclerViewAdapter.ViewHolder>() {
    private var listItem: ArrayList<InvoiceModel> = ArrayList()
    private var context: Context? = null

    interface ItemClickListener {
        fun onItemInvoiceClick(data: InvoiceModel? = null)
    }

    fun setListItem(listItem: ArrayList<InvoiceModel>) {
        this.listItem = listItem
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivProfile: ImageView = itemView.findViewById(R.id.iv_contact_profile)
        private val tooltipStatus: ImageView = itemView.findViewById(R.id.tooltip_status)
        private val tvContactName: TextView = itemView.findViewById(R.id.tv_contact_name)
        private val tvPhoneNumber: TextView = itemView.findViewById(R.id.tv_phone_number)

        fun bind(item: InvoiceModel) {

            tooltipStatus.visibility = View.VISIBLE

            ivProfile.setImageResource(R.drawable.invoice_red)
            tvContactName.text = item.no_invoice
            tvPhoneNumber.text = DateFormat.format(dateString = item.date_invoice!!, input = "yyyy-MM-dd hh:mm:ss", format = "dd MMMM yyyy hh.mm")
            when (item.status_invoice) {
                "waiting" -> {
                    tooltipStatus.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.status_data) })
                    tooltipHandler(tooltipStatus, "Unpaid invoice")
                } else -> {
                    tooltipStatus.setImageDrawable(context?.let { ContextCompat.getDrawable(it, R.drawable.status_active) })
                    tooltipHandler(tooltipStatus, "Invoice has been paid")
                }
            }

        }

        private fun tooltipHandler(content: ImageView, text: String) {
            content.setOnLongClickListener {
                TooltipCompat.setTooltipText(content, text)
                false
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room, parent, false)
        context = parent.context
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listItem[position]

        holder.bind(item)
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_item_fade_slide_up))

        holder.itemView.setOnClickListener {

            if (position != RecyclerView.NO_POSITION) {
                val animateDuration = 200L

                val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                fadeIn.duration = animateDuration

                val overlayView = holder.itemView.findViewById<LinearLayout>(R.id.overlay_view)

                overlayView.alpha = 0.7f
                overlayView.visibility = View.VISIBLE
                overlayView.startAnimation(fadeIn)

                Handler().postDelayed({
                    overlayView.alpha = 0f
                    overlayView.visibility = View.GONE
                }, animateDuration)

                val data = listItem[position]
                itemClickListener.onItemInvoiceClick(data)

            }

        }

    }

    override fun getItemCount(): Int = listItem.size
}