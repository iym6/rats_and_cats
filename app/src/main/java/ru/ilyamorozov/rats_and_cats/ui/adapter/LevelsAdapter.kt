package ru.ilyamorozov.rats_and_cats.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.ilyamorozov.rats_and_cats.R
import ru.ilyamorozov.rats_and_cats.model.Level

class LevelsAdapter(
    private val levels: List<Level>,
    private val onLevelSelected: (Level) -> Unit
) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {

    var selectedLevel: Level? = null

    class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.levelName)
        val difficulty: TextView = itemView.findViewById(R.id.levelDifficulty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_level, parent, false)
        return LevelViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levels[position]
        holder.name.text = level.name
        holder.difficulty.text = level.difficulty
        // Подсветка выбранного уровня
        holder.itemView.isSelected = level == selectedLevel
        holder.itemView.setOnClickListener {
            selectedLevel = level
            onLevelSelected(level)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = levels.size
}