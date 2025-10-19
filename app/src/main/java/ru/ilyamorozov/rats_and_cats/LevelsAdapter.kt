package ru.ilyamorozov.rats_and_cats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LevelsAdapter(
    private val levels: List<Level>,
    private val onLevelSelected: (Level) -> Unit
) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {

    var selectedLevel: Level? = null // Храним текущий выбранный уровень

    class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.levelName)
        val difficulty: TextView = itemView.findViewById(R.id.levelDifficulty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_level, parent, false)
        return LevelViewHolder(view)
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levels[position]
        holder.name.text = level.name
        holder.difficulty.text = level.difficulty
        // Подсветка выбранного уровня
        holder.itemView.isSelected = level == selectedLevel
        holder.itemView.setOnClickListener {
            selectedLevel = level
            onLevelSelected(level)
            notifyDataSetChanged() // Обновляем UI
        }
    }

    override fun getItemCount(): Int = levels.size
}