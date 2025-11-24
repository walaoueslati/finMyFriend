package com.example.wala.ui.notifications;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wala.JSONParser;
import com.example.wala.config;
import com.example.wala.databinding.FragmentNotificationsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnAdd.setOnClickListener(v -> {
            String pseudo = binding.editPseudo.getText().toString().trim();
            String numero = binding.editNumero.getText().toString().trim();
            String latitude = binding.editLatitude.getText().toString().trim();
            String longitude = binding.editLongitude.getText().toString().trim();

            if (pseudo.isEmpty() || numero.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new AddLocationTask(pseudo, numero, latitude, longitude).execute();
        });

        return root;
    }

    class AddLocationTask extends AsyncTask<Void, Void, Boolean> {
        String pseudo, numero, latitude, longitude;
        JSONObject jsonResponse;

        AddLocationTask(String pseudo, String numero, String latitude, String longitude) {
            this.pseudo = pseudo;
            this.numero = numero;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HashMap<String, String> params = new HashMap<>();
            params.put("pseudo", pseudo);
            params.put("numero", numero);
            params.put("latitude", latitude);
            params.put("longitude", longitude);

            JSONParser parser = new JSONParser();
            jsonResponse = parser.makeHttpRequest(config.URL_Add_Position, "POST", params);

            if (jsonResponse != null) {
                try {
                    return jsonResponse.getInt("success") == 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getActivity(), "Position ajoutée avec succès", Toast.LENGTH_SHORT).show();
                binding.editPseudo.setText("");
                binding.editNumero.setText("");
                binding.editLatitude.setText("");
                binding.editLongitude.setText("");


            } else {
                Toast.makeText(getActivity(), "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                Log.e("AddLocationTask", "Réponse serveur: " + jsonResponse);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
