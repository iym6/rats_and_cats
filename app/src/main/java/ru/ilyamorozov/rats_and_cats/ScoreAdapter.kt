package ru.ilyamorozov.rats_and_cats

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt

class ScoreAdapter : ListAdapter<ScoreRecord, ScoreAdapter.ScoreViewHolder>(ScoreDiffCallback()) {

    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.playerName)
        val score: TextView = itemView.findViewById(R.id.score)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val record = getItem(position)
        holder.playerName.text = record.playerName
        holder.score.text = record.score.toString()
        holder.date.text = record.date
    }
}

class ScoreDiffCallback : DiffUtil.ItemCallback<ScoreRecord>() {
    override fun areItemsTheSame(oldItem: ScoreRecord, newItem: ScoreRecord): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: ScoreRecord, newItem: ScoreRecord): Boolean = oldItem == newItem
}

// Аналогично для RemoteRecordAdapter, заменить ScoreRecord на RemoteRecord, поля name/score/date
class RemoteRecordAdapter : ListAdapter<RemoteRecord, RemoteRecordAdapter.RemoteViewHolder>(RemoteDiffCallback()) {

    class RemoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.playerName)
        val score: TextView = itemView.findViewById(R.id.score)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        return RemoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RemoteViewHolder, position: Int) {
        val record = getItem(position)
        holder.playerName.text = record.name
        holder.score.text = record.score.toString()
        holder.date.text = record.date
    }
}

class RemoteDiffCallback : DiffUtil.ItemCallback<RemoteRecord>() {
    override fun areItemsTheSame(oldItem: RemoteRecord, newItem: RemoteRecord): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: RemoteRecord, newItem: RemoteRecord): Boolean = oldItem == newItem
}