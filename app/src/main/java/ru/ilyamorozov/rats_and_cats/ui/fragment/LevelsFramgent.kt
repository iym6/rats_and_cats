package ru.ilyamorozov.rats_and_cats.ui.fragment

import android.annotation.SuppressLint
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
import ru.ilyamorozov.rats_and_cats.ui.adapter.LevelsAdapter
import ru.ilyamorozov.rats_and_cats.ui.activity.MainActivity
import ru.ilyamorozov.rats_and_cats.R
import ru.ilyamorozov.rats_and_cats.model.Level
import ru.ilyamorozov.rats_and_cats.viewmodel.SharedViewModel

class LevelsFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_levels, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.levelsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val levels = listOf(
            Level(1, getString(R.string.level1), getString(R.string.easy)),
            Level(2, getString(R.string.level2), getString(R.string.medium)),
            Level(3, getString(R.string.level3), getString(R.string.hard))
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