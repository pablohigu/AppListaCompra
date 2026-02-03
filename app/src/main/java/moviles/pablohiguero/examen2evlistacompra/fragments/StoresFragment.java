package moviles.pablohiguero.examen2evlistacompra.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import moviles.pablohiguero.examen2evlistacompra.R;
import moviles.pablohiguero.examen2evlistacompra.adapters.StoreAdapter;
import moviles.pablohiguero.examen2evlistacompra.models.Store;
import moviles.pablohiguero.examen2evlistacompra.utils.Utils;

public class StoresFragment extends Fragment {

    private RecyclerView recycler;
    private Realm realm;
    private StoreAdapter adapter;

    public StoresFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stores, container, false);

        realm = Realm.getDefaultInstance();
        recycler = view.findViewById(R.id.recyclerStores);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cargar todas las tiendas
        RealmResults<Store> stores = realm.where(Store.class).findAll();

        adapter = new StoreAdapter(stores, new StoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Store store, int position) {
                // LOGICA DE TIENDA ACTIVA
                realm.executeTransaction(r -> {
                    // 1. Desactivar todas
                    for (Store s : stores) s.setActive(false);
                    // 2. Activar la seleccionada
                    store.setActive(true);
                });
                // Notificar cambios para que se pinten los colores
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Tienda activa: " + store.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(Store store, int position) {
                // LOGICA DE MAPA (Intent Implícito)
                String mapUri = Utils.openStoreInMaps(store);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "No tienes app de mapas instalada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recycler.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) realm.close();
    }
}