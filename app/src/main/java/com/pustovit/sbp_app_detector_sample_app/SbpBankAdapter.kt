package com.pustovit.sbp_app_detector_sample_app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pustovit.sbp_app_detector.model.SbpBank
import com.pustovit.sbp_app_detector_sample_app.databinding.LayoutItemSbpBankAnswerBinding

class SbpBankAdapter(private val onClick: (SbpBank) -> Unit) :
    ListAdapter<SbpBank, SbpBankAnswerViewHolder>(itemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SbpBankAnswerViewHolder {
        val binding = LayoutItemSbpBankAnswerBinding.inflate(LayoutInflater.from(parent.context))
        return SbpBankAnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SbpBankAnswerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, onClick)
        }
    }
}

class SbpBankAnswerViewHolder(private val binding: LayoutItemSbpBankAnswerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(sbpBank: SbpBank, onClick: (SbpBank) -> Unit) {
        binding.imageView.setImageDrawable(sbpBank.activityIconDrawable)
        binding.textView.text = sbpBank.appName
        binding.root.setOnClickListener {
            onClick(sbpBank)
        }
    }
}

private val itemCallback = object : DiffUtil.ItemCallback<SbpBank>() {
    override fun areItemsTheSame(oldItem: SbpBank, newItem: SbpBank): Boolean {
        return oldItem.requiredSchema == newItem.requiredSchema
    }

    override fun areContentsTheSame(oldItem: SbpBank, newItem: SbpBank): Boolean {
        return oldItem.requiredSchema == newItem.requiredSchema
    }
}
