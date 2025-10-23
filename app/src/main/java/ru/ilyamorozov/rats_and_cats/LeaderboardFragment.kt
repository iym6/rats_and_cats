package ru.ilyamorozov.rats_and_cats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LeaderboardFragment : Fragment() {

    private val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        val localRecyclerView: RecyclerView = view.findViewById(R.id.localRecyclerView)
        localRecyclerView.layoutManager = LinearLayoutManager(context)
        val localAdapter = ScoreAdapter()
        localRecyclerView.adapter = localAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allScores.collectLatest { scores ->
                localAdapter.submitList(scores)
            }
        }

        val remoteRecyclerView: RecyclerView = view.findViewById(R.id.remoteRecyclerView)
        remoteRecyclerView.layoutManager = LinearLayoutManager(context)
        val remoteAdapter = RemoteRecordAdapter()
        remoteRecyclerView.adapter = remoteAdapter

        viewModel.top5Remote.observe(viewLifecycleOwner) { top5 ->
            remoteAdapter.submitList(top5)
        }

        val loadButton: Button = view.findViewById(R.id.loadTop5Button)
        loadButton.setOnClickListener {
            viewModel.loadTop5()
        }

        view.findViewById<Button>(R.id.backButton)?.setOnClickListener {
            Log.i("RatsAndCats", "Back button clicked in LeaderboardFragment")
            (requireActivity() as? MainActivity)?.showMainMenu()
        }
        return view
    }
}