package com.example.upskiller.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.upskiller.R;
import com.example.upskiller.model.Roadmap;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.ui.roadmap.RoadmapDetailActivity;
import com.example.upskiller.util.ApiCallback;

import java.util.ArrayList;
import java.util.List;

public class RoadmapListFragment extends Fragment {

    public static final String ARG_TYPE = "tab_type";
    public static final String TYPE_ACTIVE    = "active";
    public static final String TYPE_COMPLETED = "completed";

    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private RoadmapListAdapter adapter;
    private final List<Roadmap> roadmaps = new ArrayList<>();
    private String tabType;

    public static RoadmapListFragment newInstance(String type) {
        RoadmapListFragment f = new RoadmapListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        f.setArguments(args);
        return f;
    }

    @Override public void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        tabType = getArguments() != null ? getArguments().getString(ARG_TYPE, TYPE_ACTIVE) : TYPE_ACTIVE;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roadmap_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvEmpty      = view.findViewById(R.id.tvEmpty);
        RecyclerView rv = view.findViewById(R.id.rvRoadmaps);

        adapter = new RoadmapListAdapter(roadmaps, roadmap -> {
            Intent intent = new Intent(getContext(), RoadmapDetailActivity.class);
            intent.putExtra(RoadmapDetailActivity.EXTRA_ROADMAP_ID, roadmap.getId());
            startActivity(intent);
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadRoadmaps);
        loadRoadmaps();
    }

    @Override public void onResume() { super.onResume(); loadRoadmaps(); }

    private void loadRoadmaps() {
        swipeRefresh.setRefreshing(true);
        retrofit2.Call<List<Roadmap>> call = TYPE_ACTIVE.equals(tabType)
                ? ApiClient.get(requireContext()).getActiveRoadmaps()
                : ApiClient.get(requireContext()).getCompletedRoadmaps();

        call.enqueue(new ApiCallback<List<Roadmap>>() {
            @Override public void onSuccess(List<Roadmap> body) {
                if (!isAdded() || getView() == null) return;
                swipeRefresh.setRefreshing(false);
                adapter.updateData(body);
                tvEmpty.setVisibility(body.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String message) {
                if (!isAdded() || getView() == null) return;
                swipeRefresh.setRefreshing(false);
                tvEmpty.setVisibility(roadmaps.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }
}
