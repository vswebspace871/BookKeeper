package com.example.bookkeeper.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookkeeper.R
import com.example.bookkeeper.databinding.FragmentTransactionBinding
import com.example.bookkeeper.databinding.TransactionItemBinding
import com.example.bookkeeper.entity.Entry
import java.text.SimpleDateFormat

class TransactionAdapter(
    private val listOfTransaction: List<Entry>,
    val context: Context?,
    val listener: listListener,
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val RESULT_THRESHHOLD = 20

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = TransactionItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfTransaction[position])
        // find last item
        // if last item then load next 20 results
        //Log.d("TAG", "onBindViewHolder1: "+listEnd)
        if ((itemCount - 1) == position && itemCount % RESULT_THRESHHOLD == 0) {
            //last item loaded Add next 20
            //listEnd = true
            if (listOfTransaction[position].id != null) {
                listener.loadNext(listOfTransaction[position].id!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfTransaction.size
    }

    inner class ViewHolder(val binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Entry) {
            /*setting date to textView in perfect pattern */
            val dateFormated = SimpleDateFormat("dd MMM, yyyy").format(item.dateAndTime!!)
            binding.tvDate.text = dateFormated

            binding.tvPrice.text = item.amount
            binding.tvRemark.text = item.remarks
            if (item.payment_type == "credit") {
                binding.tvDrCr.text = "Cr"
                binding.tvDrCr.setTextColor(Color.parseColor("#04A10B"))
            }
            if (item.payment_type == "debit") {
                binding.tvDrCr.text = "Dr"
                binding.tvDrCr.setTextColor(Color.parseColor("#5A0404"))
            }
        }
    }

    interface listListener {

        fun loadNext(id: Int)
    }
}