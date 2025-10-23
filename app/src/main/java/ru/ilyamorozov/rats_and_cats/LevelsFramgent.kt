package ru.ilyamorozov.rats_and_cats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LevelsFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_levels, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.levelsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val levels = listOf(
            Level(1, "Уровень 1", "Легкий"),
            Level(2, "Уровень 2", "Средний"),
            Level(3, "Уровень 3", "Сложный")
        )

        val adapter = LevelsAdapter(levels) { level ->
            viewModel.selectLevel(level, requireContext())
        }
        recyclerView.adapter = adapter

        // Наблюдение за выбранным уровнем для UI-обновления
        lifecycleScope.launch {
            viewModel.selectedLevel.collectLatest { selected ->
                adapter.selectedLevel = selected
                adapter.notifyDataSetChanged()
            }
        }

        // Обработка нажатия кнопки "Назад"
        view.findViewById<Button>(R.id.backButton)?.setOnClickListener {
            Log.i("RatsAndCats", "Back button clicked in LevelsFragment")
            (requireActivity() as? MainActivity)?.showMainMenu()
        }

        return view
    }
}