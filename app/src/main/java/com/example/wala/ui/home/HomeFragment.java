package com.example.wala.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wala.JSONParser;
import com.example.wala.Position;
import com.example.wala.databinding.FragmentHomeBinding;
import com.example.wala.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private final ArrayList<Position> data = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // 🔹 Initialisation du binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 🔹 Configuration du thread secondaire
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // 🔹 Événement bouton
        binding.btnAdd.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Downloading");
            dialog.setMessage("Please wait ...");
            AlertDialog alert = dialog.create();
            alert.show();

            executor.execute(() -> {
                JSONParser jParser = new JSONParser();
                JSONObject response = jParser.makeHttpRequest(config.URL_GetAll_Position, "GET", null);

                ArrayList<Position> positions = new ArrayList<>();

                if (response != null) {
                    Log.e("DEBUG", "Response: " + response);
                    try {
                        int success = response.getInt("success");
                        Log.e("DEBUG", "Success: " + success);
                        if (success == 1) {
                            JSONArray tableau = response.getJSONArray("positions");
                            Log.e("DEBUG", "Positions count: " + tableau.length());
                            for (int i = 0; i < tableau.length(); i++) {
                                JSONObject ligne = tableau.getJSONObject(i);
                                int id = ligne.getInt("idPosition");
                                String pseudo = ligne.getString("pseudo");
                                String numero = ligne.getString("numero");
                                String longitude = ligne.getString("longitude");
                                String latitude = ligne.getString("latitude");
                                positions.add(new Position(id, pseudo, numero, longitude, latitude));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("DEBUG", "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e("DEBUG", "Response is null!");
                }

                handler.post(() -> {
                    Log.e("DEBUG", "Positions loaded: " + positions.size());
                    ArrayAdapter<Position> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, positions);
                    binding.lvlocation.setAdapter(adapter);
                    alert.dismiss();
                });
            });

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
